package com.qdcz.controller;

import com.qdcz.config.MyConnConfigure;
import com.qdcz.neo4jkernel.CypherSearchService;
import com.qdcz.neo4jkernel.generic.MyLabels;
import com.qdcz.sdn.entity.Edge;
import com.qdcz.service.bean.RequestParameter;
import com.qdcz.sdn.entity.Vertex;
import com.qdcz.tools.CommonTool;
import com.qdcz.service.high.InstrDemandService;
import com.qdcz.service.high.TransactionService;
import com.qdcz.sdn.entity._Vertex;
import org.neo4j.ogm.json.JSONException;
import org.neo4j.ogm.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.*;


@RestController
public class TransactionController {




    @Autowired
    private TransactionService transactionService;
    @Autowired
    private InstrDemandService instrDemandService;
    @Autowired
    private CypherSearchService cypherSearchService;




    @RequestMapping(path = "/testjianxin", method = RequestMethod.POST)
    public boolean testjianxin(@RequestBody String obj_str){//暂时废弃
        Boolean flag=true;
        List<String> getfile = CommonTool.getfile("/home/hadoop/wnd/usr/leagal/建新/点json");
        for(String str:getfile){
            try {
                JSONObject obj=new JSONObject(str);
                instrDemandService.addVertex(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    @RequestMapping(path = "/testadd", method = RequestMethod.POST)
    public boolean testadd(@RequestBody String obj_str){
        System.out.println("obj_str:"+obj_str);
        RequestParameter requestParameter =null;
        requestParameter =new RequestParameter();
        requestParameter.label="law";
        Boolean flag=true;
        transactionService.addVertexsByPath(requestParameter,obj_str+"/vertex.txt","add");
        transactionService.addEdgesByPath(requestParameter,obj_str+"/edges.txt");
        return flag;
    }
    @RequestMapping(path = "/testdel", method = RequestMethod.POST)
    public boolean testdek(@RequestBody String obj_str){
        Boolean flag=true;
        RequestParameter requestParameter =null;
        requestParameter =new RequestParameter();
        requestParameter.label="law";
        transactionService.addVertexsByPath(requestParameter,obj_str+"/vertex.txt","del");
        return flag;
    }
    @RequestMapping(path = "/testask", method = {RequestMethod.POST,RequestMethod.GET})
    public void  testQuery(@RequestParam String question){
       cypherSearchService.queryWithCypher(question);

    }

    @CrossOrigin
    @RequestMapping(path = "/graphOp", method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public String graphOp(HttpServletRequest request){
        JSONObject obj=null;
        String project=null;
        RequestParameter requestParameter =null;
        Map<String, String[]> parameterMap = request.getParameterMap();
        if(parameterMap.size()==0){
            return "param is null";
        }
        try {
            if(parameterMap.containsKey("data")){
//                System.out.println(parameterMap.get("data")[0]);
                obj= new JSONObject(parameterMap.get("data")[0]);
            }else{
                System.out.println( "error param");
                return "failure";
            }
            System.out.println(obj);
            if(obj.has("project")){
                project= obj.getString("project");
                String[] hasProjects = MyConnConfigure.project;
                boolean flag=false;
                for(String hasProject:hasProjects){
                    if(hasProject.equals(project)){
                        flag =true;
                        break;
                    }
                }
                if(!flag){
                    return "hasn`t project "+project;
                }
            }
            requestParameter =new RequestParameter();
            requestParameter.label=project;
            String type = obj.getString("type");
            if("checkByName".equals(type)){
                //通过名称查询
                String graphName = obj.getJSONObject("info").getJSONObject("node").getString("name");
                String result=transactionService.exactMatchQuery(requestParameter,graphName).toString();
                System.out.println(result);
                return result;
            }else if("checkByNameAndDepth".equals(type)){
                int depth=Integer.parseInt(obj.getJSONObject("info").getString("layer"));
                String result=transactionService.exactMatchQuery(requestParameter,obj.getJSONObject("info").getJSONObject("node").getString("name"),depth).toString();
                return result;
            }
            else if("checkByIndex".equals(type)){
                String result=transactionService.indexMatchingQuery( obj.getJSONObject("info").getJSONObject("node").getString("name")).toString();
                System.out.println(result);
                return result;
            }
            else if("checkById".equals(type)){
                int depth=Integer.parseInt(obj.getJSONObject("info").getString("layer"));
                String result=transactionService.getGraphById(Long.parseLong(obj.getJSONObject("info").getJSONObject("node").getString("id")),depth).toString();
                return result;
            }
            if("addNode".equals(type)){
                //新增节点
                JSONObject node = obj.getJSONObject("info").getJSONObject("node");
                Vertex vertex =new _Vertex(node.getString("type"),node.getString("name"),node.getString("identity"),node.getString("root"),node.getJSONObject("content"));

                Long id= transactionService.addVertex(requestParameter,vertex);
                return "success";
            }else if("addEdge".equals(type)){
                //新增边 TODO test
                JSONObject node = obj.getJSONObject("info").getJSONObject("edge");
                Long id=transactionService.addEgde(requestParameter,Long.parseLong(obj.getString("from")),Long.parseLong(obj.getString("to")),node.getString("relation"),node.getJSONObject("content"));
                return "success";
            }else if("addNodeEdge".equals(type)){
                //新增边和终点
                JSONObject node = obj.getJSONObject("info").getJSONObject("node");
                Vertex vertex =new _Vertex(node.getString("type"),node.getString("name"),node.getString("identity"),node.getString("root"),node.getJSONObject("content"));
                Long end_id= transactionService.addVertex(requestParameter,vertex);
                System.out.println("新增节点"+end_id);
                JSONObject edge = obj.getJSONObject("info").getJSONObject("edge");
                Long id2=transactionService.addEgde(requestParameter,Long.parseLong(edge.getString("from")),end_id,edge.getString("relation"),edge.getJSONObject("content"));
                System.out.println("新增边");
                return "success";
            }else if("deleteNode".equals(type)){
                //删除节点
                JSONObject node = obj.getJSONObject("info").getJSONObject("node");
                transactionService.deleteVertex(requestParameter,Long.parseLong(node.getString("id")));
                return "success";
            }if("changeNode".equals(type)){
                //修改节点
                JSONObject node = obj.getJSONObject("info").getJSONObject("node");
                Vertex vertex =new _Vertex(node.getString("type"),node.getString("name"),node.getString("identity"),node.getString("root"),node.getJSONObject("content"));
                Long id  = transactionService.changeVertex(requestParameter,Long.parseLong(node.getString("id")), vertex);
                return "success";
            }else if("changeEdge".equals(type)){
                //修改边   TODO test
                JSONObject Edge =  obj.getJSONObject("info").getJSONObject("edge");
                transactionService.changeEgde(requestParameter,Long.parseLong(Edge.getString("id")),Edge);
//                Long id  = transactionService.changeEgde(Long.parseLong(Edge.getString("id")),Long.parseLong(Edge.getString("from")),Long.parseLong(Edge.getString("to")),Edge);
                return "success";
            }
            else if("deleteEdge".equals(type)){
                //修改边   TODO test
                JSONObject edgeObj =  obj.getJSONObject("info").getJSONObject("edge");
                Edge edge = transactionService.deleteEgde(requestParameter,Long.parseLong(edgeObj.getString("id")));
                if(edge==null)
                    return "fail delete Edge,has`t this id of edge by"+edgeObj.getString("id");
                Long id =edge.getEdgeId();
                return "success";
            }
            else{
                System.out.println("error type:"+type);
                return "failure";
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return "failure";
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
                String[] hasProjects = MyConnConfigure.project;
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
        String s = transactionService.smartQA(requestParameter,"");
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
        String s = transactionService.smartQA(requestParameter,question);
        return s;
    }


//    @CrossOrigin
////    @RequestMapping(path = "/check")
//    @ResponseBody
//    public String check(HttpServletRequest request){
//        Map<String, String[]> parameterMap = request.getParameterMap();
//        JSONObject obj=null;
//        String result=null;
//        try {
//            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
////            System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue()[0]);
//                if(entry.getKey().equals("data")){
//                    obj= new JSONObject(entry.getValue()[0]);
//                }
//            }
////            System.out.println("obj:"+obj);
//            String type = obj.getString("type");
//            if("checkByName".equals(type)){
//                result=transactionService.exactMatchQuery(obj.getString("name")).toString();
//            }else if("checkByIndex".equals(type)){
//                result=transactionService.indexMatchingQuery(obj.getString("keyword")).toString();
//            }else if("checkByRelationship".equals(type)){
//                result=transactionService.getInfoByRname(obj.getString("name")).toString();
//            }else if("checkById".equals(type)){
//                result=transactionService.getGraphById(Long.parseLong(obj.getString("id")),Integer.parseInt(obj.getString("depth"))).toString();
//            }
//            else if("checkByKeyword".equals(type)){
//                result=instrDemandService.queryF(obj.getString("keyword"));
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        System.out.println(result);
//        return result;
//    }
}
