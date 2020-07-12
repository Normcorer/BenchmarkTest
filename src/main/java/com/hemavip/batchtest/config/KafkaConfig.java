package com.hemavip.batchtest.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {
    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.producer.retries}")
    private Integer retries;

    @Value("${spring.kafka.producer.batch-size}")
    private Integer batchSize;

    @Value("${spring.kafka.producer.buffer-memory}")
    private Integer bufferMemory;

    @Value("${spring.kafka.producer.linger}")
    private Integer linger;

    @Value("${spring.kafka.consumer.enable-auto-commit}")
    private Boolean autoCommit;

    @Value("${spring.kafka.consumer.auto-commit-interval}")
    private Integer autoCommitInterval;

    @Value("${spring.kafka.consumer.max-poll-records}")
    private Integer maxPollRecords;

    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;

    @Value("#{'${spring.kafka.listener.concurrencys}'.split(',')[0]}")
    private Integer concurrency1;

    @Value("#{'${spring.kafka.listener.concurrencys}'.split(',')[1]}")
    private Integer concurrency3;

    @Value("#{'${spring.kafka.listener.concurrencys}'.split(',')[2]}")
    private Integer concurrency4;

    @Value("#{'${spring.kafka.listener.concurrencys}'.split(',')[3]}")
    private Integer concurrency6;

    @Value("#{'${spring.kafka.listener.concurrencys}'.split(',')[4]}")
    private Integer concurrency8;

    @Value("${spring.kafka.listener.poll-timeout}")
    private Long pollTimeout;

    @Value("${spring.kafka.consumer.session-timeout}")
    private String sessionTimeout;

    @Value("${spring.kafka.listener.batch-listener}")
    private Boolean batchListener;

    @Value("${spring.kafka.consumer.max-poll-interval}")
    private Integer maxPollInterval;

    @Value("${spring.kafka.consumer.max-partition-fetch-bytes}")
    private Integer maxPartitionFetchBytes;

    private Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>(7);
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.RETRIES_CONFIG, retries);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
        props.put(ProducerConfig.LINGER_MS_CONFIG, linger);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }

    private ProducerFactory<String, String> producerFactory() {
        DefaultKafkaProducerFactory<String, String> producerFactory = new DefaultKafkaProducerFactory<>(producerConfigs());
        return producerFactory;
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }


    @Bean
    @ConditionalOnMissingBean(name = "kafkaBatchListener8")
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaBatchListener8() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = kafkaListenerContainerFactory();
        factory.setBatchListener(true);
        factory.setConcurrency(concurrency8);
        return factory;
    }

    @Bean
    @ConditionalOnMissingBean(name = "kafkaBatchListener6")
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaBatchListener6() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = kafkaListenerContainerFactory();
        factory.setBatchListener(true);
        factory.setConcurrency(concurrency6);
        return factory;
    }
    /**
     * 并发数4
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(name = "kafkaBatchListener4")
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaBatchListener4() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = kafkaListenerContainerFactory();
        factory.setBatchListener(true);
        factory.setConcurrency(concurrency4);
        return factory;
    }

    /**
     * 并发数3
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(name = "kafkaBatchListener3")
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaBatchListener3() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = kafkaListenerContainerFactory();
        factory.setBatchListener(true);
        factory.setConcurrency(concurrency3);
        return factory;
    }

    @Bean
    @ConditionalOnMissingBean(name = "kafkaBatchListener1")
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaBatchListener1() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = kafkaListenerContainerFactory();
        factory.setBatchListener(true);
        factory.setConcurrency(concurrency1);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }

    private ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        //批量消费
        factory.setBatchListener(batchListener);
        //如果消息队列中没有消息，等待timeout毫秒后，调用poll()方法。
        // 如果队列中有消息，立即消费消息，每次消费的消息的多少可以通过max.poll.records配置。
        //手动提交无需配置
        factory.getContainerProperties().setPollTimeout(pollTimeout);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    private ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    private Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>(10);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, autoCommitInterval);
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, autoCommit);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionTimeout);
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, maxPollInterval);
        props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, maxPartitionFetchBytes);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }
}
