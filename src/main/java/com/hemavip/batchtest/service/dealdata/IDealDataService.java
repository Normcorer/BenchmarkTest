package com.hemavip.batchtest.service.dealdata;

public interface IDealDataService {
    boolean truncate();

    int count();

    String initData(int num);
}
