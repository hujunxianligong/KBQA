package com.qdcz.graph.neo4jcypher.service;

import com.mongodb.util.JSON;
import com.qdcz.common.CommonTool;
import com.qdcz.conf.LoadConfigListener;
import com.qdcz.entity.Edge;
import com.qdcz.entity.IGraphEntity;
import com.qdcz.entity.Vertex;
import com.qdcz.graph.interfaces.IGraphBuzi;
import com.qdcz.graph.neo4jcypher.connect.Neo4jClientFactory;
import com.qdcz.graph.neo4jcypher.dao.Neo4jCYDAO;;
import jxl.Workbook;
import jxl.format.VerticalAlignment;
import jxl.read.biff.BiffException;
import jxl.write.*;
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

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
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
        instance.relationshipName("licom_relationship");
     //   instance.bfExtersion(vertex,1);
      //  instance.dfExection(19,22,4);
     //   instance.batchInsertEdge("hehe_rel","edge.csv");
     //   instance.batchInsertVertex("hehe","vertex.csv");

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
    public Map batchInsertVertex(String label,String filepath){
        Map<String,String> mapsResult=new HashMap<>();
        String sql=null;
            sql="USING PERIODIC COMMIT 1000 " +
                    "LOAD CSV WITH HEADERS FROM \"file:///" + filepath + "\" AS line  " +
                    "MERGE (p:"+label+"{root:line.root,name:line.name,type:line.type,content:line.content,identity:line.identity}) return line.identity,id(p)";

        StatementResult execute = neo4jCYDAO.execute(sql);
        while(execute.hasNext()){
            Record next = execute.next();
            String id = next.get("id(p)").toString();
            String identity = next.get("line.identity").asString();
            mapsResult.put(identity,id);
        }
        return  mapsResult;
    }
    public Map batchInsertEdge(String relatinship,String filepath){
        Map<String,String> mapsResult=new HashMap<>();
        String sql=null;
            sql="USING PERIODIC COMMIT 1000 " +
                    "LOAD CSV WITH HEADERS FROM \"file:///"+filepath+"\" AS line  " +
                    "MATCH (m{identity:line.from_id} ) MATCH (n{identity:line.to_id}) " +
                    "MERGE (m)-[r:"+relatinship+"{from:id(m),root:line.root,name:line.name,to:id(n)}]->(n) return line.identity,id(r);";
        StatementResult execute = neo4jCYDAO.execute(sql);
        while(execute.hasNext()){
            Record next = execute.next();
            String id = next.get("id(r)").toString();
            String identity = next.get("line.identity").toString();
            mapsResult.put(identity,id);
        }
        return  mapsResult;
    }
    public Map batchInsertEdgeById(String relatinship,String filepath){
        Map<String,String> mapsResult=new HashMap<>();
        String sql=null;
        sql="USING PERIODIC COMMIT 1000 " +
                "LOAD CSV WITH HEADERS FROM \"file:///"+filepath+"\" AS line  " +
                "MATCH (m  ) MATCH (n ) where id(m)=apoc.number.parseInt(line.from_id) AND id(n)=apoc.number.parseInt(line.to_id) "+
                "MERGE (m)-[r:"+relatinship+"{from:id(m),root:line.root,name:line.name,to:id(n),weight:apoc.number.parseInt(line.weight)}]->(n) return line.identity,id(r);";
        System.out.println(sql);
        StatementResult execute = neo4jCYDAO.execute(sql);
        while(execute.hasNext()){
            Record next = execute.next();
            String id = next.get("id(r)").toString();
            String identity = next.get("line.identity").asString();
            mapsResult.put(identity,id);
        }
        return  mapsResult;
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
    public List<Path> directedBfExtersion(Vertex vertex, int depth){
        List<Path> paths =new ArrayList<>();
        try {
            paths = neo4jCYDAO.bfExtersion(vertex, depth,true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return paths;
    }


    @Override
    public List<Path> bfExtersion(Vertex vertex, int depth) {
        List<Path> paths =new ArrayList<>();
        try {
             paths = neo4jCYDAO.bfExtersion(vertex, depth,false);
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

        return neo4jCYDAO.checkGraphById(id,depth);
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
    public List<String> relationshipName(String relationshipType){
        List<String> result=new ArrayList<>();
        String sql="match()-[r:"+relationshipType+"]->() return distinct(r.name)as names  limit 100";
        StatementResult execute = neo4jCYDAO.execute(sql);
        while(execute.hasNext()){
            Record next = execute.next();
            String names = next.get("names").asString();
            result.add(names);
        }
        System.out.println(result.toString()+result.size());
        return result;
    }






    public void testExtersion(String sql){
        StatementResult execute = neo4jCYDAO.execute(sql);
    }

}
