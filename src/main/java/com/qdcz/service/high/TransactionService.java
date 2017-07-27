package com.qdcz.service.high;

import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import com.qdcz.sdn.entity.Edge;
import com.qdcz.service.bean.RequestParameter;
import com.qdcz.sdn.entity.Vertex;
import com.qdcz.service.bottom.BankLawService;
import com.qdcz.service.middle.QuestionPaserService;
import com.qdcz.tools.BuildReresult;
import com.qdcz.neo4jkernel.ExpanderService;
import com.qdcz.neo4jkernel.LegacyIndexService;
import com.qdcz.neo4jkernel.LoopDataService;
import com.qdcz.sdn.entity._Edge;
import com.qdcz.sdn.entity._Vertex;
import com.qdcz.tools.CommonTool;
import com.qdcz.tools.Levenshtein;
import com.qdcz.tools.MyComparetor;
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
    private GraphDatabaseService graphDatabaseService;
    @Autowired
    private BankLawService bankLawService;
    @Autowired
    private LoopDataService loopDataService;
    @Autowired
    private LegacyIndexService legacyIndexService;
    @Autowired
    private ExpanderService expanderService;
    @Autowired
    private QuestionPaserService questionPaserService;
    @Transactional
    public long addVertexsByPath(RequestParameter requestParameter,String filePath,String type){//批量导入／删除数据节点
        FileReader re = null;
        try {
            re = new FileReader(filePath);
            BufferedReader read = new BufferedReader(re );
            String str = null;
             while((str=read.readLine())!=null){
                try {
                    System.out.println(str);
                    JSONObject obj = new JSONObject(str);
                    Vertex v = new _Vertex( obj.getString("type").trim(), obj.getString("name").trim(),obj.getString("identity").trim(),obj.getString("root").trim(),obj.getJSONObject("content"));
                    if("del".equals(type))
                        deleteVertex(requestParameter,v.name);
                    else
                    addVertex(requestParameter,v);
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
                    addEgde(requestParameter,vertex1,vertex2,obj.getString("relation").replace("\\", "、").trim());

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
    public long addVertex(RequestParameter requestParameter,Vertex vertex){//创建节点
        //1.预处理
        //2.建立节点
//        _Vertex vertex=new _Vertex("","王倪东","呵呵","奇点创智");
 //       _Vertex vertex=new _Vertex("","浩哥","呵呵","奇点创智");
        String label = requestParameter.label;

        long l = bankLawService.addVertex(label,vertex);
        //3.建立索引
        List<String > propKeys=new ArrayList<>();
        propKeys.add("name");
        propKeys.add("root");
        propKeys.add("relation");
        legacyIndexService.createFullTextIndex(l,propKeys,"vertex");
        return  l;
    }
    @Transactional
    public void deleteVertex(RequestParameter requestParameter,String name){
        String label = requestParameter.label;

        List<Vertex> vertexs = bankLawService.checkVertexByName(label,name);
        for(Vertex vertex:vertexs){
           // System.out.println(vertex.getId());
            deleteVertex(requestParameter,vertex.getId());
        }
    }
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
        List<String > propKeys=new ArrayList<>();
        propKeys.add("name");
        propKeys.add("root");
        propKeys.add("relation");
        legacyIndexService.deleteFullTextIndex(id,propKeys,"vertex");
        //5.删除节点
        bankLawService.deleteVertex(label,vertex);
    }
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
            _Vertex associationVertex = null;
            associationVertex = new _Vertex(allProperties.get("type").toString(),allProperties.get("name").toString(),allProperties.get("identity").toString(),allProperties.get("root").toString());
            associationVertex.setId(assocNode.getId());
            _Edge edge=null;
            if("start".equals(allProperties.get("type").toString())) {
                edge = new _Edge(value.get("relation").toString(), associationVertex, newVertex, value.get("root").toString());
            }else{
                edge = new _Edge(value.get("relation").toString(),newVertex, associationVertex, value.get("root").toString());
            }
            long edgeid = bankLawService.addEdge(label,edge);
            List<String > propKeys=new ArrayList<>();
            propKeys.add("name");
            propKeys.add("root");
            propKeys.add("relation");
            legacyIndexService.createFullTextIndex(edgeid,propKeys,"edge");
        }

        return l;
    }
    @Transactional
    public JSONObject indexMatchingQuery(String keyword){//索引匹配查询
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
                merge=buildReresult.mergeResult(merge,resultArray.getJSONObject(i));
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
        return jsonObject;
    }

    @Transactional
    public String smartQA( RequestParameter requestParameter,String methodName,String question)  {//智能问答
        System.out.println("智能问答提出问题：\t"+question);

        StandardTokenizer.SEGMENT.enableAllNamedEntityRecognize(false);
        List<Term> termList = StandardTokenizer.segment(question);
        for(int i=0;i<termList.size();i++){
            Term term=termList.get(i);
            if(i<termList.size()-1){
                Term nextTerm=termList.get(i+1);
                if("a".equals(term.nature.name())&&("vn".equals(nextTerm.nature.name())||"n".equals(nextTerm.nature.name()))){
                    term.nature=nextTerm.nature;
                    term.word+=nextTerm.word;
                    nextTerm.nature= term.nature;
                    nextTerm.word=term.word;
                    nextTerm.offset=term.offset;
                }
            }

        }
        CommonTool.removeDuplicateWithOrder(termList);
        MyComparetor mc = new MyComparetor("score");
        List<Map<String, Object>> maps= new ArrayList();
        for(Term term:termList) {
            Map<String, Object> node = questionPaserService.getNode(term.word);
            if(node!=null) {
                maps.add(node);
            }
        }

        String result= null;
        if(maps.size()==2){
            result = questionPaserService.traversePathBynode(requestParameter,maps);
        }
        else if(maps.size()==0){
            if("askOfWeChat".equals(methodName)){
                JSONObject obj=new JSONObject();

                JSONArray data=new JSONArray();
                data.put(questionPaserService.requestTuring(question));
                try {
                    obj.put("data",data);
                    obj.put("type","Turing");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return obj.toString();
            }
            return questionPaserService.requestTuring(question);
        }else if(maps.size()>2){
            float max=0;
            float maxScore=0;
            Map<String, Object> vertexNode=null;
            float second=0;
            float secScore=0;
            Map<String, Object> edgeNode=null;
            Levenshtein lt=new Levenshtein();
            //对候选边/节点进行筛选，分别挑选最高分数的node作为对应类型的代表
            for(Map<String, Object> node:maps){
                if(node!=null) {
                    String name = null;
                    float diffLocation=0;
                    if (node.containsKey("relation")) { //边
                        name = (String) node.get("relation");
                         diffLocation = lt.getSimilarityRatio(name, question);
                        if (diffLocation > second) {
                            second = diffLocation ;
                            edgeNode = node;
                            maxScore = (float) node.get("score");
                        }else if(diffLocation == second){
                        	// 当前词和之前的词与问句具有相同的编辑距离相似度，则通过索引分数对比
                            if(maxScore< (float) node.get("score")){
                                edgeNode = node;
                                maxScore = (float) node.get("score");
                            }
                        }
                    } else {//点
                        name = (String) node.get("name");
                         diffLocation = lt.getSimilarityRatio(name, question);
                        if (diffLocation >max) {
                            max = diffLocation;
                            vertexNode  = node;
                            secScore = (float) node.get("score");
                        }else if(diffLocation ==max){
                            if(secScore < (float) node.get("score")){
                                vertexNode = node;
                                secScore = (float) node.get("score");
                            }
                        }
                    }
                    //记录检索到的每个索引与问句的相似度分数
                    node.put("questSimilar", diffLocation);
                }
            }
            System.out.println("key:"+vertexNode+"\t"+edgeNode);
            if(vertexNode!=null&&edgeNode!=null) {
                List<Map<String, Object>> maps2 = new ArrayList();
                maps2.add(vertexNode);
                maps2.add(edgeNode);
                result = questionPaserService.traversePathBynode(requestParameter,maps2);
            }else if(vertexNode!=null&&edgeNode==null) {
                maps.remove(vertexNode);
                //查找分数第二高的索引
                Map<String, Object> vertexNode2=questionPaserService.getCloestMaps(maps);
                maps.add(vertexNode);
                List<Map<String, Object>> maps2 = new ArrayList();
                maps2.add(vertexNode);
                maps2.add(vertexNode2);
                result = questionPaserService.traversePathBynode(requestParameter,maps2);
            }else if(edgeNode!=null&&vertexNode==null){
                maps.remove(edgeNode);
                Map<String, Object> edgeNode2=questionPaserService.getCloestMaps(maps);
                maps.add(vertexNode);
                List<Map<String, Object>> maps2 = new ArrayList();
                maps2.add(edgeNode);
                maps2.add(edgeNode2);
                result = questionPaserService.traversePathBynode(requestParameter,maps2);
            }else{
                result = "learning";
            }
        }

        if(result==null||"learning".equals(result)){
            mc = new MyComparetor("questSimilar");
            Collections.sort(maps,mc);
            Collections.reverse(maps);
            String str = null;
            try {
            	//降序后，第一个node就是分数最大的点
                Map<String, Object> maxNode=null;
                for(Map<String, Object> node:maps){
                    if("node".equals(node.get("type"))){
                        maxNode=node;
                        break;
                    }
                }
                if(maxNode!=null) {
                    str = questionPaserService.findDefine(question, maxNode);
                }
            } catch (JSONException  e) {
                e.printStackTrace();
            }
            if(str==null||"learning".equals(str)||"".equals(str)) {
                if("askOfWeChat".equals(methodName)){
                    JSONObject obj=new JSONObject();
                    JSONArray data=new JSONArray();
                    data.put(questionPaserService.requestTuring(question));
                    try {
                        obj.put("data",data);
                        obj.put("type","Turing");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return obj.toString();
                }
                return questionPaserService.requestTuring(question);
            }else{
                result= str;
            }
        }
        if("askOfWeChat".equals(methodName)){
            boolean flag=false;//判断是否是之前获取案例json
            JSONObject obj=null;
            try{
                 obj=new JSONObject(result);
            }catch (Exception e){
                flag=true;
            }
            if(flag){
                 obj = new JSONObject();

                JSONArray data = new JSONArray();
                data.put(result);
                try {
                    obj.put("data", data);
                    obj.put("type", "Graph");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return obj.toString();
        }
        return result;
    }

    @Transactional
    public long addEgde(RequestParameter requestParameter,long fromId,long toid,String relation){//插入关系
        //1.预处理
        //2.建立关系
        String label = requestParameter.label;
        Vertex vertex1= bankLawService.checkVertexById(label,fromId);
        Vertex vertex2 = bankLawService.checkVertexById(label,toid);
//        _Edge edge=new _Edge("领导",vertex1,vertex2,"奇点创智");
        Edge edge=new _Edge(relation,vertex1,vertex2,vertex2.getRoot());
        //3.插入关系
        long id = bankLawService.addEdge(label,edge);
        //4.关系建立索引
        List<String > propKeys=new ArrayList<>();
        propKeys.add("name");
        propKeys.add("root");
        propKeys.add("relation");
        legacyIndexService.createFullTextIndex(id,propKeys,"edge");
        return id;

    }
    @Transactional
    public long addEgde(RequestParameter requestParameter,Vertex from,Vertex to,String relation){//插入关系
        //1.预处理
        //2.建立关系
        String label = requestParameter.label;
        Edge edge=new _Edge(relation,from,to,from.getRoot());
        //3.插入关系
        long id = bankLawService.addEdge(label,edge);
        //4.关系建立索引
        List<String > propKeys=new ArrayList<>();
        propKeys.add("name");
        propKeys.add("root");
        propKeys.add("relation");
        legacyIndexService.createFullTextIndex(id,propKeys,"edge");
        return id;
    }
    @Transactional
    public Edge deleteEgde(RequestParameter requestParameter,Long id){
        //1.预处理
        //2.建立关系
        String label = requestParameter.label;

        Edge edge=bankLawService.checkEdgeById(label,id);
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
    public long changeEgde(RequestParameter requestParameter,Long id,Long fromId,Long toId,JSONObject newEgdeInfo){
        String label = requestParameter.label;

        deleteEgde(requestParameter,id);
        long newId=0l;
        try {
            String relation=newEgdeInfo.getString("relation");
            Vertex from = bankLawService.checkVertexById(label,fromId);
            Vertex to = bankLawService.checkVertexById(label,toId);
            newId=addEgde(requestParameter,from,to,relation);
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
            newId=addEgde(requestParameter,from,to,relation);
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
