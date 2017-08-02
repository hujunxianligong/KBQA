package com.qdcz.sdn.entity.instruments;

import lombok.Data;
import org.neo4j.ogm.annotation.*;

/**
 * Created by hadoop on 17-6-29.
 */
@Data
@RelationshipEntity(type="EXTRACT")
public class Extract {
    public Extract(){

    }
    public Extract(LawScenes lawScenes,LawQuestion lawQuestion,String relation){
        this.relation = relation;
        this.from = lawScenes;
        this.to = lawQuestion;
        this.from_id = lawScenes.getId();
        this.to_id = lawQuestion.getId();
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @GraphId
    private Long id ;

    @Relationship(type = "EXTRACT", direction=Relationship.OUTGOING)
    public String relation;

    @Property(name="from")
    public Long from_id;
    @Property(name="to")
    public Long to_id;
    @StartNode
    public LawScenes from;
    @EndNode
    public LawQuestion to;


}
