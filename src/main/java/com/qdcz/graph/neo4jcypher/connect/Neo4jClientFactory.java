package com.qdcz.graph.neo4jcypher.connect;

import com.qdcz.graph.neo4jcypher.conf.Neo4jConfiger;
import com.qdcz.graph.neo4jcypher.dao.TranClient;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.GraphDatabase;

/**
 * Created by hadoop on 17-8-3.
 */
public class Neo4jClientFactory {
    public static TranClient create(){
        TranClient client = null;
        client.driver = GraphDatabase.driver( Neo4jConfiger.url, AuthTokens.basic( Neo4jConfiger.name, Neo4jConfiger.pass ) );
        return client;
    }
}
