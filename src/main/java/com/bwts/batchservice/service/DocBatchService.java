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
import com.bwts.common.security.DefaultTenants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.Iterator;

@Service
@Transactional
public class DocBatchService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DocBatchService.class);

    private static final String HEADER_USER_ID = "X-BWTS-UserId";
    private static final String HEADER_TENANT_ID = "X-BWTS-TenantId";

    @Autowired
    private KafkaMessageProducer kafkaMessageProducer;
    private final MybatisTaskDocLogDAO taskDocLogDAO;
    private final MybatisFailDocLogDAO failDocLogDAO;

    @Value("${batch.disabled}")
    private boolean batchDisabled;

    @Value("${tenant.document.failurl}")
    private String failedUrlTmpl;

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
            if (docLogDTO.getRetryTimes() >= maxRetryCount) {
                saveFailDoc(docLogDTO);
                return;
            } else {
                saveTaskDoc(docLogDTO);
            }
        } catch (Exception e) {
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

        LOGGER.info("tenant: {} document: {} {}th times, max times {}", docLogDTO.getTenantId(),
                docLogDTO.getDocumentId(), docLogDTO.getRetryTimes(), maxRetryCount);
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
        LOGGER.info("tenant: {} document: {} has tried more than {} times. stopping trying", docLogDTO.getTenantId(),
                docLogDTO.getDocumentId(), maxRetryCount);
        FailDocLog failDocLog = new FailDocLog();

        failDocLog.setTenantId(docLogDTO.getTenantId());
        failDocLog.setDocumentId(docLogDTO.getDocumentId());
        failDocLog.setPhase(docLogDTO.getPhase());

        failDocLog.setTaskId(docLogDTO.getTaskId());
        failDocLog.setMessage(docLogDTO.getMessage());
        failDocLog.setFailTimestamp(new Date());

        failDocLogDAO.insert(failDocLog);
    }

    @Scheduled(fixedRateString = "${scheduler.pull.interval}", initialDelay = 5000)
    public void pullBatchService() {
        LOGGER.info("begin pull batch job");
        int curIdx = 1;
        int pageSize = 20;
        boolean isContinue = true;
        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_USER_ID, DefaultTenants.ANONYMOUS_USER.toString());
        headers.add(HEADER_TENANT_ID, DefaultTenants.ANONYMOUS_TENANT.toString());
        HttpEntity entity = new HttpEntity(headers);
        while (isContinue) {
            String failedDocAddr = StringUtils.replace(failedUrlTmpl, "{startIdx}", String.valueOf(curIdx));
            failedDocAddr = StringUtils.replace(failedDocAddr, "{count}", String.valueOf(pageSize));
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<DocumentStatusListDTO> responseEntity =
                    restTemplate.exchange(failedDocAddr, HttpMethod.GET, entity, DocumentStatusListDTO.class);
            DocumentStatusListDTO failedJobList = responseEntity.getBody();
            if (failedJobList != null && failedJobList.getTotalResults() > 0
                    && failedJobList.getUblDataList() != null) {
                for (DocumentStatusDTO document : failedJobList.getUblDataList()) {
                    if (document.getUblData() == null) {
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
                curIdx += pageSize;
            } else {
                isContinue = false;
            }
        }
        LOGGER.info("end pull batch job");
    }

}


