package com.qdcz.graph.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.JSONObject;


/**
 * Created by hadoop on 17-6-22.
 */
public class Vertex implements IGraphEntity {

    private String id = "";

    private String name = "";
    private String root = "";

    private String label = "";//表名
    private String content = "";
    private String type = "";
    private String identity = "";

    public Vertex() {

    }

    public Vertex(String name, String root, String label, String type) {
        this.name = name;
        this.root = root;
        this.label = label;
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("%s/%s/%s", type, name,id);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();

        obj.put("type",type);
        obj.put("name",name);
        obj.put("root",root);
        obj.put("content",content);
        obj.put("identity",identity);

        return obj;
    }

    @Override
    public String getGraphId() {
        return id;
    }

    @Override
    public String getGraphType() {
        return label;
    }


    @Override
    public JSONObject toQueryJSON() {
       JSONObject obj = new JSONObject();

        if(type!=null && !type.isEmpty()) {

                obj.put("type", type);

        }

        if(name!=null && !name.isEmpty()) {
            obj.put("name", name);
        }

        if(root!=null && !root.isEmpty()) {
            obj.put("root",root);
        }
        if(content!=null && !content.isEmpty()) {
            obj.put("content",content);
        }

        return obj;
    }


    public long getId(){
        return Long.parseLong(this.id);
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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }
}
