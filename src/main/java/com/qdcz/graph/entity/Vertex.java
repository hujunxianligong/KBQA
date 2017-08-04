package com.qdcz.graph.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.JSONObject;


/**
 * Created by hadoop on 17-6-22.
 */
public class Vertex implements IGraphEntity {

    private String graphId;
    private Long id;
    public String type;
    public String name;
    public String identity;
    public String content;
    public String root;
    public String label;

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String relationship;
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getIdentity() {
        return identity;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getName() {
        return name;
    }

    public String getRoot() {
        return root;
    }


    public void setGraphId(String graphId) {
        this.graphId = graphId;
    }

    public Vertex(){

    }

    public Vertex( String type,
                  String name,
                   String identity) {
        this.type = type;
        this.name = name;
        this.identity = identity;
        this.root = null;
    }

    public Vertex( String type,
                  String name,
                   String identity,
                   String root) {
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
        return String.format("%s/%s/%s/%s", type, name, identity,graphId);
    }
    public Long getId() {
        return id;
    }


    @Override
    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();

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
        if(identity!=null && !identity.isEmpty()) {
            obj.put("identity",identity);
        }
        if(content!=null && !content.isEmpty()) {
            obj.put("content",content);
        }

        return obj;
    }

}
