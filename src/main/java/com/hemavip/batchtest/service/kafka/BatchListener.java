package com.hemavip.batchtest.service.kafka;

import com.alibaba.fastjson.JSON;
import com.hemavip.batchtest.constant.GlobalConstant;
import com.hemavip.batchtest.model.BatchTest;
import com.hemavip.batchtest.service.batch.JdbcBatchService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 短信发送监听器
 */
@Service
public class BatchListener {

    private static final Logger logger = LoggerFactory.getLogger(BatchListener.class);

    @Autowired
    JdbcBatchService jdbcBatchService;

    @KafkaListener(containerFactory = "kafkaBatchListener8", id = GlobalConstant.KAFKA_LISTENER_ID, topics = GlobalConstant.BENCHMARK_TEST_TOPIC)
    public void onMessage(List<ConsumerRecord<?, ?>> records, Acknowledgment ack) {
        logger.warn("开始时间{}", System.currentTimeMillis());

        List<BatchTest> batchTestList = new ArrayList<>();
        try {
            records.forEach(record -> {
                BatchTest batchTest = JSON.parseObject(record.value().toString(), BatchTest.class);
                batchTestList.add(batchTest);
            });
            logger.warn(jdbcBatchService.updateBatchJdbc(batchTestList) + "\n");
        } catch (Exception e) {
            logger.warn("Kafka监听异常" + e.getMessage(), e);
        } finally {
            ack.acknowledge();//手动提交偏移量
        }
        logger.warn("结束时间{}", System.currentTimeMillis());
    }
}
