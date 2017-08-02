package com.qdcz.sdn.entity;

import lombok.Data;
import org.neo4j.ogm.annotation.*;

/**
 * Rich Relationships
 * Created by zhangjing on 16-7-17.
 */

@Data
@RelationshipEntity(type="HAS_SEEN")
public class Seen {

    public Seen(Integer stars, User startNode, Movie endNode){
        this.stars = stars;
        this.startNode = startNode;
        this.endNode = endNode;
    }

    @GraphId
    private Long id;

    @Property
    private Integer stars;

    @StartNode
    private User startNode;

    @EndNode
    private Movie endNode;
}
