package com.qdcz.graph.neo4jcypher.service;

import com.qdcz.App;
import com.qdcz.conf.TestLoadConfigListener;
import com.qdcz.entity.Vertex;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Created by star on 17-8-9.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
@TestExecutionListeners(listeners = { TestLoadConfigListener.class })
@WebAppConfiguration
public class TestNeo4jCYService {
    @Autowired
    Neo4jCYService graphBuzi;

    @Test
    public void testBfExtersion(){
        graphBuzi = new Neo4jCYService();
        System.out.println(graphBuzi);
        Vertex v = new Vertex();
        v.setName("银团贷款业务");
        v.setLabel("ytdk_label");

        System.out.println(graphBuzi.bfExtersion(v,2));
    }
}
