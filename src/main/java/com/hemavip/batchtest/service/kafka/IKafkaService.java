package com.hemavip.batchtest.service.kafka;

public interface IKafkaService {

    void send(String topic, String message);

    void send(KafkaContext kafkaContext);

    String start();

    String stop();
}
