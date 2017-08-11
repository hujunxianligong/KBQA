package com.qdcz.index.elsearch.service;

import com.qdcz.conf.LoadConfigListener;
import com.qdcz.entity.IGraphEntity;
import com.qdcz.entity.Vertex;
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
        loadConfigListener.setSource_dir("/dev/");
        loadConfigListener.contextInitialized(null);


        Vertex vertex =  new Vertex();
        vertex.setType("");
        vertex.setLabel("shkx_label");
        vertex.setRoot("");
        vertex.setName("中国科学院地球化学研究");
        vertex.setId("907");
        vertex.setEla_end(15);


        ElasearchService instance = new ElasearchService();
//        instance.addOrUpdateIndex(vertex);



//        instance.delIndex(vertex);


//        instance.bulkIndex(vertex);

//        instance.bulkDelete(vertex);


//        System.out.println(instance.queryById(vertex));

        instance.queryByName("ytdk_label","银团贷款收费",0,10);


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
        return elasearchDAO.queryByName(graphtype,name,0,10,15);
    }

    @Override
    public Map<String,JSONObject> queryByName(String graphtype, String name,int range_low,int range_high) {
        return elasearchDAO.queryByName(graphtype,name,range_low,range_high,15);
    }
}
