package com.qdcz.chat.service;


import com.qdcz.entity.Edge;
import com.qdcz.entity.Vertex;

import com.qdcz.graph.interfaces.IGraphBuzi;
import com.qdcz.graph.tools.ResultBuilder;
import com.qdcz.common.CommonTool;
import com.qdcz.common.Levenshtein;
import com.qdcz.common.MyComparetorSJ;

import com.qdcz.index.interfaces.IIndexService;
import com.qdcz.service.bean.RequestParameter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.neo4j.driver.v1.types.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * Created by hadoop on 17-7-13.
 * 对知识库问题的分析获取图谱中的准确信息
 */
@Service
public class QuestionPaserService
{

    @Autowired
    @Qualifier("elasearchService")
    private IIndexService elasearchBuzi;
    @Autowired
    @Qualifier("neo4jCypherService")
    private IGraphBuzi graphBuzi;



    private  JSONObject neetNode(JSONObject node,float maxScore,String table,String question){
        String type="node";
        Levenshtein lt=new Levenshtein();

        Map<String, JSONObject> stringJSONObjectMap = elasearchBuzi.queryByName(table, question);

        List<Map.Entry<String, JSONObject>> maps = new ArrayList<Map.Entry<String, JSONObject>>(stringJSONObjectMap.entrySet());
        MyComparetorSJ mc = new MyComparetorSJ("score");
        Collections.sort(maps,mc);
        Collections.reverse(maps);
        for(Map.Entry<String, JSONObject> map:maps){
            JSONObject value = map.getValue();
            if(value.has("to")){
                type="edge";
            }
            String nodeName = value.getString("name");
            float similarityRatio1 = lt.getSimilarityRatio(nodeName, question);
            value.put("questSimilar",similarityRatio1);
            float score = (float) value.getDouble("score");//会出错
            if(similarityRatio1==1f){
                maxScore = score;
                node = value;
                continue;
            }
            if(maxScore<score){
                maxScore = score;
                node = value;
            }else if(node !=null&&maxScore==score){
                float similarityRatio = (float) node.getDouble("questSimilar");
                if(similarityRatio1>similarityRatio){
                    node = value;
                }
            }
        }
        if(node!=null){
            node.put("typeOf",type);
        }
        return node;
    }
    public  Map<String, Object> getNode(RequestParameter requestParameter,String question){
        float maxScore = 0;
       JSONObject node =null;


        node = neetNode(node, maxScore, requestParameter.label, question);
        node = neetNode(node, maxScore, requestParameter.relationship.get(0), question);
        Map<String, Object> stringObjectMap =null;
        if(node!=null)
         stringObjectMap = CommonTool.toMap(node);

        return stringObjectMap;
    }
    public Map<String, Object> getCloestMaps(List<Map<String, Object>> maps){
        Map<String, Object> result=null;
        float max=0;
        for(Map<String, Object> node:maps){
            if(node!=null) {
                String name = null;
                float   diffLocation= (float) node.get("questSimilar");
                if (diffLocation > max) {
                    max = diffLocation ;
                    result =node;
                }
            }
        }
        return  result;
    }


    public String findDefine(String question,Map<String, Object> map) {
    	//建议改成配置文件形式，可写成一条条规则，不要硬编码
        String[] defineMatchs= new String[]{"是什么","是怎么样","是啥","什么叫","如何理解","什么是","什么意思", "定义", "概念", "含义","何谓","何为", "是指","指什么","是谁","介绍","简介","解释","描述"};
        ResultBuilder resultBuilder = new ResultBuilder();
        boolean flag=false;
        if(map.containsKey("regex")){
            flag=true;
        }
        else if(map.get("name").equals(question)){
            flag=true;
        }else {
            for (String def : defineMatchs) {
                if (question.contains(def)) {
                    flag = true;
                }
            }
        }
        if(flag){
            StringBuffer sb=new StringBuffer();
            JSONArray resultArray=new JSONArray();
            String name = null;
            //此处判断是否必要？
//            if (map.containsKey("relation")) {//边
//                name = (String) map.get("relation");
//                _Edge edge = bankLawService.checkEdgeById((Long) map.get("id"));
//                JSONObject graphById = transactionService.getGraphById(edge.getFrom_id(), 1);
//                resultArray.put(graphById);
//                JSONObject graphById1 = transactionService.getGraphById(edge.getTo_id(), 1);
//                resultArray.put(graphById1);
//            }else
            {//点
                name = (String) map.get("name");
                JSONObject object = null;
//                JSONObject object = transactionService.getGraphById((Long) map.get("id"), 1);
                resultArray.put(object);
            }
            JSONObject merge=new JSONObject();
            //5.组织返回结果
            for(int i=0;i<resultArray.length();i++){
                    merge= resultBuilder.mergeResult(merge,resultArray.getJSONObject(i));

            }
            JSONObject result= resultBuilder.cleanRestult(merge);
            String rootName = result.getString("root");

            JSONArray edges = result.getJSONArray("edges");
            Map<String,Vector<String>> maps=new HashMap();
            for(int i=0;i<edges.length();i++){
                JSONObject edge=edges.getJSONObject(i);
                String relation = edge.getString("relation");
                boolean hasMatchsWord=false;
                for(String def:defineMatchs){
                    if(relation.contains(def)){
                        hasMatchsWord =true;
                        break;
                    }
                }
                if(hasMatchsWord){
                    String from = edge.getString("from");
                    String from_name=null;
                    String to = edge.getString("to");
                    String to_name=null;
                    JSONArray nodes = result.getJSONArray("nodes");
                    for(int m=0;m<nodes.length();m++){
                        JSONObject node=nodes.getJSONObject(m);
                        String id = node.getString("id");
                        if(id.equals(from)){
                            from_name=node.getString("name");
                        }else  if(id.equals(to)){
                            to_name=node.getString("name");
                        }
                        if(from_name!=null&&to_name!=null){
                            break;
                        }
                    }
                    String key=null;
                    if("杂类".equals(rootName)){
                        key=from_name+"的"+relation+"为";
                    }else{
                        key="在"+rootName+"中，"+from_name+"的"+relation+"为";
                    }

                    String value=to_name;
                    if(maps.containsKey(key)){
                        Vector<String> strs=maps.get(key);
                        strs.add(value);
                    }else{
                        Vector<String> strs=new Vector<>();
                        strs.add(value);
                        maps.put(key,strs);
                    }
                }
            }
            for (Map.Entry<String, Vector<String>> entry : maps.entrySet()){
                String key=entry.getKey();
                String value="";
                Vector<String> values = entry.getValue();
                for(String str:values){
                    value+=str+"、";
                }
                if(!"".equals(value)) {
                    value = value.substring(0, value.length() - 1) + "。";
                    if((value.startsWith("是")||value.startsWith("指"))&&key.endsWith("为")){
                        key=key.substring(0,key.length()-1);
                    }
                    sb.append(key+value);
                }
            }


            return sb.toString();
        }
        return "learning";
    }
    public String traversePathBynode(RequestParameter requestParameter, List<Map<String, Object>> maps){
        if(maps.size()==2){
            Map<String, Object> vertex1=null;
            Map<String, Object> vertex2=null;
            Map<String, Object> edge1=null;
            Map<String, Object> edge2=null;
            for(Map<String, Object> node:maps){
                String  type = (String)node.get("typeOf");
                if("node".equals(type)){
                    if(vertex1!=null){
                        vertex2=node;
                    }else{
                        vertex1=node;
                    }
                }else {
                    if(edge1!=null){
                        edge2=node;
                    }else{
                        edge1=node;
                    }
                }
            }
            String result =null;
            if(vertex2==null&&edge2==null){
                result =  getByNodeAndEdgeName(requestParameter,vertex1,edge1);
            }else if(vertex2==null&&vertex1==null){
                result = getByEdgeAndEdgeName(requestParameter,edge1,edge2);
            }else if(edge2==null&&edge1==null){
                result = getByNodeAndNodeName(requestParameter,vertex1,vertex2,false);
            }
            return result;
        }
        return "learning";
    }

    private String getByEdgeAndEdgeName(RequestParameter requestParameter,Map<String, Object> edge1,Map<String, Object> edge2){
        Set<Path>  unDealPaths=new HashSet<>();
        Edge fromEdge=new Edge();
        CommonTool.transMap2Bean(edge1,fromEdge);
        Edge toEdge=new Edge();
        CommonTool.transMap2Bean(edge2,toEdge);
        Map<String, Vertex> startVertexMap = graphBuzi.checkVertexByEdgeId(Long.parseLong(fromEdge.getId()));
        Map<String, Vertex> endVertexMap = graphBuzi.checkVertexByEdgeId(Long.parseLong(toEdge.getId()));
        if(startVertexMap.containsKey("start")&&endVertexMap.containsKey("end")){
            Vertex startVertex = startVertexMap.get("start");
            Vertex endVertex = endVertexMap.get("end");
            Map<String, JSONObject> startMaps = elasearchBuzi.queryByName(requestParameter.label, startVertex.getName());
            Map<String, JSONObject> endMaps = elasearchBuzi.queryByName(requestParameter.label, endVertex.getName());
            ArrayList<Map.Entry<String, JSONObject>> startMapList = new ArrayList<>(startMaps.entrySet());
            ArrayList<Map.Entry<String, JSONObject>> endMapList = new ArrayList<>(endMaps.entrySet());
            for(Map.Entry<String, JSONObject> startMap:startMapList){
                JSONObject value = startMap.getValue();
                for(Map.Entry<String, JSONObject> endMap:endMapList){
                    JSONObject value1 = endMap.getValue();
                    long startId =Long.parseLong(value.getString("id"));
                    long endId = Long.parseLong(value1.getString("id"));
                    Path segments = graphBuzi.dfExection(startId, endId, 5);
                    if(segments!=null) {
                        unDealPaths.add(segments);
                    }
                }
            }
            endMapList.clear();
            startMapList.clear();
        }
        Map<Object,Object> conditions= new HashMap<>();
        conditions.put("edgeDouble",fromEdge);
        conditions.put("edge",toEdge);
        StringBuffer sb=new StringBuffer();
        Set<Path> paths = parsePaths(conditions, unDealPaths);
        showPaths(sb,paths);
        if("".equals(sb.toString())){
            return "learning";
        }else{
            return sb.toString();
        }
    }
    private String getByNodeAndNodeName(RequestParameter requestParameter,Map<String, Object> vertex1 ,Map<String, Object> vertex2,boolean exchange){
        Set<Path>  unDealPaths=new HashSet<>();
        Vertex fromVertex=new Vertex();
        CommonTool.transMap2Bean(vertex1,fromVertex);
        Vertex toVertex=new Vertex();
        CommonTool.transMap2Bean(vertex1,toVertex);
        Map<String, JSONObject> startMaps = elasearchBuzi.queryByName(requestParameter.label, fromVertex.getName());
        Map<String, JSONObject> endMaps = elasearchBuzi.queryByName(requestParameter.label, toVertex.getName());
        ArrayList<Map.Entry<String, JSONObject>> startMapList= new ArrayList<>(startMaps.entrySet());
        ArrayList<Map.Entry<String, JSONObject>> endMapList= new ArrayList<>(endMaps.entrySet());
        for(Map.Entry<String, JSONObject> startMap:startMapList){
            JSONObject value = startMap.getValue();
            for(Map.Entry<String, JSONObject> endMap:endMapList){
                JSONObject value1 = endMap.getValue();
                long startId =Long.parseLong(value.getString("id"));
                long endId = Long.parseLong(value1.getString("id"));
                Path segments = graphBuzi.dfExection(startId, endId, 5);
                if(segments!=null) {
                    unDealPaths.add(segments);
                }
            }
        }
        Map<Object,Object> conditions= new HashMap<>();
        conditions.put("startVertex",fromVertex);
        conditions.put("endVertex",toVertex);
        StringBuffer sb=new StringBuffer();
        Set<Path> paths = parsePaths(conditions, unDealPaths);
        showPaths(sb,paths);
        if("".equals(sb.toString())&&!exchange){
            if(exchange) {
                return "learning";
            }
            else{
                return  getByNodeAndNodeName(requestParameter,vertex2,vertex1,true);
            }
        }else{
            return sb.toString();
        }
    }
    private String getByNodeAndEdgeName(RequestParameter requestParameter,Map<String, Object> vertexMap,Map<String, Object> edgeMap)  {
        Vertex vertex=new Vertex();
        CommonTool.transMap2Bean(vertexMap,vertex);
        Edge edge= new Edge();
        CommonTool.transMap2Bean(edgeMap,edge);
        Set<Path>  unDealPaths=new HashSet<>();
        ArrayList<Map.Entry<String, JSONObject>> startMapList;
        ArrayList<Map.Entry<String, JSONObject>> endMapList;
        if(edge!=null) {
            elasearchBuzi.queryByName(requestParameter.relationship.get(0),edge.getName());
            Map<String, Vertex> stringVertexMap = graphBuzi.checkVertexByEdgeId(Long.parseLong(edge.getId()));
            if(stringVertexMap.containsKey("end")){
                Vertex endVertex = stringVertexMap.get("end");
                Map<String, JSONObject> startMaps = elasearchBuzi.queryByName(requestParameter.label, vertex.getName());
                Map<String, JSONObject> endMaps = elasearchBuzi.queryByName(requestParameter.label, endVertex.getName());
                startMapList = new ArrayList<>(startMaps.entrySet());
                endMapList = new ArrayList<>(endMaps.entrySet());
                for(Map.Entry<String, JSONObject> startMap:startMapList){
                    JSONObject value = startMap.getValue();
                    for(Map.Entry<String, JSONObject> endMap:endMapList){
                        JSONObject value1 = endMap.getValue();
                        long startId =Long.parseLong(value.getString("id"));
                        long endId = Long.parseLong(value1.getString("id"));
                        Path segments = graphBuzi.dfExection(startId, endId, 5);
                        if(segments!=null) {
                            unDealPaths.add(segments);
                        }
                    }
                }
                endMapList.clear();
                startMapList.clear();
            }
        }
        Map<Object,Object> conditions= new HashMap<>();
        conditions.put("startVertex",vertex);
        conditions.put("edge",edge);
        StringBuffer sb=new StringBuffer();
        Set<Path> paths = parsePaths(conditions, unDealPaths);
        showPaths(sb,paths);
        if("".equals(sb.toString())){
            return "learning";
        }else{
            return sb.toString();
        }
    }
    private  void  showPaths(StringBuffer sb,Set<Path> parsePaths){
        for(Path path:parsePaths){
            org.neo4j.driver.v1.types.Node start = path.start();

            org.neo4j.driver.v1.types.Node end = path.end();

            List<org.neo4j.driver.v1.types.Relationship> relationships = (List<org.neo4j.driver.v1.types.Relationship>) path.relationships();
            org.neo4j.driver.v1.types.Relationship relationship = relationships.get(relationships.size() - 1);
            System.out.println();
            String result=start.get("name").asString()+"的"+relationship.get("name").asString()+"为"+end.get("name").asString()+"\n";
            sb.append(result);

        }
//        try{
//            JSONObject resultJSon=new JSONObject();
//            for (Map.Entry<String, Vector<String>> entry : maps.entrySet()){
//                JSONArray jsonArray=new JSONArray();
//                String result="";
//                String key = entry.getKey().replace("--","的");
//                Vector<String> value = entry.getValue();
//                result += key+"为";
//                for(String str:value){
//
//                    JSONObject object = new JSONObject(str);
//                    result="";
//                    jsonArray.put(object.toString());
//
//                }
//                if(!"".equals(result)) {
//                    result = result.substring(0, result.length() - 1) + "。";
//                    sb.append(result);
//                }
//                else if(jsonArray.length()>0){
//                    resultJSon.put("title",key);
//                    resultJSon.put("type","Law");
//                    resultJSon.put("data",jsonArray);
//                    sb.append(resultJSon);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return ;
    }
    private   Set<Path> parsePaths( Map<Object,Object> conditions,Set<Path>  Paths){
        Set<Path> tmpPaths =new HashSet<>();
        int mindepth =Integer.MAX_VALUE;
        for(Path path:Paths){
            if(path!=null){
                boolean flag = true;
                boolean[] conditionboolean=new boolean[conditions.size()];
                if(conditions.size()>0){
                    int i=0;
                    for (Map.Entry<Object, Object> entry : conditions.entrySet()){
                        if("startVertex".equals(entry.getKey().toString())||"endVertex".equals(entry.getKey().toString())){
                            Vertex vertex = (Vertex) entry.getValue();
                            Iterable<org.neo4j.driver.v1.types.Node> nodes = path.nodes();
                            for(org.neo4j.driver.v1.types.Node no:nodes){
                                if(no.get("name").asString().equals(vertex.getName())){
                                    conditionboolean[i]=true;
                                    break;
                                }else {
                                    conditionboolean[i]=false;
                                }
                            }
                            i++;
                        }
                        else if("edge".equals(entry.getKey().toString())||"edgeDouble".equals(entry.getKey().toString())){
                            Edge edge = (Edge) entry.getValue();
                            Iterable<org.neo4j.driver.v1.types.Relationship> relationships = path.relationships();
                            for(org.neo4j.driver.v1.types.Relationship re:relationships){
                                if(re.get("name").asString().equals(edge.getName())){
                                    conditionboolean[i]=true;
                                    break;
                                }else {
                                    conditionboolean[i]=false;
                                }
                            }
                            i++;
                        }

                    }
                    for(int x=0;x<conditionboolean.length;x++){
                        if(conditionboolean[x]==false){
                            flag=false;break;
                        }
                    }
                }
                if(flag){
                    tmpPaths.add(path);
                }
            }
        }
        for(Path path:tmpPaths) {//找到路径中包含边测最小最小的层数
            int pathDepth = path.length();//获取当前路径深度
            if (mindepth > pathDepth) {
                mindepth = pathDepth;
            }
        }
        Set<Path> parsePaths =new HashSet<>();
        for(Path path:tmpPaths) {//tmp中筛选出符合最小层数的路径
            int pathDepth = path.length();//获取当前路径深度
            if (mindepth == pathDepth) {
                parsePaths.add(path);
            }
        }
        return parsePaths;
    }
    public   String requestTuring(String question) {
        JSONObject request= new JSONObject();

            request.put("key","149c02a9f63a463f8b55f74b75d2d1c7");
            request.put("info",question);

        String respone=null;
        try {
            respone = CommonTool.query(request.toString(), "http://www.tuling123.com/openapi/api");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("本地没有成功获取 选择调用图灵 \t"+question);
        String result = turingDataParser(respone);
        return result;
    }


    public   String turingDataParser(String str ){
            JSONObject obj=new JSONObject( str);
            int code =obj.getInt("code");
            if(code==100000){
                return obj.getString("text");
            }else if(code==200000){
                return obj.getString("text")+" "+obj.getString("url");
            }else if(code==302000){
                JSONArray list = obj.getJSONArray("list");
                String result ="";
                result+=obj.getString("text")+"<br>";
                for(int i=0;i<list.length();i++){
                    JSONObject content=list.getJSONObject(i);
                    String s = content.getString("article") + "&nbsp;" + content.getString("source") + "&nbsp;" + content.getString("detailurl")+"<br>";
                    result+= s;
                }
                return result;
            }else if(code==308000){
                JSONArray list = obj.getJSONArray("list");
                String result ="";
                result+=obj.getString("text")+"<br>";
                for(int i=0;i<list.length();i++){
                    JSONObject content=list.getJSONObject(i);
                    String s = content.getString("name") + "&nbsp;" + content.getString("info") + "&nbsp;" + content.getString("detailurl")+"<br>";
                    result+= s;
                }
                return result;
            }else{
                return "还在学习中，请多多关照哦！^-^";
            }

    }
}
