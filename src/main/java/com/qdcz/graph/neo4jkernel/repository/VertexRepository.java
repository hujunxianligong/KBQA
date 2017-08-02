package com.qdcz.graph.neo4jkernel.repository;

import com.qdcz.graph.neo4jkernel.entity._Vertex;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by hadoop on 17-6-22.
 */
@Repository
public interface VertexRepository extends GraphRepository<_Vertex> {
    @Query( "merge (n:law {name:{name}, root:{root} , identity:{identity}}) on " +
            "create set n.type={type},n.identity={identity},n.root={root},n.content={content} on " +
            "match set n.type={type},n.identity={identity},n.root={root},n.content={content} return n")
    _Vertex getUpdateVertexCql(@Param("name") String  name, @Param("root") String  root, @Param("type") String type , @Param("identity") String  identity,@Param("content") String content);


    @Query("MATCH (n:law {name:{name}, root:{root} }) RETURN n")
    _Vertex getVertByNameCql(@Param("name") String  name, @Param("root") String  root);

    @Query("MATCH (n:law {name:{name}}) RETURN n")
    List<_Vertex> getVertsByNameCql(@Param("name") String  name);
}
