package com.qdcz.graph.neo4jcypher.buzi;

import com.qdcz.graph.entity.Edge;
import com.qdcz.graph.entity.Vertex;
import com.qdcz.graph.interfaces.IGraphBuzi;
import org.springframework.stereotype.Service;

/**
 * cypher语句，neo4j对外提供的操作
 * Created by star on 17-8-3.
 */
@Service("neo4jCypherBuzi")
public class neo4jCYBuzi implements IGraphBuzi {
    @Override
    public String addVertex(Vertex vertex) {
        return null;
    }

    @Override
    public String changeVertex(Vertex vertex) {
        return null;
    }

    @Override
    public String deleteVertex(Vertex vertex) {
        return null;
    }

    @Override
    public String addEdges(Edge edge) {
        return null;
    }

    @Override
    public String changeEdge(Edge edge) {
        return null;
    }

    @Override
    public String deleteEdge(Edge edge) {
        return null;
    }
}
