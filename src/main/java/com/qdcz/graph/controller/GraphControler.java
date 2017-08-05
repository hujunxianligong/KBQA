package com.qdcz.graph.controller;

import com.qdcz.graph.entity.Edge;
import com.qdcz.graph.entity.Vertex;
import com.qdcz.graph.service.GraphOperateService;
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
public class GraphControler {

    @Autowired
    private GraphOperateService newTrasa;
//    @Autowired



    @RequestMapping(path = "/testadd", method = {RequestMethod.POST,RequestMethod.GET})
    public boolean testadd(@RequestParam String obj_str){
        System.out.println("obj_str:"+obj_str);
        RequestParameter requestParameter =null;
        requestParameter =new RequestParameter();
        requestParameter.label="law";
        Boolean flag=true;

//        transactionService.addVertexsByPath(requestParameter,obj_str+"/vertex.txt","add");
//        transactionService.addEdgesByPath(requestParameter,obj_str+"/edges.txt");

        return flag;
    }
    @RequestMapping(path = "/testdel", method = {RequestMethod.POST,RequestMethod.GET})
    public boolean testdek(@RequestBody String obj_str){
        Boolean flag=true;
        RequestParameter requestParameter =null;
        requestParameter =new RequestParameter();
        requestParameter.label="law";
//        transactionService.addVertexsByPath(requestParameter,obj_str+"/vertex.txt","del");
        return flag;
    }


    @CrossOrigin
    @RequestMapping(path = "/graphOp", method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public String graphOp(HttpServletRequest request){
        JSONObject obj=null;
        RequestParameter requestParameter =null;
        Map<String, String[]> parameterMap = request.getParameterMap();
        if(parameterMap.size()==0){
            return "param is null";
        }

        String result =null;
        try {
            if(parameterMap.containsKey("data")){
                System.out.println(parameterMap.get("data")[0]);
                obj= new JSONObject(parameterMap.get("data")[0]);
            }else{
                System.out.println( "error param");
                return "failure";
            }
            Vertex vertex = null;
            Edge edge = null;


            System.out.println(obj);
            //TODO   将请求序列化成实体

//            {"project":"think-tank","type":"addNode","info":{"node":{"identity":"","root":"社会科学知识库","name":"测试","id":"","type":""},"edge":{"root":"","from":"","to":"","id":"","relation":""}}}


            vertex = new Vertex();

            vertex.setId(obj.getJSONObject("info").getJSONObject("node").getString("id"));
            vertex.setContent("");
            vertex.setName(obj.getJSONObject("info").getJSONObject("node").getString("name"));
            vertex.setRoot(obj.getJSONObject("info").getJSONObject("node").getString("root"));
            vertex.setLabel("test");
            vertex.setType(obj.getJSONObject("info").getJSONObject("node").getString("type"));


            edge = new Edge();

            edge.setRelationShip("gra");
            edge.setId(obj.getJSONObject("info").getJSONObject("edge").getString("id"));
            edge.setFrom(obj.getJSONObject("info").getJSONObject("edge").getString("from"));
            edge.setTo(obj.getJSONObject("info").getJSONObject("edge").getString("to"));
            edge.setName(obj.getJSONObject("info").getJSONObject("edge").getString("relation"));

            String type = obj.getString("type");



            System.out.println(type+"\t"+obj);
            vertex.setRoot("社会科学知识库");
            edge.setRoot("社会科学知识库");

            switch (type){
                case "checkByName":
                    //通过名称查询
                    result = newTrasa.exactMatchQuery(vertex);

                    break;
                case "checkByNameAndDepth":
                    int depth=Integer.parseInt(obj.getJSONObject("info").getString("layer"));

                    result = newTrasa.exactMatchQuery(obj.getJSONObject("info").getJSONObject("node").getString("name"),depth);

                    break;
                case "checkByIndex":

                    result = newTrasa.indexMatchingQuery(obj.getJSONObject("info").getJSONObject("node").getString("name"));

                    break;
                case "checkById":
                    //TODO  应该要改成通过名称多层搜索

                    result=newTrasa.getGraphById(Long.parseLong(obj.getString("id")),Integer.parseInt(obj.getString("depth")));

                    break;
                case "addNode":
                    //新增节点

                    result = newTrasa.addVertex(vertex);

                    break;


                case "deleteNode":
                    //删除节点

                    result = newTrasa.deleteVertex(vertex);


                    break;
                case "changeNode":
                    //修改节点


                    result = newTrasa.changeVertex(vertex);


                    break;

                case "addEdge":
                    //新增边


                    result = newTrasa.addEgde(edge);
                    break;


                case "changeEdge":
                    //修改边


                    result = newTrasa.changeEgde(edge);

                    break;
                case "deleteEdge":
                    //修改边


                    result = newTrasa.deleteEgde(edge);

                    break;

                case "addNodeEdge":
                    //新增边和终点

                    result = newTrasa.addNodeEdge(vertex,edge);

                    break;

                default:
                    System.out.println("error type:"+type);
                    result = "failure";
                    break;
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return result;
    }
}
