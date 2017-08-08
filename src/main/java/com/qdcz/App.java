package com.qdcz;

import com.qdcz.common.LoadConfigListener;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

/**
 *Created by hadoop on 17-6-22.
 * APP main
 */
@SpringBootApplication(exclude = {MongoAutoConfiguration.class})
public class App {
    public static void main(String[] args){
        LoadConfigListener loadConfigListener=new LoadConfigListener();
        loadConfigListener.contextInitialized(null);
        SpringApplication.run(App.class, args);

        System.out.println("MMs");
    }
}
