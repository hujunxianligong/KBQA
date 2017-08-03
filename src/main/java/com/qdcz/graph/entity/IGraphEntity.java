package com.qdcz.graph.entity;

import org.json.JSONObject;

/**
 * elasearch 需要的业务
 * Created by star on 17-8-2.
 */
public interface IGraphEntity {
    public JSONObject toJSON();
    public String getGraphId();
    public String getGraphType();

    public JSONObject toQueryJSON();
}
