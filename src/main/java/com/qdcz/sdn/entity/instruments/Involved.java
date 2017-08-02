package com.qdcz.sdn.entity.instruments;

import lombok.Data;
import org.neo4j.ogm.annotation.*;

/**
 * Created by hadoop on 17-6-29.
 */
@Data
@RelationshipEntity(type="INVOLVED")
public class Involved {
    public Involved(){

    }
    public Involved(CaseExample caseExample,Regulations regulations,String relation){
        this.from = caseExample;
        this.to = regulations;
        this.relation = relation;
        this.from_id = caseExample.getId();
        this.to_id = regulations.getId();
    }
    @GraphId
    private Long id ;

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Relationship(type = "INVOLVED", direction=Relationship.OUTGOING)
    public String relation;
    @Property(name="from")
    public Long from_id;
    @Property(name="to")
    public Long to_id;
    @StartNode
    public CaseExample from;
    @EndNode
    public Regulations to;
}
