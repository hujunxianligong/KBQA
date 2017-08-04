package com.qdcz.graph.neo4jcypher.buzi;

import com.qdcz.common.LoadConfigListener;
import com.qdcz.graph.entity.Edge;
import com.qdcz.graph.entity.Vertex;
import com.qdcz.graph.interfaces.IGraphBuzi;
import com.qdcz.graph.neo4jcypher.connect.Neo4jClientFactory;
import com.qdcz.graph.neo4jcypher.dao.Neo4jCYDAO;
import org.neo4j.driver.v1.*;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Result;
import org.springframework.stereotype.Service;

/**
 * cypher语句，neo4j对外提供的操作
 * Created by star on 17-8-3.
 */
@Service("neo4jCypherBuzi")
public class Neo4jCYBuzi implements IGraphBuzi {

    public static void main(String[] args) {
        LoadConfigListener loadConfigListener=new LoadConfigListener();
        loadConfigListener.contextInitialized(null);

        Vertex vertex =  new Vertex();
        vertex.setType("");
        vertex.setLabel("ddd");
        vertex.setRelationship("gra");
        vertex.setRoot("root");
        vertex.setIdentity("gdflgjdfklj");
        vertex.setName("gfdfggf");




        Neo4jCYBuzi instance=  new Neo4jCYBuzi();
        instance.addVertex(vertex);
    }



    private Neo4jCYDAO neo4jCYDAO;

    private Driver driver;

    public Neo4jCYBuzi(){
        driver =  Neo4jClientFactory.create();
        neo4jCYDAO = new Neo4jCYDAO(driver);
    }







    @Override
    public String addVertex(Vertex vertex) {

        return  neo4jCYDAO.addVertex(vertex);
    }

    @Override
    public String changeVertex(Vertex vertex) {

        return neo4jCYDAO.changeVertex(vertex);
    }

    @Override
    public String deleteVertex(Vertex vertex) {
        return neo4jCYDAO.deleteVertex(vertex);
    }

    @Override
    public String addEdges(Edge edge) {
        return neo4jCYDAO.addEdges(edge);
    }

    @Override
    public String changeEdge(Edge edge) {
        return neo4jCYDAO.changeEdge(edge);
    }

    @Override
    public String deleteEdge(Edge edge) {
        return neo4jCYDAO.deleteEdge(edge);
    }
    @Override
    public void dfExection(long fromId,long toId,int depth){
        String depthstr="";
        if(depth>1){
            depthstr=depth+"";
        }
        String add= "MATCH path = shortestPath ( (a ) -[*1.."+depthstr+"]- (b) )WHERE id(a)="+fromId+" AND id(b) ="+toId+" RETURN path;";
        try ( Session session = driver.session() )
        {
            Transaction transaction = session.beginTransaction();
            StatementResult run = transaction.run(add);
            while(run.hasNext()){
                Value path = run.next().get("path");
                System.out.println(path);
            }
        }
    }
    public Vertex checkVertexByIdentity(String label,String  identity){
        String quertString="MATCH (n:"+label+" {identity:'"+identity+"' }) RETURN n";
        Vertex vertex=new Vertex();
        try ( Session session = driver.session() )
        {
            Transaction transaction = session.beginTransaction();
            StatementResult run = transaction.run(quertString);
            while(run.hasNext()) {
                Node n = (Node) run.next().get("n");
                vertex.setContent(n.getProperty("content").toString());
                vertex.setRoot(n.getProperty("root").toString());
                vertex.setType(n.getProperty("type").toString());
                vertex.setId(n.getId());
                vertex.setName(n.getProperty("name").toString());
                vertex.setIdentity(n.getProperty("identity").toString());
                System.out.println(n.getAllProperties());
            }
        }
        return vertex;
    }
}
