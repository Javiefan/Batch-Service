package com.bwts.batchservice.dao.mapper;

import com.bwts.batchservice.entity.TaskDocLog;

public interface TaskDocLogMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TaskDocLog record);

    int insertSelective(TaskDocLog record);

    TaskDocLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TaskDocLog record);

    int updateByPrimaryKey(TaskDocLog record);
}
