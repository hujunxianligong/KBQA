package com.qdcz.index.interfaces;

import com.qdcz.graph.entity.IGraphEntity;

/**
 * Created by star on 17-8-3.
 */
public interface IIndexBuzi {
    public void addIndex(IGraphEntity entity);
    public void delIndex(IGraphEntity entity);
    public void changeIndex(IGraphEntity entity);

    public void bulkIndex(IGraphEntity... entities);


    public void bulkDelete(IGraphEntity... entities);
}
