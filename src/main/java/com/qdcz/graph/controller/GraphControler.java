package com.qdcz.graph.controller;

import com.qdcz.entity.Edge;
import com.qdcz.entity.Vertex;
import com.qdcz.graph.service.GraphOperateService;

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
        logger.info("bluckadd——vetexsPath:"+vetexsPath+"\tlabel:"+label+"\tedgesPath:"+edgesPath+"\trelationship："+relationship);


        return newTrasa.addVertexsByPath(vetexsPath,label,edgesPath,relationship);
    }
    @RequestMapping(path = "/testdel", method = {RequestMethod.POST,RequestMethod.GET})
    public boolean testdek(@RequestParam String vetexsPath,
                           @RequestParam String label){

        //TODO
        newTrasa.delVertexByPath(vetexsPath,label);
        return false;
    }


    @CrossOrigin
    @RequestMapping(path = "/graphOp", method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public String graphOp(HttpServletRequest request){

        JSONObject obj=null;
        String type = null;

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


            vertex = new Vertex(obj.getJSONObject("info").getJSONObject("node"));
            edge = new Edge(obj.getJSONObject("info").getJSONObject("edge"));


            type = obj.getString("type");

            vertex.setRoot("社会科学知识库");
            edge.setRoot("社会科学知识库");



            logger.debug(type+"\trequest:"+obj);

            switch (type){
                case "checkByName":
                    //通过名称查询

                    vertex.setLabel("ytdk_label");
                    edge.setRelationShip("ytdk_relationship");
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
                case "queryNodeDetail":
                    //新增边和终点

                    result = newTrasa.queryNodeDetail(vertex);

                    break;
                case "queryEdgeDetail":
                    //新增边和终点

                    result = newTrasa.queryEdgeDetail(edge);

                    break;

                default:
                    logger.error("error type:"+type);
                    result = "failure";
                    break;
            }
            logger.debug(type+"\tresult:"+result);
        } catch (Exception e) {
            logger.error(type+"\t"+e.getMessage()+"\t"+obj);
        }
        return result;
    }
}
