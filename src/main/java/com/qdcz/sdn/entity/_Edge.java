package com.qdcz.sdn.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.neo4j.ogm.annotation.*;

/**
 * Created by hadoop on 17-6-22.
 */
@Data
@RelationshipEntity(type="gra")
public class _Edge {
    public Long getNodeId() {
        return nodeId;
    }

    public void setNodeId(Long nodeId) {
        this.nodeId = nodeId;
    }

    @GraphId
    private Long nodeId;
    @Relationship(type = "gra", direction=Relationship.OUTGOING)
    public String relation;
    @Property(name="from")
    public String from_name;
    @StartNode
    public _Vertex from;
    @Property(name="to")
    public String to_name;

    @EndNode
    public _Vertex to;
    @Property(name="name")
    public String name;
    @Property
    public String root;
    public _Edge(){

    }
    @JsonCreator
    public _Edge(@JsonProperty("relation") String relation,
                 @JsonProperty("from") _Vertex from,
                 @JsonProperty("to") _Vertex to) {
        this.relation = relation;
        this.from = from;
        this.to = to;
        this.name = from+"-"+to;
        this.root =null ;
        this.from_name=this.from.name;
        this.to_name=this.to.name;
    }
    @JsonCreator
    public _Edge(@JsonProperty("relation") String relation,
                 @JsonProperty("from") _Vertex from,
                 @JsonProperty("to") _Vertex to,
                 @JsonProperty("root") String root) {
        this.relation = relation;
        this.from = from;
        this.to = to;
        this.name = from.name+"-"+to.name;
        this.root = root;
        this.from_name=this.from.name;
        this.to_name=this.to.name;
    }
    @Override
    public String toString() {
        return String.format("%s/%s/%s", from, relation, to);
    }
}
