package com.qdcz.graph.neo4jcypher.dao;

import com.qdcz.common.CommonTool;
import com.qdcz.entity.Edge;
import com.qdcz.entity.IGraphEntity;
import com.qdcz.entity.Vertex;
import com.qdcz.graph.interfaces.IGraphDAO;
import org.json.JSONArray;
import org.json.JSONObject;
import org.neo4j.driver.v1.*;

import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Path;
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
             run = session.run(sql);
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
        parameters.put("identity",vertex.getIdentity());
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
        String changeString = "match (n:"+vertex.getLabel()
                +") where id(n)= $id  set n.name=$name, n.type= $type ,n.identity= $identity,n.root=$root,n.content=$content return n";



        long id=0l;
        Map<String, Object> parameters=new HashMap();
        parameters.put("id",vertex.getId());
        parameters.put("name",vertex.getName());
        parameters.put("identity",vertex.getIdentity());
        parameters.put("root",vertex.getRoot());
        parameters.put("content",vertex.getContent());
        parameters.put("type",vertex.getType());
        try ( Session session = driver.session() )
        {
            Transaction transaction = session.beginTransaction();
            StatementResult run = transaction.run(changeString, parameters);
            while(run.hasNext()){
                Node n =  run.next().get("n").asNode();
                id=n.id();
                System.out.println(id+"");
            }
            transaction.success();
        }
        return id+"";
    }

    @Override
    public List<IGraphEntity> deleteVertex(Vertex vertex) {
        List<IGraphEntity> results=new ArrayList<>();
        Set<String> nodeIds=new HashSet<>();
        String select ="match (n:"+vertex.getLabel()+")-[r*1]-(m) where id(n)="+Long.parseLong(vertex.getId())+" return n,r";
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
                    CommonTool.transMap2Bean(edgeInfo,newEdge);
                    newEdge.setId(one_gra.id()+"");
                    results.add(newEdge);
                }
            }
        }
        String delString=  "MATCH (n:"+vertex.getLabel()+") where id(n)="+Long.parseLong(vertex.getId())+" DETACH DELETE n";


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
        String sql = "MATCH (m  ) MATCH (n ) where id(m)=$fId_L AND id(n)=$tId_L " +
                "MERGE (m)-[r:"+edge.getRelationShip()+"]-(n) ON CREATE SET r.root=$root,r.name=$name,r.from=$fromId,r.to=$toId " +
                "on match SET  r.root=$root,r.name=$name,r.from=$fromId,r.to=$toId RETURN r";

        System.out.println(edge.toJSON());
        long id=0l;
        Map<String, Object> parameters=new HashMap();
        parameters.put("fId_L",Long.parseLong(edge.getFrom()));
        parameters.put("tId_L",Long.parseLong(edge.getTo()));
        parameters.put("fromId",edge.getFrom());
        parameters.put("toId",edge.getTo());
        parameters.put("name",edge.getName());
        parameters.put("root",edge.getRoot());
        try ( Session session = driver.session() )
        {
            Transaction transaction = session.beginTransaction();
            StatementResult run = transaction.run(sql,parameters);
            while(run.hasNext()){
                Relationship r = (Relationship) run.next().get("r").asRelationship();
                id=r.id();
//                System.out.println( n.getId()+""+n.getAllProperties());
            }
            transaction.success();
        }
        return id+"";
    }


    @Override
    public String changeEdge(Edge edge) {

        return addEdges(edge);
    }

    @Override
    public String  deleteEdge(Edge edge) {

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
            return -1l+"";
        }
        return id+"";
    }

    @Override
    public List<Path> bfExtersion(Vertex vertex,int depth) throws Exception {

        String sql ="MATCH path = (n:"+vertex.getLabel()+" {name:'"+vertex.getName()+"'})-[r*0.."+depth+"]-(relateNode) return path";
       // "MATCH p = (n:"+vertex.getLabel()+" {name:'"+vertex.getName()+"'})-[r*0.."+depth+"]-(relateNode) return p, nodes(p),relationships(p),labels(n), extract (rel in rels(p) | type(rel) ) as types";
       //"MATCH p = (n:"+vertex.getLabel()+"{name:'"+vertex.getName()+"'})-[r:"+vertex.getRelationship()+"*1.."+depth+"]-(relateNode) return nodes(p),relateNode,n";
       //"MATCH p = (n:"+vertex.getLabel()+" {name:'"+vertex.getName()+"'})-[r*1.."+depth+"]->(relateNode) return nodes(p),relationships(p)";
        StatementResult execute = execute(sql);
        List<Path> segments=new ArrayList<>();
        while ( execute.hasNext() ) {

            Value path = execute.next().get("path");
            segments.add( path.asPath());
        //    System.out.println(path);
        }
        return segments;

    }
    @Override
    public Path dfExection(long fromId,long toId,int depth){
        String sql= "MATCH path = shortestPath ( (a ) -[*0.."+depth+"]-> (b) )WHERE id(a)="+fromId+" AND id(b) ="+toId+" RETURN path;";
       // String sql=" MATCH path = shortestPath((a)-[r*1..4]->(b)) WHERE id(a)="+fromId+" AND id(b) ="+toId+ " AND ALL(x IN nodes(path) WHERE (x:law)) RETURN path";
       // String sql="MATCH  p=(a)-[r*1..4]->(b)" + "WHERE id(a)="+fromId+" AND id(b) ="+toId+" " + "RETURN p AS shortestPath, reduce(distance=0, r in relationships(p)| distance+r.distance) AS totalDistance ORDER BY totalDistance ASC LIMIT 1";
        Path segments=null;
        StatementResult execute = execute(sql);
        while ( execute.hasNext() ) {
                Value path = execute.next().get("path");
             segments = path.asPath();
            System.out.println(path);
        }
        return segments;
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
                vertex.setLabel(label);
                vertex.setIdentity(identity);
        }
        return vertex;
    }

}
