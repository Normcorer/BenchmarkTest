package com.hemavip.batchtest.mapper;

import com.hemavip.batchtest.model.BatchTest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BatchTestMapper {
    int update(BatchTest batchTest);

    void updateBatch(List<BatchTest> batchTestList);

    void updateBatchList(List<BatchTest> batchTestList);

    void truncate();

    int count();
}