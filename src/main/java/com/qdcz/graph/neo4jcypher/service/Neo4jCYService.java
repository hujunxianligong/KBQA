package com.qdcz.graph.neo4jcypher.service;

import com.qdcz.common.CommonTool;
import com.qdcz.conf.LoadConfigListener;
import com.qdcz.entity.Edge;
import com.qdcz.entity.IGraphEntity;
import com.qdcz.entity.Vertex;
import com.qdcz.graph.interfaces.IGraphBuzi;
import com.qdcz.graph.neo4jcypher.connect.Neo4jClientFactory;
import com.qdcz.graph.neo4jcypher.dao.Neo4jCYDAO;;
import org.json.JSONArray;
import org.json.JSONObject;

import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Path;
import org.neo4j.driver.v1.types.Relationship;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * cypher语句，neo4j对外提供的操作
 * Created by star on 17-8-3.
 */
@Service("neo4jCypherService")
public class Neo4jCYService implements IGraphBuzi {

    public static void main(String[] args) {

        LoadConfigListener loadConfigListener=new LoadConfigListener();
        loadConfigListener.setSource_dir("/dev/");
        loadConfigListener.contextInitialized(null);
        Vertex vertex=new Vertex();
        vertex.setRoot("起点");
        vertex.setName("牵头行");
        vertex.setType("挖掘部");
        vertex.setId("55");
        vertex.setContent("");
        vertex.setLabel("ytdk_label");

        Neo4jCYService instance=  new Neo4jCYService();
      //  instance.deleteVertex(vertex);
        Edge edge=new Edge();
        edge.setRelationShip("gra");
        edge.setId(2181l+"");
        instance.bfExtersion(vertex,1);
      //  instance.dfExection(19,22,4);
    }



    private Neo4jCYDAO neo4jCYDAO;

    private Driver driver;

    public Neo4jCYService(){
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
    public List<IGraphEntity> deleteVertex(Vertex vertex) {
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
    public List<Path> bfExtersion(Vertex vertex, int depth) {
        List<Path> paths =new ArrayList<>();
        try {
             paths = neo4jCYDAO.bfExtersion(vertex, depth);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return paths;
    }

    @Override
    public Path dfExection(long fromId, long toId, int depth) {
        return neo4jCYDAO.dfExection(fromId,toId,depth);

    }

    @Override
    public Vertex checkVertexByIdentity(String label, String identity) {
        return neo4jCYDAO.checkVertexByIdentity(label,identity);
    }
    @Override
    public List<Path> checkGraphById(long id,int depth) {
        String sql ="MATCH path = (n )-[r*0.."+depth+"]-(relateNode) WHERE id(n)="+id+"  return path";
        StatementResult execute = neo4jCYDAO.execute(sql);
        List<Path> segments=new ArrayList<>();
        while ( execute.hasNext() ) {

            Value path = execute.next().get("path");
            segments.add( path.asPath());
            //    System.out.println(path);
        }
        return segments;
    }
    @Override
    public Map<String, Vertex> checkVertexByEdgeId(long id) {
        Map<String, Vertex> result=new HashMap<>();
        String sqlString="MATCH (p)-[r]->(m)WHERE id(r)= "+id+" RETURN p,m";
        StatementResult execute = neo4jCYDAO.execute(sqlString);
        while(execute.hasNext()){
            Record next = execute.next();
            Node n = next.get("p").asNode();
            Map<String, Object> nodeInfo = n.asMap();
            Vertex startVertex=new Vertex();
            CommonTool.transMap2Bean(nodeInfo,startVertex);
            startVertex.setId(n.id()+"");
            if(!result.containsKey("start")){
                result.put("start",startVertex);
            }
            Node m = next.get("m").asNode();
             nodeInfo = m.asMap();
            Vertex endVertex=new Vertex();
            CommonTool.transMap2Bean(nodeInfo,endVertex);
            endVertex.setId(m.id()+"");
            if(!result.containsKey("end")){
                result.put("end",endVertex);
            }

        }

        return result;
    }

    public void testExtersion(String sql){
        StatementResult execute = neo4jCYDAO.execute(sql);
    }

}
