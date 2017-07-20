package com.qdcz.controller;

import com.qdcz.sdn.entity._Edge;
import com.qdcz.tools.CommonTool;
import com.qdcz.service.high.InstrDemandService;
import com.qdcz.service.high.TransactionService;
import com.qdcz.sdn.entity._Vertex;
import org.neo4j.ogm.json.JSONException;
import org.neo4j.ogm.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;


@RestController
public class TransactionController {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private InstrDemandService instrDemandService;
    @RequestMapping(path = "/testjianxin", method = RequestMethod.POST)
    public boolean testjianxin(@RequestBody String obj_str){

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
        Boolean flag=true;
        transactionService.addVertexsByPath(obj_str+"/vertex.txt","add");
        transactionService.addEdgesByPath(obj_str+"/edges.txt");
        return flag;
    }
    @RequestMapping(path = "/testdel", method = RequestMethod.POST)
    public boolean testdek(@RequestBody String obj_str){
        Boolean flag=true;

        transactionService.addVertexsByPath(obj_str+"/vertex.txt","del");
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
        try {
            if(parameterMap.containsKey("data")){
//                System.out.println(parameterMap.get("data")[0]);
                obj= new JSONObject(parameterMap.get("data")[0]);
            }else{
                System.out.println( "error param");
                return "failure";
            }
            String type = obj.getString("type");
            System.out.println(obj);
            if("checkByName".equals(type)){
                //通过名称查询
                String result=transactionService.exactMatchQuery(obj.getJSONObject("info").getJSONObject("node").getString("name")).toString();
                System.out.println(result);
                return result;
            }else if("checkByNameAndDepth".equals(type)){
                int depth=Integer.parseInt(obj.getJSONObject("info").getString("layer"));
                String result=transactionService.exactMatchQuery(obj.getJSONObject("info").getJSONObject("node").getString("name"),depth).toString();
                return result;
            }
            else if("checkByIndex".equals(type)){
                String result=transactionService.indexMatchingQuery(obj.getJSONObject("info").getJSONObject("node").getString("name")).toString();
                System.out.println(result);
                return result;
            }
            else if("checkById".equals(type)){
                //TODO  应该要改成通过名称多层搜索
                String result=transactionService.getGraphById(Long.parseLong(obj.getString("id")),Integer.parseInt(obj.getString("depth"))).toString();
                return result;
            }
            if("addNode".equals(type)){
                //新增节点
                JSONObject node = obj.getJSONObject("info").getJSONObject("node");
                _Vertex vertex =new _Vertex(node.getString("type"),node.getString("name"),node.getString("identity"),node.getString("root"));
                Long id= transactionService.addVertex(vertex);
                return "success";
            }else if("addEdge".equals(type)){
                //新增边 TODO test
                JSONObject node = obj.getJSONObject("info").getJSONObject("edge");
                Long id=transactionService.addEgde(Long.parseLong(obj.getString("from")),Long.parseLong(obj.getString("to")),node.getString("relation"));
                return "success";
            }else if("addNodeEdge".equals(type)){
                //新增边和终点
                JSONObject node = obj.getJSONObject("info").getJSONObject("node");
                _Vertex vertex =new _Vertex(node.getString("type"),node.getString("name"),node.getString("identity"),node.getString("root"));
                Long end_id= transactionService.addVertex(vertex);
                System.out.println("新增节点"+end_id);
                JSONObject edge = obj.getJSONObject("info").getJSONObject("edge");
                Long id2=transactionService.addEgde(Long.parseLong(edge.getString("from")),end_id,edge.getString("relation"));
                System.out.println("新增边");
                return "success";
            }else if("deleteNode".equals(type)){
                //删除节点
                JSONObject node = obj.getJSONObject("info").getJSONObject("node");
                transactionService.deleteVertex(Long.parseLong(node.getString("id")));
                return "success";
            }if("changeNode".equals(type)){
                //修改节点
                JSONObject node = obj.getJSONObject("info").getJSONObject("node");
                _Vertex vertex =new _Vertex(node.getString("type"),node.getString("name"),node.getString("identity"),node.getString("root"));
                Long id  = transactionService.changeVertex(Long.parseLong(node.getString("id")), vertex);
                return "success";
            }else if("changeEdge".equals(type)){
                //修改边   TODO test
                JSONObject Edge =  obj.getJSONObject("info").getJSONObject("edge");
                transactionService.changeEgde(Long.parseLong(Edge.getString("id")),Edge);
//                Long id  = transactionService.changeEgde(Long.parseLong(Edge.getString("id")),Long.parseLong(Edge.getString("from")),Long.parseLong(Edge.getString("to")),Edge);
                return "success";
            }
            else if("deleteEdge".equals(type)){
                //修改边   TODO test
                JSONObject Edge =  obj.getJSONObject("info").getJSONObject("edge");
                _Edge edge = transactionService.deleteEgde(Long.parseLong(Edge.getString("id")));
                if(edge==null)
                    return "fail delete Edge,has`t this id of edge by"+Edge.getString("id");
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
            String s = transactionService.smartQA(question);
            return s;
        }
//        String result=instrDemandService.queryF(question);
//        return result;

    }

    @CrossOrigin
//    @RequestMapping(path = "/check")
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
//            System.out.println("obj:"+obj);
            String type = obj.getString("type");
            if("checkByName".equals(type)){
                result=transactionService.exactMatchQuery(obj.getString("name")).toString();
            }else if("checkByIndex".equals(type)){
                result=transactionService.indexMatchingQuery(obj.getString("keyword")).toString();
            }else if("checkByRelationship".equals(type)){
                result=transactionService.getInfoByRname(obj.getString("name")).toString();
            }else if("checkById".equals(type)){
                result=transactionService.getGraphById(Long.parseLong(obj.getString("id")),Integer.parseInt(obj.getString("depth"))).toString();
            }
            else if("checkByKeyword".equals(type)){
                result=instrDemandService.queryF(obj.getString("keyword"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(result);
        return result;
    }
}
