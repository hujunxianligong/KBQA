package com.qdcz.chat.service;

import com.hankcs.hanlp.seg.common.Term;
import com.qdcz.chat.cmbchat.CMBQA;
import com.qdcz.chat.interfaces.ChatQA;
import com.qdcz.chat.entity.RequestParameter;

import com.qdcz.chat.socialchat.SocialQA;
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
    private SocialQA socialchatQA;
    @Autowired
    private CMBQA cmbchatQA;


    public String smartQA(RequestParameter requestParameter,String project)  {//智能问答
        ChatQA chatQA = null;
        switch (project){
            case "xz":
                chatQA = cmbchatQA;
                break;
            case "sk":
                chatQA = socialchatQA;
                break;
        }

       return chatQA.smartQA(requestParameter);
    }



}
