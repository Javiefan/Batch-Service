package com.bwts.batchservice.dao;

import com.bwts.batchservice.entity.TaskDocLog;
import scala.util.parsing.combinator.testing.Str;

import java.util.List;
import java.util.UUID;

public interface TaskDocLogDAO {
    int insert(TaskDocLog taskDocLog);

    List<TaskDocLog> get(UUID tenantId, String resourceId, String phase, String resourceType);
}
