package com.hemavip.batchtest;

import com.hemavip.batchtest.constant.GlobalConstant;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest
public class KafkaTest {
    @Autowired // adminClien需要自己生成配置bean
    private AdminClient adminClient;


    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Test//自定义手动创建topic和分区
    public void testCreateTopic() throws InterruptedException {
        // 这种是手动创建 //10个分区，一个副本
        // 分区多的好处是能快速的处理并发量，但是也要根据机器的配置
        NewTopic topic = new NewTopic(GlobalConstant.BENCHMARK_TEST_TOPIC, 1, (short) 1);
        adminClient.createTopics(Arrays.asList(topic));
        Thread.sleep(1000);
    }

    @Test//自定义手动创建topic和分区
    public void testDeleteTopic() throws InterruptedException {
        adminClient.deleteTopics(Arrays.asList("benchmarkTest_topic"));
        Thread.sleep(1000);
    }



    @Test
    public void testGetAllTopic() throws Exception {
        ListTopicsResult listTopics = adminClient.listTopics();
        Set<String> topics = listTopics.names().get();

        for (String topic : topics) {
            System.err.println(topic);
        }
    }
}
