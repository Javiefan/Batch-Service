package com.bwts.batchservice.dao;

import com.bwts.batchservice.entity.TaskDocLog;

import java.util.List;
import java.util.UUID;

public interface TaskDocLogDAO {
    void insert(TaskDocLog taskDocLog);

    TaskDocLog get(UUID tenantId, UUID documentId);

    List<TaskDocLog> get(UUID tenantId);
}
