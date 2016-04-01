package com.bwts.batchservice.dao.impl;

import com.bwts.batchservice.dao.TaskDocLogDAO;
import com.bwts.batchservice.dao.TestDatabaseConfig;
import com.bwts.batchservice.entity.TaskDocLog;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestDatabaseConfig.class)
public class TaskDocLogDAOTest {
    @Autowired
    private TaskDocLogDAO taskDocLogDAO;

    private UUID flowId = UUID.randomUUID();
    private UUID tenantId = UUID.randomUUID();
    private UUID documentId = UUID.randomUUID();
    private String phase = "PDF";

    @Test
    public void testInsert_should_pass(){
        TaskDocLog taskDocLog = getDummyDocLog();

        int retryAttempt = 5;
        int curRetry =0;
        for (int i = 0; i < retryAttempt; i++) {
            this.taskDocLogDAO.insert(taskDocLog);
            curRetry = taskDocLog.getRetryTime();
        }
        assertEquals("retry "+retryAttempt+" times, but recorded "+curRetry+" times",retryAttempt,curRetry);
    }

    @Test
    public void testSelect_should_pass(){
        TaskDocLog taskDocLog = getDummyDocLog();

        int retryAttempt = 3;
        for (int i = 0; i < retryAttempt; i++) {
            this.taskDocLogDAO.insert(taskDocLog);
        }

        List<TaskDocLog> list =  this.taskDocLogDAO.get(flowId,tenantId,documentId,phase);
        assertEquals("expect "+retryAttempt+" records, but get "+ list.size()+" records", retryAttempt,list.size());
    }

    private TaskDocLog getDummyDocLog(){
        TaskDocLog taskDocLog = new TaskDocLog();
        taskDocLog.setFlowId(flowId);
        taskDocLog.setTenantId(tenantId);
        taskDocLog.setDocumentId(documentId);
        taskDocLog.setActionResult("FAIL");
        taskDocLog.setPhase(phase);
        taskDocLog.setMessage("123");
        return taskDocLog;
    }
}
