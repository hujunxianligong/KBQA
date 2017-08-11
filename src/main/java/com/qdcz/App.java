package com.qdcz;

import com.hankcs.hanlp.utility.Predefine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 *Created by hadoop on 17-6-22.
 * APP main
 */
@ServletComponentScan
@SpringBootApplication(exclude = {MongoAutoConfiguration.class})
public class App {
    public static void main(String[] args){

        SpringApplication.run(App.class, args);
    }
}
