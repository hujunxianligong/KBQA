package com.qdcz.index.elsearch.buzi;

import com.qdcz.graph.entity.IGraphEntity;
import com.qdcz.index.interfaces.IIndexBuzi;
import org.springframework.stereotype.Service;

/**
 * elasearch对外提供的所有操作
 * Created by star on 17-8-3.
 */
@Service("elasearchBuzi")
public class ElasearchBuzi implements IIndexBuzi {
    @Override
    public void addIndex(IGraphEntity entity) {

    }

    @Override
    public void delIndex(IGraphEntity entity) {

    }

    @Override
    public void changeIndex(IGraphEntity entity) {

    }

    @Override
    public void BulkIndex(IGraphEntity... entities) {

    }

    @Override
    public void bulkdelete(IGraphEntity... entities) {

    }
}
