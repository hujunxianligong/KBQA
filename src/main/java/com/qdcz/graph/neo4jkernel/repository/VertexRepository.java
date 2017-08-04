package com.qdcz.graph.neo4jkernel.repository;

import com.qdcz.graph.entity.Vertex;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by hadoop on 17-6-22.
 */
@Repository
public interface VertexRepository extends GraphRepository<Vertex> {
//    @Query( "merge (n:law {name:{name}, root:{root} , identity:{identity}}) on " +
//            "create set n.type={type},n.identity={identity},n.root={root},n.content={content} on " +
//            "match set n.type={type},n.identity={identity},n.root={root},n.content={content} return n")
//    Vertex getUpdateVertexCql(@Param("label")String label,@Param("name") String  name, @Param("root") String  root, @Param("type") String type , @Param("identity") String  identity,@Param("content") String content);
//
//
//    @Query("MATCH (n:{label} {name:{name}, root:{root} }) RETURN n")
//    Vertex getVertByNameCql(@Param("label")String label,@Param("name") String  name, @Param("root") String  root);
//
//    @Query("MATCH (n:$graph {name:{name}}) RETURN n")
//    List<Vertex> getVertsByNameCql(@Param("graph") String label,@Param("name") String  name);


}
