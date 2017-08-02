package com.qdcz.graph.entity;

import org.json.JSONObject;

/**
 * Created by star on 17-8-2.
 */
public interface IGraphEntity {
    public JSONObject toJSON();
    public String getId();

    public JSONObject toQueryJSON();
}
