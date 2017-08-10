package com.qdcz.chat.controller;

import com.qdcz.chat.service.HighService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * Created by star on 17-8-1.
 */
@RestController
public class ChatController {

    @Autowired
    private HighService highService;




    @RequestMapping(path = "/testask", method = {RequestMethod.POST,RequestMethod.GET})
    public void  testQuery(@RequestParam String question){
//        cypherSearchService.queryWithCypher(question);

    }



    @CrossOrigin
    @RequestMapping(path = "/ask", method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public String ask(@RequestParam String question){
        RequestParameter requestParameter =null;
        requestParameter =new RequestParameter();
        requestParameter.label="shkx_label";
        requestParameter.relationship=new ArrayList<>();
        requestParameter.relationship.add("ytdk_relationship");
        requestParameter.question=question;
        String s = highService.smartQA(requestParameter);
        return s;
    }


    @CrossOrigin
    @RequestMapping(path = "/askFromWeChat", method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public String askOfWeChat(@RequestParam String question){
        RequestParameter requestParameter =null;
        requestParameter =new RequestParameter();
        requestParameter.label="ytdk_label";
        requestParameter.relationship=new ArrayList<>();
        requestParameter.relationship.add("ytdk_relationship");
        requestParameter.question=question;
        String s = highService.smartQA(requestParameter);
        return s;
    }

}
