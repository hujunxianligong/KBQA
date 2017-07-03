package com.qdcz.sdn.repository.instruments;

import com.qdcz.sdn.entity.User;
import com.qdcz.sdn.entity.instruments.LawQuestion;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Created by hadoop on 17-6-29.
 */
@Repository
public interface LawQuestRepos extends GraphRepository<LawQuestion> {
    @Query("MATCH (question:QUESTION {name:{name}}) RETURN question")
    LawQuestion getQuesByName(@Param("name") String name);
}
