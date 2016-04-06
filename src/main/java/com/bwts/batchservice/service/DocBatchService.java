package com.bwts.batchservice.service;

import com.bwts.batchservice.dao.impl.MybatisFailDocLogDAO;
import com.bwts.batchservice.dao.impl.MybatisTaskDocLogDAO;
import com.bwts.batchservice.dto.DocLogDTO;
import com.bwts.batchservice.dto.DocumentStatusDTO;
import com.bwts.batchservice.dto.DocumentStatusListDTO;
import com.bwts.batchservice.entity.FailDocLog;
import com.bwts.batchservice.entity.TaskDocLog;
import com.bwts.common.kafka.message.InvoiceMessage;
import com.bwts.common.kafka.producer.KafkaMessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Service
@Transactional
public class DocBatchService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DocBatchService.class);

    @Autowired
    private KafkaMessageProducer kafkaMessageProducer;
    private final MybatisTaskDocLogDAO taskDocLogDAO;
    private final MybatisFailDocLogDAO failDocLogDAO;

    @Value("${batch.disabled}")
    private boolean batchDisabled;

    private final int maxRetryCount;
    private final String producerTopic;

    @Autowired
    public DocBatchService(MybatisTaskDocLogDAO taskDocLogDAO,
                           MybatisFailDocLogDAO failDocLogDAO,
                           @Value("${batch.max.retry}") int maxRetryCount,
                           @Value("${consumer.invoice.topic}") String producerTopic) {
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
            }
        } catch(Exception e) {
            LOGGER.info("save doc failure with some exceptions, put task into kafka again");
        }

        retry(docLogDTO);
    }

    private void retry(DocLogDTO docLogDTO) {
        InvoiceMessage.InvoiceMessgeBuilder messageBuilder = new InvoiceMessage.InvoiceMessgeBuilder();

        InvoiceMessage message = messageBuilder
                .withDocumentId(docLogDTO.getDocumentId())
                .withInvoiceData(docLogDTO.getPayload())
                .build();
        kafkaMessageProducer.send(producerTopic, message);
    }

    @Transactional
    private void saveTaskDoc(DocLogDTO docLogDTO) {

        LOGGER.info("tenant: {} document: {} {}th times, max times {}", docLogDTO.getTenantId(), docLogDTO.getDocumentId(), docLogDTO.getRetryTimes(), maxRetryCount);
        TaskDocLog taskDocLog = new TaskDocLog();
        taskDocLog.setTenantId(docLogDTO.getTenantId());
        taskDocLog.setDocumentId(docLogDTO.getDocumentId());
        taskDocLog.setPhase(docLogDTO.getPhase());
        taskDocLog.setRetryTime(docLogDTO.getRetryTimes());

        taskDocLog.setMessage(docLogDTO.getMessage());
        taskDocLog.setActionResult(docLogDTO.getActionResult());
        taskDocLog.setActionTimestamp(docLogDTO.getThrowTime());

        int retryTimes = taskDocLogDAO.insert(taskDocLog);

        docLogDTO.setRetryTimes(retryTimes);

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

    @Scheduled(fixedRate = 5000)
    public void pullBatchService() {
        LOGGER.info("begin pull batch job");
        RestTemplate restTemplate = new RestTemplate();
        DocumentStatusListDTO failedJobList = restTemplate.getForObject("http://localhost:19921/documents/failed",DocumentStatusListDTO.class);

        for(DocumentStatusDTO document: failedJobList.getUblDataList()) {
            if(document.getUblData() == null) {
                continue;
            }
            DocLogDTO docLogDTO = new DocLogDTO.Builder()
                    .setTenantId(document.getTenantId())
                    .setDocumentId(document.getDocumentId())
                    .setPayload(document.getUblData())
                    .setThrowTime(new Date())
                    .build();
            docLogDTO.setPhase("Unknown");


            processDocWithRetry(docLogDTO);
        }

        LOGGER.info("end pull batch job");

    }

}


