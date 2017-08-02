package com.qdcz.index;

import com.qdcz.graph.entity.IGraphEntity;

/**
 * Created by star on 17-8-2.
 */
public interface IIndexDAO {


    public void addIndex(IGraphEntity entity);
    public void delIndex(IGraphEntity entity);
    public void changeIndex(IGraphEntity entity);

    public void BulkIndex(IGraphEntity... entities);


    public void bulkdelete(IGraphEntity... entities);
}
