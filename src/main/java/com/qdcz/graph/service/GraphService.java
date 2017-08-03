package com.qdcz.graph.service;

import com.qdcz.config.MyConnConfigure;
import com.qdcz.graph.neo4jkernel.CypherSearchService;
import com.qdcz.graph.neo4jkernel.entity.Edge;
import com.qdcz.graph.neo4jkernel.entity.Vertex;
import com.qdcz.service.bean.RequestParameter;
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
    private TransactionService transactionService;

    @Autowired
    private CypherSearchService cypherSearchService;


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
                Vertex vertex =new Vertex(node.getString("type"),node.getString("name"),node.getString("identity"),node.getString("root"),node.getJSONObject("content"));

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
                Vertex vertex =new Vertex(node.getString("type"),node.getString("name"),node.getString("identity"),node.getString("root"),node.getJSONObject("content"));
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
                Vertex vertex =new Vertex(node.getString("type"),node.getString("name"),node.getString("identity"),node.getString("root"),node.getJSONObject("content"));
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
}
