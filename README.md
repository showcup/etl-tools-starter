# ETL 数据抽取工具包
基于Spring boot 的Starter机制提供一个开箱即用的多数据源抽取工具包，计划对RDMS(关系型数据库)、mongodb、ftp、file、http接口等数据源的数据抽取提供底层的工具包，现阶段已经实现了对关系型数据库的支持，提供多数据源管理机制和简易数据抽取api，简化的数据抽取任务的底层数据源连接部分。
### 使用方法
1. 添加依赖
``` xml
    <dependencies>
        <dependency>
            <groupId>com.showcup.etl</groupId>
            <artifactId>etl-tools-starter</artifactId>
        </dependency>
    </dependencies>    
```
2. 配置数据源
``` yaml
etl: 
  datasource:
    rdb-configs: #关系型数据库数据源
      corpInfo: #数据源名称
        jdbc-url: jdbc:mysql://192.168.11.112:3306/gbs?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&useSSL=false
        username: root
        password: yzwl#2020
        driver-class-name: com.mysql.cj.jdbc.Driver
        connectionTimeout: 3000 #数据库连接超时时间,默认30秒，即30000
        maxLifetime: 180000 #此属性控制池中连接的最长生命周期，值0表示无限生命周期，默认1800000即30分钟
        idleTimeout: 180000 #空闲连接存活最大时间，默认600000（10分钟）
        maxPoolSize: 10 #连接池最大连接数，默认是10
        minIdle: 5 #最小空闲连接数量
        connectionTestQuery: select 1
      dstlib:
        jdbc-url: jdbc:mysql://192.168.11.243:3306/egb-dstlib?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&useSSL=false
        username: root
        password: Zdkx#2020
        driver-class-name: com.mysql.cj.jdbc.Driver
    mongodb-configs: #mongodb数据源
      corpds:
        host: 192.168.11.243
        port: 27017
        username: zdkx
        password: Zdkx#2020
        database: egb
        uri: mongodb://192.168.11.113:27017/test
```
3. 抽数api调用
```java
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
```
