package com.showcup.etl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;

import com.showcup.etl.mongodb.EtlMongodbExecutor;
import com.showcup.etl.properties.EtlDataSourceMongodbProperties;
import com.showcup.etl.properties.EtlDataSourceMongodbProperties.MongodbConfig;
import com.showcup.etl.properties.EtlDataSourceRdbProperties;
import com.showcup.etl.properties.EtlDataSourceRdbProperties.RdbConfig;
import com.showcup.etl.rdbms.EtlDefaultRdbExecutor;
import com.showcup.etl.rdbms.EtlMySQLRdbExecutor;
import com.showcup.etl.rdbms.EtlRdbExecutor;
import com.zaxxer.hikari.HikariDataSource;

/**
 * @Description 数据抽取工具类，数据抽取的入口类，支持rds(关系型SQL数据库)、mongodb等
 * @Date 2020/7/6
 * @author 封厂长
 *
 */
public class EtlTools implements ApplicationContextAware, DisposableBean  {
	private static Map<String, Object> EXECUTOR_CACHE = new ConcurrentHashMap<String, Object>();
	private static ApplicationContext applicationContext = null;
	
	/**
	 * 取得存储在静态变量中的ApplicationContext.
	 */
	public static ApplicationContext getApplicationContext() {
		assertContextInjected();
		return applicationContext;
	}
	
	/**
	 * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		assertContextInjected();
		return (T) applicationContext.getBean(name);
	}

	/**
	 * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
	 */
	public static <T> T getBean(Class<T> requiredType) {
		assertContextInjected();
		return applicationContext.getBean(requiredType);
	}
	
	/**
	 * 发布事件
	 * @param event
	 */
	public static void publishEvent(ApplicationEvent event) {
		getApplicationContext().publishEvent(event);
	}

	/**
	 * 清除SpringContextHolder中的ApplicationContext为Null.
	 */
	public static void clearHolder() {
		applicationContext = null;
	}

	/**
	 * 实现ApplicationContextAware接口, 注入Context到静态变量中.
	 */
	@Override
	public void setApplicationContext(ApplicationContext appContext) {
		applicationContext = appContext;
	}

	/**
	 * 实现DisposableBean接口, 在Context关闭时清理静态变量.
	 */
	@Override
	public void destroy() throws Exception {
		EtlTools.clearHolder();
	}

	/**
	 * 检查ApplicationContext不为空.
	 */
	private static void assertContextInjected() {
		if (applicationContext == null) {
			throw new IllegalStateException("applicaitonContext属性未注入");
		}
	}
	
	private static EtlRdbExecutor createRdsExecutor(String poolName,RdbConfig rdbConfig) {
		String jdbcUrl = rdbConfig.getJdbcUrl();
		if(jdbcUrl==null || "".equals(jdbcUrl)) {
			jdbcUrl = "";
		}
		if (jdbcUrl.contains(":mysql:") || jdbcUrl.contains(":cobar:")) {
            return new EtlMySQLRdbExecutor(poolName,rdbConfig);
        } else if (jdbcUrl.contains(":mariadb:")) {
            return new EtlMySQLRdbExecutor(poolName,rdbConfig);
            
          //以下数据库类型有需要再实现，暂时用默认实现
        } else if (jdbcUrl.contains(":oracle:")) { 
            return new EtlDefaultRdbExecutor(poolName,rdbConfig);
        } else if (jdbcUrl.contains(":sqlserver:") || jdbcUrl.contains(":microsoft:")) {
            return new EtlDefaultRdbExecutor(poolName,rdbConfig);
        } else if (jdbcUrl.contains(":sqlserver2012:")) {
            return new EtlDefaultRdbExecutor(poolName,rdbConfig);
        } else if (jdbcUrl.contains(":postgresql:")) {
            return new EtlDefaultRdbExecutor(poolName,rdbConfig);
        } else if (jdbcUrl.contains(":hsqldb:")) {
            return new EtlDefaultRdbExecutor(poolName,rdbConfig);
        } else if (jdbcUrl.contains(":db2:")) {
            return new EtlDefaultRdbExecutor(poolName,rdbConfig);
        } else if (jdbcUrl.contains(":sqlite:")) {
            return new EtlDefaultRdbExecutor(poolName,rdbConfig);
        } else if (jdbcUrl.contains(":h2:")) {
            return new EtlDefaultRdbExecutor(poolName,rdbConfig);
        } else if (jdbcUrl.contains(":dm:")) {
            return new EtlDefaultRdbExecutor(poolName,rdbConfig);
        } else if (jdbcUrl.contains(":xugu:")) {
            return new EtlDefaultRdbExecutor(poolName,rdbConfig);
        } else if (jdbcUrl.contains(":kingbase:") || jdbcUrl.contains(":kingbase8:")) {
            return new EtlDefaultRdbExecutor(poolName,rdbConfig);
        } else if (jdbcUrl.contains(":phoenix:")) {
            return new EtlDefaultRdbExecutor(poolName,rdbConfig);
        } else {
            return new EtlDefaultRdbExecutor(poolName,rdbConfig);
        }
	}
	
	private static EtlRdbExecutor createRdsExecutor(DataSource datasource) {
		String jdbcUrl = null;
		if(datasource instanceof HikariDataSource) {
			jdbcUrl = ((HikariDataSource) datasource).getJdbcUrl();
		}else {
			return new EtlDefaultRdbExecutor(datasource);
		}
		if(jdbcUrl==null || "".equals(jdbcUrl)) {
			jdbcUrl = "";
		}
		if (jdbcUrl.contains(":mysql:") || jdbcUrl.contains(":cobar:")) {
            return new EtlMySQLRdbExecutor(datasource);
        } else if (jdbcUrl.contains(":mariadb:")) {
            return new EtlMySQLRdbExecutor(datasource);
            
          //以下数据库类型有需要再实现，暂时用默认实现
        } else if (jdbcUrl.contains(":oracle:")) { 
            return new EtlDefaultRdbExecutor(datasource);
        } else if (jdbcUrl.contains(":sqlserver:") || jdbcUrl.contains(":microsoft:")) {
            return new EtlDefaultRdbExecutor(datasource);
        } else if (jdbcUrl.contains(":sqlserver2012:")) {
            return new EtlDefaultRdbExecutor(datasource);
        } else if (jdbcUrl.contains(":postgresql:")) {
            return new EtlDefaultRdbExecutor(datasource);
        } else if (jdbcUrl.contains(":hsqldb:")) {
            return new EtlDefaultRdbExecutor(datasource);
        } else if (jdbcUrl.contains(":db2:")) {
            return new EtlDefaultRdbExecutor(datasource);
        } else if (jdbcUrl.contains(":sqlite:")) {
            return new EtlDefaultRdbExecutor(datasource);
        } else if (jdbcUrl.contains(":h2:")) {
            return new EtlDefaultRdbExecutor(datasource);
        } else if (jdbcUrl.contains(":dm:")) {
            return new EtlDefaultRdbExecutor(datasource);
        } else if (jdbcUrl.contains(":xugu:")) {
            return new EtlDefaultRdbExecutor(datasource);
        } else if (jdbcUrl.contains(":kingbase:") || jdbcUrl.contains(":kingbase8:")) {
            return new EtlDefaultRdbExecutor(datasource);
        } else if (jdbcUrl.contains(":phoenix:")) {
            return new EtlDefaultRdbExecutor(datasource);
        } else {
            return new EtlDefaultRdbExecutor(datasource);
        }
	}
	

	/**
	 * 指定rds数据源名称获取一个rds执行器
	 * @param dsName
	 * @return
	 */
	public static synchronized EtlRdbExecutor rdb(String dsName) {
		Object executor = EXECUTOR_CACHE.get("RDB:"+dsName);
		if(executor == null) {
			EtlDataSourceRdbProperties properties = EtlTools.getBean(EtlDataSourceRdbProperties.class);
			Map<String,RdbConfig> configs = properties.getRdbConfigs();
			if(configs == null) {
				throw new RuntimeException("RDB data source ["+dsName+"] not defined");
			}
			RdbConfig config = configs.get(dsName);
			if(config == null) {
				throw new RuntimeException("RDB data source ["+dsName+"] not defined");
			}
			EtlRdbExecutor obj =  createRdsExecutor(dsName,config);
			EXECUTOR_CACHE.put("RDB:"+dsName, obj);
			return obj;
		}else {
			return (EtlRdbExecutor)executor;
		}
	}
	
	public static synchronized EtlRdbExecutor rdb(String dsName,DataSource datasource) {
		Object executor = EXECUTOR_CACHE.get("RDB:"+dsName);
		if(executor == null) {
			EtlRdbExecutor obj =  createRdsExecutor(datasource);
			EXECUTOR_CACHE.put("RDB:"+dsName, obj);
			return obj;
		}else {
			return (EtlRdbExecutor)executor;
		}
	}
	
	/**
	 * 指定mongodb数据源名称获取一个mongodb执行器
	 * @param dsName
	 * @return
	 */
	public static synchronized EtlMongodbExecutor mongodb(String dsName) {
		Object executor = EXECUTOR_CACHE.get("MONGODB:"+dsName);
		if(executor == null) {
			EtlDataSourceMongodbProperties properties = EtlTools.getBean(EtlDataSourceMongodbProperties.class);
			Map<String,MongodbConfig> configs = properties.getMongodbConfigs();
			if(configs == null) {
				throw new RuntimeException("Mongodb data source ["+dsName+"] not defined");
			}
			MongodbConfig config = configs.get(dsName);
			if(config == null) {
				throw new RuntimeException("Mongodb data source ["+dsName+"] not defined");
			}
			EtlMongodbExecutor obj = new EtlMongodbExecutor(config);
			EXECUTOR_CACHE.put("MONGODB:"+dsName, obj);
			return obj;
		}else {
			return (EtlMongodbExecutor)executor;
		}
	}
}
