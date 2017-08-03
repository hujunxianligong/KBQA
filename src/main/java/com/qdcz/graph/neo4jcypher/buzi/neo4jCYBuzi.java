package com.qdcz.graph.neo4jcypher.buzi;

import com.qdcz.graph.entity._Edge;
import com.qdcz.graph.entity._Vertex;
import com.qdcz.graph.interfaces.IGraphBuzi;
import org.springframework.stereotype.Service;

/**
 * cypher语句，neo4j对外提供的操作
 * Created by star on 17-8-3.
 */
@Service("neo4jCypherBuzi")
public class neo4jCYBuzi implements IGraphBuzi {
    @Override
    public String addVertex(_Vertex vertex) {
        return null;
    }

    @Override
    public String changeVertex(_Vertex vertex) {
        return null;
    }

    @Override
    public String deleteVertex(_Vertex vertex) {
        return null;
    }

    @Override
    public String addEdges(_Edge edge) {
        return null;
    }

    @Override
    public String changeEdge(_Edge edge) {
        return null;
    }

    @Override
    public String deleteEdge(_Edge edge) {
        return null;
    }
}
