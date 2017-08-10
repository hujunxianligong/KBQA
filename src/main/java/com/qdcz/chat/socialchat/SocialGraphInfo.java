package com.qdcz.chat.socialchat;

import com.hankcs.hanlp.seg.common.Term;
import com.qdcz.chat.controller.RequestParameter;
import com.qdcz.chat.service.QuestionPaserService;
import com.qdcz.chat.tools.MyComparetor;
import com.qdcz.common.CommonTool;
import com.qdcz.entity.Vertex;
import com.qdcz.graph.interfaces.IGraphBuzi;
import com.qdcz.graph.tools.ResultBuilder;
import com.unboundid.util.json.JSONObject;
import org.neo4j.driver.v1.types.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by hadoop on 17-8-9.
 * 解读问题转化为智库图信息
 */
@Service
public class SocialGraphInfo {
    @Autowired
    @Qualifier("neo4jCypherService")
    private IGraphBuzi graphBuzi;

    public Set<Path> matchPath(List<Map<String, Object>> maps ) {
        Set<Path> paths = null;
        MyComparetor mc = new MyComparetor("questSimilar");
        Collections.sort(maps, mc);
        Collections.reverse(maps);
        String str = null;
            //降序后，第一个node就是分数最大的点
            Map<String, Object> maxNode = null;
            for (Map<String, Object> node : maps) {
                if ("node".equals(node.get("typeOf"))) {
                    maxNode = node;
                    break;
                }
            }
            if (maxNode != null) {
                Vertex vertex=new Vertex();
                CommonTool.transMap2Bean(maxNode,vertex);
                try {
                    List<Path> paths1 = graphBuzi.bfExtersion(vertex, 2);

                    Set<Path> set=new HashSet<Path>();
                    set.addAll(paths1);//给set填充
                    paths1.clear();//清空list，不然下次把set元素加入此list的时候是在原来的基础上追加元素的
                    paths1.addAll(set);//把set的
                    paths=set;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
      return  paths;
    }


public StringBuffer showPaths(StringBuffer sb, Set<Path> paths, List<Map<String, Object>> maps,RequestParameter requestParameter) {
    ResultBuilder resultBuilder = new ResultBuilder();
    List list = new ArrayList(paths);
    org.json.JSONObject object = resultBuilder.graphResult( list);
    Map<String,Vector<String>> edgeMapping=new HashMap<>();
    Vector<String> value=new Vector<String>();
    value.add("合作");value.add("协作");
    edgeMapping.put("发表",value);value.clear();
    value.add("合作");value.add("协作"); value.add("作者");
    edgeMapping.put("发表",value);value.clear();

    MyComparetor mc = new MyComparetor("questSimilar");
    Collections.sort(maps,mc);
    Collections.reverse(maps);
    String str = null;
    Map<String, Object> maxNode=null;
    for(Map<String, Object> node:maps){
        if("node".equals(node.get("typeOf"))){
            maxNode=node;
            break;
        }
    }
    if(maxNode!=null) {

    }
    //问题分析
    //同事
    System.out.println(object);
//    if (requestParameter.question.contains("同事") && "author".equals(maxNode.get("type"))) {
//            for (Map.Entry<String, Vector<String>> entry : resultPaths.entrySet()) {
//                String key = entry.getKey();
//                Vector<String> values = entry.getValue();
//                if (values.size() == 1) {
//                    hehe = "在" + key + "中相关" + maxNode.get("name") + "的同事。";
//                } else {
//                    hehe += "在" + key + "的同事有";//为
//                    for (String value : values) {
//                        if (value.equals(maxNode.get("name"))) {
//                            continue;
//                        }
//                        hehe += value + "、";
//                    }
//                    hehe = hehe.substring(0, hehe.length() - 1) + "。";
//                }
//            }
//            str = hehe;
//        }
//        //合作过　协作过
//        else if ((question.contains("合作") || question.contains("协作")) && "author".equals(maxNode.get("type"))) {
//            for (Map.Entry<String, Set<String>> entry : hashmap.entrySet()) {
//                String key = entry.getKey();
//                Set<String> values = entry.getValue();
//                if (values.size() == 1) {
//                    hehe = "在" + key + "中赞未找到合作成员。";
//                } else {
//                    hehe += "在《" + key + "》中合作过的成员有";//为
//                    for (String value : values) {
//                        if (value.equals(maxNode.get("name"))) {
//                            continue;
//                        }
//                        hehe += value + "、";
//                    }
//                    hehe = hehe.substring(0, hehe.length() - 1) + "。";
//                }
//
//            }
//            str = hehe;
//        }
//        //作者
//        else if (question.contains("作者") && "paper".equals(maxNode.get("type"))) {
//            for (Map.Entry<String, Set<String>> entry : hashmap.entrySet()) {
//                String key = entry.getKey();
//                Set<String> values = entry.getValue();
//                hehe += "在《" + key + "》中作者有";//为
//                for (String value : values) {
//                    hehe += value + "、";
//                }
//                hehe = hehe.substring(0, hehe.length() - 1) + "。";
//            }
//            str = hehe;
//        }
    return null;
    }
}
