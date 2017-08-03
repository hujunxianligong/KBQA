package com.qdcz.common;

import java.util.Comparator;
import java.util.Map;

/**
 * Created by hadoop on 17-7-21.
 * 根据分数排序器
 */
public class MyComparetor implements Comparator
{
    private  String compareObj =null;
    public  MyComparetor(String objstr){
        this.compareObj =objstr;
    }
    @Override
    public int compare(Object o1, Object o2)
    {
        Map sdto1= (Map )o1;
        Map sdto2= (Map )o2;
        return Float.compare(Float.parseFloat(sdto1.get(compareObj).toString()),Float.parseFloat(sdto2.get(compareObj).toString()));
    }
}
