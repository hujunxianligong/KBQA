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
@NodeEntity(label="CASE")
public class CaseExample {
    public CaseExample(){

    }
    public CaseExample(JSONObject obj) throws JSONException {
        this.mongo_id =obj.getString("mongoId");
    }
    @JsonCreator
    public CaseExample(@JsonProperty("mongoId") String mongo_id
                      ){
        this.mongo_id=mongo_id;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @GraphId
    private  Long id;

    @Property(name="mongoId")
    public   String mongo_id;

}

