package com.qdcz.sdn.repository.instruments;

import com.qdcz.sdn.entity.User;
import com.qdcz.sdn.entity.instruments.Regulations;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Created by hadoop on 17-6-29.
 */
@Repository
public interface RegulaRepos extends GraphRepository<Regulations> {
    @Query("MATCH (reg:REGULATIONS {name:{name}}) RETURN reg")
    Regulations getRegByName(@Param("name") String name);
}
