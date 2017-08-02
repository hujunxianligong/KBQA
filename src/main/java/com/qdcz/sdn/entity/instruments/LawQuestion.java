package com.qdcz.sdn.entity.instruments;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.json.JSONException;
import org.neo4j.ogm.json.JSONObject;

/**
 * Created by hadoop on 17-6-29.
 */
@Data
@NodeEntity(label="QUESTION")
public class LawQuestion {
    public LawQuestion(){

    }
    public LawQuestion(JSONObject obj) throws JSONException {
        this.name =obj.getString("name");
        this.problem =obj.getString("problem");
        this.answer = obj.getString("answer");
    }
    @JsonCreator
    public LawQuestion(@JsonProperty("name") String name,
                       @JsonProperty("problem") String problem,
                       @JsonProperty("answer") String answer){
        this.name = name;
        this.problem = problem;
        this.answer = answer;
    }
    @GraphId
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Property(name="name")
    public String name;
    @Property
    public String problem;
    @Property
    public String answer;
}
