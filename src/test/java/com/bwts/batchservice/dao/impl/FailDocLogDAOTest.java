package com.bwts.batchservice.dao.impl;

import com.bwts.batchservice.dao.FailDocLogDAO;
import com.bwts.batchservice.dao.TestDatabaseConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestDatabaseConfig.class)
public class FailDocLogDAOTest {

    @Autowired
    private FailDocLogDAO failDocLogDAO;

    @Test
    public void testInsert_should_pass(){
        this.failDocLogDAO.insert();
    }

}
