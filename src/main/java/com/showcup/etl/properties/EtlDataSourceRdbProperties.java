package com.showcup.etl.properties;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;
/**
 * @Description 关系型数据库属性配置
 * @author 封厂长
 * @date 2020-7-6
 */
@ConfigurationProperties(prefix = "etl.datasource")
@Data
public class EtlDataSourceRdbProperties {
	private Map<String,RdbConfig> rdbConfigs;
	
	@Data
	public static class RdbConfig{
		private String jdbcUrl;
		private String username;
		private String password;
		private String driverClassName;
		private long connectionTimeout;
		private long maxLifetime;
		private long idleTimeout;
		private int maxPoolSize;
		private int minIdle;
		private String connectionTestQuery;
	}
}
