package com.qdcz.chat.buzi;

import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.NLPTokenizer;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import com.qdcz.common.CommonTool;
import com.qdcz.common.ConceptRuler;
import com.qdcz.common.Levenshtein;
import com.qdcz.common.MyComparetor;
import com.qdcz.graph.neo4jkernel.BankLawService;
import com.qdcz.graph.neo4jkernel.entity._Vertex;
import org.neo4j.ogm.json.JSONArray;
import org.neo4j.ogm.json.JSONException;
import org.neo4j.ogm.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by star on 17-8-1.
 */
@Service
public class HighService {

    @Autowired
    private QuestionPaserService questionPaserService;


    @Autowired
    private BankLawService bankLawService;

    @Transactional
    public String smartQA(String methodName,String question)  {//智能问答
        System.out.println("智能问答提出问题：\t"+question);
        try {
            String s = ConceptRuler.RegexKey(question);//正则模式
            if(s!=null){
                List<_Vertex> vertices = bankLawService.checkVertexByName(s);
                String result="";
                for(_Vertex vertex:vertices){
                    Map<String, Object> map=new HashMap<>();
                    map.put("name",vertex.getName());
                    map.put("id",vertex.getId());
                    map.put("regex",true);
                    String str = questionPaserService.findDefine(question, map);
                    result+=str;
                }
                if(!"learning".equals(result)&&!"".equals(result)){
                    return result;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        StandardTokenizer.SEGMENT.enableAllNamedEntityRecognize(false);
        List<Term> termList = NLPTokenizer.segment(question);
        for(int i=0;i<termList.size();i++){
            Term term=termList.get(i);
            if(i<termList.size()-1){
                Term nextTerm=termList.get(i+1);
                if(("a".equals(term.nature.name())||"n".equals(term.nature.name()))&&("vn".equals(nextTerm.nature.name())||"n".equals(nextTerm.nature.name()))){
                    term.nature=nextTerm.nature;
                    term.word+=nextTerm.word;
                    nextTerm.nature= term.nature;
                    nextTerm.word=term.word;
                    nextTerm.offset=term.offset;
                }
            }

        }

        CommonTool.removeDuplicateWithOrder(termList);
        MyComparetor mc = new MyComparetor("score");
        List<Map<String, Object>> maps= new ArrayList();
        for(Term term:termList) {
            Map<String, Object> node = questionPaserService.getNode(term.word);
            if(node!=null) {
                maps.add(node);
            }
        }

        String result= null;
        if(maps.size()==2){
            result = questionPaserService.traversePathBynode(maps);
        }
        else if(maps.size()==0){
            if("askOfWeChat".equals(methodName)){
                JSONObject obj=new JSONObject();

                JSONArray data=new JSONArray();
                data.put(questionPaserService.requestTuring(question));
                try {
                    obj.put("data",data);
                    obj.put("type","Turing");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return obj.toString();
            }
            return questionPaserService.requestTuring(question);
        }else if(maps.size()>2){
            float max=0;
            float maxScore=0;
            Map<String, Object> vertexNode=null;
            float second=0;
            float secScore=0;
            Map<String, Object> edgeNode=null;
            Levenshtein lt=new Levenshtein();
            //对候选边/节点进行筛选，分别挑选最高分数的node作为对应类型的代表
            for(Map<String, Object> node:maps){
                if(node!=null) {
                    String name = null;
                    float diffLocation=0;
                    if (node.containsKey("relation")) { //边
                        name = (String) node.get("relation");
                        diffLocation = lt.getSimilarityRatio(name, question);
                        if (diffLocation > second) {
                            second = diffLocation ;
                            edgeNode = node;
                            maxScore = (float) node.get("score");
                        }else if(diffLocation == second){
                            // 当前词和之前的词与问句具有相同的编辑距离相似度，则通过索引分数对比
                            if(maxScore< (float) node.get("score")){
                                edgeNode = node;
                                maxScore = (float) node.get("score");
                            }
                        }
                    } else {//点
                        name = (String) node.get("name");
                        diffLocation = lt.getSimilarityRatio(name, question);
                        if (diffLocation >max) {
                            max = diffLocation;
                            vertexNode  = node;
                            secScore = (float) node.get("score");
                        }else if(diffLocation ==max){
                            if(secScore < (float) node.get("score")){
                                vertexNode = node;
                                secScore = (float) node.get("score");
                            }
                        }
                    }
                    //记录检索到的每个索引与问句的相似度分数
                    node.put("questSimilar", diffLocation);
                }
            }
            System.out.println("key:"+vertexNode+"\t"+edgeNode);
            if(vertexNode!=null&&edgeNode!=null) {
                List<Map<String, Object>> maps2 = new ArrayList();
                maps2.add(vertexNode);
                maps2.add(edgeNode);
                result = questionPaserService.traversePathBynode(maps2);
            }else if(vertexNode!=null&&edgeNode==null) {
                maps.remove(vertexNode);
                //查找分数第二高的索引
                Map<String, Object> vertexNode2=questionPaserService.getCloestMaps(maps);
                maps.add(vertexNode);
                List<Map<String, Object>> maps2 = new ArrayList();
                maps2.add(vertexNode);
                maps2.add(vertexNode2);
                result = questionPaserService.traversePathBynode(maps2);
            }else if(edgeNode!=null&&vertexNode==null){
                maps.remove(edgeNode);
                Map<String, Object> edgeNode2=questionPaserService.getCloestMaps(maps);
                maps.add(vertexNode);
                List<Map<String, Object>> maps2 = new ArrayList();
                maps2.add(edgeNode);
                maps2.add(edgeNode2);
                result = questionPaserService.traversePathBynode(maps2);
            }else{
                result = "learning";
            }
        }

        if(result==null||"learning".equals(result)||"".equals(result)){
            mc = new MyComparetor("questSimilar");
            Collections.sort(maps,mc);
            Collections.reverse(maps);
            String str = null;
            try {
                //降序后，第一个node就是分数最大的点
                Map<String, Object> maxNode=null;
                for(Map<String, Object> node:maps){
                    if("node".equals(node.get("type"))){
                        maxNode=node;
                        break;
                    }
                }
                if(maxNode!=null) {
                    str = questionPaserService.findDefine(question, maxNode);
                }
            } catch (JSONException  e) {
                e.printStackTrace();
            }
            if(str==null||"learning".equals(str)||"".equals(str)) {
                if("askOfWeChat".equals(methodName)){
                    JSONObject obj=new JSONObject();
                    JSONArray data=new JSONArray();
                    data.put(questionPaserService.requestTuring(question));
                    try {
                        obj.put("data",data);
                        obj.put("type","Turing");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return obj.toString();
                }
                return questionPaserService.requestTuring(question);
            }else{
                result= str;
            }
        }
        if("askOfWeChat".equals(methodName)){
            boolean flag=false;//判断是否是之前获取案例json
            JSONObject obj=null;
            try{
                obj=new JSONObject(result);
            }catch (Exception e){
                flag=true;
            }
            if(flag){
                obj = new JSONObject();

                JSONArray data = new JSONArray();
                data.put(result);
                try {
                    obj.put("data", data);
                    obj.put("type", "Graph");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return obj.toString();
        }
        return result;
    }
}
