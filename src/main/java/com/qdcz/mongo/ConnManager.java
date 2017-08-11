package com.qdcz.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hadoop on 17-7-3.
 */
public class ConnManager {
    private static Map<String, MongoClient> clients = null;
    public static MongoClient getClient(String host,String databaseName, int port,String user,String pass){
        if(clients==null){
            clients =  new HashMap<String, MongoClient>();
        }
        if(clients.containsKey(databaseName) && clients.get(databaseName)!=null){
            return clients.get(databaseName);
        }else{
            MongoClientOptions.Builder buide = new MongoClientOptions.Builder();
            buide.connectionsPerHost(100);   //与目标数据库能够建立的最大connection数量为50
//			buide.autoConnectRetry(true);   //自动重连数据库启动
            //如果当前所有的connection都在使用中，则每个connection上可以有50个线程排队等待
            buide.threadsAllowedToBlockForConnectionMultiplier(100);
            buide.connectionsPerHost(100);// 与目标数据库可以建立的最大链接数
            buide.connectTimeout(1000 * 60 * 20);// 与数据库建立链接的超时时间
            buide.maxWaitTime(100 * 60 * 5);// 一个线程成功获取到一个可用数据库之前的最大等待时间
            buide.maxConnectionIdleTime(0);
            buide.maxConnectionLifeTime(0);
            buide.socketTimeout(0);
            buide.socketKeepAlive(true);

            MongoClientOptions myOptions = buide.build();
            MongoCredential credential = MongoCredential.createCredential(user,
                    databaseName, pass.toCharArray());
            MongoClient new_client = new MongoClient(new ServerAddress(host, port),
                    Arrays.asList(credential),myOptions);
//            clients.put(databaseName, new_client);
            return new_client;
        }
    }


}
