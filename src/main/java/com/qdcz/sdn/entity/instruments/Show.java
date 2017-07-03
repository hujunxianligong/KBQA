package com.qdcz.sdn.entity.instruments;

import lombok.Data;
import org.neo4j.ogm.annotation.*;

/**
 * Created by hadoop on 17-6-29.
 */
@Data
@RelationshipEntity(type="SHOW")
public class Show {
    public Show(){

    }
    public Show(LawScenes lawScenes, CaseReason caseReason, String relation){
        this.from = lawScenes;
        this.to = caseReason;
        this.relation = relation;
        this.from_id = lawScenes.getId();
        this.to_id =caseReason.getId();
    }
    @GraphId
    private Long id ;

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Relationship(type = "SHOW", direction=Relationship.OUTGOING)
    public String relation;

    @Property(name="from")
    public Long from_id;
    @Property(name="to")
    public Long to_id;

    @StartNode
    public LawScenes from;
    @EndNode
    public CaseReason to;
}
