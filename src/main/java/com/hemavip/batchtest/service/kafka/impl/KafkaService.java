package com.hemavip.batchtest.service.kafka.impl;

import com.hemavip.batchtest.constant.GlobalConstant;
import com.hemavip.batchtest.service.kafka.IKafkaService;
import com.hemavip.batchtest.service.kafka.KafkaContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
public class KafkaService implements IKafkaService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaService.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private KafkaListenerEndpointRegistry registry;

    @Override
    @Async
    public void send(String topic, String message) {
        KafkaContext kafkaContext = new KafkaContext(topic, message);
        send(kafkaContext);
    }

    @Override
    @Async
    public void send(KafkaContext kafkaContext) {
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(kafkaContext.getTopic(), kafkaContext.getPartition(), kafkaContext.getTimestamp(), (String) kafkaContext.getKey(), kafkaContext.getMessage());
        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onSuccess(final SendResult<String, String> message) {
                logger.info("成功发送消息: {}, offset = {} ", message, message.getRecordMetadata().offset());
            }

            @Override
            public void onFailure(final Throwable throwable) {
                logger.error("发送消息失败: {} , 异常：{}", kafkaContext.getMessage()
                        , throwable);
            }
        });
    }

    @Override
    public String stop() {
        try {
            registry.getListenerContainer(GlobalConstant.KAFKA_LISTENER_ID).stop();
        } catch (Exception e) {
            logger.error("停止监听器异常", e);
            return "error";
        }
        return "success";
    }

    @Override
    public String start() {
        try {
            registry.getListenerContainer(GlobalConstant.KAFKA_LISTENER_ID).start();
        } catch (Exception e) {
            logger.error("停止监听器异常", e);
            return "error";
        }
        return "success";
    }
}
