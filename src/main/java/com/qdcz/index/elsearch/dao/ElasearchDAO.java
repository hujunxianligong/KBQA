package com.qdcz.index.elsearch.dao;

import com.qdcz.entity.IGraphEntity;
import com.qdcz.index.elsearch.conf.ELKConfig;
import com.qdcz.index.interfaces.IIndexDAO;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by star on 17-8-3.
 */
public class ElasearchDAO implements IIndexDAO {
    private TransportClient client;
    private String index;

    public ElasearchDAO(TransportClient client){
        this.client = client;
        index = ELKConfig.ELKindex;
    }




    @Override
    public void addOrUpdateIndex(IGraphEntity entity) {
//        System.out.println("mm:"+entity.getGraphId());
        IndexResponse response = client.prepareIndex(index, entity.getGraphType())
                .setSource(entity.toJSON().toString())
                .setId(entity.getGraphId())//自己设置了id，也可以使用ES自带的，但是看文档说，ES的会因为删除id发生变动。
                .execute()
                .actionGet();
    }

    @Override
    public void delIndex(IGraphEntity entity) {
        DeleteResponse response = client.prepareDelete(index, entity.getGraphType(), entity.getGraphId())
                .get();
    }



    public void bluckByFile(String type,String path,Map<String,String> key_value) throws Exception {
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        Scanner sc = new Scanner(new File(path));
        int count = 1;
        while(sc.hasNext()){
            String line =  sc.nextLine();
            JSONObject obj = new JSONObject(line);
            String identity = obj.getString("identity");
            if(type.contains("relationship")){
                obj.remove("identity");
            }

            bulkRequest.add(client.prepareIndex(index,type).setId(key_value.get(identity)).setSource(obj.toString()));
            if (count%1000==0) {
                BulkResponse bulkResponse = bulkRequest.execute().actionGet();
                if (bulkResponse.hasFailures()){
                    System.out.println("Bulk add index failures"+bulkResponse.buildFailureMessage());
                    throw new Exception("批量导入elasearch失败"+type);
                }
                System.out.println("1000");
            }
            count++;
        }
        bulkRequest.execute().actionGet();
    }

    @Override
    public void bulkIndex(IGraphEntity... entities) {
        if(entities.length == 0){
            return;
        }

        BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
        for (int i = 0; i < entities.length; i++){
            JSONObject obj=entities[i].toJSON();
            bulkRequestBuilder.add(client.prepareIndex(index, entities[i].getGraphType())
                    .setId(entities[i].getGraphId())
                    .setSource(obj.toString()));

            if(i%20==1 && i!=1){
                BulkResponse bulkResponse = bulkRequestBuilder.get();
                if (bulkResponse.hasFailures()){
                    System.out.println("Bulk add index failures"+bulkResponse.buildFailureMessage());
                }
                bulkRequestBuilder = client.prepareBulk();
            }
        }

        BulkResponse bulkResponse = bulkRequestBuilder.get();
        if (bulkResponse.hasFailures()){
            System.out.println("Bulk add index failures"+bulkResponse.buildFailureMessage());
        }
    }

    @Override
    public void bulkDelete(IGraphEntity... entities) {
        if(entities.length==0){
            return;
        }
        for (IGraphEntity entity:entities){
            DeleteResponse response = client.prepareDelete(index, entity.getGraphType(), entity.getGraphId())
                    .get();

        }
    }

    @Override
    public JSONObject queryById(IGraphEntity queryEctity){
        QueryBuilder matchQuery = QueryBuilders.idsQuery().addIds(queryEctity.getGraphId());

        SearchResponse response = client.prepareSearch(index ).setTypes(queryEctity.getGraphType())
                .setQuery(matchQuery)
                .execute().actionGet();


        SearchHits searchHits = response.getHits();
        //遍历结果

        JSONObject result = null;
        if(searchHits.totalHits>0){
            SearchHit hit = searchHits.iterator().next();
            JSONObject source = new JSONObject(hit.getSourceAsString());
            source.put("_id",hit.getId());
            source.put("score",hit.getScore());

            source.remove("name_length");


            result =source;
        }

        return result;
    }


    @Override
    public Map<String,JSONObject> queryByName(String graphtype, String name,int range_low,int range_high,int size) {
        QueryBuilder matchQuery = QueryBuilders
                .boolQuery()
                .must(QueryBuilders.matchQuery("name",name))
                .must(QueryBuilders.rangeQuery("name_length").gt(range_low).lt(range_high));

        // 搜索数据
        SearchResponse response = client.prepareSearch(index).setTypes(graphtype).setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(matchQuery).setSize(size)
                .execute().actionGet();


        int page=1;
        int pagesize=10;
        // 搜索数据(分页)

        //获取查询结果集
        SearchHits searchHits = response.getHits();
        //System.out.println(searchHits.totalHits);
        Map<String,JSONObject> result= new HashMap<>();
        //遍历结果
        for(SearchHit hit:searchHits){
            JSONObject source = new JSONObject(hit.getSourceAsString());
            source.put("id",hit.getId());
            source.put("score",hit.getScore());
            source.remove("name_length");

            result.put(hit.getId(),source);

        }

//        System.out.println("共搜到:"+result.size()+"条结果!");
//        System.out.println(result);
        return result;
    }


    public List<String> queryAllMatch(IGraphEntity queryEntity) {

//        JSONObject queryJson = queryEntity.toQueryJSON();



        JSONObject queryJson = new JSONObject();
        queryJson.put("root","保险管理");


        QueryBuilder matchQuery = null;


//        matchQuery = QueryBuilders.boolQuery().must(QueryBuilders.matchAllQuery()).must(QueryBuilders.termQuery("from", "完善组织架构"));

        matchQuery =  QueryBuilders.queryStringQuery("\"完善组织架构\"").field("from");



//        matchQuery = QueryBuilders.termQuery("from", "完善组织架构");

//        matchQuery = QueryBuilders.matchQuery("root", "保险管理");
//        matchQuery = QueryBuilders.termQuery("root", "保险管理");
//        matchQuery = QueryBuilders.regexpQuery("root", "保险资金");
//        BoolQueryBuilder matchQuery = QueryBuilders.boolQuery();
//        matchQuery.must(QueryBuilders.matchAllQuery());




//        for (Object key :queryJson.keySet()){
//            matchQuery.must(QueryBuilders.matchPhraseQuery(key.toString(), queryJson.getString(key.toString())));
//        }

        // 搜索数据
        SearchResponse response = client.prepareSearch("graph" ).setTypes("edges")
                .setQuery(matchQuery)
                .execute().actionGet();


        int page=1;
        int pagesize=10;
        // 搜索数据(分页)

        //获取查询结果集
        SearchHits searchHits = response.getHits();
        System.out.println(searchHits.totalHits);
        List<String> result= new ArrayList<>();
        //遍历结果
        for(SearchHit hit:searchHits){
            JSONObject source = new JSONObject(hit.getSourceAsString());
            source.put("_id",hit.getId());
            source.put("score",hit.getScore());


            result.add(source.toString());





        }

//        System.out.println("共搜到:"+result.size()+"条结果!");
//        System.out.println(result);
        return result;
    }

}
