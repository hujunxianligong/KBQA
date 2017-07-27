package com.qdcz.sdn.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.json.JSONObject;

/**
 * Created by hadoop on 17-7-27.
 */
public class Vertex {
    public void setId(Long id) {
        this.id = id;
    }
    public Long getId() {
        return id;
    }
    @GraphId
    private Long id;

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public String type;
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

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getIdentity() {
        return identity;
    }

    public String getContent() {
        return content;
    }

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


}
