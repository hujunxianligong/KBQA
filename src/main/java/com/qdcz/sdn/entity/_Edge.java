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
    public Long getEdgeId() {
        return edgeId;
    }

    public void setEdgeId(Long edgeId) {
        this.edgeId = edgeId;
    }

    @GraphId
    private Long edgeId;
    @Relationship(type = "gra", direction=Relationship.OUTGOING)
    public String relation;
    @Property(name="from")
    public Long from_id;
    @StartNode
    public _Vertex from;

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
        this.from_id=this.from.getId();
        this.to_id=this.to.getId();
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
        this.from_id=this.from.getId();
        this.to_id=this.to.getId();
    }
    @Override
    public String toString() {
        return String.format("%s/%s/%s", from, relation, to);
    }
}
