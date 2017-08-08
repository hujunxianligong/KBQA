package com.qdcz.chat.service;

import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import com.qdcz.common.CommonTool;
import com.qdcz.common.MyComparetor;
import com.qdcz.common.MyComparetorSJ;
import com.qdcz.service.bean.RequestParameter;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by star on 17-8-1.
 */

@Service
public class HighService {

    @Autowired
    private QuestionPaserService questionPaserService;



    public String smartQA(RequestParameter requestParameter, String question)  {//智能问答
        System.out.println("智能问答提出问题：\t"+question);
        String label=requestParameter.label;
        StandardTokenizer.SEGMENT.enableAllNamedEntityRecognize(false);
        List<Term> termList = StandardTokenizer.segment(question);
        List<Term> termreplace =new ArrayList<>();
        for(int i=0;i<termList.size();i++){
            Term term=termList.get(i);
            if(i<termList.size()-1){
                Term nextTerm=termList.get(i+1);
                if((("v".equals(term.nature.name()))||"a".equals(term.nature.name())||"vn".equals(term.nature.name())||"n".equals(term.nature.name()))&&("vn".equals(nextTerm.nature.name())||"n".equals(nextTerm.nature.name()))){
                    term.nature=nextTerm.nature;
                    term.word+=nextTerm.word;
                    termreplace.add(term);
                    nextTerm.nature= term.nature;
                    nextTerm.word=term.word;
                    nextTerm.offset=term.offset;
                }
            }
        }
        for(Term term:termreplace){
            termList.remove(term);
        }
        CommonTool.removeDuplicateWithOrder(termList);
        List<Map<String, Object>> maps= new ArrayList();
        for(Term term:termList) {
            Map<String, Object> node = questionPaserService.getNode(requestParameter,term.word);
            if(node!=null) {
                maps.add(node);
            }
        }

        String result= null;
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
                    String name = (String) node.get("name");
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
            }else{
                result = "learning";
            }
        }


        MyComparetor mc = null;
        if(result==null||"learning".equals(result)){
            mc = new MyComparetor("questSimilar");
            Collections.sort(maps,mc);
            Collections.reverse(maps);
            String str = null;
            //降序后，第一个node就是分数最大的点
            Map<String, Object> maxNode=null;
            for(Map<String, Object> node:maps){
                if("node".equals(node.get("typeOf"))){
                    maxNode=node;
                    break;
                }
            }
            if(maxNode!=null) {//定义获取
                str = questionPaserService.findDefine(question, maxNode);
            }
            if(str==null||"learning".equals(str)||"".equals(str)) {
                result =questionPaserService.requestTuring(question);

            }else{
                result= str;
            }
        }
        return result;
    }
}
