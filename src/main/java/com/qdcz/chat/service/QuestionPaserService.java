package com.qdcz.chat.service;


import com.qdcz.common.ConceptRuler;
import com.qdcz.conf.LoadConfigListener;
import com.qdcz.entity.Edge;
import com.qdcz.entity.Vertex;

import com.qdcz.graph.interfaces.IGraphBuzi;
import com.qdcz.graph.neo4jcypher.service.Neo4jCYService;
import com.qdcz.graph.tools.ResultBuilder;
import com.qdcz.common.CommonTool;
import com.qdcz.chat.tools.Levenshtein;
import com.qdcz.chat.tools.MyComparetorSJ;

import com.qdcz.index.elsearch.service.ElasearchService;
import com.qdcz.index.interfaces.IIndexService;
import com.qdcz.chat.controller.RequestParameter;
import org.json.JSONArray;
import org.json.JSONException;
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

    public static void main(String[] args) throws Exception {
        LoadConfigListener loadConfigListener=new LoadConfigListener();
        loadConfigListener.setSource_dir("/dev/");
        loadConfigListener.contextInitialized(null);
        Vertex vertex=new Vertex();
        vertex.setRoot("起点");
        vertex.setName("牵头行");
        vertex.setType("挖掘部");
        vertex.setId("55");
        vertex.setContent("");
        vertex.setLabel("ytdk_label");
        RequestParameter requestParameter=new RequestParameter();
        requestParameter.label="ytdk_label";
        requestParameter.question="何为银团贷款";
        QuestionPaserService instance=  new QuestionPaserService();
        instance.graphBuzi = new Neo4jCYService();
        instance.elasearchBuzi = new ElasearchService();
        instance.findDefine(requestParameter);
    }

    /**
     * 分词获取搜索点
     * @param node
     * @param maxScore
     * @param table
     * @param question
     * @return
     */
    private  JSONObject neetNode(JSONObject node,float maxScore,String table,String question){
        String type="node";
        Levenshtein lt=new Levenshtein();

        Map<String, JSONObject> stringJSONObjectMap =  elasearchBuzi.queryByName(table,question,0,question.length()*3);

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


    /**
     * 组织问题搜索点
     * @param requestParameter
     * @param question
     * @return
     */
    public  Map<String, Object> getNode(RequestParameter requestParameter,String question){
        float maxScore = 0;
       JSONObject node =null;


        node = neetNode(node, maxScore, requestParameter.label, question);
        node = neetNode(node, maxScore, requestParameter.relationship.get(0), question);
        Map<String, Object> stringObjectMap =null;
        if(node!=null){
            node.put("label",requestParameter.label);
            node.put("rerelationShip",requestParameter.relationship.get(0));
            stringObjectMap = CommonTool.toMap(node);
        }


        return stringObjectMap;
    }

    /**
     * 匹配相关度最高的点
     * @param maps
     * @return
     */
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

    /*
    *正则匹配定义
     */
    public String findDefine(RequestParameter requestParameter) throws Exception {
    	//建议改成配置文件形式，可写成一条条规则，不要硬编码
        String[] defineMatchs= new String[]{ "定义", "概念", "含义","介绍","简介","解释","描述"};
        String s = ConceptRuler.RegexKey(requestParameter.question);//正则模式
        StringBuffer sb=new StringBuffer();
        if(s!=null) {
            Vertex vertex=new Vertex();
            vertex.setLabel(requestParameter.label);
            vertex.setName(s);
            List<Path> paths = graphBuzi.bfExtersion(vertex, 1);
            Map<String,Vector<String>> maps=new HashMap();
            if(paths.size()>0){
                ResultBuilder resultBuilder=new ResultBuilder();
                JSONObject object = resultBuilder.graphResult(paths);
                JSONArray edges = object.getJSONArray("edges");
                JSONArray nodes = object.getJSONArray("nodes");
                for(int i=0;i<edges.length();i++){
                    JSONObject edge=edges.getJSONObject(i);
                    String relation = edge.getString("name");
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
                        String rootName="杂类";
                        for(int m=0;m<nodes.length();m++){
                            JSONObject node=nodes.getJSONObject(m);
                            String id = node.getString("id");
                            if(id.equals(from)){
                                from_name=node.getString("name");
                            }else  if(id.equals(to)){
                                to_name=node.getString("name");
                            }
                            if(from_name!=null&&to_name!=null){
                                rootName=node.getString("root");
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
        }
        return sb.toString();
    }
    /*
    *实体中转分类站
     */
    public Set<Path> traversePathBynode(RequestParameter requestParameter, List<Map<String, Object>> maps){
        Set<Path> paths =null;
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

            if(vertex2==null&&edge2==null){
                paths=getByNodeAndEdgeName(requestParameter,vertex1,edge1);
            }else if(vertex2==null&&vertex1==null){
//                result = getByEdgeAndEdgeName(requestParameter,edge1,edge2);
                paths = null;
            }else if(edge2==null&&edge1==null){
                paths = getByNodeAndNodeName(requestParameter,vertex1,vertex2,false);
            }
        }
        return paths;
    }

    /**
     * 获取边与边的路径
     * @param requestParameter
     * @param edge1
     * @param edge2
     * @return
     */
    @Deprecated
    private  Set<Path> getByEdgeAndEdgeName(RequestParameter requestParameter,Map<String, Object> edge1,Map<String, Object> edge2){
        Set<Path>  unDealPaths=new HashSet<>();
        Edge fromEdge=new Edge();
        CommonTool.transMap2Bean(edge1,fromEdge);
        Edge toEdge=new Edge();
        CommonTool.transMap2Bean(edge2,toEdge);
        Map<String, JSONObject> edgeSilimarMaps1 = elasearchBuzi.queryByName(requestParameter.relationship.get(0), fromEdge.getName());
        Map<String, JSONObject> edgeSilimarMaps2 = elasearchBuzi.queryByName(requestParameter.relationship.get(0), toEdge.getName());
        ArrayList<Map.Entry<String, JSONObject>> entryArrayList1 = new ArrayList<>(edgeSilimarMaps1.entrySet());
        ArrayList<Map.Entry<String, JSONObject>> entryArrayList2 = new ArrayList<>(edgeSilimarMaps2.entrySet());
        for (Map.Entry<String, JSONObject> JSONObjectEntry : entryArrayList1) {
            JSONObject value1 = JSONObjectEntry.getValue();
            Map<String, Vertex> startVertexMap = graphBuzi.checkVertexByEdgeId(value1.getLong("id"));
            for (Map.Entry<String, JSONObject> JSONObjectEntry2 : entryArrayList2){
                JSONObject value2 = JSONObjectEntry2.getValue();
                Map<String, Vertex> endVertexMap = graphBuzi.checkVertexByEdgeId(value2.getLong("id"));
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
                            JSONObject valueto = endMap.getValue();
                            long startId =Long.parseLong(value.getString("id"));
                            long endId = Long.parseLong(valueto.getString("id"));
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
        }


        Map<Object,Object> conditions= new HashMap<>();
        conditions.put("edgeDouble",fromEdge);
        conditions.put("edge",toEdge);
        StringBuffer sb=new StringBuffer();
        Set<Path> paths = parsePaths(conditions, unDealPaths);
      //  showPaths(sb,paths);
        return paths;
    }
    /*
    *实体点点匹配所有路径
     */
    private Set<Path> getByNodeAndNodeName(RequestParameter requestParameter,Map<String, Object> vertex1 ,Map<String, Object> vertex2,boolean exchange){
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

        Set<Path> paths = parsePaths(conditions, unDealPaths);
//        showPaths(sb,paths);
        if(paths.size()==0&&!exchange){
            if(exchange) {
                return paths;
            } else{
                return  getByNodeAndNodeName(requestParameter,vertex2,vertex1,true);
            }
        }else{
            return paths;
        }
    }
    /*
    *实体点边匹配所有路径
     */
    private  Set<Path> getByNodeAndEdgeName(RequestParameter requestParameter,Map<String, Object> vertexMap,Map<String, Object> edgeMap)  {
        Vertex vertex=new Vertex();
        CommonTool.transMap2Bean(vertexMap,vertex);
        Edge edge= new Edge();
        CommonTool.transMap2Bean(edgeMap,edge);
        Set<Path>  unDealPaths=new HashSet<>();
        ArrayList<Map.Entry<String, JSONObject>> startMapList;
        if(edge!=null) {
            Map<String, JSONObject> startMaps = elasearchBuzi.queryByName(requestParameter.label, vertex.getName());//获取点名称索引相关集合
            Map<String, JSONObject> edgeSilimarMaps = elasearchBuzi.queryByName(requestParameter.relationship.get(0), edge.getName());
            ArrayList<Map.Entry<String, JSONObject>> entryArrayList = new ArrayList<>(edgeSilimarMaps.entrySet());
            for (Map.Entry<String, JSONObject> JSONObjectEntry : entryArrayList) {//边名称索引结果解析末点
                JSONObject value = JSONObjectEntry.getValue();
                Map<String, Vertex> stringVertexMap = graphBuzi.checkVertexByEdgeId(value.getLong("id"));
                if(stringVertexMap.containsKey("end")){
                    Vertex endVertex = stringVertexMap.get("end");
                    startMapList = new ArrayList<>(startMaps.entrySet());
                    for(Map.Entry<String, JSONObject> startMap:startMapList){
                        JSONObject value2 = startMap.getValue();
                        long startId =Long.parseLong(value2.getString("id"));
                        long endId = Long.parseLong(endVertex.getId());
                        Path segments = graphBuzi.dfExection(startId, endId, 5);
                        if(segments!=null) {
                            unDealPaths.add(segments);
                        }

                    }
                    startMapList.clear();
                }
            }
        }
        Map<Object,Object> conditions= new HashMap<>();
        conditions.put("startVertex",vertex);
        conditions.put("edge",edge);
        Set<Path> paths = parsePaths(conditions, unDealPaths);
//        showPaths(sb,paths);
        return paths;
    }
    /*
    *对路径进行展示前的解析
     */
    public   void  showPaths(StringBuffer sb,Set<Path> parsePaths){
        ResultBuilder resultBuilder=new ResultBuilder();
        Map<String,Vector<String>> resultPaths= resultBuilder.cleanRestult(parsePaths);
        try{
            JSONObject resultJSon=new JSONObject();
            for (Map.Entry<String, Vector<String>> entry : resultPaths.entrySet()){
                JSONArray jsonArray=new JSONArray();
                String result="";
                String key = entry.getKey().replace("--","的");
                Vector<String> value = entry.getValue();
                result += key+"为";
                for(String str:value){
                    try{
                        JSONObject object = new JSONObject(str);
                        result="";
                        jsonArray.put(object.toString());
                    }catch ( JSONException je){
//                        je.printStackTrace();
                        result+=str+"、";
                    }
                }
                if(!"".equals(result)) {
                    result = result.substring(0, result.length() - 1) + "。";
                    sb.append(result);
                }
                else if(jsonArray.length()>0){
                    resultJSon.put("title",key);
                    resultJSon.put("type","Law");
                    resultJSon.put("data",jsonArray);
                    sb.append(resultJSon);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ;
    }

    /*
    *深搜路径进行筛选
     */
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

    /*
    *调用图灵接口
     */
    public String requestTuring(String question) {
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

    /*
    *图灵返回数据解析
     */
    private String turingDataParser(String str ){
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
