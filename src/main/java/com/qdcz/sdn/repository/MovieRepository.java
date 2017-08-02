package com.qdcz.sdn.repository;

import com.qdcz.sdn.entity.Movie;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MovieRepository extends GraphRepository<Movie> {
}
