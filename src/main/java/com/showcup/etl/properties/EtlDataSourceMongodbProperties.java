package com.showcup.etl.properties;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @Description mongodb数据源属性配置
 * @author 封厂长
 * @date 2020-7-6
 */
@ConfigurationProperties(prefix = "etl.datasource")
@Data
public class EtlDataSourceMongodbProperties {
	private Map<String,MongodbConfig> mongodbConfigs;
	
	@Data
	public static class MongodbConfig{
		private String host;
		private int port;
		private String username;
		private String password;
		private String database;
	}
}
