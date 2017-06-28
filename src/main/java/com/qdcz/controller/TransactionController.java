package com.qdcz.controller;

import com.qdcz.service.TransactionService;
import com.qdcz.sdn.entity._Vertex;
import org.neo4j.ogm.json.JSONException;
import org.neo4j.ogm.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;


@RestController
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @RequestMapping(path = "/testadd", method = RequestMethod.POST)
    public boolean testadd(@RequestBody String obj_str){

        Boolean flag=true;
        transactionService.addVertexsByPath(obj_str+"/vertex.txt","add");
        transactionService.addEdgesByPath(obj_str+"/edges.txt");
        return flag;
    }
    @RequestMapping(path = "/testdek", method = RequestMethod.POST)
    public boolean testdek(@RequestBody String obj_str){
        Boolean flag=true;
        transactionService.addVertexsByPath(obj_str+"/vertex.txt","del");
        return flag;
    }
    @CrossOrigin
    @RequestMapping(path = "/add", method = RequestMethod.POST)
    public boolean add(HttpServletRequest request){
        JSONObject obj=null;
        Map<String, String[]> parameterMap = request.getParameterMap();
        Boolean flag=true;
        try {
            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
//            System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue()[0]);

                if(entry.getKey().equals("data")){
                    obj= new JSONObject(entry.getValue()[0]);
                }
            }
            String type = obj.getString("type");
            if("addVertex".equals(type)){
                JSONObject node = obj.getJSONObject("node");
                _Vertex vertex =new _Vertex(node.getString("type"),node.getString("name"),node.getString("identity"),node.getString("root"));
                transactionService.addVertex(vertex);
            }else if("addEdge".equals(type)){
                JSONObject node = obj.getJSONObject("edge");
                transactionService.addEgde(Long.parseLong(obj.getString("startnode_id")),Long.parseLong(obj.getString("startnode_id")),node.getString("relation"));
            }

        } catch (JSONException e) {
            flag=false;
            e.printStackTrace();
        }
        return flag;
    }
    @CrossOrigin
    @RequestMapping(path = "/delete", method = RequestMethod.POST)
    public boolean delete(HttpServletRequest request){
        JSONObject obj=null;
        Map<String, String[]> parameterMap = request.getParameterMap();
        Boolean flag=true;
        try {
            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
//            System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue()[0]);

                if(entry.getKey().equals("data")){
                    obj= new JSONObject(entry.getValue()[0]);
                }
            }
            String type = obj.getString("type");
            JSONObject node = obj.getJSONObject("node");
            if("deleteVertex".equals(type)){
                transactionService.deleteVertex(Long.parseLong(node.getString("id")));
            }else if("deleteEdge".equals(type)){
                transactionService.deleteEgde(Long.parseLong(node.getString("id")));
            }
        } catch (JSONException e) {
            flag=false;
            e.printStackTrace();
        }
        return flag;
    }
    @CrossOrigin
    @RequestMapping(path = "/change", method = RequestMethod.POST)
    @ResponseBody
    public boolean change(HttpServletRequest request){
        JSONObject obj=null;
        Map<String, String[]> parameterMap = request.getParameterMap();
        Boolean flag=true;
        try {
            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
//            System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue()[0]);

                if(entry.getKey().equals("data")){
                    obj= new JSONObject(entry.getValue()[0]);
                }
            }
            String type = obj.getString("type");
            if("changeVertex".equals(type)){
                JSONObject node = obj.getJSONObject("node");
                _Vertex vertex =new _Vertex(node.getString("type"),node.getString("name"),node.getString("identity"),node.getString("root"));
                transactionService.changeVertex(Long.parseLong(obj.getString("id")),vertex);
            }else if("changeEdge".equals(type)){
                transactionService.changeEgde(Long.parseLong(obj.getString("id")),Long.parseLong(obj.getString("startnode_id")),Long.parseLong(obj.getString("endnode_id")),obj.getJSONObject("edge"));
                flag=false;
            }
        } catch (JSONException e) {
            flag=false;
            e.printStackTrace();
        }
        System.out.println(flag);
        return flag;
    }
    @CrossOrigin
    @RequestMapping(path = "/check", method = RequestMethod.POST)
    @ResponseBody
    public String check(HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();
        JSONObject obj=null;
        String result=null;
        try {
            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
//            System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue()[0]);

                if(entry.getKey().equals("data")){
                    obj= new JSONObject(entry.getValue()[0]);
                }
            }
            String type = obj.getString("type");
            if("checkByName".equals(type)){
                result=transactionService.show(obj.getString("name")).toString();
            }else if("checkByIndex".equals(type)){
                result=transactionService.check(obj.getString("keyword")).toString();
            }else if("checkByRelationship".equals(type)){
                result=transactionService.getInfoByRname(obj.getString("name")).toString();
            }else if("checkById".equals(type)){
                result=transactionService.getGraphById(Long.parseLong(obj.getString("id")),Integer.parseInt(obj.getString("depth"))).toString();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(result);

        return result;
    }
}
