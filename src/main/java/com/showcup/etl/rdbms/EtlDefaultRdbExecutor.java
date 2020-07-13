package com.showcup.etl.rdbms;

import javax.sql.DataSource;

import com.showcup.etl.properties.EtlDataSourceRdbProperties.RdbConfig;

/**
 * @Description 默认执行器实现
 * @author 封厂长
 * @date 2020-7-6
 */
public class EtlDefaultRdbExecutor extends EtlRdbExecutor {

	public EtlDefaultRdbExecutor(String poolName,RdbConfig config) {
		super(poolName,config);
	}
	
	public EtlDefaultRdbExecutor(DataSource datasource) {
		super(datasource);
	}
}
