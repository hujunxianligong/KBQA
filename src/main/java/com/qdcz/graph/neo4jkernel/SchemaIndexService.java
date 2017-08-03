package com.qdcz.graph.neo4jkernel;

import com.qdcz.graph.neo4jkernel.generic.MyLabels;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class SchemaIndexService {

    @Autowired
    private GraphDatabaseService graphDatabaseService;

    //声明将要使用的标签
    private Label movieLabel = MyLabels.MOVIES;
    private Label userLabel = MyLabels.USERS;
    private Label adminLabel= MyLabels.ADMIN;

    @Transactional
    public void createIndex(){
        //创建电影名字属性索引
        graphDatabaseService.schema().indexFor(movieLabel).on("name").create();
        //创建用户名字属性索引
        graphDatabaseService.schema().indexFor(userLabel).on("name").create();
    }

    @Transactional
    public void createNodeWithAutoIndexed(){
        //创建新的MOVIES节点，并设置name属性值
        Node movie = graphDatabaseService.createNode(movieLabel);
        movie.setProperty("name", "Michael Collins");

        //创建新的USERS节点，并设置name属性值
        Node user = graphDatabaseService.createNode(userLabel);
        user.setProperty("name", "Michael Collins");
    }

    @Transactional
    public void findMichaelByAutoIndex(){
        //通过名字索引查找电影
        ResourceIterator<Node> result = graphDatabaseService.findNodes(movieLabel, "name", "Michael Collins");
        result.forEachRemaining(node -> System.out.println(node.getId()+ " -> " + node.getLabels()));
    }

    @Transactional
    public void createAnotherIndex(){
        //创建用户名字属性索引，该索引在上个例子中已经创建，如果再次创建会报错
        //graphDB.schema().indexFor(userLabel).on("name").create();
        //创建管理员字属性索引
        graphDatabaseService.schema().indexFor(adminLabel).on("name").create();
    }

    @Transactional
    public void createNodeInDifferentLabel(){
        //创建同时为USERS和ADMIN类型的节点
        Node user = graphDatabaseService.createNode(userLabel, adminLabel);
        user.setProperty("name", "Peter Smith");
    }

    @Transactional
    public void findNodeByLabelAndAutoIndex(){
        ResourceIterator<Node> adminSearch = graphDatabaseService.findNodes(adminLabel, "name", "Peter Smith");
        adminSearch.forEachRemaining(node -> System.out.println(node.getId()+ " -> " + node.getLabels()));

        ResourceIterator<Node> userSearch = graphDatabaseService.findNodes(userLabel, "name", "Peter Smith");
        userSearch.forEachRemaining(node -> System.out.println(node.getId()+ " -> " + node.getLabels()));
    }
}
