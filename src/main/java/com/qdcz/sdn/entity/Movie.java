package com.qdcz.sdn.entity;

import lombok.Data;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

/**
 * Created by zhangjing on 16-7-15.
 */

@Data
@NodeEntity(label = "MOVIES")
public class Movie {

    public Movie(String name){
        this.name = name;
    }

    @GraphId
    private Long nodeId;

    @Property(name="name")
    private String name;

}
