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
@NodeEntity(label="REASON")
public class CaseReason {
    public CaseReason(){

    }
    public CaseReason(JSONObject obj) throws JSONException {
        this.name= obj.getString("name");
    }
    @JsonCreator
    public CaseReason(@JsonProperty("name") String name){
        this.name=name;
    }

    @GraphId
    private  Long id;

    @Property(name="name")
    public  String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
