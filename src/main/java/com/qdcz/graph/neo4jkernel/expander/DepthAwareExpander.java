package com.qdcz.graph.neo4jkernel.expander;

import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.PathExpander;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.traversal.BranchState;

import java.util.List;
import java.util.Map;


public class DepthAwareExpander implements PathExpander{
    //使用Map存储要跟踪的遍历深度和关系类型之间的映射
    private final Map<Integer, List<RelationshipType>> relationshipToDepthMapping;

    public DepthAwareExpander(Map<Integer, List<RelationshipType>> relationshipToDepthMapping) {
        this.relationshipToDepthMapping = relationshipToDepthMapping;
    }

    @Override
    public Iterable<Relationship> expand(Path path, BranchState branchState) {
        //查找遍历的当前深度
        int depth= path.length();

        //在当前的深度查找要跟踪的关系
        List<RelationshipType> relationshipTypes = relationshipToDepthMapping.get(depth);

        //扩展当前节点配置过的类型的所有关系
        RelationshipType[] relationshipTypeArray =  new RelationshipType[0];
        RelationshipType[] relationshipTypesArray = relationshipTypes.toArray(relationshipTypeArray);
        return path.endNode().getRelationships(relationshipTypesArray);
    }

    @Override
    public PathExpander reverse() {
        return null;
    }
}
