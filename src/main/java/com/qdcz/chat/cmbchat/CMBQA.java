package com.qdcz.chat.cmbchat;

import com.qdcz.chat.entity.RequestParameter;
import com.qdcz.chat.interfaces.ChatQA;
import org.neo4j.driver.v1.types.Path;

import java.util.*;

/**
 * Created by star on 17-8-11.
 */
public class CMBQA extends ChatQA {
    @Override
    public Set<Path> MatchPath(List<Map<String, Object>> maps, RequestParameter requestParameter) {
        Set<Path> result= null;
        if(maps.size()==2){
            result = questionPaserService.traversePathBynode(requestParameter,maps);
        }
        else if(maps.size()>2){
            float max=0;
            float maxScore=0;
            Map<String, Object> vertexNode=null;
            float second=0;
            float secScore=0;
            Map<String, Object> edgeNode=null;
            //对候选边/节点进行筛选，分别挑选最高分数的node作为对应类型的代表
            for(Map<String, Object> node:maps){
                if(node!=null) {
                    float diffLocation= Float.parseFloat(""+ node.get("questSimilar"));
                    float score=Float.parseFloat(""+ node.get("score"));
                    if ("edge".equals(node.get("typeOf"))) { //边
                        if (diffLocation > second) {
                            second = diffLocation ;
                            edgeNode = node;
                            maxScore = score;
                        }else if(diffLocation == second){
                            // 当前词和之前的词与问句具有相同的编辑距离相似度，则通过索引分数对比
                            if(maxScore< score){
                                edgeNode = node;
                                maxScore = score;
                            }
                        }
                    } else {//点
                        if (diffLocation >max) {
                            max = diffLocation;
                            vertexNode  = node;
                            secScore = score;
                        }else if(diffLocation ==max){
                            if(secScore < score){
                                vertexNode = node;
                                secScore = score;
                            }
                        }
                    }
                }
            }
            System.out.println("key:"+vertexNode+"\t"+edgeNode);
            if(vertexNode!=null&&edgeNode!=null) {
                List<Map<String, Object>> maps2 = new ArrayList();
                maps2.add(vertexNode);
                maps2.add(edgeNode);
                result = questionPaserService.traversePathBynode(requestParameter,maps2);
            }else if(vertexNode!=null&&edgeNode==null) {
                maps.remove(vertexNode);
                //查找分数第二高的索引
                Map<String, Object> vertexNode2=questionPaserService.getCloestMaps(maps);
                maps.add(vertexNode);
                List<Map<String, Object>> maps2 = new ArrayList();
                maps2.add(vertexNode);
                maps2.add(vertexNode2);
                result = questionPaserService.traversePathBynode(requestParameter,maps2);
            }else if(edgeNode!=null&&vertexNode==null){
                maps.remove(edgeNode);
                Map<String, Object> edgeNode2=questionPaserService.getCloestMaps(maps);
                maps.add(vertexNode);
                List<Map<String, Object>> maps2 = new ArrayList();
                maps2.add(edgeNode);
                maps2.add(edgeNode2);
                result = questionPaserService.traversePathBynode(requestParameter,maps2);
            }
        }
        questionPaserService=null;
        return result;
    }



}
