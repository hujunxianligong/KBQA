package com.qdcz.common;

import com.qdcz.mongo.conf.MongoConfiguration;
import com.qdcz.graph.neo4jcypher.conf.Neo4jConfiguration;
import com.qdcz.index.elsearch.conf.ELKConfig;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by hadoop on 17-6-27.
 *
 */
public class LoadConfigListener implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        try {
//            Configurator.initialize(null,new ConfigurationSource(LoadConfigListener.class.getResourceAsStream("/log4j2.xml")));
            //------------加载neo4j的配置------------
            System.out.println("------------加载neo4j的配置------------");
            //MyConnConfigure myConnConfigure = new MyConnConfigure();
            //System.out.println(LoadConfigListener.class.getResource("/com/wfxl/common/myconfig.properties"));
           // myConnConfigure.load(LoadConfigListener.class.getResourceAsStream("/neo4j.properties"));
            Neo4jConfiguration neo4jConfiger=new Neo4jConfiguration();
            neo4jConfiger.load(LoadConfigListener.class.getResourceAsStream("/neo4j.properties"));
            System.out.println("------------加载MongoDB配置文件------------");
            MongoConfiguration mongoConf = new MongoConfiguration();
            mongoConf.load(LoadConfigListener.class.getResourceAsStream("/mongo.properties"));
            System.out.println("------------加载ElaSearch配置文件------------");
            ELKConfig elkConfig = new ELKConfig();
            elkConfig.load(LoadConfigListener.class.getResourceAsStream("/elasearch.properties"));


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}