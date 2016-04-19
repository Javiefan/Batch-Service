package com.bwts.batchservice.service;

import com.bwts.batchservice.dao.impl.MybatisFailDocLogDAO;
import com.bwts.batchservice.dao.impl.MybatisTaskDocLogDAO;
import com.bwts.batchservice.dto.DocLogDTO;
import com.bwts.batchservice.dto.DocumentStatusDTO;
import com.bwts.batchservice.dto.DocumentStatusList;
import com.bwts.batchservice.dto.DocumentStatusListDTO;
import com.bwts.batchservice.entity.DocumentStatus;
import com.bwts.batchservice.entity.FailDocLog;
import com.bwts.batchservice.entity.TaskDocLog;
import com.bwts.common.security.DefaultTenants;
import com.bwts.common.security.UserContext;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.xml.ws.http.HTTPException;
import java.util.Date;

@Service
@Transactional
public class DocBatchService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DocBatchService.class);

    private static final String HEADER_USER_ID = "X-BWTS-UserId";
    private static final String HEADER_TENANT_ID = "X-BWTS-TenantId";

    private final MybatisTaskDocLogDAO taskDocLogDAO;
    private final MybatisFailDocLogDAO failDocLogDAO;

    @Value("${batch.disabled}")
    private boolean batchDisabled;

    @Value("${tenant.document.failurl}")
    private String failedJobUrl;

    @Value("${tenant.document.failstatusurl}")
    private String failedStatusUrl;

    @Value("${tenant.document.updateurlstatus}")
    private String updateStatusUrl;

    @Value("${batch.max.retry}")
    private int maxRetryCount;

    @Value("${init.page.num}")
    private int initPageNum;

    @Value("${page.size}")
    private int pageSize;

    @Autowired
    public DocBatchService(MybatisTaskDocLogDAO taskDocLogDAO,
                           MybatisFailDocLogDAO failDocLogDAO) {
        this.taskDocLogDAO = taskDocLogDAO;
        this.failDocLogDAO = failDocLogDAO;
    }

    private boolean processDoc(DocLogDTO docLogDTO) {
        boolean res = true;
        if (batchDisabled) {
            LOGGER.info("batch job has be disabled by property settings.");
            return res;
        }

        try {
            if (docLogDTO.getRetryTimes() >= maxRetryCount) {
                saveFailDoc(docLogDTO);
                res = false;
            } else {
                saveTaskDoc(docLogDTO);
                res = true;
            }
        } catch (Exception e) {
            LOGGER.info("save doc failure with some exceptions");
        }

        return res;
    }

    @Transactional
    private void saveTaskDoc(DocLogDTO docLogDTO) {

        LOGGER.info("tenant: {}  document: {}   has tried {}th times, max times {}", docLogDTO.getTenantId(),
                docLogDTO.getResourceId(), docLogDTO.getRetryTimes(), maxRetryCount);
        TaskDocLog taskDocLog = new TaskDocLog();
        taskDocLog.setTenantId(docLogDTO.getTenantId());
        taskDocLog.setResourceId(docLogDTO.getResourceId());
        taskDocLog.setPhase(docLogDTO.getPhase());
        taskDocLog.setRetryTime(docLogDTO.getRetryTimes());

        taskDocLog.setMessage(docLogDTO.getMessage());
        taskDocLog.setActionResult(docLogDTO.getActionResult());
        taskDocLog.setActionTimestamp(docLogDTO.getThrowTime());

        taskDocLogDAO.insert(taskDocLog);
    }

    @Transactional
    private void saveFailDoc(DocLogDTO docLogDTO) {
        LOGGER.info("tenant: {}  document: {}   has tried more than {} times. stopping trying", docLogDTO.getTenantId(),
                docLogDTO.getResourceId(), maxRetryCount);
        FailDocLog failDocLog = new FailDocLog();

        failDocLog.setTenantId(docLogDTO.getTenantId());
        failDocLog.setResourceId(docLogDTO.getResourceId());
        failDocLog.setPhase(docLogDTO.getPhase());

        failDocLog.setTaskId(docLogDTO.getTaskId());
        failDocLog.setMessage(docLogDTO.getMessage());
        failDocLog.setFailTimestamp(new Date());

        failDocLogDAO.insert(failDocLog);
    }

    @Transactional
    private int getRetriedTimes(DocLogDTO docLogDTO) {
        return taskDocLogDAO.get(docLogDTO.getTenantId(), docLogDTO.getResourceId(), docLogDTO.getPhase(), docLogDTO.getResourceType()).size();
    }


    @Scheduled(fixedRateString = "${scheduler.pull.interval}", initialDelay = 15 * 600)
    public void pullBatchService() {
        pullBatchService("SENDER");
        pullBatchService("RECEIVER");
    }

    private void pullBatchService(String owner) {
        int pageNum = initPageNum;
        while (true) {
            DocumentStatusListDTO documentStatusListDTO = getFailedJobList(owner, pageNum++, pageSize);
            DocumentStatusList senderStatus = getSenderStatus(owner, documentStatusListDTO);

            if (documentStatusListDTO == null || documentStatusListDTO.getTotalCount() <= 0 || documentStatusListDTO.getItems() == null) {
                return;
            }
            for (DocumentStatusDTO status : senderStatus.getItems()) {
                String type = documentStatusListDTO.getItems().get(0).getResourceType();
                status.setResourceType(type);
                switch (owner) {
                    case "SENDER": {
                        boolean res = prepareProcessDoc(status);
                        if (!res) {
                            updateDocumentStatus(status, "SENDER");
                        }
                        break;
                    }

                    case "RECEIVER": {
                        if (status.getFlowStatus() == DocumentStatus.SUCCESS) {
                            boolean res = prepareProcessDoc(status);
                            if (!res) {
                                updateDocumentStatus(status, "RECEIVER");
                            }
                        }
                        break;
                    }
                    default:
                        break;
                }
            }
        }
    }

    public boolean prepareProcessDoc(DocumentStatusDTO documentStatusDTO) {
        DocLogDTO docLogDTO = new DocLogDTO.Builder()
                .setTenantId(documentStatusDTO.getTenantId())
                .setResourceId(documentStatusDTO.getResourceId())
                .setPayload(documentStatusDTO.getUblData())
                .setThrowTime(new Date())
                .setActionResult(documentStatusDTO.getFlowStatus().toString())
                .build();

        docLogDTO.setPhase("Unknown");
        docLogDTO.setRetryTimes(getRetriedTimes(docLogDTO));

        SecurityContextHolder.setContext(new UserContext(null, docLogDTO.getTenantId()));

        boolean res = processDoc(docLogDTO);

        return res;
    }

    private DocumentStatusListDTO getFailedJobList(String owner, int pageNum, int pageSize) {
        LOGGER.info("{}: begin get failed jobs, {}", owner.toLowerCase(), new Date());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_USER_ID, DefaultTenants.SYSTEM_USER.toString());
        headers.add(HEADER_TENANT_ID, DefaultTenants.SUPER_ADMIN_TENANT.toString());
        HttpEntity entity = new HttpEntity(headers);

        String failedDocAddr = StringUtils.replace(failedJobUrl, "{pageNum}", String.valueOf(pageNum));
        failedDocAddr = StringUtils.replace(failedDocAddr, "{pageSize}", String.valueOf(pageSize));
        failedDocAddr = StringUtils.replace(failedDocAddr, "{from}", owner);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<DocumentStatusListDTO> responseEntity = null;
        try {
            responseEntity = restTemplate.exchange(failedDocAddr, HttpMethod.GET, entity, DocumentStatusListDTO.class);
        } catch (HTTPException e1) {
            LOGGER.info("get failed job: api calling exception, complete url is {}", failedJobUrl);
            LOGGER.info("{}", e1.getStackTrace());
        } catch (Exception e2) {
            LOGGER.info("exception other than HTTPException");
            LOGGER.info("{}", e2.getStackTrace());
        }

        DocumentStatusListDTO failedJobList = responseEntity.getBody();

        LOGGER.info("{}: fetched failed jobs {}", owner.toLowerCase(), new Date());

        return failedJobList;
    }

    private DocumentStatusList getSenderStatus(String owner, DocumentStatusListDTO documentStatusListDTO) {
        if (documentStatusListDTO == null) {
            LOGGER.info("documentStatusListDTO is illegal");
            return null;
        }

        LOGGER.info("{}: begin get sender status {}", owner, new Date());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("documentOwner", owner);

        JSONArray items = new JSONArray();
        for (DocumentStatusDTO documentStatusDTO : documentStatusListDTO.getItems()) {
            JSONObject status = new JSONObject();
            status.put("resourceId", documentStatusDTO.getResourceId().toString());
            status.put("tenantId", documentStatusDTO.getTenantId().toString());
            items.add(status);
        }
        jsonObject.put("items", items);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_USER_ID, DefaultTenants.SYSTEM_USER.toString());
        headers.add(HEADER_TENANT_ID, DefaultTenants.SUPER_ADMIN_TENANT.toString());
        HttpEntity entity = new HttpEntity(jsonObject, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<DocumentStatusList> responseEntity = null;
        try {
            responseEntity = restTemplate.exchange(failedStatusUrl, HttpMethod.PUT, entity, DocumentStatusList.class);
        } catch (HTTPException e1) {
            LOGGER.info("get sender status: api calling exception, complete url is {}", failedStatusUrl);
            LOGGER.info("{}", e1.getStackTrace());
        } catch (Exception e2) {
            LOGGER.info("exception other than HTTPException");
            LOGGER.info("{}", e2.getStackTrace());
        }

        DocumentStatusList senderStatus = responseEntity.getBody();

        LOGGER.info("{}: fetched sender status {}", owner, new Date());

        return senderStatus;
    }

    private void updateDocumentStatus(DocumentStatusDTO documentStatusDTO, String owner) {
        String updateStatusUrl = StringUtils.replace(this.updateStatusUrl, "{docId}", documentStatusDTO.getResourceId().toString());

        JSONObject jsonObject = new JSONObject();

        if (owner.equals("SENDER")) {
            jsonObject.put("flowStatus", DocumentStatus.RETRY_FAILED);
        } else if (owner.equals("RECEIVER")) {
            jsonObject.put("flowStatus", DocumentStatus.MIGRATION_RETRY_FAILED);
        } else {
            // do nothing
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_USER_ID, DefaultTenants.SYSTEM_USER.toString());
        headers.add(HEADER_TENANT_ID, DefaultTenants.SUPER_ADMIN_TENANT.toString());
        HttpEntity entity = new HttpEntity(jsonObject, headers);

        RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate.put(updateStatusUrl, entity);
        } catch (HTTPException e1) {
            LOGGER.info("update job status: api calling exception, complete url is {}", updateStatusUrl);
            LOGGER.info("{}", e1.getStackTrace());
        } catch (Exception e2) {
            LOGGER.info("exception other than HTTPException");
            LOGGER.info("{}", e2.getStackTrace());
        }

        LOGGER.info("document with documentid:{}, tenantid:{} has tried max times, update status finished",
                documentStatusDTO.getResourceId(), documentStatusDTO.getTenantId());
    }

}


