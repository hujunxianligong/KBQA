package com.qdcz.conf;

import com.qdcz.conf.DatabaseConfiguration;
import com.qdcz.conf.LoadConfigListener;
import com.qdcz.graph.neo4jcypher.conf.Neo4jConfiguration;
import com.qdcz.index.elsearch.conf.ELKConfig;
import com.qdcz.mongo.conf.MongoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;
import org.springframework.web.context.support.XmlWebApplicationContext;

import java.io.IOException;

/**
 * Created by star on 17-8-9.
 */
public class TestLoadConfigListener implements TestExecutionListener {
    private String source_dir = "/dev/";
    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {

    }

    @Override
    public void prepareTestInstance(TestContext testContext) throws Exception {
        try {
            //------------加载neo4j的配置------------
            System.out.println("------------加载neo4j的配置------------");
            Neo4jConfiguration neo4jConfiger=new Neo4jConfiguration();
            neo4jConfiger.load(LoadConfigListener.class.getResourceAsStream(source_dir+"neo4j.properties"));

            System.out.println("------------加载MongoDB配置文件------------");
            MongoConfiguration mongoConf = new MongoConfiguration();
            mongoConf.load(LoadConfigListener.class.getResourceAsStream(source_dir+"mongo.properties"));

            System.out.println("------------加载ElaSearch配置文件------------");
            ELKConfig elkConfig = new ELKConfig();
            elkConfig.load(LoadConfigListener.class.getResourceAsStream(source_dir+"elasearch.properties"));

            System.out.println("------------加载Database配置文件------------");
            DatabaseConfiguration databaseConf = new DatabaseConfiguration();
            databaseConf.load(LoadConfigListener.class.getResourceAsStream(source_dir+ "database.xml"));

ApplicationContext context = testContext.getApplicationContext();
//            System.out.println(context.getParent().getApplicationName());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {

    }

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {

    }

    @Override
    public void afterTestClass(TestContext testContext) throws Exception {

    }
}
