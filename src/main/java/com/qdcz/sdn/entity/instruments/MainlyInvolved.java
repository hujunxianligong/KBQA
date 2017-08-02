package com.qdcz.sdn.entity.instruments;

import lombok.Data;
import org.neo4j.ogm.annotation.*;

/**
 * Created by hadoop on 17-6-29.
 */
@Data
@RelationshipEntity(type="MAINLY_INVOLVED")
public class MainlyInvolved {
    public MainlyInvolved(){

    }
    public MainlyInvolved(LawScenes lawScenes,Regulations regulations,String relation){
        this.from = lawScenes;
        this.to = regulations;
        this.relation = relation;
        this.from_id = lawScenes.getId();
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

    @Relationship(type = "MAINLY_INVOLVED", direction=Relationship.OUTGOING)
    public String relation;
    @Property(name="from")
    public Long from_id;
    @Property(name="to")
    public Long to_id;
    @StartNode
    public LawScenes from;
    @EndNode
    public Regulations to;
}
