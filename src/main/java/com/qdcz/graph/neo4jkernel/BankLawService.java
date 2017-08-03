package com.qdcz.graph.neo4jkernel;



import com.qdcz.graph.neo4jkernel.entity.Edge;
import com.qdcz.graph.neo4jkernel.entity.Vertex;
import com.qdcz.graph.neo4jkernel.repository.EdgeRepository;

import com.qdcz.graph.neo4jkernel.repository.VertexRepository;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hadoop on 17-6-22.
 * Bottom Functional service
 */
@Service
public class BankLawService {
    @Autowired
    private EdgeRepository edgeRepository;

    @Autowired
    private VertexRepository vertexRepository;

    @Autowired
    private GraphDatabaseService graphDatabaseService;

    @Transactional
    public Node getUserById(Long id){

        return graphDatabaseService.getNodeById(id);
    }

    @Transactional
    public long addEdge(String label,Edge egde){

        Edge save = edgeRepository.save(egde);
        return save.getEdgeId();

    }

    @Transactional
    public void deleteEdge(Edge egde){
        edgeRepository.delete(egde);
    }

    @Transactional
    public Edge checkEdgeById(String label,Long id){
        Edge one = edgeRepository.findOne(id);

        return one;
    }

//    @Transactional
//    public void changeEdge(Edge egde){
//
//       edgeRepository.getUpdateEdecql(egde.name,egde.relation,egde.from.name,egde.to.name,egde.root);
//    }
    @Transactional
    public long addVertex(String label,Vertex vertex){
        String addString=
        "merge (n:"+label+" {name:'"+vertex.getName()+"', root:'"+vertex.getRoot()+"',identity:'"+vertex.getIdentity()+"' }) on " +
                "create set n.type='"+vertex.getType()+"',n.identity='"+vertex.getIdentity()+"',n.root='"+vertex.getRoot()+"',n.content='"+vertex.getContent()+"' on " +
                "match set n.type='"+vertex.getType()+"',n.identity='"+vertex.getIdentity()+"',n.root='"+vertex.getRoot()+"',n.content='"+vertex.getContent()+"' return n";
        Result result = graphDatabaseService.execute(addString);
        long id=0l;
        while(result.hasNext()){
            Node n = (Node) result.next().get("n");
            id=n.getId();
            System.out.println( n.getId()+""+n.getAllProperties());
        }

        return id;
    }

    @Transactional
    public long deleteVertex(String label,Vertex vertex){
        String delString="Match (n:"+label+"  )where id(n)="+vertex.getId()+" delete n";
        Result result = graphDatabaseService.execute(delString);
        long id=0l;
        while(result.hasNext()){
            Node n = (Node) result.next().get("n");
            System.out.println( n.getAllProperties());
        }
//        vertexRepository.delete(vertex);
        return  id;
    }
    @Transactional
    public Vertex checkVertexById(String label,Long id){
        String quertString="match (n:"+label+") where id(n)="+id+" return n";
        Result result = graphDatabaseService.execute(quertString);
        Vertex vertex=new Vertex();
        //遍历结果
        while(result.hasNext()){
            Node n = (Node) result.next().get("n");

            vertex.setContent(n.getProperty("content").toString());
            vertex.setRoot(n.getProperty("root").toString());
            vertex.setType(n.getProperty("type").toString());
            vertex.setId(n.getId());
            vertex.setName(n.getProperty("name").toString());
            vertex.setIdentity(n.getProperty("identity").toString());
            System.out.println( n.getAllProperties());
        }
        return vertex;
    }
    @Transactional
    public Vertex checkVertexByNameAndRoot(String label,String  name,String root){
        String quertString="MATCH (n:"+label+" {name:'"+name+"', root:'"+root+"' }) RETURN n";
        Result result = graphDatabaseService.execute(quertString);
        Vertex vertex=new Vertex();
        //遍历结果
        while(result.hasNext()){
            Node n = (Node) result.next().get("n");

            vertex.setContent(n.getProperty("content").toString());
            vertex.setRoot(n.getProperty("root").toString());
            vertex.setType(n.getProperty("type").toString());
            vertex.setId(n.getId());
            vertex.setName(n.getProperty("name").toString());
            vertex.setIdentity(n.getProperty("identity").toString());
            System.out.println( n.getAllProperties());
        }
        return vertex;
    }
    @Transactional
    public List<Vertex> checkVertexByName(String label, String  name){
        List<Vertex> vertexList=new ArrayList<>();
        String quertString="MATCH (n:"+label+" {name:'"+name+"'}) RETURN n";
        Result result = graphDatabaseService.execute(quertString);
        //遍历结果
        while(result.hasNext()){
            Node n = (Node) result.next().get("n");
            Vertex vertex=new Vertex();
            vertex.setContent(n.getProperty("content").toString());
            vertex.setRoot(n.getProperty("root").toString());
            vertex.setType(n.getProperty("type").toString());
            vertex.setId(n.getId());
            vertex.setName(n.getProperty("name").toString());
            vertex.setIdentity(n.getProperty("identity").toString());
            vertexList.add(vertex);
            System.out.println( n.getAllProperties());
        }
        return vertexList;
    }
    @Transactional
    public long changeVertex(String label,Vertex vertex){
        String addString=
                "merge (n:"+label+" {name:'"+vertex.getName()+"', root:'"+vertex.getRoot()+"',root:'"+vertex.getIdentity()+"' }) on " +
                        "create set n.type='"+vertex.getType()+"',n.identity='"+vertex.getIdentity()+"',n.root='"+vertex.getRoot()+"',n.content='"+vertex.getContent()+"' on " +
                        "match set n.type='"+vertex.getType()+"',n.identity='"+vertex.getIdentity()+"',n.root='"+vertex.getRoot()+"',n.content='"+vertex.getContent()+"' return n";
        Result result = graphDatabaseService.execute(addString);
        long id=0l;
        while(result.hasNext()){
            Node n = (Node) result.next().get("n");
            id=n.getId();
            System.out.println( n.getId()+""+n.getAllProperties());
        }

        return id;
    }
}
