package com.qdcz.graph.neo4jkernel.evaluator;


import com.qdcz.graph.neo4jkernel.generic.MyLabels;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;


public class CustomNodeFilteringEvaluator implements Evaluator{

    private final Node node;

    public CustomNodeFilteringEvaluator(Node node) {
        this.node = node;
    }

    @Override
    public Evaluation evaluate(Path path) {
        //遍历路径中的最后一个节点，当前例子中是所有的law节点
        Node currentNode = path.endNode();
        //判断是否是law节点，如果不是，则丢弃并且继续遍历
        if(!currentNode.hasLabel(MyLabels.law)){
            return Evaluation.EXCLUDE_AND_CONTINUE;
        }
        //遍历指向当前节点的gra关系
//        for(Relationship r : currentNode.getRelationships(Direction.INCOMING, MyRelationshipTypes.gra)){
//            //获取gra关系的源头，即law节点，如果节点是给定的目标节点（John），则丢弃
//            if(r.getStartNode().equals(node)){
//                return Evaluation.EXCLUDE_AND_CONTINUE;
//            }
//        }
        return Evaluation.INCLUDE_AND_CONTINUE;
    }
}
