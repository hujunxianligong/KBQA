package com.qdcz.graph.neo4jkernel;

import com.qdcz.graph.neo4jkernel.expander.DepthAwareExpander;
import com.qdcz.graph.neo4jkernel.generic.MyRelationshipTypes;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PathExpander;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.impl.OrderedByTypeExpander;
import org.neo4j.graphdb.impl.StandardExpander;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ExpanderService {

    @Autowired
    private GraphDatabaseService graphDatabaseService;

    @Transactional
    public Iterable<Node> orderedExpander(Long id){
        Node userJohn = graphDatabaseService.getNodeById(id);

        //OrderedByTypeExpander，添加要扩展的关系类型，总是先遍历WORK_WITH关系
        PathExpander orderedByTypeExpander = new OrderedByTypeExpander()
           //     .add(MyRelationshipTypes.WORK_WITH)
           //     .add(MyRelationshipTypes.IS_FRIEND_OF)
           //     .add(MyRelationshipTypes.LIKES);
                .add(MyRelationshipTypes.gra);

        //在最终结果中只考虑深度2上的节点，并且只保留电影节点
        TraversalDescription traversalDescription = graphDatabaseService.traversalDescription()
                .expand(orderedByTypeExpander)
                .evaluator(Evaluators.atDepth(3))
                .evaluator(path -> {
                    if(path.endNode().hasProperty("name")){
                        return Evaluation.INCLUDE_AND_CONTINUE;
                    }
                    return Evaluation.EXCLUDE_AND_CONTINUE;
                });

        //从节点john开始遍历
        Iterable<Node> nodes = traversalDescription.traverse(userJohn).nodes();
        for(Node n : nodes){
            System.out.print(n.getProperty("name") +"\t"+n.getProperty("root") + " -> ");
        }
        return nodes;
    }

    @Transactional
    public void standardExpander(Long id){
        Node userJohn = graphDatabaseService.getNodeById(id);

        //构造StandardExpander，添加要扩展的关系类型
        PathExpander standardExpander = StandardExpander.DEFAULT
                .add(MyRelationshipTypes.WORK_WITH)
                .add(MyRelationshipTypes.IS_FRIEND_OF)
                .add(MyRelationshipTypes.LIKES);

        //在最终结果中只考虑深度2上的节点，并且只保留电影节点
        TraversalDescription traversalDescription = graphDatabaseService.traversalDescription()
                .expand(standardExpander)
                .evaluator(Evaluators.atDepth(2))
                .evaluator(path -> {
                    if(path.endNode().hasProperty("name")){
                        return Evaluation.INCLUDE_AND_CONTINUE;
                    }
                    return Evaluation.EXCLUDE_AND_CONTINUE;
                });

        //从节点john开始遍历
        Iterable<Node> nodes = traversalDescription.traverse(userJohn).nodes();
        for(Node n : nodes){
            System.out.print(n.getProperty("name") + " -> ");
        }
    }

    @Transactional
    public void customExpander(Long id){
        //配置深度、关系映射
        Map<Integer, List<RelationshipType>> mappings = new HashMap<>();
        mappings.put(0,
                Arrays.asList(new RelationshipType[]{
                        MyRelationshipTypes.IS_FRIEND_OF,
                        MyRelationshipTypes.WORK_WITH})
        );
        mappings.put(1, Arrays.asList(new RelationshipType[]{MyRelationshipTypes.LIKES}));

        Node userJohn = graphDatabaseService.getNodeById(id);

        TraversalDescription traversalDescription = graphDatabaseService.traversalDescription()
                .expand(new DepthAwareExpander(mappings))
                .evaluator(Evaluators.atDepth(2));

        //从节点john开始遍历
        Iterable<Node> nodes = traversalDescription.traverse(userJohn).nodes();
        for(Node n : nodes){
            System.out.print(n.getProperty("name") + " -> ");
        }
    }

}
