package com.bwts.batchservice.dao.impl;

import com.bwts.batchservice.dao.TaskDocLogDAO;
import com.bwts.batchservice.dao.TestDatabaseConfig;
import com.bwts.batchservice.entity.TaskDocLog;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestDatabaseConfig.class)
public class TaskDocLogDAOTest {
    @Autowired
    private TaskDocLogDAO taskDocLogDAO;

    private UUID tenantId = UUID.randomUUID();
    private String resourceId = UUID.randomUUID().toString();
    private String phase = "PDF";
    private String resourceType = "generate_pdf";

    @Test
    public void testInsert_should_pass(){
        TaskDocLog taskDocLog = getDummyDocLog();

        int retryAttempt = 5;
        int curRetry =0;
        for (int i = 0; i < retryAttempt; i++) {
            this.taskDocLogDAO.insert(taskDocLog);
        }
        List<TaskDocLog> taskDocLogs = taskDocLogDAO.get(taskDocLog.getResourceId(), taskDocLog.getResourceType());
        assertEquals("retry "+retryAttempt+" times, but recorded "+curRetry+" times",retryAttempt,taskDocLogs.size());
    }

    @Test
    public void testSelect_should_pass(){
        TaskDocLog taskDocLog = getDummyDocLog();

        int retryAttempt = 3;
        for (int i = 0; i < retryAttempt; i++) {
            this.taskDocLogDAO.insert(taskDocLog);
        }

        List<TaskDocLog> list =  this.taskDocLogDAO.get(resourceId, resourceType);
        assertEquals("expect "+retryAttempt+" records, but get "+ list.size()+" records", retryAttempt,list.size());
    }

    private TaskDocLog getDummyDocLog(){
        TaskDocLog taskDocLog = new TaskDocLog();
        taskDocLog.setTenantId(tenantId);
        taskDocLog.setResourceId(resourceId);
        taskDocLog.setActionResult("FAIL");
        taskDocLog.setPhase(phase);
        taskDocLog.setMessage("123");
        taskDocLog.setActionTimestamp(new Date());
        taskDocLog.setResourceType(resourceType);
        return taskDocLog;
    }
}
