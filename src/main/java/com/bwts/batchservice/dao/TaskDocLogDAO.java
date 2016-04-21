package com.bwts.batchservice.dao;

import com.bwts.batchservice.entity.TaskDocLog;
import scala.util.parsing.combinator.testing.Str;

import java.util.List;
import java.util.UUID;

public interface TaskDocLogDAO {
    void insert(TaskDocLog taskDocLog);

    List<TaskDocLog> get(String resourceId, String resourceType);
}
