package com.qdcz.config;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver;
import org.neo4j.ogm.service.Components;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;



@Configuration
//启动类的@SpringBootApplication会自动扫描同级包以及子包，所以下面的@ComponentScan不加应该没关系
//@ComponentScan("cn.didadu.sdn")
@EnableNeo4jRepositories("com.qdcz.sdn.repository")
@EnableTransactionManagement
public class Neo4jConfig extends Neo4jConfiguration implements EmbeddedServletContainerCustomizer {
    /**
     * 嵌入式连接
     * @return
     */
    @Bean
    public org.neo4j.ogm.config.Configuration getEmbeddedConfiguration(){
        org.neo4j.ogm.config.Configuration config = new org.neo4j.ogm.config.Configuration();
        config.driverConfiguration()
                .setDriverClassName("org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver")
                .setURI("file:///mnt/vol_0/neo4j-community-3.1.1/data/databases/graph.db")
//                    .setDriverClassName(MyConnConfigure.driver).setURI(MyConnConfigure.db)
        ;

        return config;
    }

    @Bean
    public SessionFactory getSessionFactory() {
        /**
         * 如果不指定节点映射的java bean路径，保存时会报如下警告，导致无法将节点插入Neo4j中
         * ... is not an instance of a persistable class
         */
        return new SessionFactory(getEmbeddedConfiguration(), "com.qdcz.sdn.entity");
    }

    @Bean
    public GraphDatabaseService graphDatabaseService(){
        getSessionFactory();
        EmbeddedDriver embeddedDriver = (EmbeddedDriver) Components.driver();
        return embeddedDriver.getGraphDatabaseService();
    }

    @Override
    public void customize(ConfigurableEmbeddedServletContainer configurableEmbeddedServletContainer) {
        configurableEmbeddedServletContainer.setPort(14000);
    }
}
