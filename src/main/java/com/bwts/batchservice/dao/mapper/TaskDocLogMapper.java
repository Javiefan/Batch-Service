package com.bwts.batchservice.dao.mapper;

import com.bwts.batchservice.entity.TaskDocLog;

import java.util.List;
import java.util.Map;

public interface TaskDocLogMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TaskDocLog record);

    TaskDocLog selectByPrimaryKey(Long id);

    List<TaskDocLog> selectDocLog(Map map);
}
