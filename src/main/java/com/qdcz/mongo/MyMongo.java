package com.qdcz.mongo;

import com.qdcz.mongo.conf.MongoConfiguration;

/**
 * Created by hadoop on 17-7-3.
 */
public class MyMongo extends BaseMongoDAL{

    public MyMongo(String databaseName,
                   String collectionName) {
        super(MongoConfiguration.host, databaseName, MongoConfiguration.port, collectionName, MongoConfiguration.name, MongoConfiguration.pass);
    }

}

