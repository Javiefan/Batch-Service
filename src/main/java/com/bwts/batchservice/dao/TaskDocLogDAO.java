package com.bwts.batchservice.dao;

import com.bwts.batchservice.entity.TaskDocLog;

import java.util.List;
import java.util.UUID;

public interface TaskDocLogDAO {
    int insert(TaskDocLog taskDocLog);

    List<TaskDocLog> get(UUID tenantId,UUID documentId,String phase);
}
