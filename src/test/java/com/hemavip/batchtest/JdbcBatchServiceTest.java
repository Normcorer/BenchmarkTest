package com.hemavip.batchtest;

import com.hemavip.batchtest.service.batch.JdbcBatchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JdbcBatchServiceTest {

    @Autowired
    JdbcBatchService jdbcBatchService;

    @Test
    public void testInsert() {
        jdbcBatchService.insert();
    }

    @Test
    public void testSingleUpdate() {
        jdbcBatchService.singleUpdate();
    }

    @Test
    public void testBatchUpdate() {
        jdbcBatchService.batchUpdate();
    }
}
