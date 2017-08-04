package com.qdcz;

import com.qdcz.common.LoadConfigListener;
import com.qdcz.graph.neo4jkernel.Neo4jConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

/**
 *Created by hadoop on 17-6-22.
 * APP main
 */
@SpringBootApplication(exclude = {MongoAutoConfiguration.class, Neo4jConfig.class})
public class App {
    public static void main(String[] args){
        LoadConfigListener loadConfigListener=new LoadConfigListener();
        loadConfigListener.contextInitialized(null);
        SpringApplication.run(App.class, args);
    }
}