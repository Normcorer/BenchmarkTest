package com.hemavip.batchtest;

import com.hemavip.batchtest.config.PackagesSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@MapperScan(basePackages = {"com.hemavip.batchtest.mapper"})
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableAspectJAutoProxy(proxyTargetClass=true)
public class BatchTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(BatchTestApplication.class, args);
    }


    @Value("${db.driver-class-name}")
    private String driverClass;

    @Value("${db.jdbcUrl}")
    private String jdbcUrl;

    @Value("${db.username}")
    private String username;

    @Value("${db.password}")
    private String password;

    @Bean(name = "dataSource")
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean("sqlSessionFactory")
    @Primary
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        PackagesSqlSessionFactoryBean bean = new PackagesSqlSessionFactoryBean();
        //解决Mybatis在Spring boot下，linux环境找不到TypeAlias的问题，Windows和Mac不加这句没有问题.
        bean.setDataSource(dataSource());
        bean.setVfs(SpringBootVFS.class);
        bean.setTypeAliasesPackage("com.hemavip.batchtest.**.model");
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml"));
        return bean.getObject();
    }

    @Bean("sqlSessionTemplate")
    @Primary
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        //sqlSessionFactory.openSession(ExecutorType.BATCH, true);
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}


