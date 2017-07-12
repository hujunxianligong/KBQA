package com.qdcz.neo4jkernel;

import com.qdcz.neo4jkernel.evaluator.CustomNodeFilteringEvaluator;
import com.qdcz.neo4jkernel.evaluator.InstrNodeFilteringEvaluator;
import com.qdcz.neo4jkernel.generic.MyRelationshipTypes;
import com.qdcz.neo4jkernel.generic.PathPrinter;
import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.traversal.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LoopDataService {

    @Autowired
    private GraphDatabaseService graphDatabaseService;

    /**
     * 核心 api
     * 遍历John看过的电影
     */
    @Transactional
    public void findJohnSeenMovie(Long id){
        Node userJohn =graphDatabaseService.getNodeById(id);

        //获取从userJon节点出发的HAS_SEEN关系
        Iterable<Relationship> allRelationShips = userJohn.getRelationships(Direction.OUTGOING, MyRelationshipTypes.HAS_SEEN);
        allRelationShips.forEach(relationship -> System.out.println("User has sen movie: " + relationship.getEndNode().getProperty("name")));

        /**
         * 可以将Iterable强转成ResourceIterable，ResourceIterable中有stream方法
         */
        ResourceIterable<Relationship> resourceIterable = (ResourceIterable<Relationship>) userJohn.getRelationships(Direction.OUTGOING, MyRelationshipTypes.HAS_SEEN);
        Set<String> relationshipSet = resourceIterable.stream()
                .map(relationship -> relationship.getEndNode().getProperty("name").toString())
                .collect(Collectors.toSet());
        System.out.println(relationshipSet);

        System.out.println(" ");
    }

    /**
     * 核心 api
     * 遍历John的朋友喜欢而John还没有看过的电影
     */
    @Transactional
    public void recommendMovieToJohn(Long id){
        Node userJohn =graphDatabaseService.getNodeById(id);
        Set<Node> moviesFriendsLike = new HashSet<>();
        userJohn.getRelationships(MyRelationshipTypes.IS_FRIEND_OF).forEach(friendRelation -> {
            //获取该关系上的除指定节点外的其他节点
            Node friend = friendRelation.getOtherNode(userJohn);
            friend.getRelationships(Direction.OUTGOING, MyRelationshipTypes.HAS_SEEN).forEach(seenMovie -> moviesFriendsLike.add(seenMovie.getEndNode()));
        });
        moviesFriendsLike.forEach(movie -> System.out.println("Friends like movie: " + movie.getProperty("name")));
        System.out.println("");

        Set<Node> moviesJohnLike = new HashSet<>();
        userJohn.getRelationships(Direction.OUTGOING, MyRelationshipTypes.HAS_SEEN).forEach(movieJohnLike -> moviesJohnLike.add(movieJohnLike.getEndNode()));
        moviesJohnLike.forEach(movie -> System.out.println("John like movie: " + movie.getProperty("name")));
        System.out.println("");

        moviesFriendsLike.removeAll(moviesJohnLike);
        moviesFriendsLike.forEach(movie -> System.out.println("Recommend movie to John: " + movie.getProperty("name")));
        System.out.println("");
    }

    /**
     * 利用遍历Api遍历数据
     * Evaluators.atDepth(2)列出深度为2的节点，userJohn节点的深度为0
     * Note that we set the uniqueness to Uniqueness.NODE_PATH as we want to be able to revisit the same node dureing the traversal, but not the same path.
     * NODE_GLOBAL，全局相同的节点将只遍历一次
     * NODE_PATH，同一路径下，相同的节点只遍历一次
     * NODE_LEVEL
     */
    @Transactional
    public Traverser loopDataByLoopApi(Long id,int depth){
        Traverser traverser =null;
        try(Transaction tx= graphDatabaseService.beginTx()) {
            Node userJohn = graphDatabaseService.getNodeById(id);
            tx.acquireReadLock(userJohn);
            TraversalDescription traversal = graphDatabaseService.traversalDescription()
                    //      .relationships(MyRelationshipTypes.IS_FRIEND_OF)
                    //    .relationships(MyRelationshipTypes.gra, Direction.OUTGOING)
                    .relationships(MyRelationshipTypes.gra)
                    .uniqueness(Uniqueness.NODE_PATH)
                    .evaluator(Evaluators.toDepth(depth))
                    .evaluator(new CustomNodeFilteringEvaluator(userJohn));

             traverser = traversal.traverse(userJohn);
            tx.success();
        }
        /*
        *展示单一节点所有路径
         */
        /*
        Iterable<Node> vertexPath = traverser.nodes();
        vertexPath.forEach(vertex -> System.out.println(vertex.getProperty("name")));

        PathPrinter pathPrinter = new PathPrinter( "name" );
        String output = "";
        for ( Path path : traverser ) {
            output += Paths.pathToString( path, pathPrinter );
            output += "\n";
        }
        System.out.println(output);
        */
        /*
        //start到每条end节点遍历
        ResourceIterator<Path> iterators = traverser.iterator();
        while(iterators.hasNext()){
            Path next = iterators.next();
            //关系遍历
            Iterable<Relationship> relationships = next.relationships();
            for(Relationship relationship:relationships){
                System.out.println(relationship.getAllProperties());
            }
            //节点遍历
             Iterable<Node> nodes = next.nodes();
            for(Node node:nodes){
                System.out.println(node.getAllProperties());
            }
            //反向节点遍历
            Iterable<Node> nodes1 = next.reverseNodes();
            for(Node node:nodes1){
                System.out.println(node.getAllProperties());
            }
        }
        */
        return  traverser;
    }

    /**
     * 根据场景的id返回相关联的节点（根据业务需求返回的是直系关联的信息，简介关联如场景１涉及法令２　法令２被场景２涉及　　此时场景２不会包含且继续扩展）
     * @param id
     * @return
     */
    @Transactional
    public Traverser associatedNodeSearch(Long id){
        Traverser traverser =null;
        try(Transaction tx= graphDatabaseService.beginTx()) {
            Node node = graphDatabaseService.getNodeById(id);
            tx.acquireReadLock(node);
            TraversalDescription traversal = graphDatabaseService.traversalDescription()
                    .relationships(MyRelationshipTypes.EXTRACT)
                    .relationships(MyRelationshipTypes.FOR_EXAMPLE)
                    .relationships(MyRelationshipTypes.INVOLVED)
                    .relationships(MyRelationshipTypes.SHOW)
                    .relationships(MyRelationshipTypes.MAINLY_INVOLVED)
                    .uniqueness(Uniqueness.NODE_PATH)
                    .evaluator(Evaluators.toDepth(2))
                    .evaluator(new InstrNodeFilteringEvaluator(node)).breadthFirst();

            traverser = traversal.traverse(node);
            tx.success();
        }
        return  traverser;
    }

    /**
     * 深度优先遍历
     */
    @Transactional
    public void loopDataByDepth(Long id){
        Node startNode = graphDatabaseService.getNodeById(id);

        //沿着POINT_TO关系深度优先遍历
        TraversalDescription traversalDescription = graphDatabaseService.traversalDescription()
                .relationships(MyRelationshipTypes.gra, Direction.OUTGOING)
                .depthFirst();

        //从节点1开始遍历
        Iterable<Node> nodes = traversalDescription.traverse(startNode).nodes();

        for(Node n : nodes){
            System.out.print(n.getProperty("name") + " -> ");
        }
    }

    /**
     * 广度优先遍历
     */
    @Transactional
    public void loopDataByBreadth(Long id){
        Node startNode = graphDatabaseService.getNodeById(id);

        //沿着POINT_TO关系广度优先遍历
        TraversalDescription traversalDescription = graphDatabaseService.traversalDescription()
                .relationships(MyRelationshipTypes.gra, Direction.OUTGOING)
                .breadthFirst();

        //从节点1开始遍历
        Iterable<Node> nodes = traversalDescription.traverse(startNode).nodes();

        for(Node n : nodes){
            System.out.print(n.getProperty("name") + " -> ");
        }
    }

    @Transactional
    public String loopDataByDoublePath(Long startId, Long endId){
        //获取目标起始节点和目标节点
        Node jane = graphDatabaseService.getNodeById(startId);
        Node leeo = graphDatabaseService.getNodeById(endId);

        //初始化双向遍历描述BidirectionalTraversalDescription
        BidirectionalTraversalDescription description = graphDatabaseService.bidirectionalTraversalDescription()
                //设置遍历描述的起始侧遍历出
                .startSide(
                        graphDatabaseService.traversalDescription()
                                .relationships(MyRelationshipTypes.gra)
                                .uniqueness(Uniqueness.NODE_PATH)
                )
                //设置遍历描述的结束侧节点遍历进的方向
                .endSide(
                        graphDatabaseService.traversalDescription()
                                .relationships(MyRelationshipTypes.gra)
                                .uniqueness(Uniqueness.NODE_PATH)
                )
                //设置碰撞评估函数为包含找到的所有碰撞点
                .collisionEvaluator(path -> Evaluation.INCLUDE_AND_CONTINUE).collisionEvaluator(Evaluators.toDepth(2));
                //设置侧选择器为在两个遍历方向交替变换
//                .sideSelector(SideSelectorPolicies.ALTERNATING, 100);

        PathPrinter pathPrinter = new PathPrinter( "name" );
        String output = "";
        for ( Path path : description.traverse(jane, leeo) ) {
            output += Paths.pathToString( path, pathPrinter );
            output += "\n";
        }
        System.out.println(output);
        return output;
    }

    @Transactional
    public String loopDataByNodeLevel(Long startNodeId, Long endNodeId){
        //获取目标起始节点和目标节点
        Node jane = graphDatabaseService.getNodeById(startNodeId);
        Node leeo = graphDatabaseService.getNodeById(endNodeId);

        //创建KNOWS关系遍历
        TraversalDescription traversalDescription = graphDatabaseService.traversalDescription()
                .relationships(MyRelationshipTypes.gra)
                .evaluator(path -> {
                    Node currentNode = path.endNode();
                    //当到达目标节点Leeo时，停止遍历
                    if(currentNode.getId() == leeo.getId()){
                        return Evaluation.EXCLUDE_AND_PRUNE;
                    }
                    Path singlePath = GraphAlgoFactory
                            .shortestPath(PathExpanders.forType(MyRelationshipTypes.gra), 1)
                            .findSinglePath(currentNode, leeo);
                    if(singlePath != null){
                        //当前节点能直接能到达目标节点，将该节点包含在结果中并继续遍历
                        return Evaluation.INCLUDE_AND_CONTINUE;
                    }else{
                        //当前节点不能直接达到目标节点，丢弃该节点并继续遍历
                        return Evaluation.EXCLUDE_AND_CONTINUE;

                    }
                })
                .uniqueness(Uniqueness.NODE_PATH);

        Iterable<Node> nodes = traversalDescription.traverse(jane).nodes();
        for(Node n : nodes){
            System.out.println(n.getProperty("name"));
        }

        PathPrinter pathPrinter = new PathPrinter( "name" );
        String output = "";
        for ( Path path : traversalDescription.traverse(jane) ) {
            output += Paths.pathToString( path, pathPrinter );
            output += "\n";
        }
        System.out.println(output);
        return output;
    }
}
