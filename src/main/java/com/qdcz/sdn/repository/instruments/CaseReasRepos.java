package com.qdcz.sdn.repository.instruments;


import com.qdcz.sdn.entity.instruments.CaseReason;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Created by hadoop on 17-6-29.
 */
@Repository
public interface CaseReasRepos extends GraphRepository<CaseReason> {


    @Query("MATCH (n:REASON {name:{name}}) RETURN n")
    CaseReason getReas(@Param("name") String name);
}
