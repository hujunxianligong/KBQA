package com.qdcz.graph.neo4jkernel;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class CypherSearchService {

    @Autowired
    private GraphDatabaseService graphDatabaseService;

    @Transactional
    public void queryWithCypher(String quertString){
        //通过Cypher查询获得结果
//        StringBuilder sb = new StringBuilder();
//        sb.append("start john = node(0) ");
//        sb.append("match (john)-[:IS_FRIEND_OF]->(USER)-[:HAS_SEEN]->(movie) ");
//        sb.append("return movie;");
        Result result = graphDatabaseService.execute(quertString);

        //遍历结果
        while(result.hasNext()){
            //get("movie")和查询语句的return movie相匹配
            Node n = (Node) result.next().get("n");
            System.out.println( n.getAllProperties());
        }
    }
}
