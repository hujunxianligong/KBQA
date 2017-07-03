package com.qdcz.tools;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.ogm.json.JSONArray;
import org.neo4j.ogm.json.JSONException;
import org.neo4j.ogm.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hadoop on 17-6-23.
 * Result set operations entity
 */
public class BuildReresult {
    //对结果集去重组合
    public JSONObject cleanRestult(JSONObject merge){
        JSONObject result=new JSONObject();
        try {
            if(merge.has("nodes")){
                JSONArray jsonArray = reDuplicatesArray(merge.getJSONArray("nodes"));
                result.put("nodes",jsonArray);
                if(jsonArray.getJSONObject(0).has("root")) {
                    result.put("root", jsonArray.getJSONObject(0).getString("root"));
                }
            }
            if(merge.has("edges")) {
                JSONArray jsonArray1 = reDuplicatesArray(merge.getJSONArray("edges"));
                result.put("edges", jsonArray1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
    //获取所需结果集
    public JSONObject graphResult(Traverser traverser ){
        JSONArray nodesJarry=new JSONArray();
        ResourceIterable<Node> nodes = traverser.nodes();
        for(Node node:nodes){
            JSONObject jsonObject = new JSONObject(node.getAllProperties());
            try {
                jsonObject.put("id",node.getId()) ;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            nodesJarry.put(jsonObject);
       //     System.out.println(jsonObject);
        }
        JSONArray edgesJarry=new JSONArray();
        ResourceIterable<Relationship> relationships = traverser.relationships();
        for(Relationship relationship:relationships){
            JSONObject jsonObject = new JSONObject(relationship.getAllProperties());
            try {
                jsonObject.put("id",relationship.getId()) ;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            edgesJarry.put(jsonObject);
        }
        JSONObject result =new JSONObject();
        try {
            result.put("nodes",nodesJarry);
            result.put("edges",edgesJarry);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    //两个结果集合并
    public JSONObject MergeResult(JSONObject obj1,JSONObject obj2){
        JSONObject jsonObject = new JSONObject();
        try {
            if(!obj1.has("nodes")){
                obj1.put("nodes",new JSONArray());
            }
            if(!obj1.has("edges")){
                obj1.put("edges",new JSONArray());
            }
            jsonObject.put("nodes", mergeArray( obj1.getJSONArray("nodes"),obj2.getJSONArray("nodes")));
            jsonObject.put("edges", mergeArray( obj1.getJSONArray("edges"),obj2.getJSONArray("edges")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
    //合并两个jsonarray
    public JSONArray mergeArray(JSONArray a1,JSONArray a2){
        for(int i=0;i<a2.length();i++){
            try {
                a1.put(a2.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return a1;
    }
    //对array内数据进行简单去重
    public  JSONArray reDuplicatesArray(JSONArray jsonArray){
        Map maps=new HashMap<Integer,JSONObject>();
        for(int i=0;i<jsonArray.length();i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if(!maps.containsKey(jsonObject.toString().hashCode())){
                    maps.put(jsonObject.toString().hashCode(),jsonObject);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        JSONArray results=new JSONArray();
        for (Object value : maps.values()) {
          //  System.out.println("Value = " + value);
            results.put(value);
        }
        return  results;
    }
}
