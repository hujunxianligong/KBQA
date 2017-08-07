package com.qdcz.graph.neo4jcypher.service;

import com.qdcz.common.CommonTool;
import com.qdcz.common.LoadConfigListener;
import com.qdcz.graph.entity.Edge;
import com.qdcz.graph.entity.IGraphEntity;
import com.qdcz.graph.entity.Vertex;
import com.qdcz.graph.interfaces.IGraphBuzi;
import com.qdcz.graph.neo4jcypher.connect.Neo4jClientFactory;
import com.qdcz.graph.neo4jcypher.dao.Neo4jCYDAO;;
import org.json.JSONObject;

import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Path;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * cypher语句，neo4j对外提供的操作
 * Created by star on 17-8-3.
 */
@Service("neo4jCypherBuzi")
public class Neo4jCYBuzi implements IGraphBuzi {

    public static void main(String[] args) {
        LoadConfigListener loadConfigListener=new LoadConfigListener();
        loadConfigListener.contextInitialized(null);
        Vertex vertex=new Vertex();
        vertex.setRoot("起点");
        vertex.setName("牵头行");
        vertex.setType("挖掘部");
        vertex.setId("55");
        vertex.setContent("");
        vertex.setLabel("law");

        Neo4jCYBuzi instance=  new Neo4jCYBuzi();
      //  instance.deleteVertex(vertex);
        Edge edge=new Edge();
        edge.setRelationShip("gra");
        edge.setId(2181l+"");
//        instance.bfExtersion(vertex,1);
        instance.dfExection(55,74,2);
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
    public JSONObject bfExtersion(Vertex vertex, int depth) {
        try {
            return neo4jCYDAO.bfExtersion(vertex,depth);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JSONObject();
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
             nodeInfo = n.asMap();
            Vertex endVertex=new Vertex();
            CommonTool.transMap2Bean(nodeInfo,endVertex);
            endVertex.setId(n.id()+"");
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
