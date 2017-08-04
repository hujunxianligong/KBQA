package com.qdcz.graph.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.json.JSONObject;


/**
 * Created by hadoop on 17-6-22.
 */
public class Edge implements IGraphEntity{
    public Long getEdgeId() {
        return edgeId;
    }

    public void setEdgeId(Long edgeId) {
        this.edgeId = edgeId;
    }

    private String graphId;
    private String content;
    private Long edgeId;

    public String getRelation() {
        return relation;
    }

    public String relation;
    public Long from_id;
    public Vertex from;
    public Long to_id;
    public Vertex to;

    public String getContent() {
        return content;
    }

    public String getName() {
        return name;
    }

    public String name;
    public String root;
    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    private String relationship;
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    private String label;
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

    public Edge(){

    }
    @JsonCreator
    public Edge( String relation,
                 Vertex from,
                 Vertex to) {
        this.relation = relation;
        this.from = from;
        this.to = to;
        this.name = from+"-"+to;
        this.root =null ;
        this.from_id=this.from.getId();
        this.to_id=this.to.getId();
    }

    public Edge( String relation,
                Vertex from,
                Vertex to,
                 String root) {
        this.relation = relation;
        this.from = from;
        this.to = to;
        this.name = from.name+"-"+to.name;
        this.root = root;
        this.from_id=this.from.getId();
        this.to_id=this.to.getId();
    }

    public Edge( String relation,
                Vertex from,
                 Vertex to,
                 String root,
                JSONObject content) {
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

    @Override
    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();

            obj.put("from",from.getName());
            obj.put("to",to.getName());
            obj.put("root",root);
            obj.put("relation",relation);


        return obj;
    }

    @Override
    public String getGraphId() {

        return graphId;
    }

    @Override
    public String getGraphType() {
        return "edges";
    }

    @Override
    public JSONObject toQueryJSON() {
        JSONObject obj = new JSONObject();

        if(from!=null && !from.getName().isEmpty()) {

                obj.put("from", from.getName());

        }

        if(to!=null && !to.getName().isEmpty()) {
            obj.put("to", to);
        }

        if(root!=null && !root.isEmpty()) {
            obj.put("root",root);
        }
        if(relation!=null && !relation.isEmpty()) {
            obj.put("relation",relation);
        }

        return obj;
    }


    public void setGraphId(String graphId) {
        this.graphId = graphId;
    }
}
