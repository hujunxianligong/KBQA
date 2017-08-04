package com.qdcz.graph.service;

import com.qdcz.graph.interfaces.IGraphBuzi;
import com.qdcz.index.interfaces.IIndexBuzi;
import com.qdcz.graph.entity.Edge;
import com.qdcz.graph.entity.Vertex;
import com.qdcz.service.bean.RequestParameter;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

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

    /**
     *批量导入或删除数据节点
     */
    public long addVertexsByPath(RequestParameter requestParameter, String filePath, String type){//批量导入／删除数据节点

        FileReader re = null;
        try {
            re = new FileReader(filePath);
            BufferedReader read = new BufferedReader(re );
            String str = null;
            while((str=read.readLine())!=null){
                try {
                    System.out.println(str);
                    JSONObject obj = new JSONObject(str);
                    Vertex v = new Vertex( obj.getString("type").trim(), obj.getString("name").trim(),obj.getString("identity").trim(),obj.getString("root").trim(),obj.getJSONObject("content"));
                    v.setLabel(requestParameter.label);
                    if("del".equals(type))
                        deleteVertex(v);
                    else
                        addVertex(v);
//
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            read.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("完毕！");
        return 0l;
    }

    /**
     * //批量导入边
     * @param filePath
     * @return
     */
    public long addEdgesByPath(RequestParameter requestParameter,String filePath){//批量导入边
        String label = requestParameter.label;


        FileReader re = null;
        try {
            re = new FileReader(filePath);
            BufferedReader read = new BufferedReader(re );
            String str = null;
            while((str=read.readLine())!=null){
                try {
                    System.out.println(str);
                    JSONObject obj = new JSONObject(str);
                    Vertex vertex1= graphBuzi.checkVertexByIdentity(label,obj.getString("identity").replace("\\", "、").trim());
                    Vertex vertex2 = graphBuzi.checkVertexByIdentity(label,obj.getString("identity").replace("\\", "、").trim());
                    Edge newEdge=new Edge(obj.getString("relation"),vertex1,vertex2,vertex1.getRoot());
                    newEdge.setLabel(requestParameter.label);
                    addEgde(newEdge);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            read.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("完毕！");
        return 0l;
    }


}
