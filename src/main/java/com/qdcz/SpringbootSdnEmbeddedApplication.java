package com.qdcz;

import com.qdcz.tools.LoadConfigListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *Created by hadoop on 17-6-22.
 * APP main
 */
@SpringBootApplication
public class SpringbootSdnEmbeddedApplication {
    public static void main(String[] args){
        LoadConfigListener loadConfigListener=new LoadConfigListener();
        loadConfigListener.contextInitialized(null);
        SpringApplication.run(SpringbootSdnEmbeddedApplication.class, args);
    }
}
