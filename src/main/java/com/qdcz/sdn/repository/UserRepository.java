package com.qdcz.sdn.repository;

import com.qdcz.sdn.entity.User;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;



@Repository
public interface UserRepository extends GraphRepository<User>{

    @Query("MATCH (user:USERS {name:{name}}) RETURN user")
    User getUserByName(@Param("name") String name);

    @Query("MATCH (user:USERS {name:{name}}) RETURN user")
    List<User> getUserByNames(@Param("name") String name);
}
