package com.qdcz.chat.interfaces;

import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.NLPTokenizer;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import com.qdcz.chat.entity.RequestParameter;
import com.qdcz.chat.service.QuestionPaserService;
import com.qdcz.common.CommonTool;
import com.qdcz.graph.tools.ResultBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.neo4j.driver.v1.types.Path;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created by star on 17-8-11.
 */
public abstract class ChatQA {

    @Autowired
    public QuestionPaserService questionPaserService;

    public abstract Set<Path> MatchPath(List<Map<String, Object>> maps , RequestParameter requestParameter);



    /**
     * 分词
     * @param question
     * @return
     */
    public List<Term> getltpInfo(String question){
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


    public List<Term> transGraphInfo(RequestParameter requestParameter,List<Term> termLists){

        if("shkx_label".equals(requestParameter.label)){

        }

        return termLists;
    }

    public List<Map<String, Object>> searchGraphEntity(List<Term> termLists,RequestParameter requestParameter){
        List<Map<String, Object>> maps= new ArrayList();
        for(Term term:termLists) {
            Map<String, Object> node = questionPaserService.getNode(requestParameter,term.word);
            if(node!=null) {
                maps.add(node);
            }
        }
        return maps;
    }


    public StringBuffer sortResult(RequestParameter requestParameter,StringBuffer pathResult){
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


    public StringBuffer parsePathToResult(Set<Path> paths, List<Map<String, Object>> maps, RequestParameter requestParameter) {
        StringBuffer sb=new StringBuffer();
        ResultBuilder resultBuilder=new ResultBuilder();
        Map<String,Vector<String>> resultPaths= resultBuilder.cleanRestult(paths);
        try{
            JSONObject resultJSon=new JSONObject();
            for (Map.Entry<String, Vector<String>> entry : resultPaths.entrySet()){
                JSONArray jsonArray=new JSONArray();
                String result="";
                String key = entry.getKey().replace("--","的");
                Vector<String> value = entry.getValue();
                result += key+"为";
                for(String str:value){
                    try{
                        JSONObject object = new JSONObject(str);
                        result="";
                        jsonArray.put(object.toString());
                    }catch ( JSONException je){
//                        je.printStackTrace();
                        result+=str+"、";
                    }
                }
                if(!"".equals(result)) {
                    result = result.substring(0, result.length() - 1) + "。";
                    sb.append(result);
                }
                else if(jsonArray.length()>0){
                    resultJSon.put("title",key);
                    resultJSon.put("type","Law");
                    resultJSon.put("data",jsonArray);
                    sb.append(resultJSon);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb;
    }
}
