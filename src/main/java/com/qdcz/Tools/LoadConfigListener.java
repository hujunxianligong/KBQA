package com.qdcz.Tools;

import com.qdcz.config.MyConnConfigure;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
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
            //------------加载neo4j的配置------------
            System.out.println("------------加载neo4j的配置------------");
            MyConnConfigure myConnConfigure = new MyConnConfigure();
            //System.out.println(LoadConfigListener.class.getResource("/com/wfxl/common/myconfig.properties"));
            myConnConfigure.load(LoadConfigListener.class.getResourceAsStream("/neo4j.properties"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}