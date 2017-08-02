package com.qdcz.graph.service;

import com.qdcz.index.IIndexDAO;
import com.qdcz.graph.entity._Edge;
import com.qdcz.graph.entity._Vertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Created by star on 17-8-2.
 */
public class NewTrasa {

    @Autowired
    @Qualifier()
    private IIndexDAO indexDAO;

    @Autowired
    @Qualifier()
    private IGraphDAO graphDAO;

    /**
     * 创建节点
     * @param vertex
     * @return
     */
    public String addVertex(_Vertex vertex){
        String graphId = graphDAO.addVertex(vertex);


        vertex.setGraphId(graphId);

        indexDAO.addIndex(vertex);

        return "success";
    }

    /**
     * 删除节点通过id
     * @param vertex
     * @return
     */
    public String deleteVertex(_Vertex vertex){

        graphDAO.addVertex(vertex);

        indexDAO.delIndex(vertex);

        return "success";
    }


    /**
     * 修改节点，通过id
     * @param vertex
     * @return
     */
    public String changeVertex(_Vertex vertex){
        graphDAO.changeVertex(vertex);

        indexDAO.changeIndex(vertex);

        return "success";
    }


    /**
     * 新增边
     * @param edge
     * @return
     */
    public String addEgde(_Edge edge){
        String graphId = graphDAO.addEdges(edge);

        edge.setGraphId(graphId);

        indexDAO.addIndex(edge);

        return "success";
    }

    /**
     * 删除边
     * @param edge
     * @return
     */
    public String deleteEgde(_Edge edge){
        graphDAO.deleteEdge(edge);

        indexDAO.delIndex(edge);

        return "success";
    }


    /**
     * 修改边,通过id
     * @param edge
     * @return
     */
    public String changeEgde(_Edge edge){
        graphDAO.changeEdge(edge);

        indexDAO.changeIndex(edge);

        return "success";
    }


    /**
     * 新增边和终点
     * @param vertex
     * @param edge
     * @return
     */
    public String addNodeEdge(_Vertex vertex,_Edge edge){

        String vertexId = graphDAO.addVertex(vertex);

        vertex.setGraphId(vertexId);

        indexDAO.addIndex(edge);


        String edgeId = graphDAO.addEdges(edge);

        edge.setGraphId(edgeId);

        indexDAO.addIndex(edge);

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
