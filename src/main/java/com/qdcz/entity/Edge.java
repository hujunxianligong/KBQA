package com.qdcz.entity;

import org.json.JSONObject;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.types.Relationship;
import org.neo4j.driver.v1.util.Function;

import java.util.Map;


/**
 * Created by hadoop on 17-6-22.
 */
public class Edge implements IGraphEntity,Relationship{

    private String id = "";

    private String name = "";
    private String root = "";


    private String from = "";
    private String to;
    private String relationShip;//表名

    public Edge(){

    }
    public Edge(JSONObject json){
        if(json.has("id")){
            this.id = json.getString("id");
        }
        if(json.has("name")){
            this.name = json.getString("name");
        }
        if(json.has("from")){
            this.from = json.getString("from");
        }
        if(json.has("root")){
            this.root = json.getString("root");
        }
        if(json.has("to")){
            this.to = json.getString("to");
        }
        if(json.has("relationShip")){
            this.relationShip = json.getString("relationShip");
        }
    }


    public Edge(String name, String root, String from, String to, String relationShip) {
        this.name = name;
        this.root = root;
        this.from = from;
        this.to = to;
        this.relationShip = relationShip;
    }
    @Override
    public String toString() {
        return String.format("%s/%s/%s", from, name, to);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();

            obj.put("from",from);
            obj.put("to",to);
            obj.put("root",root);
            obj.put("name",name);


        return obj;
    }

    @Override
    public String getGraphId() {

        return id;
    }

    @Override
    public String getGraphType() {
        return relationShip;
    }

    @Override
    public JSONObject toQueryJSON() {
        JSONObject obj = new JSONObject();

        if(root!=null && !root.isEmpty()) {
            obj.put("root",root);
        }
        if(root!=null && !root.isEmpty()) {
            obj.put("root",root);
        }
        if(root!=null && !root.isEmpty()) {
            obj.put("root",root);
        }
        if(root!=null && !root.isEmpty()) {
            obj.put("root",root);
        }

        return obj;
    }
    public String getId(){
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getRelationShip() {
        return relationShip;
    }

    public void setRelationShip(String relationShip) {
        this.relationShip = relationShip;
    }



    @Override
    public long startNodeId() {
        return  Long.parseLong(this.from);
    }

    @Override
    public long endNodeId() {
        return   Long.parseLong(this.to);
    }

    @Override
    public String type() {
        return this.getRelationShip();
    }

    @Override
    public boolean hasType(String s) {
        return false;
    }

    @Override
    public long id() {
        return Long.parseLong(id);
    }

    @Override
    public Iterable<String> keys() {

        return null;
    }

    @Override
    public boolean containsKey(String s) {
        return false;
    }

    @Override
    public Value get(String s) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Iterable<Value> values() {
        return null;
    }

    @Override
    public <T> Iterable<T> values(Function<Value, T> function) {
        return null;
    }

    @Override
    public Map<String, Object> asMap() {
        return null;
    }

    @Override
    public <T> Map<String, T> asMap(Function<Value, T> function) {
        return null;
    }
}
