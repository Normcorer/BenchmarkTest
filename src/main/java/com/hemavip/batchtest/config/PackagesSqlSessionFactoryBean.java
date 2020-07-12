package com.hemavip.batchtest.config;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PackagesSqlSessionFactoryBean extends SqlSessionFactoryBean {

    static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";
    private static final Logger logger = LoggerFactory.getLogger(PackagesSqlSessionFactoryBean.class);

    @Override
    public void setTypeAliasesPackage(String typeAliasesPackage) {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resolver);
        typeAliasesPackage = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                ClassUtils.convertClassNameToResourcePath(typeAliasesPackage) + "/" + DEFAULT_RESOURCE_PATTERN;

        //将加载多个绝对匹配的所有Resource
        //将首先通过ClassLoader.getResource("META-INF")加载非模式路径部分
        //然后进行遍历模式匹配
        try {
            List<String> result = new ArrayList<>();
            Resource[] resources = resolver.getResources(typeAliasesPackage);

            if (resources.length > 0) {
                MetadataReader metadataReader = null;
                for (Resource resource : resources) {
                    if (resource.isReadable()) {
                        metadataReader = metadataReaderFactory.getMetadataReader(resource);
                        result.add(Class.forName(metadataReader.getClassMetadata().getClassName()).getPackage().getName());
                    }
                }
            }

            if (!result.isEmpty()) {
                super.setTypeAliasesPackage(StringUtils.join(result.toArray(), ","));
            } else {
                logger.warn("参数typeAliasesPackage: {}，未找到任何包", typeAliasesPackage);
            }
        } catch (IOException | ClassNotFoundException e) {
            logger.error("PackagesSqlSessionFactoryBean加载资源异常：", e);
        }
    }
}
