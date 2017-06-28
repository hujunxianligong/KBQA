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
    @Query( "MATCH (m:law { name:{e_name} , root:{e_root} }) MATCH (p:law { name:{e_name}, root:{e_root} }) " +
            "MERGE (p)-[r:gra]-(m) ON CREATE SET r.relation ={e_relation},r.name={e_name},r.from={e_from},r.to={e_to},r.root={e_root} " +
            "on match set r.relation ={e_relation},r.name={e_name},r.from={e_from},r.to={e_to},r.root={e_root}")
    _Edge getUpdateEdecql(@Param("e_name") String  name,@Param("e_relation") String  relation,@Param("e_from") String  from,@Param("e_to") String to ,@Param("e_root") String  root);




}
