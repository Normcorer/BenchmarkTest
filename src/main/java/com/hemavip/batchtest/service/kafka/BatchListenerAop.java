package com.hemavip.batchtest.service.kafka;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Aspect
@Configuration
public class BatchListenerAop {
    @Autowired
    IKafkaService kafkaService;

//    @Before("execution(* com.hemavip.batchtest.service.dealdata.impl.DealDataServiceImpl.initData(..))")
//    public void initStop() {
//        System.out.println("停止消费者成功");
//        kafkaService.stop();
//    }
}
