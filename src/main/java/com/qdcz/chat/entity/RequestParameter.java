package com.qdcz.chat.entity;

import com.qdcz.conf.DatabaseConfiguration;
import com.qdcz.entity.Graph;

import java.util.*;

/**
 * Created by hadoop on 17-7-27.
 */
public class RequestParameter {
    public RequestParameter(String project) throws Exception {
        relationship=new ArrayList<>();

        List<Graph> graphs = DatabaseConfiguration.getProject(project);
        this.label = graphs.get(0).getLabel();
        relationship.add(graphs.get(0).getRelationship());
    }
    public String type;
    public String label;
    public List<String> relationship;
    public String requestSource;
    public String question;

    public void clear(){
        type=null;

        label=null;
        relationship.clear();
        relationship=null;
    }
}
