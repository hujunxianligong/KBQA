package com.qdcz.sdn.repository;

import com.qdcz.neo4jkernel.generic.MyRelationshipTypes;
import com.qdcz.sdn.entity._Vertex;
import com.qdcz.sdn.entity.Vertex;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by hadoop on 17-6-22.
 */
@Repository
public interface VertexRepository extends GraphRepository<Vertex> {
    @Query( "merge (n:{label} {name:{name}, root:{root} }) on " +
            "create set n.type={type},n.identity={identity},n.root={root},n.content={content} on " +
            "match set n.type={type},n.identity={identity},n.root={root},n.content={content} return n")
    _Vertex getUpdateVertexCql(@Param("label")String label,@Param("name") String  name, @Param("root") String  root, @Param("type") String type , @Param("identity") String  identity,@Param("content") String content);


    @Query("MATCH (n:{label} {name:{name}, root:{root} }) RETURN n")
    _Vertex getVertByNameCql(@Param("label")String label,@Param("name") String  name, @Param("root") String  root);

    @Query("MATCH (n:$graph {name:{name}}) RETURN n")
    List<Vertex> getVertsByNameCql(@Param("graph") String label,@Param("name") String  name);


}
