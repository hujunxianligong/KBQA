package com.qdcz.chat.entity;

import java.util.*;

/**
 * Created by hadoop on 17-7-27.
 */
public class RequestParameter {
    public RequestParameter(){
        relationship=new ArrayList<>();
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
