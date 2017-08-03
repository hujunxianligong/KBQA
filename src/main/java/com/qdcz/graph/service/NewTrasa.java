package com.qdcz.graph.service;

import com.qdcz.graph.interfaces.IGraphBuzi;
import com.qdcz.index.interfaces.IIndexBuzi;
import com.qdcz.graph.entity.Edge;
import com.qdcz.graph.entity.Vertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Created by star on 17-8-2.
 */
@Service
public class NewTrasa {

    @Autowired
    @Qualifier("elasearchBuzi")
    private IIndexBuzi indexBuzi;

    @Autowired
    @Qualifier("neo4jCypherBuzi")
    private IGraphBuzi graphBuzi;

    /**
     * 创建节点
     * @param vertex
     * @return
     */
    public String addVertex(Vertex vertex){
        String graphId = graphBuzi.addVertex(vertex);


        vertex.setGraphId(graphId);

        indexBuzi.addIndex(vertex);

        return "success";
    }

    /**
     * 删除节点通过id
     * @param vertex
     * @return
     */
    public String deleteVertex(Vertex vertex){

        graphBuzi.addVertex(vertex);

        indexBuzi.delIndex(vertex);

        return "success";
    }


    /**
     * 修改节点，通过id
     * @param vertex
     * @return
     */
    public String changeVertex(Vertex vertex){
        graphBuzi.changeVertex(vertex);

        indexBuzi.changeIndex(vertex);

        return "success";
    }


    /**
     * 新增边
     * @param edge
     * @return
     */
    public String addEgde(Edge edge){
        String graphId = graphBuzi.addEdges(edge);

        edge.setGraphId(graphId);

        indexBuzi.addIndex(edge);

        return "success";
    }

    /**
     * 删除边
     * @param edge
     * @return
     */
    public String deleteEgde(Edge edge){
        graphBuzi.deleteEdge(edge);

        indexBuzi.delIndex(edge);

        return "success";
    }


    /**
     * 修改边,通过id
     * @param edge
     * @return
     */
    public String changeEgde(Edge edge){
        graphBuzi.changeEdge(edge);

        indexBuzi.changeIndex(edge);

        return "success";
    }


    /**
     * 新增边和终点
     * @param vertex
     * @param edge
     * @return
     */
    public String addNodeEdge(Vertex vertex, Edge edge){

        String vertexId = graphBuzi.addVertex(vertex);

        vertex.setGraphId(vertexId);

        indexBuzi.addIndex(edge);


        String edgeId = graphBuzi.addEdges(edge);

        edge.setGraphId(edgeId);

        indexBuzi.addIndex(edge);

        return "success";
    }



    /**
     * 通过名称查询
     * @param name
     * @return
     */
    public String exactMatchQuery(String name){
        return null;
    }

    /**
     * 根据精准name查图
     * @param name
     * @param depth
     * @return
     */
    public String exactMatchQuery(String name,int depth){
        return null;
    }

    /**
     * 索引匹配查询
     * @param keyword
     * @return
     */
    public String indexMatchingQuery(String keyword){
        return null;
    }


    /**
     * 根据 id与深度返回结果
     * @param id
     * @param depth
     * @return
     */
    public String getGraphById(Long id,int depth){
        return null;
    }

}
