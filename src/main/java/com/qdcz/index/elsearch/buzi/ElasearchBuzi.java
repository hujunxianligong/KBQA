package com.qdcz.index.elsearch.buzi;

import com.qdcz.graph.entity.IGraphEntity;
import com.qdcz.index.elsearch.dao.ElasearchDAO;
import com.qdcz.index.elsearch.elk.ElasearchClientFactory;
import com.qdcz.index.interfaces.IIndexBuzi;
import org.elasticsearch.client.transport.TransportClient;
import org.springframework.stereotype.Service;

/**
 * elasearch对外提供的所有操作
 * Created by star on 17-8-3.
 */
@Service("elasearchBuzi")
public class ElasearchBuzi implements IIndexBuzi {

    private ElasearchDAO elasearchDAO;

    private TransportClient client;

    public ElasearchBuzi(){
        client =  ElasearchClientFactory.create();
        elasearchDAO = new ElasearchDAO(client);
    }

    @Override
    public void addIndex(IGraphEntity entity) {
        elasearchDAO.addIndex(entity);
    }

    @Override
    public void delIndex(IGraphEntity entity) {
        elasearchDAO.delIndex(entity);
    }

    @Override
    public void changeIndex(IGraphEntity entity) {
        elasearchDAO.changeIndex(entity);
    }

    @Override
    public void bulkIndex(IGraphEntity... entities) {
        elasearchDAO.bulkIndex();
    }

    @Override
    public void bulkDelete(IGraphEntity... entities) {
        elasearchDAO.bulkDelete(entities);
    }
}
