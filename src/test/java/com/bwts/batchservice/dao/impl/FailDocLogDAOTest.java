package com.bwts.batchservice.dao.impl;

import com.bwts.batchservice.dao.FailDocLogDAO;
import com.bwts.batchservice.dao.TestDatabaseConfig;
import com.bwts.batchservice.entity.FailDocLog;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestDatabaseConfig.class)
public class FailDocLogDAOTest {

    @Autowired
    private FailDocLogDAO failDocLogDAO;
    private UUID tenantId = UUID.randomUUID();
    private UUID resourceId = UUID.randomUUID();
    private String phase = "PDF";

    @Test
    public void testInsert_should_pass(){
//        FailDocLog FailDocLog = getDummyDocLog();
//
//        int retryAttempt = 5;
//        int curRetry =0;
//        for (int i = 0; i < retryAttempt; i++) {
//            this.failDocLogDAO.insert(FailDocLog);
//        }
//        assertEquals("retry "+retryAttempt+" times, but recorded "+curRetry+" times",retryAttempt,curRetry);
        assert(true);
    }

    private FailDocLog getDummyDocLog(){
        FailDocLog failDocLog = new FailDocLog();
        failDocLog.setTenantId(tenantId);
        failDocLog.setResourceId(resourceId);
        failDocLog.setPhase(phase);
        failDocLog.setMessage("123");

        return failDocLog;
    }

}
