package com.qdcz.chat.service;

import com.hankcs.hanlp.seg.common.Term;
import com.qdcz.chat.allchat.AllChatQA;
import com.qdcz.chat.cmbchat.CMBQA;
import com.qdcz.chat.interfaces.ChatQA;
import com.qdcz.chat.entity.RequestParameter;

import com.qdcz.chat.socialchat.SocialQA;
import com.qdcz.graph.controller.GraphControler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.driver.v1.types.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by star on 17-8-1.
 */
@Service
public class SmartQAService {
    private Logger logger = LogManager.getLogger(GraphControler.class.getSimpleName());
    @Autowired
    private SocialQA socialchatQA;
    @Autowired
    private CMBQA cmbchatQA;

    @Autowired
    private AllChatQA allChatQA;

    public String smartQA(RequestParameter requestParameter,String project) throws Exception {//智能问答
        ChatQA chatQA = null;
        switch (project){
            case "xz":
                chatQA = cmbchatQA;
                break;
            case "sk":
                chatQA = socialchatQA;
                break;
            case "all":
                chatQA = allChatQA;
                break;
            default:
                logger.error("非法请求！\t"+project);
                return "非法请求！";
        }

       return chatQA.smartQA(requestParameter);
    }



}
