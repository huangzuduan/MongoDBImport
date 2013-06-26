package util;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.MongoOptions;
import com.mongodb.ServerAddress;

public class MongoDB {

	/**
	 * 初始化DB
	 * 
	 * @author Huang
	 * @date 2013-5-25 下午3:04:54
	 * @return void
	 */
	public static DB initDB(String host, int port, String dbName,
			String username, String password) throws MongoException,
			UnknownHostException {
		MongoOptions options = new MongoOptions();
		options.autoConnectRetry = true;
		options.connectTimeout = 30000;
		options.connectionsPerHost = 100;
		options.threadsAllowedToBlockForConnectionMultiplier = 20;
		options.maxWaitTime = 30000;
		options.socketTimeout = 5000;
		options.socketKeepAlive = true;
		Mongo mg = new Mongo(new ServerAddress(host, port), options);
		mg.getDB(dbName).authenticate(username, password.toCharArray());
		return mg.getDB(dbName);

	}

}
