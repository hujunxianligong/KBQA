
package com.qdcz.graph.service;

import com.qdcz.graph.entity.Vertex;
import com.qdcz.graph.neo4jkernel.BankLawService;
import com.qdcz.common.BuildReresult;
import com.qdcz.graph.neo4jkernel.ExpanderService;
import com.qdcz.graph.neo4jkernel.LegacyIndexService;
import com.qdcz.graph.neo4jkernel.LoopDataService;

import com.qdcz.graph.entity.Edge;
import com.qdcz.service.bean.RequestParameter;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.ogm.json.JSONArray;
import org.neo4j.ogm.json.JSONException;
import org.neo4j.ogm.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


/**
 * Created by hadoop on 17-6-22.
 * main service
 */
@Service
public class TransactionService {

    @Autowired
    private NewTrasa newTrasa;
    @Autowired
    private GraphDatabaseService graphDatabaseService;
    @Autowired
    private BankLawService bankLawService;
    @Autowired
    private LoopDataService loopDataService;
    @Autowired
    private LegacyIndexService legacyIndexService;
    @Autowired
    private ExpanderService expanderService;


    /**
     *批量导入或删除数据节点
     */
    @Transactional

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
                    if("del".equals(type))
                        deleteVertex(requestParameter,v.name);
                    else {
                        long dd = addVertex(requestParameter, v);

                        v.setGraphId(String.valueOf(dd));
                        newTrasa.addVertex(v);
                    }
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
    @Transactional
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
                    Vertex vertex1= bankLawService.checkVertexByNameAndRoot(label,obj.getString("from").replace("\\", "、").trim(), obj.getString("root").replace("\\", "、").trim());
                    Vertex vertex2 = bankLawService.checkVertexByNameAndRoot(label,obj.getString("to").replace("\\", "、").trim(), obj.getString("root").replace("\\", "、").trim());
                    Edge newEdge=new Edge(obj.getString("relation"),vertex1,vertex2,vertex1.getRoot());
                    addEgde(requestParameter,newEdge);

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
     * 创建节点
     * @param vertex
     * @return
     */
    @Transactional

    public long addVertex(RequestParameter requestParameter,Vertex vertex){//创建节点
        //1.预处理
        //2.建立节点
//        _Vertex vertex=new _Vertex("","王倪东","呵呵","奇点创智");
 //       _Vertex vertex=new _Vertex("","浩哥","呵呵","奇点创智");
        String label = requestParameter.label;

        long l = bankLawService.addVertex(label,vertex);
        if(vertex.name.length()<30) {
            //3.建立索引
            List<String> propKeys = new ArrayList<>();
            propKeys.add("name");
            propKeys.add("root");
            propKeys.add("relation");
            legacyIndexService.createFullTextIndex(l, propKeys, "vertex");
        }
        return  l;
    }
    /**
     *删除节点
     */
    @Transactional
    public void deleteVertex(RequestParameter requestParameter,String name){
        String label = requestParameter.label;

        List<Vertex> vertexs = bankLawService.checkVertexByName(label,name);
        for(Vertex vertex:vertexs){
           // System.out.println(vertex.getId());
            deleteVertex(requestParameter,vertex.getId());
        }
    }

    /**
     * 通过
     * @param id
     */
    @Transactional
    public void deleteVertex(RequestParameter requestParameter,long id){//删除节点
        //1.预处理
        //2.获取节点及关系
        String label = requestParameter.label;
        Vertex vertex = bankLawService.checkVertexById(label,id);
        if(vertex==null)
            return ;
        BuildReresult buildReresult = new BuildReresult();
        Traverser traverser = loopDataService.loopDataByLoopApi(vertex.getId(),1);
        JSONObject jsonObject = buildReresult.graphResult(traverser);
        //3.删除关系
        try {
            JSONArray edges = jsonObject.getJSONArray("edges");
            for(int i=0;i<edges.length();i++){
                JSONObject jsonObject1 = edges.getJSONObject(i);
                String edgeId = jsonObject1.getString("id");
                System.out.println(id);
                deleteEgde(requestParameter,Long.parseLong(edgeId));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //4.删除索引
        if(vertex.name.length()<30){
            List<String > propKeys=new ArrayList<>();
            propKeys.add("name");
            propKeys.add("root");
            propKeys.add("relation");
            legacyIndexService.deleteFullTextIndex(id,propKeys,"vertex");
        }
        //5.删除节点
        bankLawService.deleteVertex(label,vertex);
    }

    /**
     * 修改点信息
     * @param id
     * @param newVertex
     * @return
     */
    @Transactional
    public long changeVertex(RequestParameter requestParameter,long id,Vertex newVertex){//修改点信息

        //1.预处理
        //2.获取要删除点的相关信息
        String label = requestParameter.label;

        Vertex vertex = bankLawService.checkVertexById(label,id);
        if(vertex ==null){
            return 0l;
        }
        Traverser traverser = loopDataService.loopDataByLoopApi(vertex.getId(),1);
        ResourceIterable<Node> nodes = traverser.nodes();
        Node targetNode=null;
        for(Node node:nodes){
            if(node.getId()==id){
                targetNode=node;
                break;
            }
        }

        HashMap<Long,Map<String, Object>> association=new HashMap<>();//存放将要删除目标点关联点边
        ResourceIterable<Relationship> relationships = traverser.relationships();
        for(Relationship relationship:relationships) {
            Map<String, Object> allProperties = relationship.getAllProperties();
            if(relationship.getEndNode().equals(targetNode)){
                Node startNode = relationship.getStartNode();
                startNode.setProperty("type","start");
                allProperties.put("associationVertex",startNode);

            }else{
                Node endNode = relationship.getEndNode();
                endNode.setProperty("type","end");
                allProperties.put("associationVertex",endNode);
            }
            association.put(relationship.getId(),allProperties);
        }

        //3.调用删除点
        deleteVertex(requestParameter,id);
        //4.重新建立点
        long l = addVertex(requestParameter,newVertex);
        newVertex.setId(l);
        //5.重新建立边
        for (Map.Entry<Long, Map<String, Object>> entry : association.entrySet()) {
            Map<String, Object> value = entry.getValue();
            Node assocNode = (Node) value.get("associationVertex");
            Map<String, Object> allProperties = assocNode.getAllProperties();
            Vertex associationVertex = null;
            JSONObject content = new JSONObject(allProperties.get("content"));
            associationVertex = new Vertex(allProperties.get("type").toString(),allProperties.get("name").toString(),allProperties.get("identity").toString(),allProperties.get("root").toString(),content);
            associationVertex.setId(assocNode.getId());
            Edge edge=null;
            JSONObject edgeContent=new JSONObject(value.get("content"));
            if("start".equals(allProperties.get("type").toString())) {
                edge = new Edge(value.get("relation").toString(), associationVertex, newVertex, value.get("root").toString(),edgeContent);
            }else{
                edge = new Edge(value.get("relation").toString(),newVertex, associationVertex, value.get("root").toString(),edgeContent);
            }
            long edgeid = bankLawService.addEdge(label,edge);
            List<String > propKeys=new ArrayList<>();
          //  propKeys.add("name");
            propKeys.add("root");
            propKeys.add("relation");
            legacyIndexService.createFullTextIndex(edgeid,propKeys,"edge");
        }

        return l;
    }

    /**
     * 索引匹配查询
     * @param keyword
     * @return
     */
    @Transactional
    public JSONObject indexMatchingQuery(String keyword){
        //1.预处理

        //2.分词
        //3.索引查询节点
       // String keyword="银行创新指引";
        String[] fields={"root","name","relation"};
        JSONArray resultArray=new JSONArray();
            List<Map<String, Object>> maps = legacyIndexService.selectByFullTextIndex(fields, keyword,"vertex");
        BuildReresult buildReresult = new BuildReresult();
        //4.各个节点广搜路径
        for(Map<String, Object> map:maps){
            Traverser traverser = loopDataService.loopDataByLoopApi((Long) map.get("id"),2);
            JSONObject jsonObject = buildReresult.graphResult(traverser);
            resultArray.put(jsonObject);
        }
        //边索引
        maps = legacyIndexService.selectByFullTextIndex(fields, keyword,"edge");
        //边取上2节点进行搜索
        for(Map<String, Object> map:maps){
            System.out.println((Long) map.get("id"));
            Node[] nodes    =   null;
            try (   Transaction tx = graphDatabaseService.beginTx()) {
                Relationship r = graphDatabaseService.getRelationshipById((Long)map.get("id"));
                tx.acquireReadLock(r);
                nodes = r.getNodes();
                tx.success();
            }
            if(nodes!=null)
            for(Node node:nodes){
                Traverser traverser = loopDataService.loopDataByLoopApi( node.getId(),2);
                JSONObject jsonObject = buildReresult.graphResult(traverser);
                resultArray.put(jsonObject);
            }

        }
        JSONObject merge=new JSONObject();
        //5.组织返回结果
        for(int i=0;i<resultArray.length();i++){
            try {
                merge=buildReresult.mergeResult(merge,resultArray.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        JSONObject result= buildReresult.cleanRestult(merge);
        System.out.println(result);
        return result;
    }
    @Transactional
    public JSONObject exactMatchQuery(RequestParameter requestParameter, String name){
        JSONObject show = exactMatchQuery(requestParameter,name, 2);
        return  show;
    }

    /**
     * 根据精准name查图
     * @param name
     * @param depth
     * @return
     */
    @Transactional
    public JSONObject exactMatchQuery(RequestParameter requestParameter,String name,int depth){//根据精准name查图
        //2.name查询id
        String label = requestParameter.label;

        List<Vertex> vertices = bankLawService.checkVertexByName(label,name);
        //3.各个节点遍历
//        HashMap<Long, Iterable<Node>> results=new HashMap<>();
        BuildReresult buildReresult = new BuildReresult();
        JSONArray resultArray=new JSONArray();
        for(Vertex vertex:vertices){
            Traverser traverser = loopDataService.loopDataByLoopApi(vertex.getId(),depth);
            JSONObject jsonObject = buildReresult.graphResult(traverser);
            resultArray.put(jsonObject);
        }
        //边索引
        String[] fields={"relation"};
        List<Map<String, Object>> maps = legacyIndexService.selectByFullTextIndex(fields, name,"edge");
        //边取上2节点进行搜索
        for(Map<String, Object> map:maps){
            System.out.println((Long) map.get("id"));
            Node[] nodes    =   null;
            try (   Transaction tx = graphDatabaseService.beginTx()) {
                Relationship r = graphDatabaseService.getRelationshipById((Long)map.get("id"));
                tx.acquireReadLock(r);
                nodes = r.getNodes();
                tx.success();
            }
            if(nodes!=null)
                for(Node node:nodes){
                    Traverser traverser = loopDataService.loopDataByLoopApi( node.getId(),depth);
                    JSONObject jsonObject = buildReresult.graphResult(traverser);
                    resultArray.put(jsonObject);
                }
        }
        JSONObject merge=new JSONObject();
        //4.结果组织返回
        for(int i=0;i<resultArray.length();i++){
            try {
                merge=buildReresult.mergeResult(merge,resultArray.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        JSONObject result= buildReresult.cleanRestult(merge);
        return result;
    }

    /**
     * 根据边relation 查询相关
     * @param relationship
     * @return
     */
    @Transactional
    public JSONObject getInfoByRname(String relationship){
        JSONArray resultArray=new JSONArray();
        BuildReresult buildReresult = new BuildReresult();
        //边索引
        String[] fields={"relation"};
        List<Map<String, Object>> maps = legacyIndexService.selectByFullTextIndex(fields, relationship,"edge");
        //边取上2节点进行搜索
        for(Map<String, Object> map:maps){
            System.out.println((Long) map.get("id"));
            Node[] nodes    =   null;
            try (   Transaction tx = graphDatabaseService.beginTx()) {
                Relationship r = graphDatabaseService.getRelationshipById((Long)map.get("id"));
                tx.acquireReadLock(r);
                nodes = r.getNodes();
                tx.success();
            }
            if(nodes!=null)
                for(Node node:nodes){
                    Traverser traverser = loopDataService.loopDataByLoopApi( node.getId(),2);
                    JSONObject jsonObject = buildReresult.graphResult(traverser);
                    resultArray.put(jsonObject);
                }
        }
        JSONObject merge=new JSONObject();
        //5.组织返回结果
        for(int i=0;i<resultArray.length();i++){
            try {
                merge=buildReresult.mergeResult(merge,resultArray.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        JSONObject result= buildReresult.cleanRestult(merge);

        return result;
    }

    /**
     * 根据 id与深度返回结果
     * @param id
     * @param depth
     * @return
     */
    @Transactional
    public JSONObject getGraphById(Long id,int depth){
        //1.预处理操作

        //2.根据id 层数遍历
        BuildReresult buildReresult = new BuildReresult();
        Traverser traverser = loopDataService.loopDataByLoopApi(id,depth);

        //3.结果组织返回
        JSONObject jsonObject = buildReresult.graphResult(traverser);
        return jsonObject;
    }


    /**
     * 插入关系
     * @return
     */
    @Transactional
    public long addEgde(RequestParameter requestParameter,Edge edge){//插入关系

        //1.预处理
        //2.建立关系
        String label = requestParameter.label;
//        Vertex vertex1= bankLawService.checkVertexById(label,fromId);
//        Vertex vertex2 = bankLawService.checkVertexById(label,toid);
//        _Edge edge=new _Edge("领导",vertex1,vertex2,"奇点创智");
//        Edge edge=new _Edge(relation,vertex1,vertex2,vertex2.getRoot());
        //3.插入关系
        long id = bankLawService.addEdge(label,edge);
        //4.关系建立索引
        List<String > propKeys=new ArrayList<>();
      //  propKeys.add("name");
        propKeys.add("root");
        propKeys.add("relation");
        legacyIndexService.createFullTextIndex(id,propKeys,"edge");
        return id;
    }

    /**
     * 插入关系
     */
    @Transactional
    public long addEgde(RequestParameter requestParameter,long fromId,long toId,String relation,JSONObject content){//插入关系

        //1.预处理
        //2.建立关系
        String label = requestParameter.label;
        Vertex vertex1= bankLawService.checkVertexById(label,fromId);
        Vertex vertex2 = bankLawService.checkVertexById(label,toId);
        Edge edge=new Edge(relation,vertex1,vertex2,vertex2.getRoot(),content);
        //3.插入关系
        long id = bankLawService.addEdge(label,edge);
        //4.关系建立索引
        List<String > propKeys=new ArrayList<>();
//        propKeys.add("name");
        propKeys.add("root");
        propKeys.add("relation");
        legacyIndexService.createFullTextIndex(id,propKeys,"edge");
        return id;
    }

    /**
     *
     * @param id
     * @return
     */
    @Transactional
    public Edge deleteEgde(RequestParameter requestParameter,Long id){
        //1.预处理
        //2.建立关系
        String label = requestParameter.label;
        Edge edge=bankLawService.checkEdgeById(label,id);
        if(edge!=null){
            //3.删除关系索引
            List<String > propKeys=new ArrayList<>();
//            propKeys.add("name");
            propKeys.add("root");
            propKeys.add("relation");
            legacyIndexService.deleteFullTextIndex(id,propKeys,"edge");
            //4.删除关系
            bankLawService.deleteEdge(edge);
        }
        return edge;
    }
    @Transactional
    public long changeEgde(RequestParameter requestParameter,Long id,Long fromId,Long toId,JSONObject newEgdeInfo){
        String label = requestParameter.label;

        deleteEgde(requestParameter,id);
        long newId=0l;
        try {
            String relation=newEgdeInfo.getString("relation");
            Vertex from = bankLawService.checkVertexById(label,fromId);
            Vertex to = bankLawService.checkVertexById(label,toId);
            Edge newEdge=new Edge(relation,from,to,from.getRoot());
            newId=addEgde(requestParameter,newEdge);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  newId;
    }

    @Transactional
    public void changeEgde(RequestParameter requestParameter,Long id,JSONObject newEgdeInfo){
        String label = requestParameter.label;
        Edge edge = deleteEgde(requestParameter,id);
        long newId=0l;
        try {
            String relation=newEgdeInfo.getString("relation");
            Vertex from = bankLawService.checkVertexById(label,edge.getFrom_id());
            Vertex to = bankLawService.checkVertexById(label,edge.getTo_id());
            Edge newEdge=new Edge(relation,from,to,from.getRoot(),newEgdeInfo.getJSONObject("content"));
            newId=addEgde(requestParameter,newEdge);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(newId);
    }

}
