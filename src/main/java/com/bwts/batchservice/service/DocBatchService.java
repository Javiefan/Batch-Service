package com.bwts.batchservice.service;

import com.bwts.batchservice.dao.impl.MyBatisFailDocLogDAO;
import com.bwts.batchservice.dao.impl.MybatisTaskDocLogDAO;
import com.bwts.batchservice.dto.DocLogDTO;
import com.bwts.batchservice.entity.FailDocLog;
import com.bwts.batchservice.entity.TaskDocLog;
import com.bwts.common.batch.BatchMessage;
import com.bwts.common.kafka.producer.KafkaMessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Service
public class DocBatchService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DocBatchService.class);

    @Autowired
    private final KafkaMessageProducer kafkaMessageProducer;
    private final MybatisTaskDocLogDAO taskDocLogDAO;
    private final MyBatisFailDocLogDAO failDocLogDAO;

    @Value("${batch.disabled}")
    private boolean batchDisabled;

    private final int maxRetryCount;
    private final String producerTopic;

    @Autowired
    public DocBatchService(KafkaMessageProducer kafkaMessageProducer,
                           MybatisTaskDocLogDAO taskDocLogDAO,
                           MyBatisFailDocLogDAO failDocLogDAO,
                           @Value("${batch.max.retry}") int maxRetryCount,
                           @Value("${producer.batch.topic}") String producerTopic) {
        this.kafkaMessageProducer = kafkaMessageProducer;
        this.taskDocLogDAO = taskDocLogDAO;
        this.failDocLogDAO = failDocLogDAO;
        this.maxRetryCount = maxRetryCount;
        this.producerTopic = producerTopic;
    }

    public void processDocWithRetry(DocLogDTO docLogDTO) {
        if (batchDisabled) {
            LOGGER.info("batch job has be disabled by property settings.");
            return;
        }

        try {
            if(docLogDTO.getRetryTimes() >= maxRetryCount) {
                saveFailDoc(docLogDTO);
                return;
            } else {
                saveTaskDoc(docLogDTO);
                //!!!
                docLogDTO.setRetryTimes(docLogDTO.getRetryTimes());
            }
        } catch(Exception e) {
            LOGGER.info("save doc failure with some exceptions, put task into kafka again");
            //!!!
        }

        retry(docLogDTO);
    }

    private void retry(DocLogDTO docLogDTO) {
        BatchMessage.BatchMessageBuilder messageBuilder = new BatchMessage.BatchMessageBuilder();

        BatchMessage message = messageBuilder
                .withTenantId(docLogDTO.getTenantId())
                .withDocumentId(docLogDTO.getDocumentId())
                .withPayload(docLogDTO.getPayload())
                .withPhase(docLogDTO.getPhase())
                .withThrowTime(docLogDTO.getThrowTime())
                .build();
        kafkaMessageProducer.send(producerTopic, message);
    }

    @Transactional
    private void saveTaskDoc(DocLogDTO docLogDTO) {

        LOGGER.info("tenant: {} document: {} {}th times, max times {}", docLogDTO.getTenantId(), docLogDTO.getDocumentId(), maxRetryCount);
        TaskDocLog taskDocLog = new TaskDocLog();
        taskDocLog.setTenantId(docLogDTO.getTenantId());
        taskDocLog.setDocumentId(docLogDTO.getDocumentId());
        taskDocLog.setPhase(docLogDTO.getPhase());
        taskDocLog.setRetryTime(getTriedTimes(docLogDTO.getTenantId(), docLogDTO.getDocumentId()));

        taskDocLog.setMessage(docLogDTO.getMessage());
        taskDocLog.setActionResult(docLogDTO.getActionResult());
        taskDocLog.setActionTimestamp(new Date());

        taskDocLogDAO.insert(taskDocLog);
    }

    @Transactional
    public int getTriedTimes(UUID tenantId, UUID documentId) {
        return taskDocLogDAO.get(tenantId, documentId).size();
    }


    @Transactional
    public void saveFailDoc(DocLogDTO docLogDTO) {
        LOGGER.info("tenant: {} document: {} has tried more than {} times. stopping trying", docLogDTO.getTenantId(), docLogDTO.getDocumentId(), maxRetryCount);
        FailDocLog failDocLog = new FailDocLog();

        failDocLog.setTenantId(docLogDTO.getTenantId());
        failDocLog.setDocumentId(docLogDTO.getDocumentId());
        failDocLog.setPhase(docLogDTO.getPhase());

        failDocLog.setTaskId(docLogDTO.getTaskId());
        failDocLog.setMessage(docLogDTO.getMessage());
        failDocLog.setFailTimestamp(new Date());

        failDocLogDAO.insert(failDocLog);
    }

}


