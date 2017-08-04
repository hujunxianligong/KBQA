package com.qdcz.graph.neo4jcypher.conf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by hadoop on 17-8-3.
 */
public class Neo4jConfiger extends Properties {

    public static String driver;
    public static String url;
    public static String name;
    public static String pass;


    @Override
    public synchronized void load(InputStream inStream) throws IOException {
        super.load(inStream);
        driver = this.getProperty("driver");
        url = this.getProperty("url");
        name = this.getProperty("name");
        pass = this.getProperty("pass");
    }

}
