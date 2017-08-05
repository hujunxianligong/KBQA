package com.qdcz.graph.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.json.JSONObject;


/**
 * Created by hadoop on 17-6-22.
 */
public class Edge implements IGraphEntity{

    private String id;

    private String name;
    private String root;


    private String from;
    private String to;
    private String relationShip;//表名

    public Edge(){

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

    public Long getId(){
        return Long.parseLong(id);
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
}
