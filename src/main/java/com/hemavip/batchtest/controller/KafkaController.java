package com.hemavip.batchtest.controller;

import com.hemavip.batchtest.service.kafka.BatchListener;
import com.hemavip.batchtest.service.kafka.IKafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kafka")
public class KafkaController {
    @Autowired
    IKafkaService kafkaService;

    @Autowired
    BatchListener batchListener;

    @RequestMapping("start")
    public String start() {
        return kafkaService.start();
    }

    @RequestMapping("stop")
    public String stop() {
        return kafkaService.stop();
    }
}
