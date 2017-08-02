package com.qdcz.graph.neo4jkernel;

import com.qdcz.graph.neo4jkernel.entity._Edge;
import com.qdcz.graph.neo4jkernel.entity._Vertex;
import com.qdcz.graph.neo4jkernel.repository.EdgeRepository;

import com.qdcz.graph.neo4jkernel.repository.VertexRepository;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public long addEdge(_Edge egde){
        _Edge save = edgeRepository.save(egde);
        return save.getEdgeId();

    }
    @Transactional
    public void addEdge(List<_Edge> egdes){
        edgeRepository.save(egdes);
    }
    @Transactional
    public void deleteEdge(_Edge egde){
        edgeRepository.delete(egde);
    }
    @Transactional
    public void deleteEdge(List<_Edge> egdes){
        edgeRepository.delete(egdes);
    }
    @Transactional
    public _Edge checkEdgeById(Long id){
        _Edge one = edgeRepository.findOne(id);

        return one;
    }

    @Transactional
    public void changeEdge(_Edge egde){
       edgeRepository.getUpdateEdecql(egde.name,egde.relation,egde.from.name,egde.to.name,egde.root);
    }
    @Transactional
    public void addVertex(_Vertex vertex){
        vertexRepository.save(vertex);
    }
    @Transactional
    public void addVertex(List<_Vertex> vertexs){
        vertexRepository.save(vertexs);
    }
    @Transactional
    public void deleteVertex(_Vertex vertex){
        vertexRepository.delete(vertex);
    }
    @Transactional
    public void deleteVertex(List<_Vertex> vertexs){
        vertexRepository.delete(vertexs);
    }
    @Transactional
    public _Vertex checkVertexById(Long id){
        return vertexRepository.findOne(id);
    }
    @Transactional
    public _Vertex checkVertexByNameAndRoot(String  name,String root){
        return vertexRepository.getVertByNameCql(name,root);
    }
    @Transactional
    public List<_Vertex> checkVertexByName(String  name){
        return vertexRepository.getVertsByNameCql(name);
    }
    @Transactional
    public long changeVertex(_Vertex vertex){
        Long id = vertexRepository.getUpdateVertexCql(vertex.name, vertex.root, vertex.type, vertex.identity,vertex.content.toString()).getId();
        return id;
    }
}
