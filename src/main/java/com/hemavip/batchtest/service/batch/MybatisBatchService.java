package com.hemavip.batchtest.service.batch;

import com.hemavip.batchtest.mapper.BatchTestMapper;
import com.hemavip.batchtest.model.BatchTest;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MybatisBatchService {
    @Autowired
    BatchTestMapper batchTestMapper;

    @Autowired
    SqlSessionFactory sqlSessionFactory;

    public void single() {
        int count = 0;
        long begin = System.currentTimeMillis();
        for (int i = 0; i < 50000; i++) {
            BatchTest batchTest = new BatchTest(i, "name" + i + 4, new Date(), new Date());
            count = count + batchTestMapper.update(batchTest);
        }
        Long end = System.currentTimeMillis();
        System.out.println("总计MyBatis单条更新" + count + "条，共计耗时" + (end - begin) / 1000 + "秒");
    }

    public void executorTypeBatchSingle() {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
        BatchTestMapper mapper = sqlSession.getMapper(BatchTestMapper.class);
        long begin = System.currentTimeMillis();
        try {
            for (int i = 0; i < 50000; i++) {
                BatchTest batchTest = new BatchTest(i, "name" + i + 8, new Date(), new Date());
                mapper.update(batchTest);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqlSession.commit();
            sqlSession.close();
            Long end = System.currentTimeMillis();
            System.out.println("总计MyBatis单条更新" + 50000 + "条，共计耗时" + (end - begin) / 1000 + "秒");
        }
    }

    public void updateBatch() {
        try {
            List<BatchTest> batchTestList = generateTestDate(50000, 1);
            long begin = System.currentTimeMillis();
            batchTestMapper.updateBatch(batchTestList);
            Long end = System.currentTimeMillis();
            System.out.println("总计MyBatis批量更新" + 50000 + "条，共计耗时" + (end - begin) / 1000 + "秒");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void executorTypeBatch() {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
        BatchTestMapper mapper = sqlSession.getMapper(BatchTestMapper.class);
        List<BatchTest> batchTestList = generateTestDate(50000, 4);
        long begin = System.currentTimeMillis();
        try {
            mapper.updateBatch(batchTestList);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqlSession.commit();
            sqlSession.close();
            Long end = System.currentTimeMillis();
            System.out.println("总计MyBatis批量更新" + 50000 + "条，共计耗时" + (end - begin) / 1000 + "秒");
        }
    }

    public void executorTypeBatchList() {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
        BatchTestMapper mapper = sqlSession.getMapper(BatchTestMapper.class);
        List<BatchTest> batchTestList = generateTestDate(50000, 2);
        long begin = System.currentTimeMillis();
        try {
            mapper.updateBatchList(batchTestList);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqlSession.commit();
            sqlSession.close();
            Long end = System.currentTimeMillis();
            System.out.println("总计MyBatis批量更新" + 50000 + "条，共计耗时" + (end - begin) / 1000 + "秒");
        }
    }


    public void updateBatchList() {
        List<BatchTest> batchTestList = generateTestDate(50000, 1);
        long begin = System.currentTimeMillis();
        try {
            batchTestMapper.updateBatchList(batchTestList);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Long end = System.currentTimeMillis();
            System.out.println("总计MyBatis批量更新" + 50000 + "条，共计耗时" + (end - begin) / 1000 + "秒");
        }
    }

    private List<BatchTest> generateTestDate(int num, int offset) {
        List<BatchTest> batchTestList = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            BatchTest batchTest = new BatchTest(i, "name" + i + offset, new Date(), new Date());
            batchTestList.add(batchTest);
        }
        return batchTestList;
    }
}
