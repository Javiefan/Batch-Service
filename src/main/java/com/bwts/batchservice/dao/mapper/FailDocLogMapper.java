package com.bwts.batchservice.dao.mapper;

import com.bwts.batchservice.entity.FailDocLog;

public interface FailDocLogMapper {
    int deleteByPrimaryKey(Long id);

    int insert(FailDocLog record);

    int insertSelective(FailDocLog record);

    FailDocLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FailDocLog record);

    int updateByPrimaryKey(FailDocLog record);
}
