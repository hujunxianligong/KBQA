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
public class GraphOperateService {

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

        System.out.println("graphId:"+graphId);

        vertex.setId(graphId);

        indexBuzi.addOrUpdateIndex(vertex);

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

        indexBuzi.addOrUpdateIndex(vertex);

        return "success";
    }


    /**
     * 新增边
     * @param edge
     * @return
     */
    public String addEgde(Edge edge){
        String graphId = graphBuzi.addEdges(edge);

        edge.setId(graphId);

        indexBuzi.addOrUpdateIndex(edge);

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

        indexBuzi.addOrUpdateIndex(edge);

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

        vertex.setId(vertexId);

        indexBuzi.addOrUpdateIndex(edge);


        String edgeId = graphBuzi.addEdges(edge);

        edge.setId(edgeId);

        indexBuzi.addOrUpdateIndex(edge);

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
    public long addVertexsByPath(RequestParameter requestParameter, String filePath){//批量导入／删除数据节点

        FileReader re = null;
        try {
            re = new FileReader(filePath);
            BufferedReader read = new BufferedReader(re );
            String str = null;
            while((str=read.readLine())!=null){
                try {
                    System.out.println(str);
                    JSONObject obj = new JSONObject(str);
                    String type = obj.getString("type").trim();
                    String root = obj.getString("root").trim();
                    String label = obj.getString("label").trim();
                    String content = obj.getString("content").trim();
                    String name = obj.getString("name").trim();


                    Vertex v = new Vertex(name, root, label, type );
                    v.setContent(content);


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





                    //TODO
                    Edge newEdge=new Edge();
//                    newEdge.setLabel(requestParameter.label);
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
