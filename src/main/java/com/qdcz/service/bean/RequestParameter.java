package com.qdcz.service.bean;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by hadoop on 17-7-27.
 */
public class RequestParameter {
    public RequestParameter(){
        relationship=new HashSet<>();
    }
    public String type;
    public String label;
    public Set<String> relationship;
    public String requestSource;
    public void clear(){
        type=null;

        label=null;
        relationship.clear();
        relationship=null;
    }
}
