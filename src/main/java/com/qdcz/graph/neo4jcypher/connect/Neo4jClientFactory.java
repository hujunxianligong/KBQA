package com.qdcz.graph.neo4jcypher.connect;

import com.qdcz.graph.neo4jcypher.conf.Neo4jConfiguration;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;

/**
 * Created by hadoop on 17-8-3.
 */
public class Neo4jClientFactory {
    public static Driver create(){
        Driver driver =GraphDatabase.driver( Neo4jConfiguration.url, AuthTokens.basic( Neo4jConfiguration.name, Neo4jConfiguration.pass ) );

        return driver;
    }
}
