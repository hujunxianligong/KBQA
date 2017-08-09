package com.qdcz.graph.tools;

import com.qdcz.common.CommonTool;
import com.qdcz.entity.Edge;
import com.qdcz.entity.Vertex;
import org.json.JSONArray;
import org.json.JSONObject;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Path;
import org.neo4j.driver.v1.types.Relationship;


import java.util.*;

/**
 * Created by hadoop on 17-6-23.
 * Result set operations entity
 */
public class ResultBuilder {
    //对结果集去重组合
    public JSONObject cleanRestult(JSONObject merge){
        JSONObject result=new JSONObject();

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

        return result;
    }
//    //获取所需结果集
    public JSONObject graphResult(List<Path> paths ){
        JSONArray nodesJarry=new JSONArray();
        JSONArray edgesJarry=new JSONArray();
        Set<String> nodeIds=new HashSet<>();
        Set<String> edgeIds=new HashSet<>();
        JSONObject centreNodeObj =null;
        JSONObject result =new JSONObject();
        if(paths!=null) {
            for (Path path : paths) {
                Iterable<Node> nodes = path.nodes();
                for (Node node : nodes) {
                    Map<String, Object> nodeInfo = node.asMap();
                    String label = node.labels().iterator().next();
                    Vertex newVertex = new Vertex();
                    CommonTool.transMap2Bean(nodeInfo, newVertex);
                    newVertex.setId(node.id() + "");
                    newVertex.setLabel(label);
                    if (!nodeIds.contains(newVertex.getGraphId())) {
                        JSONObject resultobj = newVertex.toJSON();
                        resultobj.put("id", newVertex.getId());
                        resultobj.put("label", newVertex.getLabel());
                        if (centreNodeObj == null) {
                            centreNodeObj = resultobj;
                        }
                        nodesJarry.put(resultobj);
                    }
                    nodeIds.add(node.id() + "");
                }
                Iterable<Relationship> relationships = path.relationships();
                for (Relationship relationship : relationships) {

                    Map<String, Object> edgeInfo = relationship.asMap();
                    Edge newEdge = new Edge();
                    CommonTool.transMap2Bean(edgeInfo, newEdge);
                    newEdge.setId(relationship.id() + "");
                    newEdge.setRelationShip(relationship.type());
                    if (!edgeIds.contains(newEdge.getGraphId())) {
                        JSONObject resultobj = newEdge.toJSON();
                        resultobj.put("id", newEdge.getId());
                        resultobj.put("relationship", newEdge.getRelationShip());
                        edgesJarry.put(resultobj);
                    }
                    edgeIds.add(relationship.id() + "");
                }
            }
            nodeIds.clear();
            edgeIds.clear();
            String center = "";
            if (centreNodeObj != null) {
                center = centreNodeObj.getString("id");
            } else {
                return result;
            }
            result.put("nodes", nodesJarry);
            result.put("edges", edgesJarry);
            result.put("center", center);
        }
        return result;
    }

    //两个结果集合并
    public JSONObject mergeResult(JSONObject obj1, JSONObject obj2){
        JSONObject jsonObject = new JSONObject();

        if(!obj1.has("nodes")){
            obj1.put("nodes",new JSONArray());
        }
        if(!obj1.has("edges")){
            obj1.put("edges",new JSONArray());
        }
        jsonObject.put("nodes", mergeArray( obj1.getJSONArray("nodes"),obj2.getJSONArray("nodes")));
        jsonObject.put("edges", mergeArray( obj1.getJSONArray("edges"),obj2.getJSONArray("edges")));

        return jsonObject;
    }
    //合并两个jsonarray
    public JSONArray mergeArray(JSONArray a1,JSONArray a2){
        for(int i=0;i<a2.length();i++){

            a1.put(a2.getJSONObject(i));

        }
        return a1;
    }
    //对array内数据进行简单去重
    public  JSONArray reDuplicatesArray(JSONArray jsonArray){
        Map maps=new HashMap<Integer,JSONObject>();
        for(int i=0;i<jsonArray.length();i++){

            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if(!maps.containsKey(jsonObject.toString().hashCode())){
                maps.put(jsonObject.toString().hashCode(),jsonObject);
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
