# BenchmarkTest
Mysql批量更新（Jdbc,Mybatis,Kafka,多线程）效率测试

## 前言
由于最近公司业务需求，我们需要重新开发一套系统，其中需求就是需要高TPS，高性能数据库读写能力，由此我测试了Mysql，JDBC、MyBatis并且集成Kafka来做一个性能测试。由于在框架测试上的代码不能公开，这里我自己重新快速写了个Demo，具体代码和完整的测试文档我也会上传至我的Github上。

## JDBC效率测试

### 单条新增及更新
```java
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
            for (int i = 0; i < 50000; i++) {
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
```
**最后结果**
注意代码中的conn.setAutoCommit(false)，下面分别时开启自动提交和关闭自动提交的结果：
- 开启自动提交最后结果：总计插入50000条，共计耗时61秒
- 关闭自动提交最后结果：总计插入50000条，共计耗时19秒

同样测试单条更新的结果，由于更新和新增只是单纯的sql语句不一样，这里代码就不贴了，如有需求可以去Git上下载完整代码
- 开启自动提交最后结果：总计单条循环更新50000条，共计耗时96秒
- 关闭自动提交最后结果：总计单条循环更新50000条，共计耗时26秒

### 批量新增及更新
```java
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
```
想要让批量更新的效率提高，网上也有很多答案，我这里整理了一下，大多是在jdbcUrl上做操作:
```
&allowMultiQueries=true&rewriteBatchedStatements=true
```
经过我一番测试&rewriteBatchedStatements=true这行参数加了是真的起了效果，如下是对这个参数的介绍：
```
   MySQL的JDBC连接的url中要加rewriteBatchedStatements参数，并保证5.1.13以上版本的驱动，才能实现高性能的批量插入。
   MySQL的JDBC驱动在默认情况下会无视executeBatch()语句，把我们期望批量执行的一组sql语句拆散，一条一条地发给MySQL数据库，批量插入实际上是单条插入，直接造成较低的性能。
   只有把rewriteBatchedStatements参数置为true, 驱动才会帮你批量执行SQL
   另外这个选项对INSERT/UPDATE/DELETE都有效
```
&allowMultiQueries=true这个参数加与不加对JDBC批量更新基本无影响，他只是支持可以一次发送多条sql语句，如果不加这个参数当我们发送多条并以`；`隔开会报错，如下是官方的解释：
```
allowMultiQueries
Allow the use of ';' to delimit multiple queries during one statement (true/false), defaults to 'false', and does not affect the addBatch() and executeBatch() methods, which instead rely on rewriteBatchStatements.
Default: false
Since version: 3.1.1
```
**最后结果**
- JdbcUrl不加任何配置，总计批量循环更新50000条，共计耗时27秒
- Jdbcurl加&rewriteBatchedStatements=true，总计批量循环更新50000条，共计耗时3秒
- jdbcurl加&allowMultiQueries=true，总计批量循环更新50000条，共计耗时24秒
- jdbcurl加&allowMultiQueries=true&rewriteBatchedStatements=true，总计批量循环更新50000条，共计耗时3秒

***
## Mybatis效率测试
### Mybatis单条更新
```java
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
```
此处测试的是整合Mybatis测试单条更新效率，和JDBC一样我们针对不同jdbcUrl参数来进行测试
**最后结果**
- 不配置JdbcUrl，最后结果：总计MyBatis单条更新50000条，共计耗时234秒。
- 配置&rewriteBatchedStatements=true，最后结果：总计MyBatis单条更新50000条，共计耗时224秒
- 配置&allowMultiQueries=true，最后结果：总计MyBatis单条更新50000条，共计耗时222秒
- 配置&allowMultiQueries=true&rewriteBatchedStatements=true，最后结果：总计MyBatis单条更新50000条，共计耗时228秒

### Mybatis批量更新
#### CASEWHEN实现批量更新
```java
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
    
private List<BatchTest> generateTestDate(int num, int offset) {
        List<BatchTest> batchTestList = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            BatchTest batchTest = new BatchTest(i, "name" + i + offset, new Date(), new Date());
            batchTestList.add(batchTest);
        }
        return batchTestList;
    }
```
```xml
<update id="updateBatch" parameterType="java.util.List">
        update batch_test
        SET test_name =
        <foreach collection="list" item="item" index="index"
                 separator=" " open="case id" close="end">
            when #{item.id} then #{item.testName}
        </foreach>
        , create_time =
        <foreach collection="list" item="item" index="index"
                 separator=" " open="case id" close="end">
            when #{item.id} then #{item.createTime}
        </foreach>
        , update_time =
        <foreach collection="list" item="item" index="index"
                 separator=" " open="case id" close="end">
            when #{item.id} then #{item.updateTime}
        </foreach>
        where id in (
        <foreach collection="list" item="item" index="index"
                 separator=",">
            #{item.id}
        </foreach>
        )
    </update>
```
CASEWHEN方法实际上是把，众多更新拼接成一条sql语句，一次性提交，如果语句很长的话，效率并不理想
**最后结果**
- 不配置JdbcUrl，最后结果：总计MyBatis批量更新50000条，共计耗时146秒
- 配置&rewriteBatchedStatements=true，最后结果：总计MyBatis批量更新50000条，共计耗时144秒
- 配置&allowMultiQueries=true，最后结果：总计MyBatis批量更新50000条，共计耗时142秒
- 配置&allowMultiQueries=true&rewriteBatchedStatements=true，最后结果：总计MyBatis批量更新50000条，共计耗时143秒

#### 拼接多条SQL实现批量更新
```java
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
```
```xml
<update id="updateBatchList" parameterType="java.util.List">
        <foreach collection="list" item="item" index="index" open="" close="" separator=";">
            update batch_test
            <set>
            <if test="item.testName != null">
                test_name = #{item.testName},
            </if>
            <if test="item.createTime != null">
                create_time = #{item.createTime},
            </if>
            <if test="item.updateTime != null">
                update_time = #{item.updateTime},
            </if>
            </set>
            where id = #{item.id}
        </foreach>
    </update>
```
使用该方法，必须在JdbcUrl后面拼接参数&allowMultiQueries=true，否则将会无法运行。
该方法主要是将众多更新语句，用“；”隔开，拼接成多条SQL语句进行批量提交，该方法在要更新大量数据的情况下，效率客观，但是数量越多，服务卡住可能性比较高。
**最后结果**
- 配置&rewriteBatchedStatements=true，最后结果：总计MyBatis批量更新50000条，共计耗时25秒
- 配置&allowMultiQueries=true&rewriteBatchedStatements=true，总计MyBatis批量更新50000条，共计耗时26秒
#### 开启Mybaits批处理多条SQL
```java
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
```
```xml
<update id="updateBatchList" parameterType="java.util.List">
        <foreach collection="list" item="item" index="index" open="" close="" separator=";">
            update batch_test
            <set>
            <if test="item.testName != null">
                test_name = #{item.testName},
            </if>
            <if test="item.createTime != null">
                create_time = #{item.createTime},
            </if>
            <if test="item.updateTime != null">
                update_time = #{item.updateTime},
            </if>
            </set>
            where id = #{item.id}
        </foreach>
    </update>
```
**最后结果**
- 配置&rewriteBatchedStatements=true，最后结果：总计MyBatis批量更新50000条，共计耗时23秒
- 配置&allowMultiQueries=true&rewriteBatchedStatements=true，总计MyBatis批量更新50000条，共计耗时25秒

***
## 多线程批量更新效率测试
![多线程批量测试1](https://molzhao-pic.oss-cn-beijing.aliyuncs.com/2020-07-05/%E5%A4%9A%E7%BA%BF%E7%A8%8B1.png)
![多线程批量测试2](https://molzhao-pic.oss-cn-beijing.aliyuncs.com/2020-07-05/%E5%A4%9A%E7%BA%BF%E7%A8%8B2.png)
测试发现多线程并不是越多效率越高，多线程之所以快是因为能提高CPU的使用率，但是多线程数量多了，会导致频繁切换CPU上下文，最后就得不偿失了。
**最后结果**
- 开启3个线程更新5W条，最后结果：共计耗时22秒
- 开启4个线程更新5W条，最后结果：共计耗时65秒
- 开启5个线程更新5W条，最后结果：共计耗时65秒
- 开启10个线程更新5W条，最后结果：共计耗时98秒

***
## kafka批量消费更新入库效率测试

```java
@Service
public class BatchListener {

    private static final Logger logger = LoggerFactory.getLogger(BatchListener.class);

    @Autowired
    JdbcBatchService jdbcBatchService;

    @KafkaListener(containerFactory = "kafkaBatchListener8", id = GlobalConstant.KAFKA_LISTENER_ID, topics = GlobalConstant.BENCHMARK_TEST_TOPIC)
    public void onMessage(List<ConsumerRecord<?, ?>> records, Acknowledgment ack) {
        logger.warn("开始时间{}", System.currentTimeMillis());

        List<BatchTest> batchTestList = new ArrayList<>();
        try {
            records.forEach(record -> {
                BatchTest batchTest = JSON.parseObject(record.value().toString(), BatchTest.class);
                batchTestList.add(batchTest);
            });
            logger.warn(jdbcBatchService.updateBatchJdbc(batchTestList) + "\n");
        } catch (Exception e) {
            logger.warn("Kafka监听异常" + e.getMessage(), e);
        } finally {
            ack.acknowledge();//手动提交偏移量
        }
        logger.warn("结束时间{}", System.currentTimeMillis());
    }
}

```
kafka配置文件
```yml
spring:
  kafka:
    producer:
      bootstrap-servers: 127.0.0.1:9092
      batch-size: 16785                                   #一次最多发送数据量
      retries: 1                                          #发送失败后的重复发送次数
      buffer-memory: 33554432                             #32M批处理缓冲区
      linger: 1                                           #如果不设置linger.ms，其默认值就是0，也就说即使batch不满也会发送出去。可现在设置了linger.ms，这样这些本该早就发出去的消息被迫至少等待了linger.ms时间，所以说增加了发送方的延迟
    consumer:
      bootstrap-servers: 127.0.0.1:9092
      auto-offset-reset: earliest                           #最早未被消费的offset earliest
      max-poll-records: 10000                              #批量消费一次最大拉取的数据量
      enable-auto-commit: false                           #是否开启自动提交
      auto-commit-interval: 1000                          #自动提交的间隔时间
      session-timeout: 20000                              #连接超时时间
      max-poll-interval: 15000                            #手动提交设置与poll的心跳数,如果消息队列中没有消息，等待毫秒后，调用poll()方法。如果队列中有消息，立即消费消息，每次消费的消息的多少可以通过max.poll.records配置。
      max-partition-fetch-bytes: 15728640                 #设置拉取数据的大小,15M
    listener:
      batch-listener: true                                #是否开启批量消费，true表示批量消费
      concurrencys: 1,3,4,6,8                                   #设置消费的线程数
      poll-timeout: 1000
```
注意：要使用kafk批量消费，必须开启`batch-listener`属性,kafka的线程数，和他的分区数对应，有多少分区数，就能开启多少线程去消费。
**最后结果**
**一个Topic 4个分区 副本1**
- 5W条 5000条为一批 单线程 4145ms 
- 5W条 5000条为一批 三线程 1934ms 
- 5W条 5000条为一批 四线程 2187ms 

- 5W条 1W条为一批 单线程 5746ms 
- 5W条 1W条为一批 三线程 3372ms 
- 5W条 1W条为一批 四线程 2149ms 

- 10W条 1W条为一批 单线程 14039ms
- 10W条 1W条为一批 三线程 6249ms 
- 10W条 1W条为一批 四线程 4335ms 
**一个Topic 8个分区 副本1**
- 10W条 1W条为一批 单线程 11180ms 
- 10W条 1W条为一批 四线程 4303ms 
- 10W条 1W条为一批 六线程 3976ms 
- 10W条 1W条为一批 八线程 3590ms 
