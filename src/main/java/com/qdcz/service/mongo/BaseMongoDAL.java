package com.qdcz.service.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.QueryOperators;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.neo4j.ogm.json.JSONArray;
import org.neo4j.ogm.json.JSONException;
import org.neo4j.ogm.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by hadoop on 17-7-3.
 */
public abstract class BaseMongoDAL {

    /**
     * mongo节点包含查询时的包含类型枚举
     * @author star
     *
     */
    public static enum ContainsType {
        left, right, complete, contains
    }

    private String user = "";
    private String pass = "";
    private MongoClient client = null;
    private MongoDatabase db = null;
    private MongoCollection<Document> collection = null;

    private String host = "";
    private String databaseName = "";
    private String collectionName = "";
    private int port = 0;

    /**
     * 关闭mongo链接
     */
    public void close() {
        if (this.client != null)
            this.client.close();
    }

    /**
     * 获取连接的结合名称
     * @return
     */
    public String getCollectionName() {
        return collectionName;
    }

    /**
     * 获取数据库名称
     * @return
     */
    public String getDatabaseName() {
        return databaseName;
    }


    public BaseMongoDAL(String host, String databaseName, int port,
                        String collectionName,String user,String pass) {
        this.host = host;
        this.databaseName = databaseName;
        this.port = port;
        this.collectionName = collectionName;
        this.user = user;
        this.pass = pass;
        connect();
    }

    public void connect() {
        client = ConnManager.getClient(host, databaseName, port, user, pass);
//		MongoCredential credential = MongoCredential.createCredential(user,
//				databaseName, pass.toCharArray());
//		client = new MongoClient(new ServerAddress(host, port),
//				Arrays.asList(credential));
        db = client.getDatabase(databaseName);
        collection = db.getCollection(collectionName);
    }



    /**
     * 更换连接的mongo集合
     *
     * @param collectionName
     */
    public void changeCollection(String collectionName) {
        if(!this.collectionName.equals(collectionName)){
            this.collectionName = collectionName;
            collection = db.getCollection(collectionName);
        }
    }

    /**
     * 通过节点包含进行搜索,注意：在数据量多的库内搜索的时候必须在所搜索的字段上面做好索引，
     * @param node
     * @param contains
     * @param type
     * @return
     */
    public JSONArray getByNodeContains(String node,
                                       String contains, ContainsType type) {
        JSONArray arr = new JSONArray();
        try {
            Pattern pattern = null;
            switch (type) {
                case complete:
                    // 1、完全匹配
                    pattern = Pattern.compile("^" + contains + "$",
                            Pattern.CASE_INSENSITIVE);
                    break;
                case left:
                    // 2、左匹配
                    pattern = Pattern.compile("^" + contains + ".*$",
                            Pattern.CASE_INSENSITIVE);
                    break;
                case right:
                    // 3、右匹配
                    pattern = Pattern.compile("^.*" + contains + "$",
                            Pattern.CASE_INSENSITIVE);
                    break;
                default:
                    // 4、模糊匹配
                    pattern = Pattern.compile("^.*" + contains + ".*$",
                            Pattern.CASE_INSENSITIVE);
                    break;
            }

            BasicDBObject query = new BasicDBObject();
            query.put(node, pattern);
            MongoCursor<Document> list = collection.find(query).iterator();
            while (list.hasNext()) {
                Document doc = list.next();
                arr.put(new JSONObject(doc.toJson()));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arr;
    }

    /**
     * 询问是否存在传入索引
     * @param _id
     * @return
     */
    public boolean ask_exist(String _id) {
        Bson query = new Document("_id", _id);
        Document d = null;
        try {
            d = collection.find(query).first();
        } catch (Exception ex) {
            try {
                ex.printStackTrace();
                d = collection.find(query).first();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        if (d == null)
            return false;
        return true;
    }

    /**
     * 通过上一页最后ID寻找下一页
     */
    public JSONArray getOnePage(String lastId, int pageSize) {
        // Bson query = new Document("_id",new );
        JSONArray onepage = new JSONArray();
        try {
            BasicDBObject query = new BasicDBObject("_id", new BasicDBObject(
                    QueryOperators.GT, lastId));
            MongoCursor<Document> limit = null;
            if (lastId.equals("one") || lastId.isEmpty())
                limit = collection.find().skip(0).sort(new BasicDBObject("_id", 1))
                        .limit(pageSize).iterator();
            else
                limit = collection.find(query).skip(0)
                        .sort(new BasicDBObject("_id", 1)).limit(pageSize)
                        .iterator();
            while (limit.hasNext()) {
                Document doc = limit.next();
                onepage.put(new JSONObject(doc.toJson()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return onepage;
    }

    /**
     * 按照翻页获取mongo中的数据，首页为第一页
     *
     * @param page
     * @param pageSize
     * @return
     */
    public JSONArray getOnePage(int page, int pageSize) {
        JSONArray onepage = new JSONArray();
        try {
            long pagecount = getCollectionCount();
            int total_page = (int) ((pagecount - 1) / pageSize) + 1;
            if (page < 1)
                page = 1;
            else if (page > total_page)
                page = total_page;
            MongoCursor<Document> limit = collection.find()
                    .skip((page - 1) * pageSize).sort(new BasicDBObject())
                    .limit(pageSize).iterator();
            while (limit.hasNext()) {
                Document doc = limit.next();
                onepage.put(new JSONObject(doc.toJson()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return onepage;
    }

    /**
     * 获取集合中的记录总计数
     *
     * @return
     */
    public long getCollectionCount() {
        return collection.count();
    }

    /**
     * 获取集合中的所有文档,文档多的时候特慢
     * @return
     */
    @Deprecated
    public JSONArray get_all_documents() {
        JSONArray all = new JSONArray();
        try {
            MongoCursor<Document> iterator = collection.find(new Document())
                    .iterator();
            while (iterator.hasNext()) {
                Document doc = iterator.next();
                all.put(new JSONObject(doc.toJson()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return all;
    }


    /**
     * 移除一条记录
     *
     * @param _id
     */
    public void removeOneDocument(String _id) {
        Bson query = new Document("_id", _id);
        collection.deleteOne(query);


    }

    /**
     * 获取一条记录，有则返回，无则返回null
     *
     * @param _id
     * @return
     */
    public synchronized JSONObject getOneDocument(String _id) {
        JSONObject result = null;
        Bson query = new Document("_id", _id);
        Document d = null;
        try {
            d = collection.find(query).first();
            if (d != null) {
                result = new JSONObject(d.toJson());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 不存在保存，存在替换一条记录
     *
     * @param _id
     * @param record
     * @return
     */
    public boolean saveOrReplaceOneDocument(String _id, JSONObject record) {
        Document article = Document.parse(record.toString());
        article.put("_id", _id);
        if (saveWhenNotEist(_id, record))
            return true;
        collection.replaceOne(new Document().append("_id", _id), article);

        return false;
    }

    /**
     * 不存在则保存，存在则忽略此次操作
     *
     * @param _id
     * @param obj
     * @return
     */
    public boolean saveWhenNotEist(String _id, JSONObject obj) {
        if (!ask_exist(_id)) {
            Document article = Document.parse(obj.toString());
            article.put("_id", _id);
            collection.insertOne(article);

            return true;
        }
        return false;
    }

    /**
     * 往一条记录中添加多个节点
     *
     * @param _id
     * @param node_key
     */
    @Deprecated
    public void saveOrUpdateSomeNode(String _id, JSONObject node_key) {
        if (node_key == null )
            return;
        if (saveWhenNotEist(_id, node_key))
            return;
        JSONArray names = node_key.names();
        String key = null;
        JSONObject obj = null;
        for (int i = 0; i < names.length(); i++) {
            obj = new JSONObject();
            try {
                key = names.getString(i);
                obj.put(key, node_key.getString(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 一个根节点是数组的里面添加元素
     *
     * @param _id
     * @param key
     * @param value
     */
    @Deprecated
    public void appendToNode(String _id, String key, JSONArray value) {
        Bson query = new Document("_id", _id);
        Document d = null;
        try {
            d = collection.find(query).first();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (d != null) {
            @SuppressWarnings("unchecked")
            ArrayList<JSONObject> arr = (ArrayList<JSONObject>) d.get(key);
            if (arr == null) {
                arr = new ArrayList<JSONObject>();
            }
            for (int i = 0; i < arr.size(); i++) {
                value.put(arr.get(i));
            }
            JSONObject obj = new JSONObject();
            try {
                obj.put(key, value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            BasicDBObject newdoc = new BasicDBObject(d);
            newdoc.putAll(Document.parse(obj.toString()));
            if (d != null) {
                collection.updateOne(new Document().append("_id", _id),
                        new Document("$set", newdoc));
                System.out.println("table:  " + this.collectionName
                        + " append one node:" + key + "  ID:" + _id);
            }
        }
    }

    /**
     * 保存或者更新节点
     *
     * @param _id
     * @param key
     * @param value
     */
    public void saveOrUpdateNode(String _id, String key, JSONArray value) {
        JSONObject obj = new JSONObject();
        try {
            obj.put(key, value);
            if (saveWhenNotEist(_id, obj))
                return;
            updateOneNode(_id, key, obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存或者更新节点
     *
     * @param _id
     * @param key
     * @param value
     */
    public void saveOrUpdateNode(String _id, String key, JSONObject value) {
        JSONObject obj = new JSONObject();
        try {
            obj.put(key, value);
            if (saveWhenNotEist(_id, obj))
                return;
            updateOneNode(_id, key, obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存或者更新节点
     *
     * @param _id
     * @param key
     * @param value
     */
    public void saveOrUpdateNode(String _id, String key, String value) {
        JSONObject obj = new JSONObject();
        try {
            obj.put(key, value);
            if (saveWhenNotEist(_id, obj))
                return;
            updateOneNode(_id, key, obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新节点
     *
     * @param _id
     * @param key
     * @param obj
     */
    private void updateOneNode(String _id, String key, JSONObject obj) {}
}
