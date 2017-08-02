package com.qdcz.sdn.repository;

import com.qdcz.sdn.entity.Seen;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface SeenRepository extends GraphRepository<Seen> {
}
