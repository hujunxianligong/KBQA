package com.qdcz.elsearch.elk;

import com.qdcz.elsearch.conf.ELKConfig;
import com.qdcz.graph.entity.IGraphEntity;
import com.qdcz.graph.neo4jkernel.entity._Edge;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.bulk.byscroll.BulkByScrollResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.*;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient;
import org.elasticsearch.xpack.security.authc.support.SecuredString;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import static org.elasticsearch.xpack.security.authc.support.UsernamePasswordToken.basicAuthHeaderValue;

/**
 * Created by hadoop on 17-5-23.
 */
public class ElasticSearchClient {
    private static TransportClient client = null;
    public ElasticSearchClient(){

    }
    private  static String cluster;
    private  static String user;
    private  static String passwd;
    private  static String host;
    private  static int[] port;
    private  static String indces;
    private  static String type;

    public ElasticSearchClient(ELKConfig elkConfig){
        cluster =   elkConfig.getELKcluster();
        user    =   elkConfig.getELKuser();
        passwd  =   elkConfig.getELKpasswd();
        host    =   elkConfig.getELKhost();
        String[] portStr=elkConfig.getELKpost().split(",");
        int portNum=portStr.length;
        port = new int[portNum] ;
        for(int i=0;i<portNum;i++){
            port[i]=Integer.parseInt(portStr[i]);
        }
        try{
            client = new PreBuiltXPackTransportClient(Settings.builder()
                    .put("cluster.name", cluster)
                    .put("xpack.security.user", user+":"+passwd)
                    .build())
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port[0]))
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port[port.length-1]));
            String token = basicAuthHeaderValue(user, new SecuredString(passwd.toCharArray()));
            client.filterWithHeader(Collections.singletonMap("Authorization", token))
                    .prepareSearch().get();
        }catch(UnknownHostException e){
            e.printStackTrace();
        }
        indces  =   elkConfig.getELKindex();
        type    =   elkConfig.getELKtype();
    }


    public void init() {
        try{
         client = new PreBuiltXPackTransportClient(Settings.builder()
                .put("cluster.name", "my-application")
                .put("xpack.security.user", "elastic:123qdcz$%^")
                .build())
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("h134"), 9300))
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("h134"), 9301));

        String token = basicAuthHeaderValue("elastic", new SecuredString("123qdcz$%^".toCharArray()));
        client.filterWithHeader(Collections.singletonMap("Authorization", token))
                    .prepareSearch().get();

        }catch(UnknownHostException e){
            e.printStackTrace();
        }
        indces  =   "law";
        type    =  "firstVersion" ;

    }

    private void BulkIndex(IGraphEntity... entities){

        if(entities.length == 0){
            return;
        }

        BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
        for (int i = 0; i < entities.length; i++){
            JSONObject obj=entities[i].toJSON();
            bulkRequestBuilder.add(client.prepareIndex("graph", "edges")
                    .setId(entities[i].getId())
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
        System.out.println(bulkResponse);
        if (bulkResponse.hasFailures()){
            System.out.println("Bulk add index failures"+bulkResponse.buildFailureMessage());
        }
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

        System.out.println("共搜到:"+result.size()+"条结果!");
        System.out.println(result);
        return result;
    }



    public static void main(String[] args) {//test
        ElasticSearchClient client = new ElasticSearchClient();
        client.init();
//        client.Index(null);
//        client.BulkIndex(null,null);

        client.queryAllMatch(null);
       // client.delete();
//        client. queryByFilter("人民共和国国家赔偿法 第三条");
//        client. queryByFilter2("人民共和国国家赔偿法","第三条");
        // client.BulkIndex(client.GetData());
        client.close();
    }


    public void  close()
    {
        try {
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                client.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }


    private void query() {
        GetResponse response = client.prepareGet("twitter", "tweet", "1")
                .setOperationThreaded(false)
                .get();
        System.out.println("queryResult：\t"+response.toString());
    }

    private void delete(){
        DeleteResponse response = client.prepareDelete("twitter", "tweet", "1")
                .get();

    }
    private void bulkdelete(){
        BulkByScrollResponse response =
                DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
                        .filter(QueryBuilders.matchQuery("gender"   , "male"))
                        .source("persons")
                        .get();
        long deleted = response.getDeleted();
        DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
                .filter(QueryBuilders.matchQuery("gender", "male"))
                .source("persons")
                .execute(new ActionListener<BulkByScrollResponse>() {
                    @Override
                    public void onResponse(BulkByScrollResponse response) {
                        long deleted = response.getDeleted();
                    }
                    @Override
                    public void onFailure(Exception e) {
                        // Handle the exception
                    }
                });
    }
}
