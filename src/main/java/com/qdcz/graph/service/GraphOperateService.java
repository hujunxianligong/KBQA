package com.qdcz.graph.service;

import com.qdcz.common.CommonTool;
import com.qdcz.conf.DatabaseConfiguration;
import com.qdcz.conf.LoadConfigListener;
import com.qdcz.entity.Graph;
import com.qdcz.graph.interfaces.IGraphBuzi;
import com.qdcz.graph.neo4jcypher.service.Neo4jCYService;
import com.qdcz.graph.tools.ResultBuilder;
import com.qdcz.index.elsearch.service.ElasearchService;
import com.qdcz.index.interfaces.IIndexService;
import com.qdcz.entity.Edge;
import com.qdcz.entity.Vertex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Path;
import org.neo4j.driver.v1.types.Relationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

/**
 * Created by star on 17-8-2.
 */
@Service
public class GraphOperateService {
    private Logger logger =  LogManager.getLogger(GraphOperateService.class.getSimpleName());
    @Autowired
    @Qualifier("elasearchService")
    private IIndexService indexBuzi;

    @Autowired
    @Qualifier("neo4jCypherService")
    private IGraphBuzi graphBuzi;


    public static void main(String[] args) {
        LoadConfigListener loadConfigListener = new LoadConfigListener();
        loadConfigListener.contextInitialized(null);



        GraphOperateService instance = new GraphOperateService();
        instance.indexBuzi = new ElasearchService();
        instance.graphBuzi = new Neo4jCYService();

        String vetexsPath = "/media/star/Doc/工作文档/智能小招/vertex.txt";
        String label = "test";
        String edgesPath = "/media/star/Doc/工作文档/智能小招/edges.txt";
        String relationship = "gra";

        instance.addVertexsByPath(vetexsPath,label,edgesPath,relationship);
    }



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

        graphBuzi.deleteVertex(vertex);

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
    public String addNodeEdge(Vertex vertex, Edge edge) throws Exception {

        String vertexId = graphBuzi.addVertex(vertex);

        vertex.setId(vertexId);

        indexBuzi.addOrUpdateIndex(vertex);


        edge.setRelationShip(DatabaseConfiguration.getRelationshipByLabel(vertex.getLabel()));


        edge.setTo(vertexId);
        String edgeId = graphBuzi.addEdges(edge);

        edge.setId(edgeId);

        indexBuzi.addOrUpdateIndex(edge);

        return "success";
    }


    /**
     * 通过名称查询
     * @param vertex
     * @return
     */
    public String exactMatchQuery(Vertex vertex,int depth){

        List<Path> paths=null;
        try {
            paths = graphBuzi.bfExtersion(vertex, 3);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ResultBuilder resultBuilder=new ResultBuilder();
        JSONObject result = resultBuilder.graphResult(paths);
        resultBuilder=null;
        return result.toString();
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
    public boolean addVertexsByPath( String vertexfilePath,String nodeLabel,String edgefilePath,String edgeRelationship){//批量导入／删除数据节点

        FileReader re = null;
        try {
            Map<String,String> key_value =  new HashMap<>();
            Scanner sc= new Scanner(new File(vertexfilePath));
            String str = null;
            while(sc.hasNext()){
                str = sc.nextLine();
                try {
                    JSONObject obj = new JSONObject(str);
                    String type = obj.getString("type").trim();
                    String root = obj.getString("root").trim();
                    String content = obj.getString("content").trim();
                    String name = obj.getString("name").trim();
                    String identity = obj.getString("identity").trim();


                    Vertex v = new Vertex(name, root, nodeLabel, type );
                    v.setContent(content);



                    String graphId = graphBuzi.addVertex(v);

                    System.out.println("graphId:"+graphId);

                    v.setId(graphId);

                    indexBuzi.addOrUpdateIndex(v);

                    key_value.put(identity,graphId);
                } catch (Exception e) {
                    logger.error("批量增点错误："+e.getMessage()+"\n"+str);
                    throw e;
                }
            }
            sc.close();



            sc= new Scanner(new File(edgefilePath));
            str = null;
            while(sc.hasNext()){
                str = sc.nextLine();
                try {
                    JSONObject obj = new JSONObject(str);
//                    Vertex vertex1= graphBuzi.checkVertexByIdentity(label,obj.getString("identity").replace("\\", "、").trim());
//                    Vertex vertex2 = graphBuzi.checkVertexByIdentity(label,obj.getString("identity").replace("\\", "、").trim());

                    String from  = key_value.get(obj.getString("from"));
                    String to =  key_value.get(obj.getString("to"));
                    String name =  obj.getString("name");
                    String root =  obj.getString("root");

                    Edge edge=new Edge(name, root, from, to, edgeRelationship);

                    String graphId = graphBuzi.addEdges(edge);

                    edge.setId(graphId);

                    indexBuzi.addOrUpdateIndex(edge);

                } catch (Exception e) {

                    logger.error("批量增边错误："+e.getMessage()+"\n"+str);
                    throw e;
                }
            }
            sc.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("完毕！");
        return true;
    }


    /**
     * 查看点的详情
     * @param vertex
     * @return
     */
    public String queryNodeDetail(Vertex vertex) {
        return indexBuzi.queryById(vertex).toString();
    }

    public String queryEdgeDetail(Edge edge) {
        return indexBuzi.queryById(edge).toString();
    }

    public void delVertexByPath(String vertexsPath, String label) {
        //TODO
    }
}
