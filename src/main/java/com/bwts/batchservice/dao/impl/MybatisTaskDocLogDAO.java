package com.bwts.batchservice.dao.impl;

import com.bwts.batchservice.dao.TaskDocLogDAO;
import com.bwts.batchservice.dao.mapper.TaskDocLogMapper;
import com.bwts.batchservice.entity.TaskDocLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class MybatisTaskDocLogDAO implements TaskDocLogDAO{

    private final TaskDocLogMapper taskDocLogMapper;

    @Autowired
    public MybatisTaskDocLogDAO(TaskDocLogMapper taskDocLogMapper) {
        this.taskDocLogMapper = taskDocLogMapper;
    }

    @Override
    public int insert(TaskDocLog taskDocLog) {
        this.taskDocLogMapper.insert(taskDocLog);
        return taskDocLog.getRetryTime();
    }

    @Override
    public List<TaskDocLog> get(UUID flowId, UUID tenantId, UUID documentId, String phase) {
        Map<String,Object> map = new HashMap<>();
        map.put("flowId",flowId);
        map.put("tenantId",tenantId);
        map.put("documentId",documentId);
        map.put("phase",phase);
        return this.taskDocLogMapper.selectDocLog(map);
    }
}
