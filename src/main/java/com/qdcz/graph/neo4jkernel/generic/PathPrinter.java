package com.qdcz.graph.neo4jkernel.generic;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.traversal.Paths;
import org.neo4j.ogm.json.JSONException;
import org.neo4j.ogm.json.JSONObject;


public class PathPrinter implements Paths.PathDescriptor<Path>{

    private final String nodePropertyKey;

    public PathPrinter(String nodePropertyKey){
        this.nodePropertyKey = nodePropertyKey;
    }

    @Override
    public String nodeRepresentation(Path path, Node node){
        if("裁判文书".equals(node.getProperty("type").toString())){
            JSONObject object = new JSONObject();
            try {

                object.put("name",node.getProperty("name").toString());
                object.put("content",new JSONObject(node.getProperty("content").toString()));
                object.put("type",node.getProperty("type").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return object.toString();
  //          return "(" + node.getProperty( nodePropertyKey, "" ) + ")";
        }
        return node.getProperty(nodePropertyKey).toString();
//        return "(" + node.getProperty( nodePropertyKey, "" ) + ")";
    }

    @Override
    public String relationshipRepresentation(Path path, Node from, Relationship relationship){
        String prefix = "--", suffix = "--";
        if (from.equals( relationship.getEndNode())){
            prefix = "<-";
        } else {
            suffix = "->";
        }
        //relationship.getType().toString()
        return prefix  +relationship.getProperty("relation")+  suffix;
//        return prefix + "[" +relationship.getProperty("relation")+ "]" + suffix;
    }
}
