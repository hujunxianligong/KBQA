package com.qdcz.neo4jkernel;



import cn.qdcz.IK.IKAnalyzer5x;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.helpers.collection.MapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wltea.analyzer.lucene.IKQueryParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Service
public class LegacyIndexService {

    @Autowired
    private GraphDatabaseService graphDatabaseService;


    @Transactional
    public void createIndex(){
        String johnEmail = "john@example.org";
        String kateEmail = "kage@example.org";
        String jackEmail = "jack@example.org";

        //获取用户节点，并且设置email属性
        Node userJohn = graphDatabaseService.getNodeById(0l);
        userJohn.setProperty("email", johnEmail);
        Node userKate = graphDatabaseService.getNodeById(1l);
        userKate.setProperty("email", kateEmail);
        Node userJack = graphDatabaseService.getNodeById(2l);
        userJack.setProperty("email", jackEmail);

        //获取索引管理器
        IndexManager indexManager = graphDatabaseService.index();
        //查找名称为users的索引，若不存在则创建一个
        Index<Node> userIndex =indexManager.forNodes("users");
        //以email为值，为users索引添加具体的索引项目
        userIndex.add(userJohn, "email", johnEmail);
        userIndex.add(userKate, "email", kateEmail);
        userIndex.add(userJack, "email", jackEmail);
    }
    /**
     * 为单个结点创建索引
     *
     * @param propKeys //节点属性
     */
    @Transactional
    public  void createFullTextIndex(long id, List<String> propKeys,String type) {
        if("vertex".equals(type)){
            Index<Node> entityIndex = null;
            try (Transaction tx =graphDatabaseService.beginTx()) {
                entityIndex = graphDatabaseService.index().forNodes("NodeFullTextIndex",
                        MapUtil.stringMap(IndexManager.PROVIDER, "lucene", "analyzer", IKAnalyzer5x.class.getName()));

                Node node = graphDatabaseService.getNodeById(id);
                tx.acquireWriteLock(node);
                //  log.info("method[createFullTextIndex] get node id<"+node.getId()+"> name<" +node.getProperty("knowledge_name")+">");
                /**获取node详细信息*/
                Set<Map.Entry<String, Object>> properties = node.getProperties(propKeys.toArray(new String[0]))
                        .entrySet();
                for (Map.Entry<String, Object> property : properties) {
                    // log.info("method[createFullTextIndex] index prop<"+property.getKey()+":"+property.getValue()+">");
                    entityIndex.add(node, property.getKey(), property.getValue());
                }
                tx.success();
            }
        }
        else if("edge".equals(type)){
            Index<Relationship> edgeIndex = null;
            try (Transaction tx =graphDatabaseService.beginTx()) {
                edgeIndex = graphDatabaseService.index().forRelationships("NodeFullTextIndex",
                        MapUtil.stringMap(IndexManager.PROVIDER, "lucene", "analyzer", IKAnalyzer5x.class.getName()));
                Relationship relationship = graphDatabaseService.getRelationshipById(id);
                tx.acquireWriteLock(relationship);
                Set<Map.Entry<String, Object>> entries = relationship.getProperties(propKeys.toArray(new String[0])).entrySet();
                for (Map.Entry<String, Object> entry : entries) {
                    edgeIndex.add(relationship, entry.getKey(), entry.getValue());
                }
                tx.success();
            }
        }
    }
    /**
     * 使用索引查询
     *
     * @param query
     * @return
     */
    @Transactional
    public  List<Map<String, Object>> selectByFullTextIndex(String[] fields, String query,String type)  {
        List<Map<String, Object>> ret = new ArrayList();

        try (   Transaction tx = graphDatabaseService.beginTx()) {
            IndexManager index = graphDatabaseService.index();
            Query q = null;
            try {
                q = IKQueryParser.parseMultiField(fields, query);
            } catch (IOException e) {
                e.printStackTrace();
            }
            /**查询*/
            if("vertex".equals(type)) {
                Index<Node> addressNodeFullTextIndex = index.forNodes("NodeFullTextIndex",
                        MapUtil.stringMap(IndexManager.PROVIDER, "lucene", "analyzer", IKAnalyzer5x.class.getName()));
//            org.apache.lucene.search.Query q = IKQueryParser.parse("name", query);
                IndexHits<org.neo4j.graphdb.Node> foundNodes = addressNodeFullTextIndex.query(q);
                for (org.neo4j.graphdb.Node n : foundNodes) {
                    tx.acquireReadLock(n);
                    Map<String, Object> m = n.getAllProperties();

                    m.put("score", foundNodes.currentScore());
//                    if (!Float.isNaN(foundNodes.currentScore())) {
//                        m.put("score", foundNodes.currentScore() + "");
//                    }
                    m.put("id", n.getId());
                    // log.info("method[selectByIndex] score<"+foundNodes.currentScore()+">");
                    ret.add(m);
                }
            }else if("edge".equals(type)) {
                Index<Relationship> addressRelationFullTextIndex = index.forRelationships("NodeFullTextIndex",
                        MapUtil.stringMap(IndexManager.PROVIDER, "lucene", "analyzer", IKAnalyzer5x.class.getName()));
                IndexHits<Relationship> relationshipIndexHits = addressRelationFullTextIndex.query(q);
                for (Relationship r : relationshipIndexHits) {
                    tx.acquireReadLock(r);
                    Map<String, Object> m = r.getAllProperties();
                    if (!Float.isNaN(relationshipIndexHits.currentScore())) {
                        m.put("score", relationshipIndexHits.currentScore() + "");
                    }
                    m.put("id", r.getId());
                    // log.info("method[selectByIndex] score<"+foundNodes.currentScore()+">");
                    ret.add(m);
                }
            }
            tx.success();
        }
        return ret;
    }


    /**
     * 删除索引
     */
    public  void deleteFullTextIndex(long id, List<String> propKeys,String type) {
        if("vertex".equals(type)){
            Index<Node> entityIndex = null;
            try (Transaction tx =graphDatabaseService.beginTx()) {
                entityIndex = graphDatabaseService.index().forNodes("NodeFullTextIndex",
                        MapUtil.stringMap(IndexManager.PROVIDER, "lucene", "analyzer", IKAnalyzer5x.class.getName()));

                Node node = graphDatabaseService.getNodeById(id);
                tx.acquireWriteLock(node);
                //  log.info("method[createFullTextIndex] get node id<"+node.getId()+"> name<" +node.getProperty("knowledge_name")+">");
                /**获取node详细信息*/
                Set<Map.Entry<String, Object>> properties = node.getProperties(propKeys.toArray(new String[0]))
                        .entrySet();
                for (Map.Entry<String, Object> property : properties) {
                    // log.info("method[createFullTextIndex] index prop<"+property.getKey()+":"+property.getValue()+">");
                    entityIndex.remove(node, property.getKey(), property.getValue());
                }
                tx.success();
            }
        }
        else if("edge".equals(type)){
            Index<Relationship> edgeIndex = null;
            try (Transaction tx =graphDatabaseService.beginTx()) {
                edgeIndex = graphDatabaseService.index().forRelationships("NodeFullTextIndex",
                        MapUtil.stringMap(IndexManager.PROVIDER, "lucene", "analyzer", IKAnalyzer5x.class.getName()));
                Relationship relationship = graphDatabaseService.getRelationshipById(id);
                tx.acquireWriteLock(relationship);
                Set<Map.Entry<String, Object>> entries = relationship.getProperties(propKeys.toArray(new String[0])).entrySet();
                for (Map.Entry<String, Object> entry : entries) {
                    edgeIndex.remove(relationship, entry.getKey(), entry.getValue());
                }
                tx.success();
            }
        }
    }

    /**
     * 更新索引--还未完成
     */
    public  void updateFullTextIndex(long id, List<String> propKeys,String updateContent) {
        Index<Node> entityIndex = null;

        try (Transaction tx =graphDatabaseService.beginTx()) {
            entityIndex = graphDatabaseService.index().forNodes("NodeFullTextIndex",
                    MapUtil.stringMap(IndexManager.PROVIDER, "lucene", "analyzer", IKAnalyzer5x.class.getName()));
            String[] fields=new String[propKeys.size()];
            int i=0;
            for(String proKey:propKeys){
                fields[i] = proKey;
            }
            BooleanQuery q = (BooleanQuery) IKQueryParser.parseMultiField(fields, updateContent);
            for(int c=0;c<q.clauses().size();c++){
                System.out.println( q.clauses().get(c).getOccur()+"\t"+q.clauses().get(c).getQuery()+"");
            }
            Node node = graphDatabaseService.getNodeById(id);
            tx.acquireWriteLock(node);
            //  log.info("method[createFullTextIndex] get node id<"+node.getId()+"> name<" +node.getProperty("knowledge_name")+">");
            /**获取node详细信息*/
            Set<Map.Entry<String, Object>> properties = node.getProperties(propKeys.toArray(new String[0]))
                    .entrySet();
            for (Map.Entry<String, Object> property : properties) {
                // log.info("method[createFullTextIndex] index prop<"+property.getKey()+":"+property.getValue()+">");
                entityIndex.remove(node, property.getKey(), property.getValue());
            }
            tx.success();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Transactional
    public void getNodeByIndex(){
        //获取索引管理器
        IndexManager indexManager = graphDatabaseService.index();
        //查找名称为users的索引
        Index<Node> userIndex =indexManager.forNodes("users");
        //获取索引命中的结果集
        IndexHits<Node> indexHits = userIndex.get("email", "john@example.org");
        /**
         * 获取命中的节点，且要求命中节点只有一个，如果有多个则抛出NoSuchElementException("More than one element in...")
         * 若索引命中的结果集中不只一条是，秩序遍历indexHits即可
         * for(Node user : indexHits){
         *     System.out.println(user.getProperty("name"));
         * }
         */
        Node loggedOnUserNode = indexHits.getSingle();
        if(loggedOnUserNode != null){
            System.out.println(loggedOnUserNode.getProperty("name"));
        }
    }
    @Transactional
    public void updateIndex(){
        String johnEmail = "john@example.org";
        String updateJohnEmail = "john@new.example.org";

        //获取索引管理器
        IndexManager indexManager = graphDatabaseService.index();
        //查找名称为users的索引
        Index<Node> userIndex =indexManager.forNodes("users");
        //获取索引命中的结果集
        IndexHits<Node> indexHits = userIndex.get("email", johnEmail);
        Node loggedOnUserNode = indexHits.getSingle();
        if(loggedOnUserNode != null){
            //删除索引
            userIndex.remove(loggedOnUserNode, "email", johnEmail);
            //更新
            loggedOnUserNode.setProperty("email",updateJohnEmail);
            //新增索引
            userIndex.add(loggedOnUserNode, "email", updateJohnEmail);
        }
    }

}
