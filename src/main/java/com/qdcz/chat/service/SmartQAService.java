package com.qdcz.chat.service;

import com.hankcs.hanlp.seg.common.Term;
import com.qdcz.chat.interfaces.ChatQA;
import com.qdcz.chat.entity.RequestParameter;

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

    private ChatQA chatQA;
    public SmartQAService(Class<ChatQA> clazz){
        try {
            chatQA = clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    public String smartQA(RequestParameter requestParameter)  {//智能问答
        System.out.println("智能问答提出问题：\t"+requestParameter.question);
        /*
        *分词获取分词关键词
         */
        List<Term> termLists = chatQA.getltpInfo(requestParameter.question);

        /*
        *关键词转换为图上信息
         */
        List<Term> transGraphInfo = chatQA.transGraphInfo(requestParameter,termLists);

        /*
        * 搜索图上关键实体
         */
        List<Map<String, Object>> maps = chatQA.searchGraphEntity(termLists, requestParameter);


        /*
        *实体匹配路径
         */
        Set<Path> paths = chatQA.MatchPath(maps, requestParameter);


        /*
        * 路径解析
         */
        StringBuffer resultPath = chatQA.parsePathToResult(paths,maps,requestParameter);

        /*
        *结果发送
         */
        StringBuffer stringBuffer = chatQA.sortResult( requestParameter, resultPath);

        return stringBuffer.toString();
    }



}
