package com.qdcz.graph.neo4jkernel.generic;

import org.neo4j.graphdb.RelationshipType;

/**
 * @author Aleksa Vukotic
 */
public enum MyRelationshipTypes implements RelationshipType {
    IS_FRIEND_OF,
    HAS_SEEN,
    POINT_TO,
    WORK_WITH,
    LIKES,
    KNOWS,
    gra,
    EXTRACT,
    FOR_EXAMPLE,
    INVOLVED,
    MAINLY_INVOLVED,
    SHOW
}
