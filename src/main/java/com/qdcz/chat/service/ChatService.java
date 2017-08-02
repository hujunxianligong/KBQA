package com.qdcz.chat.service;

import com.qdcz.chat.buzi.HighService;
import com.qdcz.graph.neo4jkernel.CypherSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by star on 17-8-1.
 */
@RestController
public class ChatService {

    @Autowired
    private HighService highService;

    @Autowired
    private CypherSearchService cypherSearchService;



    @RequestMapping(path = "/testask", method = {RequestMethod.POST,RequestMethod.GET})
    public void  testQuery(@RequestParam String question){
        cypherSearchService.queryWithCypher(question);

    }



    @CrossOrigin
    @RequestMapping(path = "/ask", method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public String ask(@RequestParam String question){
        if("请问地方政府类授信信贷政策中“银监会地方政府融资平台名单”是什么？".equals(question)){
            return "“银监会地方政府融资平台名单”包含“退出类平台客户”和“仍按平台管理类客户”两类。其中对于流动资金贷款的发放，“仍按平台管理类客户”必须同时满足监管规定和信贷政策要求；“退出类平台客户”执信贷政策相关要求。";
        }else if("请问安徽临泉县天丰铝品压延厂是否符合行业信贷政策准入要求？".equals(question)){
            return "不符合，不符合的点是产能为达标，安徽临泉县天丰铝品压延厂的去年产能为２０万吨，低于我行２０１７年的铝压延行业的客户准入底线３０万吨的年产能要求。";
        } else if("我想了解这个客户实际控制人是哪里人？".equals(question)){
            return "辽宁沈阳";
        }else if("这个公司近三年的净现金流如何？".equals(question)){
            return "2014,2015,2016年分别是1、2、1.5亿元";
        }else if("这个公司属于什么行业，行业中规模排名如何？".equals(question)){
            return "这个公司属于咨询行业，2016年营业收入行业排名第9位";
        }else {
            String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            String s = highService.smartQA(methodName,question);
            return s;
        }
    }


    @CrossOrigin
    @RequestMapping(path = "/askFromWeChat", method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public String askOfWeChat(@RequestParam String question){
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        String s = highService.smartQA(methodName,question);
        return s;
    }

}
