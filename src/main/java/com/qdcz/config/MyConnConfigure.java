package com.qdcz.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by hadoop on 17-6-27.
 */
public class MyConnConfigure extends Properties {
    public static String driver;
    public static String db;
    public static int port;
    @Override
    public synchronized void load(InputStream inStream) throws IOException {
        super.load(inStream);
        driver = this.getProperty("driver");
        db = this.getProperty("db");
        port =Integer.parseInt(this.getProperty("port"));
    }
}
