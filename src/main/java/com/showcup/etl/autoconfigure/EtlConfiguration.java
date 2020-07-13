package com.showcup.etl.autoconfigure;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.showcup.etl.EtlTools;
import com.showcup.etl.properties.EtlDataSourceMongodbProperties;
import com.showcup.etl.properties.EtlDataSourceRdbProperties;

/**
 * @Description ETL组件自动配置类
 * @author 封厂长
 * @date 2020-7-6
 */
@Configuration
@EnableConfigurationProperties({EtlDataSourceRdbProperties.class,EtlDataSourceMongodbProperties.class})
public class EtlConfiguration {
	
	@Bean
	public EtlTools etlTools() {
		return new EtlTools();
	}
}
