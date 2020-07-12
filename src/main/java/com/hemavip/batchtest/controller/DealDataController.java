package com.hemavip.batchtest.controller;

import com.hemavip.batchtest.service.dealdata.IDealDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dealData")
public class DealDataController {
    @Autowired
    IDealDataService dealDataService;

    @RequestMapping("initData/{num}")
    public String initData(@PathVariable("num") int num) {
       return dealDataService.initData(num);
    }

    @RequestMapping("truncate")
    public boolean truncate() {
        return dealDataService.truncate();
    }
}
