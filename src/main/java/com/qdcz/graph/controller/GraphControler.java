package com.qdcz.graph.controller;

import com.qdcz.graph.entity.Edge;
import com.qdcz.graph.entity.Vertex;
import com.qdcz.graph.service.GraphOperateService;
import com.qdcz.service.bean.RequestParameter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
//import java.util.logging.Logger;

/**
 * Created by star on 17-8-1.
 */
@RestController
public class GraphControler {

    private Logger logger = LogManager.getLogger(GraphControler.class.getSimpleName());
    @Autowired
    private GraphOperateService newTrasa;
//    @Autowired



    @RequestMapping(path = "/bluckadd", method = {RequestMethod.POST,RequestMethod.GET})
    public boolean testadd(@RequestParam String vetexsPath,
                           @RequestParam String label,
                           @RequestParam String edgesPath,
                           @RequestParam String relationship){
        System.out.println("vetexsPath:"+vetexsPath+"\tlabel:"+label+"\tedgesPath:"+edgesPath+"\trelationship："+relationship);


        return newTrasa.addVertexsByPath(vetexsPath,label,edgesPath,relationship);
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
                obj= new JSONObject(parameterMap.get("data")[0]);
            }else{
                System.out.println( "error param");
                return "failure";
            }
            Vertex vertex = null;
            Edge edge = null;

            System.out.println(obj);

            vertex = new Vertex(obj.getJSONObject("info").getJSONObject("node"));
            edge = new Edge(obj.getJSONObject("info").getJSONObject("edge"));

            vertex.setLabel("test");
            edge.setRelationShip("gra");

            String type = obj.getString("type");

            logger.info(type+"\t"+obj);
            logger.error(type+"\t"+obj);
//            System.out.println(type+"\t"+obj);
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
