package com.hemavip.batchtest;

import com.hemavip.batchtest.service.batch.MybatisBatchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class MybatisBatchServiceTest {
    @Autowired
    MybatisBatchService mybatisBatchService;

    @Test
    public void testSingle() {
        mybatisBatchService.single();
    }

    @Test
    public void testBatch() {
        mybatisBatchService.updateBatch();
    }

    @Test
    public void testExecutorTypeBatch() {
        mybatisBatchService.executorTypeBatch();
    }

    @Test
    public void testUpdateBatchList() {
        mybatisBatchService.updateBatchList();
    }

    @Test
    public void testExecutorTypeBatchList() {
        mybatisBatchService.executorTypeBatchList();
    }

    @Test
    public void testExecutorTypeBatchSingle() {
        mybatisBatchService.executorTypeBatchSingle();
    }
}
