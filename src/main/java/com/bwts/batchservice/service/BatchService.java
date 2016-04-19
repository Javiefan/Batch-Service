package com.bwts.batchservice.service;


import com.bwts.batchservice.dao.FailDocLogDAO;
import com.bwts.batchservice.dao.TaskDocLogDAO;
import com.bwts.batchservice.dto.DocLogDTO;
import com.bwts.batchservice.entity.FailDocLog;
import com.bwts.batchservice.entity.TaskDocLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class BatchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchService.class);

    @Autowired
    private TaskDocLogDAO taskDocLogDAO;
    @Autowired
    private FailDocLogDAO failDocLogDAO;

    @Value("${batch.max.retry}")
    private int maxRetryCount;


    public void processTask(DocLogDTO docLogDTO, BatchCallback callback) {
        int retriedTimes = getRetriedTimes(docLogDTO);
        if (retriedTimes >= maxRetryCount) {
            LOGGER.info("type: {} resourceId: {}   has tried more than {} times. stopping trying", docLogDTO.getResourceType(), docLogDTO.getTenantId(),
                    docLogDTO.getResourceId(), maxRetryCount);
            saveFailDoc(docLogDTO);
            callback.exceedMaxRetry();
        } else {
            LOGGER.info("type: {} resourceId: {}  has tried {}th times, max times {}", docLogDTO.getResourceType(), docLogDTO.getTenantId(),
                    docLogDTO.getResourceId(), docLogDTO.getRetryTimes(), maxRetryCount);
            docLogDTO.setRetryTimes(docLogDTO.getRetryTimes() + 1);
            saveTaskDoc(docLogDTO);
            callback.afterRetry();
        }
    }

    private void saveTaskDoc(DocLogDTO docLogDTO) {
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

    private void saveFailDoc(DocLogDTO docLogDTO) {
        FailDocLog failDocLog = new FailDocLog();

        failDocLog.setTenantId(docLogDTO.getTenantId());
        failDocLog.setResourceId(docLogDTO.getResourceId());
        failDocLog.setPhase(docLogDTO.getPhase());

        failDocLog.setTaskId(docLogDTO.getTaskId());
        failDocLog.setMessage(docLogDTO.getMessage());
        failDocLog.setFailTimestamp(new Date());

        failDocLogDAO.insert(failDocLog);
    }

    public int getRetriedTimes(DocLogDTO docLogDTO) {
        return taskDocLogDAO.get(docLogDTO.getTenantId(), docLogDTO.getResourceId(), docLogDTO.getPhase(), docLogDTO.getResourceType()).size();
    }

}
