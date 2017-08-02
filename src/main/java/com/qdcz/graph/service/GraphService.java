package com.qdcz.graph.service;

import com.qdcz.graph.neo4jkernel.CypherSearchService;
import com.qdcz.graph.entity._Edge;
import com.qdcz.graph.entity._Vertex;
import org.neo4j.ogm.json.JSONObject;
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
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private CypherSearchService cypherSearchService;

    @RequestMapping(path = "/testdel", method = RequestMethod.POST)
    public boolean testdek(@RequestBody String obj_str){
        Boolean flag=true;

        transactionService.addVertexsByPath(obj_str+"/vertex.txt","del");
        return flag;
    }

    @RequestMapping(path = "/testadd", method = RequestMethod.POST)
    public boolean testadd(@RequestBody String obj_str){
        System.out.println("obj_str:"+obj_str);
        Boolean flag=true;

        transactionService.addVertexsByPath(obj_str+"/vertex.txt","add");
        transactionService.addEdgesByPath(obj_str+"/edges.txt");


        return flag;
    }


    @CrossOrigin
    @RequestMapping(path = "/graphOp", method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public String graphOp(HttpServletRequest request){
        JSONObject obj=null;
        Map<String, String[]> parameterMap = request.getParameterMap();
        if(parameterMap.size()==0){
            return "param is null";
        }
        String result = "";
        try {
            if(parameterMap.containsKey("data")){
//                System.out.println(parameterMap.get("data")[0]);
                obj= new JSONObject(parameterMap.get("data")[0]);
            }else{
                System.out.println( "error param");
                return "failure";
            }


            _Vertex vertex = null;
            _Edge edge = null;
            //TODO   将请求序列化成实体


            String type = obj.getString("type");
            System.out.println(obj);



            Long id;
            JSONObject node;
            JSONObject Edge;


            switch (type){
                case "checkByName":
                    //通过名称查询
                    result = newTrasa.exactMatchQuery(obj.getJSONObject("info").getJSONObject("node").getString("name"));

                    result =transactionService.exactMatchQuery(obj.getJSONObject("info").getJSONObject("node").getString("name")).toString();
                    break;
                case "checkByNameAndDepth":
                    int depth=Integer.parseInt(obj.getJSONObject("info").getString("layer"));

                    result = newTrasa.exactMatchQuery(obj.getJSONObject("info").getJSONObject("node").getString("name"),depth);



                    result=transactionService.exactMatchQuery(obj.getJSONObject("info").getJSONObject("node").getString("name"),depth).toString();
                    break;
                case "checkByIndex":

                    result = newTrasa.indexMatchingQuery(obj.getJSONObject("info").getJSONObject("node").getString("name"));


                    result=transactionService.indexMatchingQuery(obj.getJSONObject("info").getJSONObject("node").getString("name")).toString();
                    break;
                case "checkById":
                    //TODO  应该要改成通过名称多层搜索

                    result=newTrasa.getGraphById(Long.parseLong(obj.getString("id")),Integer.parseInt(obj.getString("depth")));


                    result=transactionService.getGraphById(Long.parseLong(obj.getString("id")),Integer.parseInt(obj.getString("depth"))).toString();
                    break;
                case "addNode":
                    //新增节点



                    result = newTrasa.addVertex(vertex);





                    node = obj.getJSONObject("info").getJSONObject("node");
                    vertex =new _Vertex(node.getString("type"),node.getString("name"),node.getString("identity"),node.getString("root"));
                    id= transactionService.addVertex(vertex);
                    result = "success";
                    break;


                case "deleteNode":
                    //删除节点


                    result = newTrasa.deleteVertex(vertex);


                    node = obj.getJSONObject("info").getJSONObject("node");
                    transactionService.deleteVertex(Long.parseLong(node.getString("id")));
                    result = "success";
                    break;
                case "changeNode":
                    //修改节点


                    result = newTrasa.changeVertex(vertex);


                    node = obj.getJSONObject("info").getJSONObject("node");
                    vertex =new _Vertex(node.getString("type"),node.getString("name"),node.getString("identity"),node.getString("root"));
                    id  = transactionService.changeVertex(Long.parseLong(node.getString("id")), vertex);
                    result = "success";
                    break;

                case "addEdge":
                    //新增边


                    result = newTrasa.addEgde(edge);



                    node = obj.getJSONObject("info").getJSONObject("edge");
                    id=transactionService.addEgde(Long.parseLong(obj.getString("from")),Long.parseLong(obj.getString("to")),node.getString("relation"));
                    result = "success";
                    break;


                case "changeEdge":
                    //修改边


                    result = newTrasa.changeEgde(edge);


                    Edge =  obj.getJSONObject("info").getJSONObject("edge");
                    transactionService.changeEgde(Long.parseLong(Edge.getString("id")),Edge);
//                Long id  = transactionService.changeEgde(Long.parseLong(Edge.getString("id")),Long.parseLong(Edge.getString("from")),Long.parseLong(Edge.getString("to")),Edge);
                    result = "success";
                    break;
                case "deleteEdge":
                    //修改边


                    result = newTrasa.deleteEgde(edge);

                    Edge =  obj.getJSONObject("info").getJSONObject("edge");
                    edge = transactionService.deleteEgde(Long.parseLong(Edge.getString("id")));
                    if(edge==null) {
                        result = "fail delete Edge,has`t this id of edge by" + Edge.getString("id");
                    }
                    id =edge.getEdgeId();
                    result = "success";
                    break;

                case "addNodeEdge":
                    //新增边和终点

                    result = newTrasa.addNodeEdge(vertex,edge);


                    node = obj.getJSONObject("info").getJSONObject("node");
                    vertex =new _Vertex(node.getString("type"),node.getString("name"),node.getString("identity"),node.getString("root"));
                    Long end_id= transactionService.addVertex(vertex);
                    System.out.println("新增节点"+end_id);
                    Edge = obj.getJSONObject("info").getJSONObject("edge");
                    Long id2=transactionService.addEgde(Long.parseLong(Edge.getString("from")),end_id,Edge.getString("relation"));
                    System.out.println("新增边");
                    result = "success";
                    break;

                default:
                    System.out.println("error type:"+type);
                    result = "failure";
                    break;
            }
        } catch (Exception e) {
            result = "failure";
            e.printStackTrace();
        }
        return result;
    }
}
