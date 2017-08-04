package com.qdcz.graph.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.json.JSONObject;


/**
 * Created by hadoop on 17-6-22.
 */
public class Edge implements IGraphEntity{

    private String graphId;

    public void setContent(String content) {
        this.content = content;
    }

    private String content;
    private Long edgeId;
    public String name;
    public String root;


    public String relation;
    public Long from;
    public Vertex fromVertex;
    public Long to;
    public Vertex toVertex;








    public String getRelation() {
        return relation;
    }
    public Long getEdgeId() {
        return edgeId;
    }

    public void setEdgeId(Long edgeId) {
        this.edgeId = edgeId;
    }
    public String getContent() {
        return content;
    }

    public String getName() {
        return name;
    }

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
    public Long getfrom() {
        return from;
    }

    public void setfrom(Long from) {
        this.from = from;
    }

    public Long getto() {
        return to;
    }

    public void setto(Long to) {
        this.to = to;
    }

    public Edge(){

    }

    @JsonCreator
    public Edge( String relation,
                 Vertex fromVertex,
                 Vertex toVertex) {
        this.relation = relation;
        this.fromVertex = fromVertex;
        this.toVertex = toVertex;
        this.name = fromVertex.getName()+"-"+toVertex.getName();
        this.root =null ;
        this.from=this.fromVertex.getId();
        this.to=this.toVertex.getId();
    }

    public Edge( String relation,
                Vertex fromVertex,
                Vertex toVertex,
                 String root) {
        this.relation = relation;
        this.fromVertex = fromVertex;
        this.toVertex = toVertex;
        this.name = fromVertex.getName()+"-"+toVertex.getName();
        this.root = root;
        this.from=this.fromVertex.getId();
        this.to=this.toVertex.getId();
    }

    public Edge( String relation,
                Vertex fromVertex,
                 Vertex toVertex,
                 String root,
                JSONObject content) {
        this.relation = relation;
        this.fromVertex = fromVertex;
        this.toVertex = toVertex;
        this.name = fromVertex.getName()+"-"+toVertex.getName();
        this.root = root;
        this.from=this.fromVertex.getId();
        this.to=this.toVertex.getId();
        this.content=content.toString();
    }
    @Override
    public String toString() {
        return String.format("%s/%s/%s", fromVertex, relation, toVertex);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();

            obj.put("from",fromVertex.getName());
            obj.put("to",toVertex.getName());
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

        if(fromVertex!=null && !fromVertex.getName().isEmpty()) {

                obj.put("from", fromVertex.getName());

        }

        if(toVertex!=null && !toVertex.getName().isEmpty()) {
            obj.put("to", toVertex);
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
