package com.qdcz.graph.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qdcz.graph.entity.IGraphEntity;
import lombok.Data;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.json.JSONObject;

/**
 * Created by hadoop on 17-6-22.
 */
@Data
@NodeEntity(label="law")
public class Vertex implements IGraphEntity {

    public void setId(Long id) {
        this.id = id;
    }

    private String graphId;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    @GraphId
    private Long id;

    public String type;

    public String getName() {
        return name;
    }

    @Property(name="name")
    public String name;
    public String identity;


    public String getRoot() {
        return root;
    }

    @Property
    public String content;
    @Property
    public String root;

    public Vertex(){

    }
    @JsonCreator
    public Vertex(@JsonProperty("type") String type,
                  @JsonProperty("name") String name,
                  @JsonProperty("identity") String identity) {
        this.type = type;
        this.name = name;
        this.identity = identity;
        this.root = null;
    }
    @JsonCreator
    public Vertex(@JsonProperty("type") String type,
                  @JsonProperty("name") String name,
                  @JsonProperty("identity") String identity,
                  @JsonProperty("root") String root) {
        this.type = type;
        this.name = name;
        this.identity = identity;
        this.root =root;
        this.content = "";
    }
    @JsonCreator
    public Vertex(@JsonProperty("type") String type,
                  @JsonProperty("name") String name,
                  @JsonProperty("identity") String identity,
                  @JsonProperty("root") String root,
                  @JsonProperty("content") JSONObject content) {
        this.type = type;
        this.name = name;
        this.identity = identity;
        this.root =root;
        this.content = content.toString();
    }

    @Override
    public String toString() {
        return String.format("%s/%s/%s", type, name, identity);
    }
    public Long getId() {
        return id;
    }


    @Override
    public org.json.JSONObject toJSON() {
        org.json.JSONObject obj = new org.json.JSONObject();
        obj.put("type",type);
        obj.put("name",name);
        obj.put("root",root);
        obj.put("identity",identity);
        obj.put("content",content);
        return obj;
    }

    @Override
    public String getGraphId() {
        return graphId;
    }

    @Override
    public String getGraphType() {
        return "vertex";
    }


    @Override
    public org.json.JSONObject toQueryJSON() {
        org.json.JSONObject obj = new org.json.JSONObject();

        if(type!=null && !type.isEmpty()) {
            obj.put("type", type);
        }

        if(name!=null && !name.isEmpty()) {
            obj.put("name", name);
        }

        if(root!=null && !root.isEmpty()) {
            obj.put("root",root);
        }
        if(identity!=null && !identity.isEmpty()) {
            obj.put("identity",identity);
        }
        if(content!=null && !content.isEmpty()) {
            obj.put("content",content);
        }
        return obj;
    }


    public void setGraphId(String graphId) {
        this.graphId = graphId;
    }
}
