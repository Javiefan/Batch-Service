package com.bwts.batchservice.dao.impl;

import com.bwts.batchservice.dao.FailDocLogDAO;
import com.bwts.batchservice.dao.mapper.FailDocLogMapper;
import com.bwts.batchservice.entity.FailDocLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MybatisFailDocLogDAO implements FailDocLogDAO{

    @Autowired
    private FailDocLogMapper failDocLogMapper;

    @Override
    public void insert(FailDocLog failDocLog) {
        this.failDocLogMapper.insert(failDocLog);
    }
}
