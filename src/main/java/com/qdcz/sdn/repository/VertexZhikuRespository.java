package com.qdcz.sdn.repository;
import com.qdcz.sdn.entity._Vertex_zhiku;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by hadoop on 17-7-27.
 */
interface  VertexZhikuRespository extends GraphRepository<_Vertex_zhiku> {
    @Query( "merge (n:zhiku {name:{name}, root:{root} }) on " +
            "create set n.type={type},n.identity={identity},n.root={root},n.content={content} on " +
            "match set n.type={type},n.identity={identity},n.root={root},n.content={content} return n")
    _Vertex_zhiku getUpdateVertexCql(@Param("name") String  name, @Param("root") String  root, @Param("type") String type , @Param("identity") String  identity, @Param("content") String content);


    @Query("MATCH (n:zhiku {name:{name}, root:{root} }) RETURN n")
    _Vertex_zhiku getVertByNameCql(@Param("name") String  name, @Param("root") String  root);

    @Query("MATCH (n:zhiku {name:{name}}) RETURN n")
    List<_Vertex_zhiku> getVertsByNameCql(@Param("name") String  name);
}
