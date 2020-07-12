package com.hemavip.batchtest.model;

import java.util.Date;

public class BatchTest {
    private Integer id;

    private String testName;

    private Date createTime;

    private Date updateTime;

    public BatchTest() {

    }

    public BatchTest(Integer id, String testName, Date createTime, Date updateTime) {
        this.id = id;
        this.testName = testName;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "BatchTest{" +
                "id=" + id +
                ", testName='" + testName + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}