package com.qdcz.sdn.entity.instruments;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import lombok.Data;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.json.JSONArray;
import org.neo4j.ogm.json.JSONException;
import org.neo4j.ogm.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by hadoop on 17-6-29.
 */
@Data
@NodeEntity(label="SCENES")
public class LawScenes {
    public LawScenes(){

    }
    public LawScenes(JSONObject obj) throws JSONException {
        this.name =obj.getString("name");
        this.identity=obj.getString("identity");
        this.type=obj.getString("type");
        this.contract=obj.getString("contract");
        JSONArray showKeyWord = obj.getJSONArray("showKeyWord");
        this.showKeyWord=new ArrayList<>();
        for(int i=0;i<showKeyWord.length();i++){
            this.showKeyWord.add(showKeyWord.getString(i));
        }
        JSONArray resultKeyWords = obj.getJSONArray("resultKeyWord");
        this.resultKeyWord=new ArrayList<>();
        for(int i=0;i<resultKeyWords.length();i++){
            this.resultKeyWord.add(resultKeyWords.getString(i));
        }
    }
    @JsonCreator
    public LawScenes(@JsonProperty("name") String name,
                     @JsonProperty("identity") String identity,
                     @JsonProperty("type") String type,
                     @JsonProperty("contract") String contract,
                     @JsonProperty("showKeyWord") List<String> showKeyWord,
                     @JsonProperty("resultKeyWord") List<String> resultKeyWord){
        this.name =name;
        this.identity = identity;
        this.type = type;
        this.contract = contract;
        this.showKeyWord = showKeyWord;
        this.resultKeyWord = resultKeyWord;
    }
    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @GraphId
    private Long id ;

    public String getName() {
        return name;
    }

    @Property(name="name")
    public String name;
    @Property
    public String identity;
    @Property
    public String type;
    @Property
    public String contract;

    public List<String> getShowKeyWord() {
        return showKeyWord;
    }

    public List<String> getResultKeyWord() {
        return resultKeyWord;
    }

    @Property
    public List<String> showKeyWord;
    @Property
    public List<String> resultKeyWord;

}
