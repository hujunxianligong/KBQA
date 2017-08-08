package com.qdcz.chat.tools;

import org.json.JSONObject;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hadoop on 17-7-21.
 * 根据分数排序器
 */
public class MyComparetorSJ implements Comparator
{
    private  String compareObj =null;
    public MyComparetorSJ(String objstr){
        this.compareObj =objstr;
    }
    @Override
    public int compare(Object o1, Object o2)
    {
        Map.Entry<String,JSONObject> sdto1= (Map.Entry<String, JSONObject>) o1;
        Map.Entry<String,JSONObject> sdto2= (Map.Entry<String, JSONObject>) o2;


        return Float.compare(Float.parseFloat( sdto1.getValue().get(compareObj).toString()),Float.parseFloat(sdto2.getValue().get(compareObj).toString()));
    }
}
