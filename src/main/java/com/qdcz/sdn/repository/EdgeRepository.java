package com.qdcz.sdn.repository;


import com.qdcz.sdn.entity._Edge;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Created by hadoop on 17-6-22.
 */
@Repository
public interface EdgeRepository extends GraphRepository<_Edge> {
    @Query( "MATCH (m:law { name:{name} , root:{root} }) MATCH (p:law { name:{name}, root:{root} }) " +
            "MERGE (p)-[r:gra]-(m) ON CREATE SET r.relation ={relation},r.name={name},r.from={from},r.to={to},r.root={root} " +
            "on match set r.relation ={relation},r.name={name},r.from={from},r.to={to},r.root={root}")
    _Edge getUpdateEdecql(@Param("name") String  name,@Param("relation") String  relation,@Param("from") String  from,@Param("to") String to ,@Param("root") String  root);




}
