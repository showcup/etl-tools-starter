package com.showcup.etl.rdbms;

import javax.sql.DataSource;

import com.showcup.etl.properties.EtlDataSourceRdbProperties.RdbConfig;

/**
 * @Description MYSQL执行器实现
 * @author 封厂长
 * @date 2020-7-6
 */
public class EtlMySQLRdbExecutor extends EtlRdbExecutor {

	public EtlMySQLRdbExecutor(String poolName,RdbConfig config) {
		super(poolName,config);
	}
	
	public EtlMySQLRdbExecutor(DataSource datasource) {
		super(datasource);
	}
	
	@Override
	protected String getCountSQLTemplate() {
		return "select count(1) from (%s) tmp";
	}
	
	@Override
	protected String getPagingSQLTemplate() {
		return "select * from ( %s ) tmp limit %d,%d";
	}
}
