package com.qdcz.mongo.conf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by hadoop on 17-7-3.
 */
public class MongoConfiguration extends Properties {

    public static String host = null;

    public static int port;
    public static String name;
    public static String pass;
    public static String dbTest;
    public static String dbHouse;
    public static String dbOnline;
    @Override
    public synchronized void load(InputStream inStream) throws IOException {
        super.load(inStream);
        host = this.getProperty("mongo_host");
        port = Integer.parseInt(this.getProperty("mongo_port"));
        name = this.getProperty("mongo_user");
        pass = this.getProperty("mongo_pass");
        dbTest = this.getProperty("mongo_db_test");
        dbHouse = this.getProperty("mongo_db_TradeCollection");
        dbOnline = this.getProperty("mongo_db_Trade");
    }
}