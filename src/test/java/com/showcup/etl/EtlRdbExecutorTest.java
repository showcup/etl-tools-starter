package com.showcup.etl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.showcup.etl.autoconfigure.EtlConfiguration;
import com.showcup.etl.rdbms.PageResult;

import lombok.SneakyThrows;

/**
 * rdbExecutor 单元测试
 * 
 * @author 封厂长
 * @date 2020-7-6
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= {EtlConfiguration.class,TestPropertyLoaderConfiguration.class})
public class EtlRdbExecutorTest {
	String DSNAME = "corpInfo";
	
	@Test
	public void selectOne() throws SQLException {
		Map<String,Object> map = EtlTools.rdb(DSNAME).selectOne("select * from res_corp where CORP_CID=?", "91440000633210884T");
		System.out.println(map);
	}
	
	@Test
	public void selectList()throws SQLException {
		List<Map<String,Object>> map = EtlTools.rdb(DSNAME).selectList("select * from res_corp limit 10");
		System.out.println(map);
	}
	
	@Test
	public void selectSingleColumnList()throws SQLException {
		List<Object> map = EtlTools.rdb(DSNAME).selectSingleColumnList("select LABEL_CN from res_corp limit 10");
		System.out.println(map);
	}
	
	@Test
	public void selectSingleColumn()throws SQLException {
		Long map = EtlTools.rdb(DSNAME).selectSingleColumn(Long.class,"select count(1) from res_corp");
		System.out.println(map);
	}
	
	@Test
	public void selectPageList()throws SQLException {
		PageResult<Map<String,Object>> map = EtlTools.rdb(DSNAME).selectPageList(1,10,"select * from res_corp");
		System.out.println(map);
	}
	
	@Test
	public void selectPageListAutoFlip()throws SQLException {
		EtlTools.rdb(DSNAME).selectPageListAutoFlip(100,(pageNum)->{
			return true;
		},(pageResult)->{
			System.err.println("获取第"+pageResult.getPageNum()+"数据");
			System.err.println(pageResult);
		},"select * from res_corp limit 1000");
	}
	
	
	@Test
	public void selectSingleColumn2()throws SQLException {
		Long map = EtlTools.rdb("dstlib").selectSingleColumn(Long.class,"select count(1) from uppro_accept");
		System.out.println(map);
	}
	
	@Test
	@SneakyThrows
	public void multiDsTest() {
		Long map = EtlTools.rdb("dstlib").selectSingleColumn(Long.class,"select count(1) from uppro_accept");
		System.err.println(map);
		map = EtlTools.rdb(DSNAME).selectSingleColumn(Long.class,"select count(1) from res_corp");
		System.err.println(map);
		map = EtlTools.rdb("dstlib").selectSingleColumn(Long.class,"select count(1) from uppro_accept");
		System.err.println(map);
		EtlTools.rdb(DSNAME).selectSingleColumn(Long.class,"select count(1) from res_corp");
		System.err.println(map);
	}
	
	@Test
	@SneakyThrows
	public void multiThreadAccessDataSource() {
		int threshold = 100;
		CountDownLatch countDownLatch = new CountDownLatch(threshold);
		ExecutorService threadPool = Executors.newCachedThreadPool();
		for (int i = 0; i < threshold; i++) {
			threadPool.submit(()->{
				try {
					EtlTools.rdb(DSNAME).selectSingleColumn(Long.class,"select count(1) from res_corp");
					countDownLatch.countDown();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			});
		}
		countDownLatch.await();
	}
}
