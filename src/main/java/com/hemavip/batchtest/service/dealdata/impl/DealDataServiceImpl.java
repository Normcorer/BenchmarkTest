package com.hemavip.batchtest.service.dealdata.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hemavip.batchtest.constant.GlobalConstant;
import com.hemavip.batchtest.mapper.BatchTestMapper;
import com.hemavip.batchtest.model.BatchTest;
import com.hemavip.batchtest.service.dealdata.IDealDataService;
import com.hemavip.batchtest.service.kafka.IKafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class DealDataServiceImpl implements IDealDataService {
    @Autowired
    BatchTestMapper batchTestMapper;

    @Autowired
    IKafkaService kafkaService;

    @Override
    public boolean truncate() {
        batchTestMapper.truncate();
        return count() == 0;
    }

    @Override
    public int count() {
        return batchTestMapper.count();
    }

    @Override
    public String initData(int num) {
        int count = 0;
        for (int i = 0; i < num; i++) {
            kafkaService.send(GlobalConstant.BENCHMARK_TEST_TOPIC, JSON.toJSONStringWithDateFormat(new BatchTest(i, "name-kafka" + i, new Date(), new Date()), "yyyy-MM-dd HH:mm:ss", SerializerFeature.PrettyFormat));
            count++;
        }
        return "总计初始化" + count + "条数据";
    }
}
