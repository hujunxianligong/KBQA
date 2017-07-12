package com.qdcz.service;

import com.qdcz.tools.BuildReresult;
import com.qdcz.neo4jkernel.ExpanderService;
import com.qdcz.neo4jkernel.LegacyIndexService;
import com.qdcz.neo4jkernel.LoopDataService;
import com.qdcz.sdn.entity._Edge;
import com.qdcz.sdn.entity._Vertex;
import com.qdcz.tools.CommonTool;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by hadoop on 17-6-22.
 * main service
 */
@Service
public class TransactionService {


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

    @Transactional
    public long addVertexsByPath(String filePath,String type){//批量导入数据节点
        FileReader re = null;
        try {
            re = new FileReader(filePath);
            BufferedReader read = new BufferedReader(re );
            String str = null;
             while((str=read.readLine())!=null){
                try {
                    System.out.println(str);
                    JSONObject obj = new JSONObject(str);
                    _Vertex v = new _Vertex( obj.getString("type").trim(), obj.getString("name").trim(),obj.getString("identity").trim(),obj.getString("root").trim());
                    if("del".equals(type))
                        deleteVertex(v.name);
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
    @Transactional
    public long addEdgesByPath(String filePath){//批量导入边
        FileReader re = null;
        try {
            re = new FileReader(filePath);
            BufferedReader read = new BufferedReader(re );
            String str = null;
            while((str=read.readLine())!=null){
                try {
                    System.out.println(str);
                    JSONObject obj = new JSONObject(str);
                    _Vertex vertex1= bankLawService.checkVertexByNameAndRoot(obj.getString("from").replace("\\", "、").trim(), obj.getString("root").replace("\\", "、").trim());
                    _Vertex vertex2 = bankLawService.checkVertexByNameAndRoot(obj.getString("to").replace("\\", "、").trim(), obj.getString("root").replace("\\", "、").trim());
                    addEgde(vertex1,vertex2,obj.getString("relation").replace("\\", "、").trim());

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
    @Transactional
    public long addVertex(_Vertex vertex){//创建节点
        //1.预处理
        //2.建立节点
//        _Vertex vertex=new _Vertex("","王倪东","呵呵","奇点创智");
 //       _Vertex vertex=new _Vertex("","浩哥","呵呵","奇点创智");
        long l = bankLawService.changeVertex(vertex);
        //3.建立索引
        System.out.println("ddd");
        List<String > propKeys=new ArrayList<>();
        propKeys.add("name");
        propKeys.add("root");
        propKeys.add("relation");
        legacyIndexService.createFullTextIndex(l,propKeys,"vertex");
        return  l;
    }
    @Transactional
    public void deleteVertex(String name){
        List<_Vertex> vertexs = bankLawService.checkVertexByName(name);
        for(_Vertex vertex:vertexs){
           // System.out.println(vertex.getId());
            deleteVertex(vertex.getId());
        }
    }
    @Transactional
    public void deleteVertex(long id){//删除节点
        //1.预处理

        //2.获取节点及关系
//        long id=2686l;

        _Vertex vertex = bankLawService.checkVertexById(id);
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
                deleteEgde(Long.parseLong(edgeId));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //4.删除索引
        List<String > propKeys=new ArrayList<>();
        propKeys.add("name");
        propKeys.add("root");
        propKeys.add("relation");
        legacyIndexService.deleteFullTextIndex(id,propKeys,"vertex");
        //5.删除节点
        bankLawService.deleteVertex(vertex);
    }
    @Transactional
    public long changeVertex(long id,_Vertex newVertex){
        //1.预处理
        //2.获取要删除点的相关信息
        _Vertex vertex = bankLawService.checkVertexById(id);
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
        deleteVertex(id);
        //4.重新建立点
        long l = addVertex(newVertex);
        newVertex.setId(l);
        //5.重新建立边
        for (Map.Entry<Long, Map<String, Object>> entry : association.entrySet()) {
            Map<String, Object> value = entry.getValue();
            Node assocNode = (Node) value.get("associationVertex");
            Map<String, Object> allProperties = assocNode.getAllProperties();
            _Vertex associationVertex =new _Vertex(allProperties.get("type").toString(),allProperties.get("name").toString(),allProperties.get("identity").toString(),allProperties.get("root").toString());
            associationVertex.setId(assocNode.getId());
            _Edge edge=null;
            if("start".equals(allProperties.get("type").toString())) {
                edge = new _Edge(value.get("relation").toString(), associationVertex, newVertex, value.get("root").toString());
            }else{
                edge = new _Edge(value.get("relation").toString(),newVertex, associationVertex, value.get("root").toString());
            }
            long edgeid = bankLawService.addEdge(edge);
            List<String > propKeys=new ArrayList<>();
            propKeys.add("name");
            propKeys.add("root");
            propKeys.add("relation");
            legacyIndexService.createFullTextIndex(edgeid,propKeys,"edge");
        }

        return l;
    }
    @Transactional
    public JSONObject check(String keyword){//索引匹配查询
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
                merge=buildReresult.MergeResult(merge,resultArray.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        JSONObject result= buildReresult.cleanRestult(merge);
        System.out.println(result);
        return result;
    }
    @Transactional
    public JSONObject show(String name){
        JSONObject show = show(name, 2);
        return  show;
    }
    @Transactional
    public JSONObject show(String name,int depth){//根据精准name查图
        //1.预处理


        //2.name查询id
        List<_Vertex> vertices = bankLawService.checkVertexByName(name);
        //3.各个节点遍历
//        HashMap<Long, Iterable<Node>> results=new HashMap<>();
        BuildReresult buildReresult = new BuildReresult();
        JSONArray resultArray=new JSONArray();
        for(_Vertex vertex:vertices){
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
                merge=buildReresult.MergeResult(merge,resultArray.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        JSONObject result= buildReresult.cleanRestult(merge);
        return result;
    }
    @Transactional
    public JSONObject getInfoByRname(String relationship){//根据边relation 查询相关
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
                merge=buildReresult.MergeResult(merge,resultArray.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        JSONObject result= buildReresult.cleanRestult(merge);

        return result;
    }
    @Transactional
    public JSONObject getGraphById(Long id,int depth){//根据 id与深度返回结果
        //1.预处理操作

        //2.根据id 层数遍历
        BuildReresult buildReresult = new BuildReresult();
        Traverser traverser = loopDataService.loopDataByLoopApi(id,depth);

        //3.结果组织返回
        JSONObject jsonObject = buildReresult.graphResult(traverser);
        System.out.println(jsonObject.toString());
        return jsonObject;
    }
    @Transactional
    public String checkTest1(String question){
        String node = CommonTool.getNode(question);
        String edge = CommonTool.getEdge(question);
        String byNodeAndEdgeName = getByNodeAndEdgeName(node, edge);
        return byNodeAndEdgeName;
    }
    @Transactional
    public String getByNodeAndEdgeName(String nodeName,String edgeName)  {

//        String nodeName = object.getJSONObject("node").getString("name");
//        String edgeName = object.getJSONObject("edge").getString("name");
        //找点首ＩＤ
//        String[] fields={"root","name"};
//        JSONArray resultArray=new JSONArray();
//        List<Map<String, Object>> mapsNode = legacyIndexService.selectByFullTextIndex(fields, nodeName,"vertex");
//
        List<_Vertex> vertices = bankLawService.checkVertexByName(nodeName);
        BuildReresult buildReresult = new BuildReresult();


        //边索引
        String[] fields= new String[]{"relation"};
        List<Map<String, Object>> mapsEdge = legacyIndexService.selectByFullTextIndex(fields, edgeName,"edge");
        StringBuffer sb=new StringBuffer();
        for(Map<String, Object> map:mapsEdge){
            System.out.println((Long) map.get("id"));
            Node node    =   null;
            try (   Transaction tx = graphDatabaseService.beginTx()) {
                Relationship r = graphDatabaseService.getRelationshipById((Long)map.get("id"));
                tx.acquireReadLock(r);
                node = r.getEndNode();
                tx.success();
            }
            if(node!=null) {
//                for(Map<String, Object> mapNode:mapsNode){
//                    Long startid = (Long) mapNode.get("id");
//                   long endid = node.getId();
//                    String s = loopDataService.loopDataByDoublePath(startid, endid)+"\n";
//                    sb.append(s);
//                }
                for(_Vertex vertexL:vertices){
                    Long startid = vertexL.getId();
                    long endid = node.getId();
                    String s =loopDataService.loopDataByDoublePath(startid, endid);
                    if(!"".equals(s))
                    sb.append(s+"\n");
                }
            }
        }
        if("".equals(sb)){
            return "learning";
        }else{
            return sb.toString();
        }
    }
    @Transactional
    public long addEgde(long fromId,long toid,String relation){//插入关系
        //1.预处理

        //2.建立关系
        _Vertex vertex1= bankLawService.checkVertexById(fromId);
        _Vertex vertex2 = bankLawService.checkVertexById(toid);
//        _Edge edge=new _Edge("领导",vertex1,vertex2,"奇点创智");
        _Edge edge=new _Edge(relation,vertex1,vertex2,vertex2.getRoot());
        //3.插入关系
        long id = bankLawService.addEdge(edge);

        //4.关系建立索引
        List<String > propKeys=new ArrayList<>();
        propKeys.add("name");
        propKeys.add("root");
        propKeys.add("relation");
        legacyIndexService.createFullTextIndex(id,propKeys,"edge");
        return id;

    }
    @Transactional
    public long addEgde(_Vertex from,_Vertex to,String relation){//插入关系
        //1.预处理

        //2.建立关系
//        _Vertex vertex1= bankLawService.checkVertexById(2686l);
//        _Vertex vertex2 = bankLawService.checkVertexByNameAndRoot("王倪东", "奇点创智");
//        _Edge edge=new _Edge("领导",vertex1,vertex2,"奇点创智");
        _Edge edge=new _Edge(relation,from,to,from.getRoot());
        //3.插入关系
        long id = bankLawService.addEdge(edge);

        //4.关系建立索引
        List<String > propKeys=new ArrayList<>();
        propKeys.add("name");
        propKeys.add("root");
        propKeys.add("relation");
        legacyIndexService.createFullTextIndex(id,propKeys,"edge");
        return id;
    }
    @Transactional
    public _Edge deleteEgde(Long id){
        //1.预处理

        //2.建立关系
        _Edge edge=bankLawService.checkEdgeById(id);
        if(edge!=null){
            //3.删除关系索引
            List<String > propKeys=new ArrayList<>();
            propKeys.add("name");
            propKeys.add("root");
            propKeys.add("relation");
            legacyIndexService.deleteFullTextIndex(id,propKeys,"edge");
            //4.删除关系
            bankLawService.deleteEdge(edge);
        }

        return edge;
    }
    @Transactional
    public long changeEgde(Long id,Long fromId,Long toId,JSONObject newEgdeInfo){
        deleteEgde(id);
        long newId=0l;
        try {
            String relation=newEgdeInfo.getString("relation");
            _Vertex from = bankLawService.checkVertexById(fromId);
            _Vertex to = bankLawService.checkVertexById(toId);
            newId=addEgde(from,to,relation);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  newId;
    }
    @Transactional
    public void changeEgde(Long id,JSONObject newEgdeInfo){
        _Edge edge = deleteEgde(id);
        long newId=0l;
        try {
            String relation=newEgdeInfo.getString("relation");
            _Vertex from = bankLawService.checkVertexById(edge.getFrom_id());
            _Vertex to = bankLawService.checkVertexById(edge.getTo_id());
            newId=addEgde(from,to,relation);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(newId);
    }

    @Transactional
    public void threadB(){
        //2
        Node john = graphDatabaseService.getNodeById(143l);
        System.out.println(john.getProperty("age"));

        //3
        john.setProperty("age", 35);
        System.out.println(john.getProperty("age"));

        //5、threadB方法结束，提交事物
    }

    public void readLock(){
        try(Transaction tx= graphDatabaseService.beginTx()){
            //1
            Node john = graphDatabaseService.getNodeById(143l);
            System.out.println(john.getProperty("age"));
            //读锁，其他线程对于该节点的写会等待读锁释放
            tx.acquireReadLock(john);

            //4
            john = graphDatabaseService.getNodeById(143l);
            System.out.println(john.getProperty("age"));
            tx.success();
        }
    }

    @Transactional
    public void writeWaitReadLock(){
        //2
            Node john = graphDatabaseService.getNodeById(143l);
        System.out.println(john.getProperty("age"));

        //3
        john.setProperty("age", 38);
        System.out.println(john.getProperty("age"));
    }



}
