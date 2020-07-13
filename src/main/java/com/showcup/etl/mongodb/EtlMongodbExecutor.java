package com.showcup.etl.mongodb;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.springframework.util.StringUtils;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.showcup.etl.properties.EtlDataSourceMongodbProperties.MongodbConfig;

/**
 * @Description mongodb数据库抽取执行器
 * @author 封厂长
 * @date 2020-7-6
 */
public class EtlMongodbExecutor {
	private MongodbConfig config;
	private MongoClient mongoClient;
	
	public EtlMongodbExecutor(MongodbConfig config) {
		this.config = config;
		mongoClient = createMongoClient();
	}
	
	private MongoClient createMongoClient() {
		MongoClientOptions options = MongoClientOptions.builder().build();
		
		String username = config.getUsername();
		String database = config.getDatabase();
		String password = config.getPassword();
		MongoCredential credentials = null;
		if(!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
			credentials = MongoCredential.createCredential(username, database, password.toCharArray());
		}
		String host = config.getHost();
		int port = config.getPort();
		List<ServerAddress> seeds = Collections
				.singletonList(new ServerAddress(host, port));
		return (credentials != null) ? new MongoClient(seeds, credentials, options)
				: new MongoClient(seeds, options);
	}
	
	protected MongoDatabase getConnection() {
		return mongoClient.getDatabase(config.getDatabase());
	}
	
	public void selectOne() throws SQLException {
		//MongoDatabase conn = getConnection();
		//conn.getCollection("RES_CORP_BRANCH").find(BsonInvalidOperationException)
	}
}
