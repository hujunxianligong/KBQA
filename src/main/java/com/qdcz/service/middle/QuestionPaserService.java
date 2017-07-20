package com.qdcz.service.middle;

import com.qdcz.config.MongoConfigure;
import com.qdcz.neo4jkernel.LegacyIndexService;
import com.qdcz.neo4jkernel.LoopDataService;
import com.qdcz.sdn.entity._Vertex;
import com.qdcz.service.bottom.BankLawService;
import com.qdcz.service.mongo.BaseMongoDAL;
import com.qdcz.service.mongo.MyMongo;
import com.qdcz.tools.CommonTool;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.ogm.json.JSONArray;
import org.neo4j.ogm.json.JSONException;
import org.neo4j.ogm.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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
    private GraphDatabaseService graphDatabaseService;
    @Autowired
    private LegacyIndexService legacyIndexService;
    @Autowired
    private LoopDataService loopDataService;
    @Autowired
    private BankLawService bankLawService;
    public  Map<String, Object> getNode(String question){
        String[] fields={"name"};
        float maxScore = 0;
        Map<String, Object> node =null;
        String type="node";
        List<Map<String, Object>> maps = legacyIndexService.selectByFullTextIndex(fields, question,"vertex");
        for(Map<String, Object> map:maps){
            float score = (float) map.get("score");//会出错
            if(maxScore<score){
                maxScore = score;
                node = map;
            }
        }
        fields= new String[]{"relation"};
        maps = legacyIndexService.selectByFullTextIndex(fields, question,"edge");
        for(Map<String, Object> map:maps){
            float score = 0;
            try {
                score = Float.parseFloat( map.get("score").toString());//会出错
            }catch (Exception e){
                System.out.println();
            }
            if(maxScore<score){
                type="edge";
                maxScore = score;
                node = map;
            }
        }

        return node;
    }
    public Map<String, Object> getCloestMaps(List<Map<String, Object>> maps){
        Map<String, Object> result=null;
        float max=0;
        for(Map<String, Object> node:maps){
            if(node!=null) {
                String name = null;
                float   diffLocation= (float) node.get("score");
                if (diffLocation > max) {
                    max = diffLocation ;
                    result =node;
                }
            }
        }
        return  result;
    }
    public String traversePathBynode(List<Map<String, Object>> maps){
        if(maps.size()==2){
            String nodeName1="";
            String nodeName2="";
            String edgeName1="";
            String edgeName2="";
            for(Map<String, Object> node:maps){
                if(node.containsKey("relation")){
                    if(!"".equals(edgeName1)){
                        edgeName2= (String) node.get("relation");
                    }else{
                        edgeName1= (String) node.get("relation");
                    }
                }else if(node.containsKey("name")){
                    if(!"".equals(nodeName1)){
                        nodeName2= (String) node.get("name");
                    }else{
                        nodeName1= (String) node.get("name");
                    }
                }
            }
            String result =null;
            if("".equals(nodeName2)&&"".equals(edgeName2)){
                result =  getByNodeAndEdgeName(nodeName1,edgeName1);
            }else if("".equals(nodeName2)&&"".equals(nodeName1)){
                result = getByEdgeAndEdgeName(edgeName1,edgeName2);
            }else if("".equals(edgeName2)&&"".equals(edgeName1)){
                result = getByNodeAndNodeName(nodeName1,nodeName2);
            }
            return result;
        }
        return "learning";
    }

    private String getByEdgeAndEdgeName(String edgeName1,String edgeName2){
        String[] fields= new String[]{"relation"};
        List<Map<String, Object>> mapsEdge = legacyIndexService.selectByFullTextIndex(fields, edgeName1,"edge");
        List<Map<String, Object>> mapsEdge2 = legacyIndexService.selectByFullTextIndex(fields, edgeName2,"edge");
        Set<String> resultPaths= new HashSet<>();
        for(Map<String, Object> map:mapsEdge){
            System.out.println((Long) map.get("id"));
            Node nodeStart    =   null;
            try (   Transaction tx = graphDatabaseService.beginTx()) {
                Relationship r = graphDatabaseService.getRelationshipById((Long)map.get("id"));
                tx.acquireReadLock(r);
                nodeStart = r.getStartNode();
                tx.success();
            }
            if(nodeStart!=null) {
                for(Map<String, Object> map2:mapsEdge2){
                    Node nodeEnd    =   null;
                    try (   Transaction tx = graphDatabaseService.beginTx()) {
                        Relationship r = graphDatabaseService.getRelationshipById((Long)map2.get("id"));
                        tx.acquireReadLock(r);
                        nodeEnd = r.getEndNode();
                        tx.success();
                    }
                    Long startid = nodeStart.getId();
                    long endid = nodeEnd.getId();
                    Set<String> strings = loopDataService.loopDataByNodeLevel(startid, endid);
                    resultPaths.addAll(strings);
                }
            }
        }
        Map<String,String> conditions= new HashMap<>();
        conditions.put(edgeName1,"contain");
        conditions.put(edgeName1,"contain");
        StringBuffer sb=new StringBuffer();
        parsePaths(conditions,sb,resultPaths);
        if("".equals(sb.toString())){
            return "learning";
        }else{
            return sb.toString();
        }
    }
    private String getByNodeAndNodeName(String nodeName1,String nodeName2){
        List<_Vertex> verticesStart = bankLawService.checkVertexByName(nodeName1);
        List<_Vertex> verticesEnd = bankLawService.checkVertexByName(nodeName2);
        Set<String>  resultPaths= new HashSet<>();
        for(_Vertex vertexeE:verticesEnd){
            for(_Vertex vertexL:verticesStart){
                Long startid = vertexL.getId();
                long endid = vertexeE.getId();
                Set<String> strings = loopDataService.loopDataByNodeLevel(startid, endid);
                resultPaths.addAll(strings);
            }
        }
        Map<String,String> conditions= new HashMap<>();
        StringBuffer sb=new StringBuffer();
        parsePaths(conditions,sb,resultPaths);
        if("".equals(sb.toString())){
            return "learning";
        }else{
            return sb.toString();
        }
    }
    private String getByEdgeAndNodeName(String edgeName,String nodeName){
        List<_Vertex> vertices = bankLawService.checkVertexByName(nodeName);
        Set<String>  resultPaths= new HashSet<>();
        String[] fields= new String[]{"relation"};
        List<Map<String, Object>> mapsEdge = legacyIndexService.selectByFullTextIndex(fields, edgeName,"edge");
        for(Map<String, Object> map:mapsEdge){
            System.out.println((Long) map.get("id"));
            Node node    =   null;
            try (   Transaction tx = graphDatabaseService.beginTx()) {
                Relationship r = graphDatabaseService.getRelationshipById((Long)map.get("id"));
                tx.acquireReadLock(r);
                node = r.getStartNode();
                tx.success();
            }
            if(node!=null) {
                for(_Vertex vertexL:vertices){
                    Long startid = node.getId();
                    long endid = vertexL.getId();
                    Set<String> strings = loopDataService.loopDataByNodeLevel(startid, endid);
                    resultPaths.addAll(strings);
                }
            }
        }
        Map<String,String> conditions= new HashMap<>();
        StringBuffer sb=new StringBuffer();
        parsePaths(conditions,sb,resultPaths);
        if("".equals(sb.toString())){
            return "learning";
        }else{
            return sb.toString();
        }
    }
    private String getByNodeAndEdgeName(String nodeName,String edgeName)  {
//        String nodeName = object.getJSONObject("node").getString("name");
//        String edgeName = object.getJSONObject("edge").getString("name");
        //找点首ＩＤ
//        String[] fields={"root","name"};
//        JSONArray resultArray=new JSONArray();
//        List<Map<String, Object>> mapsNode = legacyIndexService.selectByFullTextIndex(fields, nodeName,"vertex");
        Set<String>  resultPaths= new HashSet<>();
        List<_Vertex> vertices = bankLawService.checkVertexByName(nodeName);
        //边索引
        String[] fields= new String[]{"relation"};
        List<Map<String, Object>> mapsEdge = legacyIndexService.selectByFullTextIndex(fields, edgeName,"edge");
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
                for(_Vertex vertexL:vertices){
                    Long startid = vertexL.getId();
                    long endid = node.getId();
                    Set<String> strings = loopDataService.loopDataByNodeLevel(startid, endid);
                    resultPaths.addAll(strings);
                }
            }
        }
        Map<String,String> conditions= new HashMap<>();
        conditions.put(edgeName,"contain");

        StringBuffer sb=new StringBuffer();
        parsePaths(conditions,sb,resultPaths);
        if("".equals(sb.toString())){
            return "learning";
        }else{
            return sb.toString();
        }
    }
    private  StringBuffer parsePaths( Map<String,String> conditions,StringBuffer sb,Set<String>  Paths){
        Set<String> parsePaths =new HashSet<>();
        for(String path:Paths){
            if(path.contains("--")&&!path.contains("<-")) {
                boolean flag = false;
                if(conditions.size()==0){
                    flag =true;
                }else{
                    for (Map.Entry<String, String> entry : conditions.entrySet()){
                        if("contain".equals(entry.getValue().toString())&&path.contains(entry.getKey().toString())){
                            flag =true;
                        }
                    }
                }
                if(flag){
                    String[] split = path.split("--");
                    String result = split[0] + "--" + split[split.length - 1];
                    parsePaths.add(result);
                }
            }
        }
        Map<String,Vector<String>> maps=new HashMap();
        for(String result:parsePaths){
            String[] split = result.split("->");
            String key=split[0];
            String value=split[1];
            if(maps.containsKey(key)){
                Vector<String> strs=maps.get(key);
                strs.add(value);
            }else{
                Vector<String> strs=new Vector<>();
                strs.add(value);
                maps.put(key,strs);
            }
        }
        System.out.println();

        //BaseMongoDAL mongo = new MyMongo(MongoConfigure.dbOnline,"law_details");

        try{
            JSONObject resultJSon=new JSONObject();
            for (Map.Entry<String, Vector<String>> entry : maps.entrySet()){
                JSONArray jsonArray=new JSONArray();
                String result="";
                String key = entry.getKey().replace("--","的");
                resultJSon.put("title",key);
                Vector<String> value = entry.getValue();
                result += key+"为";
                for(String str:value){
                    try{

                        JSONObject object = new JSONObject(str);
                        result="";
                        jsonArray.put(object);
          //              JSONObject oneDocument = mongo.getOneDocument(name);
          //              if(oneDocument!=null) {
          //                  String title = oneDocument.getString("title");
         //                   result += title + "、";
          //              }else{
          //                  result+=name+"、";
          //              }
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
                    resultJSon.put("data",jsonArray);
                    sb.append(resultJSon);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        finally {
//            if(mongo!=null){
//                mongo.close();
//            }
//        }
        return sb;
    }
    public   String requestTuring(String question) {
        JSONObject request= new JSONObject();
        try {
            request.put("key","149c02a9f63a463f8b55f74b75d2d1c7");
            request.put("info",question);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String respone=null;
        try {
            respone = CommonTool.query(request.toString(), "http://www.tuling123.com/openapi/api");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("本地没有成功获取 选择调用图灵 \t"+question);
        String result = turingDataParser(respone);
        return result;
    }
    public   String turingDataParser(String str ){
        try {
            JSONObject obj=new JSONObject( str);
            int code = Integer.parseInt(obj.getString("code"));
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "还在学习中，请多多关照哦！^-^";
    }
}
