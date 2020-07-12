package com.hemavip.batchtest.service.kafka;

public class KafkaContext {
    private String topic;

    private String key;

    private String message;

    private Integer partition;

    Long timestamp;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getPartition() {
        return partition;
    }

    public void setPartition(Integer partition) {
        this.partition = partition;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public KafkaContext() {
    }

    public KafkaContext(String topic, String message) {
        this.topic = topic;
        this.message = message;
    }
}
