package com.qdcz.index.interfaces;

import com.qdcz.graph.entity.IGraphEntity;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by star on 17-8-3.
 */
public interface IIndexBuzi {
    public void addOrUpdateIndex(IGraphEntity entity);
    public void delIndex(IGraphEntity entity);

    public void bulkIndex(IGraphEntity... entities);


    public void bulkDelete(IGraphEntity... entities);


    public JSONObject queryById(IGraphEntity queryEctity);

    public List<JSONObject> queryByName(String graphtype, String name);
}
