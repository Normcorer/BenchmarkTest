package com.hemavip.batchtest.config;

import com.hemavip.batchtest.constant.GlobalConstant;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaInitConfiguration {
    //创建Topic并设置分区数为8以及副本数为1
    @Bean
    public NewTopic initialTopic() {
        return new NewTopic(GlobalConstant.BENCHMARK_TEST_TOPIC, 1, (short) 1);
    }

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> props = new HashMap<>();
        //配置Kafka实例的连接地址                                                                    //kafka的地址，不是zookeeper
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.230.26:9092,192.168.230.27:9092,192.168.230.28:9092");
        KafkaAdmin admin = new KafkaAdmin(props);
        return admin;
    }

    @Bean  //kafka客户端，在spring中创建这个bean之后可以注入并且创建topic
    public AdminClient adminClient() {
        return AdminClient.create(kafkaAdmin().getConfig());
    }
}
