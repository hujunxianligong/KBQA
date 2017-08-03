package com.qdcz.graph.neo4jkernel.evaluator;

import com.qdcz.graph.neo4jkernel.generic.MyLabels;
import com.qdcz.graph.neo4jkernel.generic.MyRelationshipTypes;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;

/**
 * Created by hadoop on 17-7-3.
 */
public class InstrNodeFilteringEvaluator implements Evaluator {

    private final Node node;

    public InstrNodeFilteringEvaluator(Node node) {
        this.node = node;
    }

    @Override
    public Evaluation evaluate(Path path) {

        //遍历路径中的最后一个节点，当前例子中是所有的law节点
        Node currentNode = path.endNode();
        if(currentNode.hasLabel(MyLabels.REASON)){
            return Evaluation.INCLUDE_AND_PRUNE;
        }
        if(currentNode.hasLabel(MyLabels.QUESTION)){
            return Evaluation.INCLUDE_AND_CONTINUE;
        }
        if(currentNode.hasLabel(MyLabels.CASE)){
            //遍历指向当前节点的gra关系
            boolean flag=false;
            for(Relationship r_f : currentNode.getRelationships( MyRelationshipTypes.FOR_EXAMPLE)){
                if(r_f.getStartNode().equals(node)){
                    flag = true;
                }
            }
            if(flag) {
                return Evaluation.INCLUDE_AND_CONTINUE;
            }
        }
        if(currentNode.hasLabel(MyLabels.REGULATIONS)){
            //遍历指向当前节点的gra关系
            boolean flag=false;
            for(Relationship r_m : currentNode.getRelationships( MyRelationshipTypes.MAINLY_INVOLVED)){
                if(r_m.getStartNode().equals(node)){
                    flag = true;
                }

            }
            if(flag) {
                return Evaluation.INCLUDE_AND_CONTINUE;
            }
        }

        if(currentNode.hasLabel(MyLabels.SCENES)) {
            if(currentNode.equals(node)) {
                return Evaluation.INCLUDE_AND_CONTINUE;
            }
//            else if(node.hasLabel(MyLabels.QUESTION)){
//                return Evaluation.INCLUDE_AND_CONTINUE;
//            }
            else{
                return Evaluation.EXCLUDE_AND_PRUNE;
            }
        }
        return Evaluation.EXCLUDE_AND_PRUNE;
    }
}
