package com.qdcz.sdn.entity.instruments;

import lombok.Data;
import org.neo4j.ogm.annotation.*;

/**
 * Created by hadoop on 17-6-29.
 */
@Data
@RelationshipEntity(type="FOR_EXAMPLE")
public class ForExample {
    public ForExample(){

    }
    public ForExample(LawScenes lawScenes,CaseExample caseExample,String relation){
        this.from = lawScenes;
        this.to = caseExample;
        this.relation = relation;
        this.from_id=lawScenes.getId();
        this.to_id=caseExample.getId();
    }
    @Property(name="from")
    public Long from_id;
    @Property(name="to")
    public Long to_id;

    @GraphId
    private Long id ;

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Relationship(type = "FOR_EXAMPLE", direction=Relationship.OUTGOING)
    public String relation;

    @StartNode
    public LawScenes from;
    @EndNode
    public CaseExample to;
}
