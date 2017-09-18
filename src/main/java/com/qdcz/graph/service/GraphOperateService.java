package com.qdcz.graph.service;

import com.qdcz.common.CommonTool;
import com.qdcz.conf.DatabaseConfiguration;
import com.qdcz.conf.LoadConfigListener;
import com.qdcz.entity.Graph;
import com.qdcz.graph.interfaces.IGraphBuzi;
import com.qdcz.graph.neo4jcypher.service.Neo4jCYService;
import com.qdcz.graph.tools.ResultBuilder;
import com.qdcz.index.elsearch.service.ElasearchService;
import com.qdcz.index.interfaces.IIndexService;
import com.qdcz.entity.Edge;
import com.qdcz.entity.Vertex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Path;
import org.neo4j.driver.v1.types.Relationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

/**
 * Created by star on 17-8-2.
 */
@Service
public class GraphOperateService {
    private Logger logger =  LogManager.getLogger(GraphOperateService.class.getSimpleName());

    @Autowired
    @Qualifier("elasearchService")
    private IIndexService indexBuzi;

    @Autowired
    @Qualifier("neo4jCypherService")
    private IGraphBuzi graphBuzi;


    public static void main(String[] args) {
        LoadConfigListener loadConfigListener = new LoadConfigListener();
        loadConfigListener.setSource_dir("/dev/");
        loadConfigListener.contextInitialized(null);



        GraphOperateService instance = new GraphOperateService();
        instance.indexBuzi = new ElasearchService();
        instance.graphBuzi = new Neo4jCYService();

        String vetexsPath = "/media/star/Doc/工作文档/智能小招/vertex.txt";
        String label = "test";
        String edgesPath = "/media/star/Doc/工作文档/智能小招/edges.txt";
        String relationship = "gra";

    //    instance.addVertexsByPath(vetexsPath,label,edgesPath,relationship);

//        instance.indexMatchingQuery("银团贷款业务","xz");
    }



    /**
     * 创建节点
     * @param vertex
     * @return
     */
    public String addVertex(Vertex vertex){
        String graphId = graphBuzi.addVertex(vertex);

        System.out.println("graphId:"+graphId);

        vertex.setId(graphId);

        indexBuzi.addOrUpdateIndex(vertex);

        return "success";
    }

    /**
     * 删除节点通过id
     * @param vertex
     * @return
     */
    public String deleteVertex(Vertex vertex){

        graphBuzi.deleteVertex(vertex);

        indexBuzi.delIndex(vertex);

        return "success";
    }


    /**
     * 修改节点，通过id
     * @param vertex
     * @return
     */
    public String changeVertex(Vertex vertex){
        graphBuzi.changeVertex(vertex);

        indexBuzi.addOrUpdateIndex(vertex);

        return "success";
    }


    /**
     * 新增边
     * @param edge
     * @return
     */
    public String addEgde(Edge edge){
        String graphId = graphBuzi.addEdges(edge);

        edge.setId(graphId);


        indexBuzi.addOrUpdateIndex(edge);
        return "success";
    }

    /**
     * 删除边
     * @param edge
     * @return
     */
    public String deleteEgde(Edge edge){
        graphBuzi.deleteEdge(edge);

        indexBuzi.delIndex(edge);

        return "success";
    }


    /**
     * 修改边,通过id
     * @param edge
     * @return
     */
    public String changeEgde(Edge edge){
        graphBuzi.changeEdge(edge);

        indexBuzi.addOrUpdateIndex(edge);

        return "success";
    }


    /**
     * 新增边和终点
     * @param vertex
     * @param edge
     * @return
     */
    public String addNodeEdge(Vertex vertex, Edge edge) throws Exception {

        String vertexId = graphBuzi.addVertex(vertex);

        vertex.setId(vertexId);

        indexBuzi.addOrUpdateIndex(vertex);


        edge.setRelationShip(DatabaseConfiguration.getRelationshipByLabel(vertex.getLabel()));


        edge.setTo(vertexId);
        String edgeId = graphBuzi.addEdges(edge);

        edge.setId(edgeId);

        indexBuzi.addOrUpdateIndex(edge);

        return "success";
    }


    /**
     * 通过名称查询
     * @param vertex
     * @return
     */
    public String exactMatchQuery(Vertex vertex,int depth){

        List<Path> paths=null;
        try {
            paths = graphBuzi.bfExtersion(vertex, depth);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ResultBuilder resultBuilder=new ResultBuilder();
        JSONObject result = resultBuilder.graphResult(paths);
        resultBuilder=null;
        return result.toString();
    }
    /**
     * 获取结果集合中边的名称列表
     * @param result
     * @return
     */
    private JSONObject getRelationshipName(JSONObject result){
        if(result.has("nodes")) {
            JSONArray jsonArray = result.getJSONArray("nodes");
            Map maps=new HashMap<Integer,JSONObject>();
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String type = jsonObject.getString("type");
                String name =null;
                if("com".equals(type)){
                    name = "公司名";
                }else if("attribute".equals(type)){
                    name = "属性";
                }else
                {
                    name=jsonObject.getString("name");
                }
                JSONObject typeobj=new JSONObject();
                typeobj.put("type_en",type);
                typeobj.put("type_cn",name);
                if(!maps.containsKey(typeobj.toString().hashCode())){
                    maps.put(typeobj.toString().hashCode(),typeobj);
                }
            }
            JSONArray results=new JSONArray();
            for (Object value : maps.values()) {
                //  System.out.println("Value = " + value);
                results.put(value);
            }
            result.put("nodeTypeName",results);
        }
        return result;
    }

    /**
     * 无向搜索
     * @param name
     * @param graphNames
     * @return
     */
    public String directedBfExtersion(String name,JSONArray graphNames,int depth){
        ResultBuilder resultBuilder= new ResultBuilder();
        JSONObject result=new JSONObject();
        for(int i=0;i<graphNames.length();i++){
            String graphName = graphNames.getString(i);
            try {
                Graph graph = DatabaseConfiguration.getGraph(graphName);
                String label = graph.getLabel();
                Vertex vertex=new Vertex();
                vertex.setLabel(label);
                vertex.setName(name);
                List<Path> paths=null;
                try {
                    paths = graphBuzi.directedBfExtersion(vertex, depth);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                JSONObject object = resultBuilder.graphResult(paths);
                result = resultBuilder.mergeResult(result, object);



                paths = graphBuzi.bfExtersion(vertex, 1);

                object = resultBuilder.graphResult(paths);
                result= resultBuilder.mergeResult(result, object);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        result = resultBuilder.reDupResult(result);
        result=getRelationshipName(result);
        return result.toString();
    }




    /**
     * 根据精准name查图
     * @param name
     * @param graphNames
     * @return
     */
    public String exactMatchQuery(String name,JSONArray graphNames,int depth){
        ResultBuilder resultBuilder= new ResultBuilder();
        JSONObject result=new JSONObject();
        for(int i=0;i<graphNames.length();i++){
            String graphName = graphNames.getString(i);
            try {
                Graph graph = DatabaseConfiguration.getGraph(graphName);
                String label = graph.getLabel();
                Vertex vertex=new Vertex();
                vertex.setLabel(label);
                vertex.setName(name);
                List<Path> paths=null;
                try {
                    paths = graphBuzi.bfExtersion(vertex, depth);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                JSONObject object = resultBuilder.graphResult(paths);
                result = resultBuilder.mergeResult(result, object);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        result = resultBuilder.reDupResult(result);
        result=getRelationshipName(result);
        return result.toString();
    }

    /**
     * 索引匹配查询
     * @param keyword
     * @return
     */
    public String indexMatchingQuery(String keyword,JSONArray graphNames)  {
        JSONObject result=new JSONObject();
        ResultBuilder resultBuilder= new ResultBuilder();
        for(int i=0;i<graphNames.length();i++){
            String graphName = graphNames.getString(i);
            try {
                Graph graph = DatabaseConfiguration.getGraph(graphName);
                String label = graph.getLabel();
                Set<Map.Entry<String, JSONObject>> entries = indexBuzi.queryByName(label, keyword, 0, keyword.length() * 3 + 1).entrySet();
                for (Map.Entry<String, JSONObject> entry : entries) {
                    JSONObject value = entry.getValue();
                    Map map= CommonTool.jsonToMap(value);
                    Vertex vertex=new Vertex();
                    CommonTool.transMap2Bean(map,vertex);
                    vertex.setLabel(label);
                    List<Path> paths = graphBuzi.bfExtersion(vertex, 3);

                    JSONObject object = resultBuilder.graphResult(paths);
                    result= resultBuilder.mergeResult(result, object);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        result = resultBuilder.reDupResult(result);
        result=getRelationshipName(result);
        return result.toString();
    }


    /**
     * 根据 id与深度返回结果
     * @param id
     * @param depth
     * @return
     */
    public String getGraphById(Long id,int depth){
        List<Path> paths=null;
        try {
            paths = graphBuzi.checkGraphById(id, depth);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ResultBuilder resultBuilder=new ResultBuilder();
        JSONObject result = resultBuilder.graphResult(paths);
        result=getRelationshipName(result);
        resultBuilder=null;
        return result.toString();

    }



    /**
     *批量导入或删除数据节点
     */
    public boolean addVertexsByPath2( String vertexfilePath,String nodeLabel,String edgefilePath,String edgeRelationship){//批量导入／删除数据节点

        FileReader re = null;
        try {


            long time = System.currentTimeMillis();

            try {

                System.out.println("开始导入点");
                Map<String,String> identity_id = graphBuzi.batchInsertVertex(nodeLabel,"vertex.csv");


                System.out.println("开始elasearch中导入点："+(System.currentTimeMillis()-time)/1000+"秒");
                time = System.currentTimeMillis();

                indexBuzi.bluckByFile(nodeLabel,vertexfilePath,identity_id);

            } catch (Exception e) {
                logger.error("批量增点错误："+e.getMessage()+"\n");
                throw e;
            }


            try {

                System.out.println("开始neo4j导入边："+(System.currentTimeMillis()-time)/1000+"秒");
                time = System.currentTimeMillis();

                Map<String,String> identity_id = graphBuzi.batchInsertEdge(edgeRelationship,"edges.csv");

                System.out.println("开始elasearch中导入边："+(System.currentTimeMillis()-time)/1000+"秒");
                time = System.currentTimeMillis();

//                indexBuzi.bluckByFile(edgeRelationship,edgefilePath,identity_id);

            } catch (Exception e) {

                logger.error("批量增边错误："+e.getMessage()+"\n");
                throw e;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("完毕！");
        return true;
    }



    /**
     *批量导入或删除数据节点
     */
    @Deprecated
    public boolean addVertexsByPath( String vertexfilePath,String nodeLabel,String edgefilePath,String edgeRelationship){//批量导入／删除数据节点

        FileReader re = null;
        try {
            Map<String,String> key_value =  new HashMap<>();
            Scanner sc= new Scanner(new File(vertexfilePath));
            String str = null;
            while(sc.hasNext()){
                str = sc.nextLine();
                try {
                    JSONObject obj = new JSONObject(str);
                    String identity = obj.getString("identity").trim();


                    Vertex v = new Vertex(obj);
                    v.setLabel(nodeLabel);


                    String graphId = graphBuzi.addVertex(v);

                    System.out.println("graphId:"+graphId);

                    v.setId(graphId);

                    indexBuzi.addOrUpdateIndex(v);

                    key_value.put(identity,graphId);
                } catch (Exception e) {
                    logger.error("批量增点错误："+e.getMessage()+"\n"+str);
                    throw e;
                }
            }
            sc.close();



            sc= new Scanner(new File(edgefilePath));
            str = null;
            while(sc.hasNext()){
                str = sc.nextLine();
                try {
                    JSONObject obj = new JSONObject(str);
//                    Vertex vertex1= graphBuzi.checkVertexByIdentity(label,obj.getString("identity").replace("\\", "、").trim());
//                    Vertex vertex2 = graphBuzi.checkVertexByIdentity(label,obj.getString("identity").replace("\\", "、").trim());

                    String from  = key_value.get(obj.getString("from"));
                    String to =  key_value.get(obj.getString("to"));
                    String name =  obj.getString("name");
                    String root =  obj.getString("root");

                    Edge edge=new Edge(name, root, from, to, edgeRelationship);

                    String graphId = graphBuzi.addEdges(edge);

                    edge.setId(graphId);

                    indexBuzi.addOrUpdateIndex(edge);

                } catch (Exception e) {

                    logger.error("批量增边错误："+e.getMessage()+"\n"+str);
                    throw e;
                }
            }
            sc.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("完毕！");
        return true;
    }


    /**
     * 查看点的详情
     * @param vertex
     * @return
     */
    public String queryNodeDetail(Vertex vertex) {
        return indexBuzi.queryById(vertex).toString();
    }

    public String queryEdgeDetail(Edge edge) {
        return indexBuzi.queryById(edge).toString();
    }
public  String relationshipName(Edge edge,JSONArray graphNames){

    for(int i=0;i<graphNames.length();i++) {
        String graphName = graphNames.getString(i);
        try {
            Graph graph = DatabaseConfiguration.getGraph(graphName);
            String label = graph.getRelationship();
            return  graphBuzi.relationshipName(label).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    return  null;
}
    public void delVertexByPath(String vertexsPath, String label) {
        //TODO
    }

    public boolean bluckAddvertex(String vertexsPath, String nodeLabel) throws IOException {
        long time = System.currentTimeMillis();

        try {

            System.out.println("开始neo4j中导入点");
            Map<String,String> identity_id = graphBuzi.batchInsertVertex(nodeLabel,"vertex.csv");

            time = System.currentTimeMillis();

            String path = vertexsPath+"vertex_before.txt";

            Scanner sc = new Scanner(new File(path));
            FileOutputStream fileOutputStream = new FileOutputStream(vertexsPath+"vertex.txt", true);
            while(sc.hasNext()){
                String line = sc.nextLine();

                JSONObject obj = new JSONObject(line);
                obj.put("id",identity_id.get(obj.getString("identity")));


                fileOutputStream.write((obj.toString()+"\n").getBytes());
                fileOutputStream.flush();
            }
            sc.close();
            fileOutputStream.close();



//            for(String str:identity_id.keySet()){
//                System.out.println("MM:"+str+"\t"+identity_id.get(str));
//            }


            String path_ed = vertexsPath+"edges_before.txt";
            Scanner sc_ed = new Scanner(new File(path_ed));

            CommonTool.printFile("",vertexsPath+"edges.txt",false);
            CommonTool.printFile("",vertexsPath+"edges.csv",false);
            CommonTool.printFile("",vertexsPath+"edges_dan.csv",false);


            FileOutputStream fileOutputStream_ed = new FileOutputStream(vertexsPath+"edges.txt", true);
            FileOutputStream fileOutputStream_ed_csv = new FileOutputStream(vertexsPath+"edges.csv", true);
            FileOutputStream fileOutputStream_ed_danbao = new FileOutputStream(vertexsPath+"edges_dan.csv", true);

            fileOutputStream_ed_csv.write("root,name,from_id,to_id,identity,weight\n".getBytes());
            fileOutputStream_ed_danbao.write("root,name,from_id,to_id,identity,weight\n".getBytes());
            while(sc_ed.hasNext()){
                String line = sc_ed.nextLine();

                JSONObject obj = new JSONObject(line);
                obj.put("from",identity_id.get(obj.getString("from")));
                obj.put("to",identity_id.get(obj.getString("to")));




                String root = obj.getString("root").replace(",","，");
                String name = obj.getString("name").replace(",","，");
                String from = obj.getString("from").replace(",","，");
                String to = obj.getString("to").replace(",","，");
                String identity = obj.getString("identity");
                int weight = obj.getInt("weight");

                String one = root+","+name+","+from+","+to+","+identity+","+weight+"\n";
                fileOutputStream_ed_csv.write(one.getBytes());

                if(name.equals("担保")){
                    fileOutputStream_ed_danbao.write(one.getBytes());
                }
                else{
                    fileOutputStream_ed_csv.write(one.getBytes());
                }


                fileOutputStream_ed.write((obj.toString()+"\n").getBytes());
                fileOutputStream_ed.flush();
            }
            sc_ed.close();
            fileOutputStream_ed.close();
            fileOutputStream_ed_csv.close();
            fileOutputStream_ed_danbao.close();




            //新担保
            CommonTool.printFile("",vertexsPath+"edges2.csv",false);
            FileOutputStream tmp2 = new FileOutputStream(vertexsPath+"edges2.csv", true);
            tmp2.write("root,name,from_id,to_id,identity,weight\n".getBytes());

            Scanner sc_tmp2 = new Scanner(new File(vertexsPath+"edges2.tmp"));
            while(sc_tmp2.hasNext()){
                String line = sc_tmp2.nextLine();
                String[] dd = line.split(",");

                String fromid = identity_id.get(dd[2]);
                String toid = identity_id.get(dd[3]);

                String one = dd[0]+","+dd[1]+","+fromid+","+toid+","+dd[4]+","+dd[5]+"\n";
                tmp2.write(one.getBytes());


            }
            sc_tmp2.close();






            System.out.println("neo4j导入点完成："+(System.currentTimeMillis()-time)/1000+"秒");
        } catch (Exception e) {
            logger.error("批量增点错误："+e.getMessage()+"\n");
            throw e;
        }

        return false;
    }

    public boolean bluckaddedges(String edgesfile, String relationship) {
        long time = System.currentTimeMillis();
        try {

            System.out.println("开始neo4j导入边");
            time = System.currentTimeMillis();

            Map<String,String> identity_id = graphBuzi.batchInsertEdgeById(relationship,edgesfile);

            System.out.println("导入边完成："+(System.currentTimeMillis()-time)/1000+"秒");

            //TODO

//          indexBuzi.bluckByFile(edgeRelationship,edgefilePath,identity_id);

        } catch (Exception e) {

            logger.error("批量增边错误："+e.getMessage()+"\n");
            throw e;
        }
        return false;
    }
}
