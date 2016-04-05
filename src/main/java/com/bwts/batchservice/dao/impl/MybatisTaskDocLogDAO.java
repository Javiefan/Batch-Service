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

    @Autowired
    private TaskDocLogMapper taskDocLogMapper;

    @Override
    public int insert(TaskDocLog taskDocLog) {
        int result = taskDocLogMapper.insert(taskDocLog);
        System.out.println(result);
        return taskDocLog.getRetryTime();
    }

    @Override
    public List<TaskDocLog> get(UUID tenantId, UUID documentId, String phase) {
        Map<String,Object> map = new HashMap<>();
        map.put("tenantId",tenantId);
        map.put("documentId",documentId);
        map.put("phase",phase);
        return taskDocLogMapper.selectDocLog(map);
    }
}
