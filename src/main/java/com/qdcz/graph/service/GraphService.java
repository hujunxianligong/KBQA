package com.qdcz.graph.service;

import com.qdcz.graph.entity.Edge;
import com.qdcz.graph.entity.Vertex;
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
public class GraphService {

    @Autowired
    private NewTrasa newTrasa;
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
            //TODO   将请求序列化成实体


            vertex = new Vertex();
            vertex.setContent(obj.getJSONObject("info").getJSONObject("node").getString("content"));
            vertex.setIdentity(obj.getJSONObject("info").getJSONObject("node").getString("identity"));
            vertex.setName(obj.getJSONObject("info").getJSONObject("node").getString("name"));
            vertex.setRoot(obj.getJSONObject("info").getJSONObject("node").getString("root"));
            vertex.setLabel(obj.getJSONObject("info").getJSONObject("node").getString("label"));
            vertex.setType(obj.getJSONObject("info").getJSONObject("node").getString("type"));


            edge = new Edge();
            edge.setfrom(Long.parseLong(obj.getJSONObject("info").getJSONObject("edge").getString("from")));
            edge.setto(Long.parseLong(obj.getJSONObject("info").getJSONObject("edge").getString("to")));
            edge.setRelationship(obj.getJSONObject("info").getJSONObject("edge").getString("relation"));

            String type = obj.getString("type");
            System.out.println(obj);





            switch (type){
                case "checkByName":
                    //通过名称查询
                    result = newTrasa.exactMatchQuery(obj.getJSONObject("info").getJSONObject("node").getString("name"));

//                    result =transactionService.exactMatchQuery(requestParameter,obj.getJSONObject("info").getJSONObject("node").getString("name")).toString();
                    break;
                case "checkByNameAndDepth":
                    int depth=Integer.parseInt(obj.getJSONObject("info").getString("layer"));

                    result = newTrasa.exactMatchQuery(obj.getJSONObject("info").getJSONObject("node").getString("name"),depth);



//                    result=transactionService.exactMatchQuery(requestParameter,obj.getJSONObject("info").getJSONObject("node").getString("name"),depth).toString();
                    break;
                case "checkByIndex":

                    result = newTrasa.indexMatchingQuery(obj.getJSONObject("info").getJSONObject("node").getString("name"));


//                    result=transactionService.indexMatchingQuery(obj.getJSONObject("info").getJSONObject("node").getString("name")).toString();
                    break;
                case "checkById":
                    //TODO  应该要改成通过名称多层搜索

                    result=newTrasa.getGraphById(Long.parseLong(obj.getString("id")),Integer.parseInt(obj.getString("depth")));


//                    result=transactionService.getGraphById(Long.parseLong(obj.getString("id")),Integer.parseInt(obj.getString("depth"))).toString();
                    break;
                case "addNode":
                    //新增节点



                    result = newTrasa.addVertex(vertex);





//                    node = obj.getJSONObject("info").getJSONObject("node");
//                    vertex =new Vertex(node.getString("type"),node.getString("name"),node.getString("identity"),node.getString("root"));
//                    id= transactionService.addVertex(requestParameter,vertex);
//                    result = "success";
                    break;


                case "deleteNode":
                    //删除节点


                    result = newTrasa.deleteVertex(vertex);


//                    node = obj.getJSONObject("info").getJSONObject("node");
//                    transactionService.deleteVertex(requestParameter,Long.parseLong(node.getString("id")));
//                    result = "success";
                    break;
                case "changeNode":
                    //修改节点


                    result = newTrasa.changeVertex(vertex);


//                    node = obj.getJSONObject("info").getJSONObject("node");
//                    vertex =new Vertex(node.getString("type"),node.getString("name"),node.getString("identity"),node.getString("root"));
//                    id  = transactionService.changeVertex(requestParameter,Long.parseLong(node.getString("id")), vertex);
//                    result = "success";
                    break;

                case "addEdge":
                    //新增边


//                    result = newTrasa.addEgde(edge);
//                    node = obj.getJSONObject("info").getJSONObject("edge");
//                    JSONObject content=obj.getJSONObject("info").getJSONObject("edge").getJSONObject("content");
//                    id=transactionService.addEgde(requestParameter,Long.parseLong(obj.getString("from")),Long.parseLong(obj.getString("to")),node.getString("relation"),content);
//                    result = "success";
                    break;


                case "changeEdge":
                    //修改边


                    result = newTrasa.changeEgde(edge);


//                    Edge =  obj.getJSONObject("info").getJSONObject("edge");
//                    transactionService.changeEgde(requestParameter,Long.parseLong(Edge.getString("id")),Edge);
////                Long id  = transactionService.changeEgde(Long.parseLong(Edge.getString("id")),Long.parseLong(Edge.getString("from")),Long.parseLong(Edge.getString("to")),Edge);
//                    result = "success";
                    break;
                case "deleteEdge":
                    //修改边


                    result = newTrasa.deleteEgde(edge);

//                    Edge =  obj.getJSONObject("info").getJSONObject("edge");
//                    edge = transactionService.deleteEgde(requestParameter,Long.parseLong(Edge.getString("id")));
//                    if(edge==null) {
//                        result = "fail delete Edge,has`t this id of edge by" + Edge.getString("id");
//                    }
//                    id =edge.getEdgeId();
//                    result = "success";
                    break;

                case "addNodeEdge":
                    //新增边和终点

                    result = newTrasa.addNodeEdge(vertex,edge);


//                    node = obj.getJSONObject("info").getJSONObject("node");
//                    vertex =new Vertex(node.getString("type"),node.getString("name"),node.getString("identity"),node.getString("root"));
//                    Long end_id= transactionService.addVertex(requestParameter,vertex);
//                    System.out.println("新增节点"+end_id);
//                    Edge = obj.getJSONObject("info").getJSONObject("edge");
//                    content = obj.getJSONObject("info").getJSONObject("edge").getJSONObject("content");
//                    Long id2=transactionService.addEgde(requestParameter,Long.parseLong(Edge.getString("from")),end_id,Edge.getString("relation"),content);
//                    System.out.println("新增边");
//                    result = "success";
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
