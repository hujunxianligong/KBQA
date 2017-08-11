package com.qdcz.index.interfaces;

import com.qdcz.entity.IGraphEntity;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by star on 17-8-3.
 */
public interface IIndexService {
    public void addOrUpdateIndex(IGraphEntity entity);
    public void delIndex(IGraphEntity entity);

    public void bulkIndex(IGraphEntity... entities);


    public void bulkDelete(IGraphEntity... entities);


    public JSONObject queryById(IGraphEntity queryEctity);

    /**
     * 根据字符串长度区间进行查询
     * @param graphtype
     * @param name
     * @param range_low
     * @param range_high
     * @return
     */
    public Map<String,JSONObject> queryByName(String graphtype, String name,int range_low,int range_high);

    @Deprecated
    /**
     * 默认为(0,10)
     */
    public Map<String,JSONObject> queryByName(String graphtype, String name);
}
