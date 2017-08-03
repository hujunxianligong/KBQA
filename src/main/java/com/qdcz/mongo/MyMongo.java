package com.qdcz.mongo;

import com.qdcz.config.MongoConfigure;

/**
 * Created by hadoop on 17-7-3.
 */
public class MyMongo extends BaseMongoDAL{

    public MyMongo(String databaseName,
                   String collectionName) {
        super(MongoConfigure.host, databaseName, MongoConfigure.port, collectionName, MongoConfigure.name, MongoConfigure.pass);
    }

}

