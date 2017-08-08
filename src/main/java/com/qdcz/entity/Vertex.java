package com.qdcz.entity;

import org.json.JSONObject;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.util.Function;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Created by hadoop on 17-6-22.
 */
public class Vertex implements IGraphEntity,Node {

    private String id = "";

    private String name = "";
    private String root = "";

    private String label = "";//表名
    private String content = "";
    private String type = "";
    private String identity = "";

    public Vertex() {

    }

    public Vertex(JSONObject json) {
        if(json.has("id")){
            this.id = json.getString("id");
        }
        if(json.has("name")){
            this.name = json.getString("name");
        }
        if(json.has("content")){
            this.content = json.getString("content");
        }
        if(json.has("root")){
            this.root = json.getString("root");
        }
        if(json.has("type")){
            this.type = json.getString("type");
        }
        if(json.has("identity")){
            this.identity = json.getString("identity");
        }
        if(json.has("label")){
            this.label = json.getString("label");
        }
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




    @Override
    public Iterable<String> labels() {
        Set<String > set=new HashSet<>();
        set.add(label);
        return set;
    }

    @Override
    public boolean hasLabel(String s) {
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
