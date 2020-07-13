package com.showcup.etl.rdbms;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.springframework.beans.BeanUtils;

import com.showcup.etl.properties.EtlDataSourceRdbProperties.RdbConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * @Description 关系型SQL数据库执行器抽象实现
 * @author 封厂长
 * @date 2020-7-6
 */
public abstract class EtlRdbExecutor {
	private String poolName;
	private RdbConfig config;
	private DataSource dataSource;
	
	public EtlRdbExecutor(String poolName,RdbConfig config) {
		this.poolName = poolName;
		this.config = config;
		initDataSource();
	}
	
	public EtlRdbExecutor(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	private void initDataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        BeanUtils.copyProperties(config, hikariConfig);
        hikariConfig.setPoolName(poolName);
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        dataSource = new HikariDataSource(hikariConfig);
	}
	
	public int update(String sql,Object ...params) throws SQLException {
		QueryRunner qr = new QueryRunner(dataSource);
		return qr.update(sql, params);
	}
	
	public Map<String,Object> selectOne(String sql,Object ...params) throws SQLException {
		QueryRunner qr = new QueryRunner(dataSource);
		return qr.query(sql, new MapHandler(), params);
	}
	
	public <T> T selectOne(Class<T> resultType,String sql,Object ...params) throws SQLException {
		QueryRunner qr = new QueryRunner(dataSource);
		return qr.query(sql, new BeanHandler<T>(resultType), params);
	}
	
	public List<Map<String,Object>> selectList(String sql,Object ...params) throws SQLException {
		QueryRunner qr = new QueryRunner(dataSource);
		return qr.query(sql, new MapListHandler(), params);
	}
	
	public <T> List<T> selectList(Class<T> resultType,String sql,Object ...params) throws SQLException {
		QueryRunner qr = new QueryRunner(dataSource);
		return qr.query(sql, new BeanListHandler<T>(resultType), params);
	}
	
	public List<Object> selectSingleColumnList(String sql,Object ...params) throws SQLException {
		QueryRunner qr = new QueryRunner(dataSource);
		return qr.query(sql, new ColumnListHandler<Object>(), params);
	}
	
	public <T> List<T> selectSingleColumnList(Class<T> resultType,String sql,Object ...params) throws SQLException {
		QueryRunner qr = new QueryRunner(dataSource);
		return qr.query(sql, new ColumnListHandler<T>(), params);
	}
	
	public <T> T selectSingleColumn(Class<T> resultType,String sql,Object ...params) throws SQLException {
		QueryRunner qr = new QueryRunner(dataSource);
		return qr.query(sql, new ScalarHandler<T>(), params);
	}
	
	protected String getCountSQLTemplate() {
		return "select count(1) from (%s) tmp";
	}
	
	protected String getPagingSQLTemplate() {
		return "select * from ( %s ) tmp limit %d,%d";
	}
	
	private long getTotal(String sql,Object ...params) throws SQLException {
		String wrapSQL = String.format(getCountSQLTemplate(), sql);
		Number total = selectSingleColumn(Number.class,wrapSQL,params);
		return total.longValue();
	}
	
	private PageResult<Map<String,Object>> selectPageListInner(long total,int pageNum,int pageSize,String sql,Object ...params) throws SQLException{
		List<Map<String,Object>> records = null;
		if(total > 0) {
			int offset = pageNum > 0 ? (pageNum-1) * pageSize : 0;
			String wrapSQL = String.format(getPagingSQLTemplate(), sql,offset,pageSize);
			records = this.selectList(wrapSQL, params);
		}else {
			records = new ArrayList<Map<String,Object>>();
		}
		PageResult<Map<String,Object>> pageResult = new PageResult<Map<String,Object>>(total, records);
		pageResult.setPageNum(pageNum);
		pageResult.setPageSize(pageSize);
		pageResult.setPages( (total +pageSize - 1) / pageSize);
		return pageResult;
	}
	
	private <T> PageResult<T> selectPageListInner(Class<T> resultType,long total,int pageNum,int pageSize,String sql,Object ...params) throws SQLException{
		List<T> records = null;
		if(total > 0) {
			int offset = pageNum > 0 ? (pageNum-1) * pageSize : 0;
			String wrapSQL = String.format(getPagingSQLTemplate(), sql,offset,pageSize);
			records = this.selectList(resultType,wrapSQL, params);
		}else {
			records = new ArrayList<T>();
		}
		PageResult<T> pageResult = new PageResult<T>(total, records);
		pageResult.setPageNum(pageNum);
		pageResult.setPageSize(pageSize);
		pageResult.setPages( (total +pageSize - 1) / pageSize);
		return pageResult;
	}
	
	public PageResult<Map<String,Object>> selectPageList(int pageNum,int pageSize,String sql,Object ...params) throws SQLException {
		long total = getTotal(sql,params);
		return selectPageListInner(total,pageNum,pageSize,sql,params);
	}
	
	public <T> PageResult<T> selectPageList(Class<T> resultType,int pageNum,int pageSize,String sql,Object ...params) throws SQLException {
		long total = getTotal(sql,params);
		return selectPageListInner(resultType,total,pageNum,pageSize,sql,params);
	}
	
	public void selectPageListAutoFlip(int pageSize,PageFilter pageFilter,Consumer<PageResult<Map<String,Object>>> pageResultHandler,String sql,Object ...params) throws SQLException {
		long total = getTotal(sql,params);
		if(total>0) {
			long pages = (total +pageSize - 1) / pageSize;
			for (int i = 1; i <= pages; i++) {
				int pageNum = i;
				boolean fetchPage = true;
				if(pageFilter != null) {
					fetchPage = pageFilter.accept(pageNum);
				}
				if(fetchPage) {
					//TODO 提高抓数性能，这里可以开多线程去取
					PageResult<Map<String,Object>> pageResult = selectPageListInner(total,pageNum,pageSize,sql,params);
					if(pageResultHandler!=null) {
						pageResultHandler.accept(pageResult);
					}
				}
			}
		}
	}
	
	public <T> void selectPageListAutoFlip(Class<T> resultType,int pageSize,PageFilter pageFilter,Consumer<PageResult<T>> pageResultHandler,String sql,Object ...params) throws SQLException {
		long total = getTotal(sql,params);
		if(total>0) {
			long pages = (total +pageSize - 1) / pageSize;
			for (int i = 1; i <= pages; i++) {
				int pageNum = i;
				boolean fetchPage = true;
				if(pageFilter != null) {
					fetchPage = pageFilter.accept(pageNum);
				}
				if(fetchPage) {
					//TODO 提高抓数性能，这里可以开多线程去取
					PageResult<T> pageResult = selectPageListInner(resultType,total,pageNum,pageSize,sql,params);
					if(pageResultHandler!=null) {
						pageResultHandler.accept(pageResult);
					}
				}
			}
		}
	}
	
	public void selectPageListAutoFlip(int pageSize,Consumer<PageResult<Map<String,Object>>> pageResultHandler,String sql,Object ...params) throws SQLException {
		selectPageListAutoFlip(pageSize,null,pageResultHandler,sql,params);
	}
	
	public <T> void selectPageListAutoFlip(Class<T> resultType,int pageSize,Consumer<PageResult<T>> pageResultHandler,String sql,Object ...params) throws SQLException {
		selectPageListAutoFlip(resultType,pageSize,null,pageResultHandler,sql,params);
	}
	
	@FunctionalInterface
	public static interface PageFilter{
		public boolean accept(int pageNum);
	}
}
