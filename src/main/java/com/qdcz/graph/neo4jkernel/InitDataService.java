package com.qdcz.graph.neo4jkernel;

import com.qdcz.graph.neo4jkernel.generic.MyLabels;
import com.qdcz.graph.neo4jkernel.generic.MyRelationshipTypes;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
public class InitDataService {

    @Autowired
    private GraphDatabaseService graphDatabaseService;

    @Transactional
    public void initData(){
        /**
         * 新增User节点
         */
        Node user1 = graphDatabaseService.createNode();
        Node user2 = graphDatabaseService.createNode();
        Node user3 = graphDatabaseService.createNode();

        /**
         * 为User节点添加Friend关系
         */
        user1.createRelationshipTo(user2, MyRelationshipTypes.IS_FRIEND_OF);
        user1.createRelationshipTo(user3,MyRelationshipTypes.IS_FRIEND_OF);

        /**
         * 给节点设置name属性
         */
        user1.setProperty("name", "John Johnson");
        user2.setProperty("name", "Kate Smith");
        user3.setProperty("name", "Jack Jeffries");

        /**
         * 新增Movie节点
         */
        Node movie1 = graphDatabaseService.createNode();
        movie1.setProperty("name", "Fargo");
        Node movie2 = graphDatabaseService.createNode();
        movie2.setProperty("name", "Alien");
        Node movie3 = graphDatabaseService.createNode();
        movie3.setProperty("name", "Heat");

        /**
         * 为User节点和Friend节点添加HAS_SEEN关系
         */
        Relationship relationship1 = user1.createRelationshipTo(movie1, MyRelationshipTypes.HAS_SEEN);
        relationship1.setProperty("stars", 5);

        Relationship relationship2 = user2.createRelationshipTo(movie3, MyRelationshipTypes.HAS_SEEN);
        relationship2.setProperty("stars", 3);
        Relationship relationship6 = user2.createRelationshipTo(movie2, MyRelationshipTypes.HAS_SEEN);
        relationship6.setProperty("stars", 6);

        Relationship relationship3 = user3.createRelationshipTo(movie1, MyRelationshipTypes.HAS_SEEN);
        relationship3.setProperty("stars", 4);
        Relationship relationship4 = user3.createRelationshipTo(movie2, MyRelationshipTypes.HAS_SEEN);
        relationship4.setProperty("stars", 5);

        /**
         * 节点添加label，以区分各个不同类型的节点
         */
        user1.addLabel(MyLabels.USERS);
        user2.addLabel(MyLabels.USERS);
        user3.addLabel(MyLabels.USERS);

        movie1.addLabel(MyLabels.MOVIES);
        movie2.addLabel(MyLabels.MOVIES);
        movie3.addLabel(MyLabels.MOVIES);
    }
}
