package com.qdcz.index.elsearch.service;

import com.qdcz.common.LoadConfigListener;
import com.qdcz.graph.entity.IGraphEntity;
import com.qdcz.graph.entity.Vertex;
import com.qdcz.index.elsearch.dao.ElasearchDAO;
import com.qdcz.index.elsearch.elk.ElasearchClientFactory;
import com.qdcz.index.interfaces.IIndexService;
import org.elasticsearch.client.transport.TransportClient;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * elasearch对外提供的所有操作
 * Created by star on 17-8-3.
 */
//@Scope("properties")
@Service("elasearchService")
public class ElasearchService implements IIndexService {

    private ElasearchDAO elasearchDAO;

    private TransportClient client;

    public ElasearchService(){
        client =  ElasearchClientFactory.create();
        elasearchDAO = new ElasearchDAO(client);
    }


    public static void main(String[] args) {
        LoadConfigListener loadConfigListener=new LoadConfigListener();
        loadConfigListener.contextInitialized(null);


        Vertex vertex =  new Vertex();
        vertex.setType("");
        vertex.setLabel("ddd");
        vertex.setRoot("root");
        vertex.setName("完善组织架构");
        vertex.setId("4105");


        ElasearchService instance = new ElasearchService();
//        instance.addOrUpdateIndex(vertex);



//        instance.delIndex(vertex);


//        instance.bulkIndex(vertex);

//        instance.bulkDelete(vertex);


//        System.out.println(instance.queryById(vertex));

        instance.queryByName("ytdk_relationship","对象");


    }

    @Override
    public void addOrUpdateIndex(IGraphEntity entity) {

        elasearchDAO.addOrUpdateIndex(entity);
    }

    @Override
    public void delIndex(IGraphEntity entity) {
        elasearchDAO.delIndex(entity);
    }


    @Override
    public void bulkIndex(IGraphEntity... entities) {
        elasearchDAO.bulkIndex(entities);
    }

    @Override
    public void bulkDelete(IGraphEntity... entities) {
        elasearchDAO.bulkDelete(entities);
    }

    @Override
    public JSONObject queryById(IGraphEntity entity){
        return elasearchDAO.queryById(entity);
    }

    @Override
    public Map<String,JSONObject> queryByName(String graphtype, String name) {
        return elasearchDAO.queryByName(graphtype,name);
    }
}
