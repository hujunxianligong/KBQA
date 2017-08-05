package com.qdcz.chat.controller;

import com.qdcz.chat.service.HighService;
import com.qdcz.service.bean.RequestParameter;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

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
    public String ask(HttpServletRequest request){
        JSONObject obj=null;
        String project=null;
        RequestParameter requestParameter =null;
        Map<String, String[]> parameterMap = request.getParameterMap();
        if(parameterMap.size()==0){
            return "param is null";
        }
        try {
            if (parameterMap.containsKey("data")) {
//                System.out.println(parameterMap.get("data")[0]);
                obj = new JSONObject(parameterMap.get("data")[0]);
            } else {
                System.out.println("error param");
                return "failure";
            }
            System.out.println(obj);
            if (obj.has("project")) {
                project = obj.getString("project");




                //TODO

                String[] hasProjects = null;
//                String[] hasProjects = MyConnConfigure.project;



                boolean flag = false;
                for (String hasProject : hasProjects) {
                    if (hasProject.equals(project)) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    return "hasn`t project " + project;
                }
            }
        }
        catch (Exception e) {

            e.printStackTrace();
        }
        requestParameter =new RequestParameter();
        requestParameter.label="law";
        requestParameter.requestSource = Thread.currentThread().getStackTrace()[1].getMethodName();
        String s = highService.smartQA(requestParameter,"");
        return s;
    }


    @CrossOrigin
    @RequestMapping(path = "/askFromWeChat", method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public String askOfWeChat(@RequestParam String question){
        RequestParameter requestParameter =null;
        requestParameter =new RequestParameter();
        requestParameter.label="law";
        requestParameter.requestSource = Thread.currentThread().getStackTrace()[1].getMethodName();
        String s = highService.smartQA(requestParameter,question);
        return s;
    }

}
