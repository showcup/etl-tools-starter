package com.showcup.etl;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.showcup.etl.properties.EtlDataSourceMongodbProperties;
import com.showcup.etl.properties.EtlDataSourceRdbProperties;
import com.showcup.etl.properties.YamlPropertySourceFactory;

/**
 * 
 * @Description 用于单元测试时加载配置文件，由于单元测试并没有启动一个Spring Boot Application的上下文，故而不会自动加载application名称的配置文件 
 * @author 封厂长
 * @date 2020-7-8
 */
@Configuration
@PropertySource(value = { "classpath:application.yaml", },
		encoding = "utf-8",factory = YamlPropertySourceFactory.class)
@EnableConfigurationProperties({EtlDataSourceRdbProperties.class,EtlDataSourceMongodbProperties.class})
public class TestPropertyLoaderConfiguration {

}
