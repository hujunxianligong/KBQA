package com.qdcz.graph.neo4jcypher.dao;

import com.qdcz.graph.entity.Edge;
import com.qdcz.graph.entity.Vertex;
import com.qdcz.graph.interfaces.IGraphDAO;
import org.json.JSONObject;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.types.Relationship;
import org.neo4j.graphdb.Node;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * neo4j的dao操作
 * Created by star on 17-8-3.
 */
public class Neo4jCYDAO implements IGraphDAO{
    private TranClient client;


    public Neo4jCYDAO(TranClient client){
        this.client = client;
    }


    private String execute(String sql){
        long id=0l;
        try ( Session session = client.driver.session() )
        {
            Transaction transaction = session.beginTransaction();
            StatementResult run = transaction.run(sql);
            while(run.hasNext()){
                Node n = (Node) run.next().get("n");
                id=n.getId();
                System.out.println( n.getId()+""+n.getAllProperties());
            }
        }
        return id+"";
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
        try ( Session session = client.driver.session() )
        {
            StatementResult run = session.run(addString, parameters);
//            Transaction transaction = session.beginTransaction();
//            StatementResult run = transaction.run(addString);
            while(run.hasNext()){
                Node n = (Node) run.next().get("n");
                id=n.getId();
                System.out.println( n.getId()+""+n.getAllProperties());
            }
        }
        return id+"";
    }

    @Override
    public String changeVertex(Vertex vertex) {
       return  addVertex(vertex);
    }

    @Override
    public String deleteVertex(Vertex vertex) {
        String delString="Match (n:"+vertex.getLabel()+"  )where id(n)="+vertex.getId()+" delete n";
        return execute(delString);
    }

    @Override
    public String addEdges(Edge edge) {
        String sql = "MATCH (m:"+edge.getLabel()+"  {identity:$fromIdentity }) MATCH (n:"+edge.getLabel()+" {identity:$toIdentity }) " +
                "MERGE (m)-[r:"+edge.getRelationship()+"]-(n) ON CREATE SET r.relation =$relation ,r.name=$name,r.from=$fromId,r.to=$toId " +
                "on match SET r.relation =$relation ,r.name=$name,r.from=$fromId,r.to=$toId RETURN r";
        long id=0l;
        Map<String, Object> parameters=new HashMap();
        parameters.put("fromIdentity",edge.from.getIdentity());
        parameters.put("toIdentity",edge.to.getIdentity());
        parameters.put("fromId",edge.to.getId());
        parameters.put("toId",edge.from.getId());
        parameters.put("name",edge.getName());
        parameters.put("relation",edge.getRelation());
        try ( Session session = client.driver.session() )
        {
            Transaction transaction = session.beginTransaction();
            StatementResult run = transaction.run(sql);
            while(run.hasNext()){
                Node n = (Node) run.next().get("n");
                id=n.getId();
                System.out.println( n.getId()+""+n.getAllProperties());
            }
        }
        return id+"";
    }

    @Override
    public String changeEdge(Edge edge) {

        return addEdges(edge);
    }

    @Override
    public String deleteEdge(Edge edge) {
        String delString = "MATCH (f:"+edge.getLabel()+")-[r:"+edge.getRelationship()+"]->(t:"+edge.getLabel()+") WHERE id(r)="+edge.getGraphId()+" DELETE r";
        return execute(delString);
    }

    @Override
    public JSONObject bfExtersion(Vertex vertex,int depth) {
        String sql = "MATCH (n:"+vertex.getLabel()+"{name:'"+vertex.getName()+"'})-[r:"+vertex.getRelationship()+"*1.."+depth+"]-(relateNode) return r,relateNode,n";

        try ( Session session = client.driver.session())
        {
            try ( Transaction tx = session.beginTransaction() )
            {
//				 System.out.println(sql);
                StatementResult result = tx.run(sql);
                while ( result.hasNext() )
                {
                    Record record = result.next();

                }

            }
        }

        return null;
    }
    @Override
    public void dfExection(long fromId,long toId,int depth){
        String depthstr="";
        if(depth>1){
            depthstr=depth+"";
        }
        String add= "MATCH path = shortestPath ( (a ) -[*1.."+depthstr+"]- (b) )WHERE id(a)="+fromId+" AND id(b) ="+toId+" RETURN path;";
        try ( Session session = client.driver.session() )
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
        try ( Session session = client.driver.session() )
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
