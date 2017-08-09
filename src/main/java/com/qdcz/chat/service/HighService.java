package com.qdcz.chat.service;

import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.NLPTokenizer;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import com.qdcz.common.CommonTool;
import com.qdcz.service.bean.RequestParameter;

import org.neo4j.driver.v1.types.Path;
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

    private List<Term> getltpInfo(String question){
        StandardTokenizer.SEGMENT.enableAllNamedEntityRecognize(false);
        List<Term> termList = NLPTokenizer.segment(question);
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
        return termList;
    }

    private List<Term> transGraphInfo(RequestParameter requestParameter,List<Term> termLists){

        if("shkx_label".equals(requestParameter.label)){

        }

        return termLists;
    }

    private List<Map<String, Object>> searchGraphEntity(List<Term> termLists,RequestParameter requestParameter){
        List<Map<String, Object>> maps= new ArrayList();
        for(Term term:termLists) {
            Map<String, Object> node = questionPaserService.getNode(requestParameter,term.word);
            if(node!=null) {
                maps.add(node);
            }
        }
        return maps;
    }

    private Set<Path> MatchPath(List<Map<String, Object>> maps ,RequestParameter requestParameter){
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
        return result;
    }


    private StringBuffer parsePathToResult( Set<Path> paths){
        StringBuffer sb=new StringBuffer();
        questionPaserService.showPaths(sb,paths);
        return sb;
    }
    private StringBuffer sortResult(String question,RequestParameter requestParameter,StringBuffer pathResult){
        StringBuffer finalResult=new StringBuffer();
        if("".equals(pathResult)||"learning".equals(pathResult)){
            String str = null;
            try {//定义获取
                str = questionPaserService.findDefine(question,requestParameter);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(str==null||"learning".equals(str)||"".equals(str)) {
                finalResult.append(questionPaserService.requestTuring(question));
            }else{
                finalResult.append(str);
            }
        }
        return finalResult;
    }
    public String smartQA(RequestParameter requestParameter, String question)  {//智能问答
        System.out.println("智能问答提出问题：\t"+question);
        /*
        *分词获取分词关键词
         */
        List<Term> termLists = getltpInfo(question);

        /*
        *关键词转换为图上信息
         */
        List<Term> transGraphInfo = transGraphInfo(requestParameter,termLists);

        /*
        * 搜索图上关键实体
         */
        List<Map<String, Object>> maps = searchGraphEntity(termLists, requestParameter);


        /*
        *实体匹配路径
         */
        Set<Path> paths = MatchPath(maps, requestParameter);


        /*
        * 路径解析
         */

        StringBuffer resultPath = parsePathToResult(paths);

        /*
        *结果发送
         */
        StringBuffer stringBuffer = sortResult(question, requestParameter, resultPath);

        return stringBuffer.toString();
    }



}
