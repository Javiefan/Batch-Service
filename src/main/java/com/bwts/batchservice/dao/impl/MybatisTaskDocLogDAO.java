package com.bwts.batchservice.dao.impl;

import com.bwts.batchservice.dao.TaskDocLogDAO;
import com.bwts.batchservice.dao.mapper.TaskDocLogMapper;
import com.bwts.batchservice.entity.TaskDocLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class MybatisTaskDocLogDAO implements TaskDocLogDAO{

    private final TaskDocLogMapper taskDocLogMapper;

    @Autowired
    public MybatisTaskDocLogDAO(TaskDocLogMapper taskDocLogMapper) {
        this.taskDocLogMapper = taskDocLogMapper;
    }

    @Override
    public void insert(TaskDocLog taskDocLog) {

    }

    @Override
    public TaskDocLog get(UUID tenantId, UUID documentId) {
        return null;
    }

    @Override
    public List<TaskDocLog> get(UUID tenantId) {
        return null;
    }
}
