package com.hemavip.batchtest.service.batch;

import com.hemavip.batchtest.model.BatchTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.List;

@Service
public class JdbcBatchService {
    private static final Logger logger = LoggerFactory.getLogger(JdbcBatchService.class);

    @Value("${db.driver-class-name}")
    private String driverClass;

    @Value("${db.jdbcUrl}")
    private String jdbcUrl;

    @Value("${db.username}")
    private String username;

    @Value("${db.password}")
    private String password;

    public void insert() {
        long begin = System.currentTimeMillis();
        try {
            Class.forName(driverClass);
        } catch (ClassNotFoundException e) {
            return;
        }
        //插入sql语句
        StringBuilder sql = new StringBuilder();
        sql.append(" insert into batch_test" +
                "(id, test_name, create_time, update_time)values(?,?,?,?)");
        int count = 0;
        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            conn.setAutoCommit(false);
            for (int i = 0; i < 10000; i++) {
                ps.setInt(1, i);
                ps.setString(2, "name" + i);
                ps.setDate(3, new Date(System.currentTimeMillis()));
                ps.setDate(4, new Date(System.currentTimeMillis()));
                count = count + ps.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        // 结束时间
        Long end = System.currentTimeMillis();
        // 耗时
        System.out.println("总计插入" + count + "条，共计耗时" + (end - begin) / 1000 + "秒");
    }

    public void singleUpdate() {
        long begin = System.currentTimeMillis();

        try {
            Class.forName(driverClass);
        } catch (ClassNotFoundException e) {
            return;
        }

        //插入sql语句
        StringBuilder sql = new StringBuilder();
        sql.append("update batch_test set test_name = ?, create_time = ?, update_time = ? where id = ?");
        int count = 0;
        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            conn.setAutoCommit(false);
            for (int i = 0; i < 50000; i++) {
                ps.setString(1, "name" + (i + 1));
                ps.setDate(2, new Date(System.currentTimeMillis()));
                ps.setDate(3, new Date(System.currentTimeMillis()));
                ps.setInt(4, i);
                count = count + ps.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        // 结束时间
        Long end = System.currentTimeMillis();
        // 耗时
        System.out.println("总计单条循环更新" + count + "条，共计耗时" + (end - begin) / 1000 + "秒");
    }

    public void batchUpdate() {
        long begin = System.currentTimeMillis();

        try {
            Class.forName(driverClass);
        } catch (ClassNotFoundException e) {
            return;
        }

        //插入sql语句
        StringBuilder sql = new StringBuilder();
        sql.append("update batch_test set test_name = ?, create_time = ?, update_time = ? where id = ?");
        int count = 0;
        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            conn.setAutoCommit(false);
            for (int i = 0; i < 50000; i++) {
                ps.setString(1, "name" + (i));
                ps.setDate(2, new Date(System.currentTimeMillis()));
                ps.setDate(3, new Date(System.currentTimeMillis()));
                ps.setInt(4, i);
                ps.addBatch();
                count++;
                if ((i + 1) % 5000 == 0) {
                    ps.executeBatch();
                    conn.commit();
                    ps.clearBatch();
                }
            }
            ps.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        // 结束时间
        Long end = System.currentTimeMillis();
        // 耗时
        System.out.println("总计单条循环更新" + count + "条，共计耗时" + (end - begin) / 1000 + "秒");
    }


    public String updateBatchJdbc(List<BatchTest> batchTests) {
        try {
            Class.forName(driverClass);
        } catch (ClassNotFoundException e) {
            logger.error("加载数据库驱动错误", e);
        }
        StringBuilder result = new StringBuilder();
        StringBuilder sql = new StringBuilder();
        sql.append("update batch_test set test_name = ?, create_time = ?, update_time = ? where id = ?");
        int count = 0;

        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            long begin = System.currentTimeMillis();
            conn.setAutoCommit(false);
            for (BatchTest batchTest : batchTests) {
                ps.setString(1, batchTest.getTestName());
                ps.setTimestamp(2, new Timestamp(batchTest.getCreateTime().getTime()));
                ps.setTimestamp(3, new Timestamp(batchTest.getUpdateTime().getTime()));
                ps.setInt(4, batchTest.getId().intValue());
                ps.addBatch();
                count++;
            }

            ps.executeBatch();
            conn.commit();
            Long end = System.currentTimeMillis();
            result.append(Thread.currentThread().getName() + "总计JDBC批量更新" + count + "条，总计耗时" + (end - begin) + "毫秒");
        } catch (SQLException e) {
            logger.error("数据库JDBC批量更新表benchmark_test错误", e);
        }
        return result.toString();
    }
}
