package com.qdcz.graph.neo4jcypher.dao;

import com.qdcz.common.CommonTool;
import com.qdcz.graph.entity.Edge;
import com.qdcz.graph.entity.IGraphEntity;
import com.qdcz.graph.entity.Vertex;
import com.qdcz.graph.interfaces.IGraphDAO;
import org.json.JSONArray;
import org.json.JSONObject;
import org.neo4j.cypher.internal.frontend.v2_3.ast.functions.E;
import org.neo4j.driver.v1.*;

import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Relationship;

import java.util.*;

/**
 * neo4j的dao操作
 * Created by star on 17-8-3.
 */
public class Neo4jCYDAO implements IGraphDAO{
    private Driver driver;


    public Neo4jCYDAO(Driver driver){
        this.driver = driver;
    }


    public StatementResult execute(String sql){
        StatementResult run=null;
        try ( Session session = driver.session() )
        {
            Transaction transaction = session.beginTransaction();
             run = transaction.run(sql);

        }
        return run;
    }
    @Override
    public String addVertex(Vertex vertex) {
//        String addString=
//                "merge (n:"+vertex.getLabel()+" {name:'"+vertex.getName()+"', root:'"+vertex.getRoot()+"',identity:'"+vertex.getIdentity()+"' }) on " +
//                        "create set n.type='"+vertex.getType()+"',n.identity='"+vertex.getIdentity()+"',n.root='"+vertex.getRoot()+"',n.content='"+vertex.getContent()+"' on " +
//                        "match set n.type='"+vertex.getType()+"',n.identity='"+vertex.getIdentity()+"',n.root='"+vertex.getRoot()+"',n.content='"+vertex.getContent()+"' return n";

        String addString= "merge (n:"+vertex.getLabel()+" {name:$name, root:$root ,identity:$identity }) on " +
                "create set n.type= $type ,n.identity= $identity,n.root= $root,n.content=$content on " +
                "match set n.type=$type ,n.identity=$identity,n.root=$root,n.content=$content return n";
        long id=0l;
        Map<String, Object> parameters=new HashMap();
        parameters.put("name",vertex.getName());
  //      parameters.put("identity",vertex.getIdentity());
        parameters.put("root",vertex.getRoot());
        parameters.put("content",vertex.getContent());
        parameters.put("type",vertex.getType());
        try ( Session session = driver.session() )
        {
            StatementResult run = session.run(addString, parameters);
//            Transaction transaction = session.beginTransaction();
//            StatementResult run = transaction.run(addString);
            while(run.hasNext()){
                Node n =  run.next().get("n").asNode();
                id=n.id();
                System.out.println(id+"");
            }
        }
        return id+"";
    }

    @Override
    public String changeVertex(Vertex vertex) {
       return  addVertex(vertex);
    }

    @Override
    public List<IGraphEntity> deleteVertex(Vertex vertex) {
        List<IGraphEntity> results=new ArrayList<>();
        Set<String> nodeIds=new HashSet<>();
        String select ="match (n:"+vertex.getLabel()+")-[r*1]-(m) where id(n)="+vertex.getId()+" return n,r";
        try ( Session session = driver.session() )
        {
            StatementResult run = session.run(select);
            while(run.hasNext()){
                Record next = run.next();
                Node n = next.get("n").asNode();
                Map<String, Object> nodeInfo = n.asMap();
                Vertex newVertex=new Vertex();
                CommonTool.transMap2Bean(nodeInfo,newVertex);
                newVertex.setId(n.id()+"");
                if(!nodeIds.contains(newVertex.getGraphId())) {
                    results.add(newVertex);
                }
                nodeIds.add(n.id()+"");

                List<Object> rels =  next.get( "r" ).asList();
                for (Object rel : rels) {
                    Relationship one_gra = (Relationship) rel;
                    Map<String, Object> edgeInfo = one_gra.asMap();
                    Edge newEdge=new Edge();
                    newEdge.setId(one_gra.id()+"");
                    //CommonTool.transMap2Bean(edgeInfo,newEdge);
                    results.add(newEdge);
                }
            }
        }
        String delString=  "MATCH (n:"+vertex.getLabel()+")-[r]-(m) where id(n)="+vertex.getId()+" DELETE n,r";

        try ( Session session = driver.session() )
        {
            StatementResult run=null;
            Transaction transaction = session.beginTransaction();
            run = transaction.run(delString);
            transaction.success();
        }
        System.out.println();
        return results;
    }

    @Override
    public String addEdges(Edge edge) {
        String sql = "MATCH (m  {identity:$fromIdentity }) MATCH (n {identity:$toIdentity }) " +
                "MERGE (m)-[r:"+edge.getRelationShip()+"]-(n) ON CREATE SET r.relation =$relation ,r.name=$name,r.from=$fromId,r.to=$toId " +
                "on match SET r.relation =$relation ,r.name=$name,r.from=$fromId,r.to=$toId RETURN r";
        long id=0l;
        Map<String, Object> parameters=new HashMap();
        parameters.put("fromId",edge.getFrom());
        parameters.put("toId",edge.getTo());
        parameters.put("name",edge.getName());
        try ( Session session = driver.session() )
        {
            Transaction transaction = session.beginTransaction();
            StatementResult run = transaction.run(sql);
            while(run.hasNext()){
                Node n = (Node) run.next().get("n").asNode();
                id=n.get("id").asLong();
//                System.out.println( n.getId()+""+n.getAllProperties());
            }
        }
        return id+"";
    }

    @Override
    public String changeEdge(Edge edge) {

        return addEdges(edge);
    }

    @Override
    public boolean deleteEdge(Edge edge) {

        String delString = "MATCH (f)-[r:"+edge.getRelationShip()+"]->(t) WHERE id(r)="+edge.getGraphId()+" DELETE r";
        long id=0l;
        try ( Session session = driver.session() )
        {
            StatementResult run=null;
            Transaction transaction = session.beginTransaction();
            run = transaction.run(delString);
            transaction.success();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public JSONObject bfExtersion(Vertex vertex,int depth) {
        JSONArray nodesJarry=new JSONArray();
        JSONArray edgesJarry=new JSONArray();
        Set<String> nodeIds=new HashSet<>();
        Set<String> edgeIds=new HashSet<>();
        String sql = "MATCH p = (n:"+vertex.getLabel()+" {name:'"+vertex.getName()+"'})-[r*1.."+depth+"]->(relateNode) return nodes(p),relationships(p)";
       //"MATCH p = (n:"+vertex.getLabel()+"{name:'"+vertex.getName()+"'})-[r:"+vertex.getRelationship()+"*1.."+depth+"]-(relateNode) return nodes(p),relateNode,n";
        StatementResult execute = execute(sql);
        while ( execute.hasNext() ) {
            Record record = execute.next();
            System.out.println();
            List<Object> nodes = record.get("nodes(p)").asList();
            for(Object node:nodes){
                Node n=(Node) node;
                Map<String, Object> nodeInfo = n.asMap();
                Vertex newVertex=new Vertex();
                CommonTool.transMap2Bean(nodeInfo,newVertex);
                newVertex.setId(n.id()+"");
                if(!nodeIds.contains(newVertex.getGraphId())) {
                   // nodesJarry.put();
                }
                nodeIds.add(n.id()+"");
            }
            List<Object> rels =  record.get( "relationships(p)" ).asList();
            for (Object rel : rels) {
                Relationship one_gra = (Relationship) rel;
                Map<String, Object> edgeInfo = one_gra.asMap();
                Edge newEdge=new Edge();
                CommonTool.transMap2Bean(edgeInfo,newEdge);
                newEdge.setId(one_gra.id()+"");
                if(!edgeIds.contains(newEdge.getGraphId())) {
                    edgesJarry.put(newEdge.toQueryJSON());
                }
                edgeIds.add(one_gra.id()+"");
            }
        }
        nodeIds.clear();
        edgeIds.clear();

        JSONObject result =new JSONObject();
        result.put("nodes",nodesJarry);
        result.put("edges",edgesJarry);
        System.out.println(result);
        return null;
    }
    @Override
    public void dfExection(long fromId,long toId,int depth){
        String sql= "MATCH path = shortestPath ( (a ) -[*1.."+depth+"]- (b) )WHERE id(a)="+fromId+" AND id(b) ="+toId+" RETURN path;";
        StatementResult execute = execute(sql);
        while ( execute.hasNext() ) {
                Value path = execute.next().get("path");
                System.out.println(path);

        }
    }
    public Vertex checkVertexByIdentity(String label,String  identity){
        String quertString="MATCH (n:"+label+" {identity:'"+identity+"' }) RETURN n";
        Vertex vertex=new Vertex();
        StatementResult execute = execute(quertString);
        while ( execute.hasNext() ){
                Node n =  execute.next().get("n").asNode();
                vertex.setContent(n.get("content").toString());
                vertex.setRoot(n.get("root").toString());
                vertex.setType(n.get("type").toString());
                vertex.setId(n.id()+"");
                vertex.setName(n.get("name").toString());
        }
        return vertex;
    }

}
