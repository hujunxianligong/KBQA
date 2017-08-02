package com.qdcz.sdn.entity.instruments;

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
@NodeEntity(label="REGULATIONS")
public class Regulations {
    public Regulations(){

    }
    public Regulations(JSONObject obj) throws JSONException {
        this.code=obj.getString("code");
        this.provisions=obj.getString("provisions");
        this.name=this.code+"-"+this.provisions;
    }
    public  Regulations(@JsonProperty("code") String code,
                        @JsonProperty("provisions") String provisions
                        ){
        this.code = code;
        this.provisions = provisions;
        this.name =code+"-"+provisions;
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @GraphId
    private  Long id ;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Property(name="name")
    public  String name ;
    @Property
    public  String code ;

    @Property
    public  String provisions;


}
