package com.qdcz.graph.neo4jkernel.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.neo4j.ogm.annotation.*;
import org.neo4j.ogm.json.JSONObject;

/**
 * Created by hadoop on 17-7-27.
 * 边结构定义
 */
public class Edge {
    public Long getEdgeId() {
        return edgeId;
    }

    public void setEdgeId(Long edgeId) {
        this.edgeId = edgeId;
    }

    @GraphId
    private Long edgeId;
    @Relationship( direction=Relationship.OUTGOING)
    public String relation;
    @Property(name="from")
    public Long from_id;
    @StartNode
    public Vertex from;

    public Long getFrom_id() {
        return from_id;
    }

    public void setFrom_id(Long from_id) {
        this.from_id = from_id;
    }

    public Long getTo_id() {
        return to_id;
    }

    public void setTo_id(Long to_id) {
        this.to_id = to_id;
    }

    @Property(name="to")

    public Long to_id;

    @EndNode
    public Vertex to;
    @Property(name="name")
    public String name;
    @Property
    public String root;
    @Property
    public String content;
    public Edge(){

    }
    @JsonCreator
    public Edge(@JsonProperty("relation") String relation,
                 @JsonProperty("from") Vertex from,
                 @JsonProperty("to") Vertex to) {
        this.relation = relation;
        this.from = from;
        this.to = to;
        this.name = from+"-"+to;
        this.root =null ;
        this.from_id=this.from.getId();
        this.to_id=this.to.getId();
    }
    @JsonCreator
    public Edge(@JsonProperty("relation") String relation,
                 @JsonProperty("from") Vertex from,
                 @JsonProperty("to") Vertex to,
                 @JsonProperty("root") String root) {
        this.relation = relation;
        this.from = from;
        this.to = to;
        this.name = from.name+"-"+to.name;
        this.root = root;
        this.from_id=this.from.getId();
        this.to_id=this.to.getId();
    }
    @JsonCreator
    public Edge(@JsonProperty("relation") String relation,
                @JsonProperty("from") Vertex from,
                @JsonProperty("to") Vertex to,
                @JsonProperty("root") String root,
                @JsonProperty("content") JSONObject content) {
        this.relation = relation;
        this.from = from;
        this.to = to;
        this.name = from.name+"-"+to.name;
        this.root = root;
        this.from_id=this.from.getId();
        this.to_id=this.to.getId();
        this.content=content.toString();
    }
    @Override
    public String toString() {
        return String.format("%s/%s/%s", from, relation, to);
    }
}
