package com.qdcz.chat.service;

import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.NLPTokenizer;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import com.qdcz.chat.cmbchat.CmbGraphInfo;
import com.qdcz.chat.socialchat.SocialGraphInfo;
import com.qdcz.common.CommonTool;
import com.qdcz.chat.controller.RequestParameter;

import org.neo4j.driver.v1.types.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by star on 17-8-1.
 */

@Service
public class SmartQAService {

    @Autowired
    private QuestionPaserService questionPaserService;
    @Autowired
    private  SocialGraphInfo socialGraphInfo;
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

        switch (requestParameter.label){
            case "ytdk_label":
                CmbGraphInfo cmbGraphInfo=new CmbGraphInfo();
                result= cmbGraphInfo.matchPath(maps,requestParameter);

                break;
            case "shkx_label":
                result= socialGraphInfo.matchPath(maps);
                break;
            default:
                break;
        }
        return result;
    }


    private StringBuffer parsePathToResult( Set<Path> paths,List<Map<String, Object>> maps,RequestParameter requestParameter){
        StringBuffer sb=new StringBuffer();

        switch (requestParameter.label){
            case "shkx_label":


                socialGraphInfo.showPaths(sb,paths,maps,requestParameter);
                break;
            default:
                questionPaserService.showPaths(sb,paths);
                break;
        }
        return sb;
    }
    private StringBuffer sortResult(RequestParameter requestParameter,StringBuffer pathResult){
        StringBuffer finalResult=new StringBuffer();
        if(!"".equals(pathResult.toString())){
            return pathResult;
        }
        //应急处理阶段
        String str = null;
        try {//定义获取
            str = questionPaserService.findDefine(requestParameter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(str==null||"".equals(str)) {
            finalResult.append(questionPaserService.requestTuring(requestParameter.question));
        }else{
            finalResult.append(str);
        }
        return finalResult;
    }
    public String smartQA(RequestParameter requestParameter)  {//智能问答
        System.out.println("智能问答提出问题：\t"+requestParameter.question);
        /*
        *分词获取分词关键词
         */
        List<Term> termLists = getltpInfo(requestParameter.question);

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
        StringBuffer resultPath = parsePathToResult(paths,maps,requestParameter);

        /*
        *结果发送
         */
        StringBuffer stringBuffer = sortResult( requestParameter, resultPath);

        return stringBuffer.toString();
    }



}
