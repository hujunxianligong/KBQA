package com.qdcz.graph.neo4jcypher.service;

import com.mongodb.util.JSON;
import com.qdcz.common.CommonTool;
import com.qdcz.conf.LoadConfigListener;
import com.qdcz.entity.Edge;
import com.qdcz.entity.IGraphEntity;
import com.qdcz.entity.Vertex;
import com.qdcz.graph.interfaces.IGraphBuzi;
import com.qdcz.graph.neo4jcypher.connect.Neo4jClientFactory;
import com.qdcz.graph.neo4jcypher.dao.Neo4jCYDAO;;
import org.json.JSONArray;
import org.json.JSONObject;

import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Path;
import org.neo4j.driver.v1.types.Relationship;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * cypher语句，neo4j对外提供的操作
 * Created by star on 17-8-3.
 */
@Service("neo4jCypherService")
public class Neo4jCYService implements IGraphBuzi {

    public static void main(String[] args) {

        LoadConfigListener loadConfigListener=new LoadConfigListener();
        loadConfigListener.setSource_dir("/dev/");
        loadConfigListener.contextInitialized(null);
        Vertex vertex=new Vertex();
        vertex.setRoot("起点");
        vertex.setName("牵头行");
        vertex.setType("挖掘部");
        vertex.setId("55");
        vertex.setContent("");
        vertex.setLabel("ytdk_label");

        Neo4jCYService instance=  new Neo4jCYService();
      //  instance.deleteVertex(vertex);
        Edge edge=new Edge();
        edge.setRelationShip("gra");
        edge.setId(2181l+"");
        instance.relationshipName("licom_relationship");
     //   instance.bfExtersion(vertex,1);
      //  instance.dfExection(19,22,4);
     //   instance.batchInsertEdge("hehe_rel","edge.csv");
     //   instance.batchInsertVertex("hehe","vertex.csv");
        String [] provinceStrs= new String[]{"HeiLongJiang", "JiLin", "LiaoNing", "HeBei", "HeNan", "ShanDong", "JiangSu"
                , "ShanXi", "ShanXi", "GanSu", "SiChuan", "QingHai", "HuNan", "HuBei"
                , "JiangXi", "AnHui", "ZheJiang", "FuJian", "GuangDong", "GuangXi", "TaiWan", "GuiZhou"
                , "YunNan", "HaiNan", "BeiJing", "TianJin", "ShangHai", "ChongQing", "XiangGang"
                , "AoMen", "XiZang", "XinJiang", "NeiMengGu", "NingXia",
        };
        String [] quartStrs=new String[]{"down_2014","down_2015","down_2016","up_2015","up_2016","up_2017"};
        for(String quarter:quartStrs){
            for(String province:provinceStrs){
                instance.unionFindStream(province+"_label",quarter+"_danbao_relationship",quarter);
                instance.distinctWeightDanbao(province+"_label",quarter+"_label",quarter+"_relationship",quarter);
                instance.getDanbaoMoney(province+"_label",quarter+"_label",quarter+"_relationship",quarter);
            }
        }
    }



    private Neo4jCYDAO neo4jCYDAO;

    private Driver driver;

    public Neo4jCYService(){
        driver =  Neo4jClientFactory.create();
        neo4jCYDAO = new Neo4jCYDAO(driver);
    }
    @Override
    public String addVertex(Vertex vertex) {

        return  neo4jCYDAO.addVertex(vertex);
    }

    @Override
    public String changeVertex(Vertex vertex) {

        return neo4jCYDAO.changeVertex(vertex);
    }
    public Map batchInsertVertex(String label,String filepath){
        Map<String,String> mapsResult=new HashMap<>();
        String sql=null;
            sql="USING PERIODIC COMMIT 1000 " +
                    "LOAD CSV WITH HEADERS FROM \"file:///" + filepath + "\" AS line  " +
                    "MERGE (p:"+label+"{root:line.root,name:line.name,type:line.type,content:line.content,identity:line.identity}) return line.identity,id(p)";

        StatementResult execute = neo4jCYDAO.execute(sql);
        while(execute.hasNext()){
            Record next = execute.next();
            String id = next.get("id(p)").toString();
            String identity = next.get("line.identity").asString();
            mapsResult.put(identity,id);
        }
        return  mapsResult;
    }
    public Map batchInsertEdge(String relatinship,String filepath){
        Map<String,String> mapsResult=new HashMap<>();
        String sql=null;
            sql="USING PERIODIC COMMIT 1000 " +
                    "LOAD CSV WITH HEADERS FROM \"file:///"+filepath+"\" AS line  " +
                    "MATCH (m{identity:line.from_id} ) MATCH (n{identity:line.to_id}) " +
                    "MERGE (m)-[r:"+relatinship+"{from:id(m),root:line.root,name:line.name,to:id(n)}]->(n) return line.identity,id(r);";
        StatementResult execute = neo4jCYDAO.execute(sql);
        while(execute.hasNext()){
            Record next = execute.next();
            String id = next.get("id(r)").toString();
            String identity = next.get("line.identity").toString();
            mapsResult.put(identity,id);
        }
        return  mapsResult;
    }
    public Map batchInsertEdgeById(String relatinship,String filepath){
        Map<String,String> mapsResult=new HashMap<>();
        String sql=null;
        sql="USING PERIODIC COMMIT 1000 " +
                "LOAD CSV WITH HEADERS FROM \"file:///"+filepath+"\" AS line  " +
                "MATCH (m  ) MATCH (n ) where id(m)=apoc.number.parseInt(line.from_id) AND id(n)=apoc.number.parseInt(line.to_id) "+
                "MERGE (m)-[r:"+relatinship+"{from:id(m),root:line.root,name:line.name,to:id(n),weight:apoc.number.parseInt(line.weight)}]->(n) return line.identity,id(r);";
        System.out.println(sql);
        StatementResult execute = neo4jCYDAO.execute(sql);
        while(execute.hasNext()){
            Record next = execute.next();
            String id = next.get("id(r)").toString();
            String identity = next.get("line.identity").asString();
            mapsResult.put(identity,id);
        }
        return  mapsResult;
    }
    @Override
    public List<IGraphEntity> deleteVertex(Vertex vertex) {
        return neo4jCYDAO.deleteVertex(vertex);
    }

    @Override
    public String addEdges(Edge edge) {
        return neo4jCYDAO.addEdges(edge);
    }

    @Override
    public String changeEdge(Edge edge) {
        return neo4jCYDAO.changeEdge(edge);
    }

    @Override
    public String deleteEdge(Edge edge) {
        return neo4jCYDAO.deleteEdge(edge);
    }

    @Override
    public List<Path> directedBfExtersion(Vertex vertex, int depth){
        List<Path> paths =new ArrayList<>();
        try {
            paths = neo4jCYDAO.bfExtersion(vertex, depth,true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return paths;
    }


    @Override
    public List<Path> bfExtersion(Vertex vertex, int depth) {
        List<Path> paths =new ArrayList<>();
        try {
             paths = neo4jCYDAO.bfExtersion(vertex, depth,false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return paths;
    }

    @Override
    public Path dfExection(long fromId, long toId, int depth) {
        return neo4jCYDAO.dfExection(fromId,toId,depth);

    }

    @Override
    public Vertex checkVertexByIdentity(String label, String identity) {
        return neo4jCYDAO.checkVertexByIdentity(label,identity);
    }
    @Override
    public List<Path> checkGraphById(long id,int depth) {

        return neo4jCYDAO.checkGraphById(id,depth);
    }
    @Override
    public Map<String, Vertex> checkVertexByEdgeId(long id) {
        Map<String, Vertex> result=new HashMap<>();
        String sqlString="MATCH (p)-[r]->(m)WHERE id(r)= "+id+" RETURN p,m";
        StatementResult execute = neo4jCYDAO.execute(sqlString);
        while(execute.hasNext()){
            Record next = execute.next();
            Node n = next.get("p").asNode();
            Map<String, Object> nodeInfo = n.asMap();
            Vertex startVertex=new Vertex();
            CommonTool.transMap2Bean(nodeInfo,startVertex);
            startVertex.setId(n.id()+"");
            if(!result.containsKey("start")){
                result.put("start",startVertex);
            }
            Node m = next.get("m").asNode();
             nodeInfo = m.asMap();
            Vertex endVertex=new Vertex();
            CommonTool.transMap2Bean(nodeInfo,endVertex);
            endVertex.setId(m.id()+"");
            if(!result.containsKey("end")){
                result.put("end",endVertex);
            }

        }

        return result;
    }
    public List<String> relationshipName(String relationshipType){
        List<String> result=new ArrayList<>();
        String sql="match()-[r:"+relationshipType+"]->() return distinct(r.name)as names  limit 100";
        StatementResult execute = neo4jCYDAO.execute(sql);
        while(execute.hasNext()){
            Record next = execute.next();
            String names = next.get("names").asString();
            result.add(names);
        }
        System.out.println(result.toString()+result.size());
        return result;
    }



    /*******************************************************/
    /************统计查询********/
    public void test(){
        String sql= "CALL algo.unionFind.stream('licom_label', 'danbao_relationship', { defaultValue:0.0, threshold:1.0}) YIELD nodeId,setId match(n)where id(n)=nodeId return nodeId,setId,n.partition_cc_Info";
        Set<Long> info1=new HashSet();
        StatementResult execute = neo4jCYDAO.execute(sql);
        while(execute.hasNext()){
            Record next = execute.next();
            long partition_cc_Info = next.get("n.partition_cc_Info").asLong();
            info1.add(partition_cc_Info);

        }
        String sql2="CALL algo.unionFind.stream('licom_label', 'licom_relationship', { defaultValue:0.0, threshold:1.0}) YIELD nodeId,setId WITH distinct(setId) as setIdS RETURN setIdS";
        Set<Long> info2=new HashSet();
        execute = neo4jCYDAO.execute(sql2);
        while(execute.hasNext()){
            Record next = execute.next();
            long partition_cc_Info = next.get("setIdS").asLong();
            info2.add(partition_cc_Info);

        }
        System.out.println();
    }


    public void unionFindStream(String label,String relationship,String  quarter){
        Map<Long,List<JSONObject>> tmpMaps=new HashMap<>();
        String sql=null;
        //CALL algo.unionFind.stream(label:String, relationship:String, {weightProperty:'weight', threshold:0.42, defaultValue:1.0)YIELD nodeId, setId

    //    sql="CALL algo.unionFind.stream('"+label+"', '"+relationship+"', { defaultValue:0.0, threshold:1.0}) YIELD nodeId,setId";
        sql= "CALL algo.unionFind.stream('"+label+"', '"+relationship+"', { defaultValue:0.0, threshold:1.0}) YIELD nodeId,setId match(n)where id(n)=nodeId and n.type = 'com' return nodeId,setId,n.name";
        StatementResult execute = neo4jCYDAO.execute(sql);
        while(execute.hasNext()){
            Record next = execute.next();
            long nodeId = next.get("nodeId").asLong();
            long setId = next.get("setId").asLong();

            String companyName = next.get("n.name").asString();
            JSONObject obj=new JSONObject();
            obj.put("id",nodeId);
            obj.put("name",companyName);
            List<JSONObject> namelist=null;
            if(tmpMaps.containsKey(setId)){
                namelist=tmpMaps.get(setId);
                namelist.add(obj);
            }else{
                namelist=new ArrayList<>();
                namelist.add(obj);
                tmpMaps.put(setId,namelist);
            }
        }
        Map<Long,List<JSONObject>> resultMaps=new HashMap<>();
        for (Map.Entry<Long, List<JSONObject>> entry : tmpMaps.entrySet()){
            if(entry.getValue().size()>1){
                resultMaps.put(entry.getKey(),entry.getValue());
            }
        }
        tmpMaps.clear();
        Map<Integer,Map<Long,List<JSONObject>>> statisticalMaps=new HashMap<>();
        Map<Integer,Integer> quantityMap=new HashMap<>();
        for (Map.Entry<Long, List<JSONObject>> entry : resultMaps.entrySet()){
          int key= entry.getValue().size();
            if(quantityMap.containsKey(key)){
                Integer value = quantityMap.get(key);
                Map<Long, List<JSONObject>> longListMap = statisticalMaps.get(key);
                longListMap.put(entry.getKey(),entry.getValue());
                quantityMap.put(key,value+1);
            }else{
                quantityMap.put(key,1);
                Map<Long, List<JSONObject>> longListMap=new HashMap<>();
                longListMap.put(entry.getKey(),entry.getValue());
                statisticalMaps.put(key,longListMap);
            }
        }
        System.out.println(quantityMap);
       int sum=0;
        for (Map.Entry<Integer,Integer> entry : quantityMap.entrySet()){
            sum+=entry.getValue();
        }
        System.out.println("担保圈数量:"+sum);
        JSONObject result=new JSONObject();
        JSONArray jsonArray=new JSONArray();
        result.put("担保圈数量",sum);
        result.put("分布坐标",quantityMap.toString());
        for (Map.Entry<Integer, Map<Long, List<JSONObject>>> entry : statisticalMaps.entrySet()){
            JSONObject obj=new JSONObject();
            Integer quanSize = entry.getKey();
            Map<Long, List<JSONObject>> quans = entry.getValue();
            JSONArray array=new JSONArray();
            for (Map.Entry<Long, List<JSONObject>> quan : quans.entrySet()){
                Long partitionId = quan.getKey();
                List<JSONObject> partitionContents = quan.getValue();
                JSONArray partitionContentArray=new JSONArray();
                for(JSONObject partitionContent:partitionContents){
                    partitionContentArray.put(partitionContent);
                }
                JSONObject quanObj=new JSONObject();
                quanObj.put("partitionId",partitionId);
                quanObj.put("Content",partitionContentArray);
                array.put(quanObj);
            }
            obj.put("partitionSum",array.length());
            obj.put("partitionSize",quanSize);
            obj.put("partitionContents",array);
            jsonArray.put(obj);
        }
        result.put("partition",jsonArray);
        CommonTool.printFile(result.toString(),"/home/hadoop/下载/结果数据/担保圈公司数量_"+quarter+".txt",false);
    }
    public void getDanbaoMoney(String label_com,String label,String relationship,String  quarter){
        Map<Long,List<JSONObject>> tmpMaps=new HashMap<>();
        String sql="MATCH (x:"+label_com+")-[v:"+relationship+"*1{name:\"担保方\"}]->(n:"+label+")-[r*1{name:\"实际担保金额\"}]->(m:"+label+") " +
                "with distinct(x.partition_cc)as partition,collect(distinct id(x)) as companyId,collect(distinct x.name) as companyName,collect(m.name) as money " +
                "return partition,companyId,companyName,money";
        StatementResult execute = neo4jCYDAO.execute(sql);
        while(execute.hasNext()){
            Record next = execute.next();
            long partitionId=next.get("partition").asLong();
            List<Object> companyId = next.get("companyId").asList();
            List<Object> companyName = next.get("companyName").asList();
            List<Object> money = next.get("money").asList();
            JSONObject obj=new JSONObject();
            obj.put("companyId",companyId);
            obj.put("companyName",companyName);
            obj.put("money",money);
            List<JSONObject> weightList=null;
            if(tmpMaps.containsKey(partitionId)){
                weightList=tmpMaps.get(partitionId);
                weightList.add(obj);
            }else{
                weightList=new ArrayList<>();
                weightList.add(obj);
                tmpMaps.put(partitionId,weightList);
            }

        }
        JSONObject resultObj=new JSONObject();
        JSONArray resultArray=new JSONArray();
        for (Map.Entry<Long, List<JSONObject>> entry : tmpMaps.entrySet()){
            List<JSONObject> value = entry.getValue();
            for(JSONObject moneyObj:value){
                float Sum=0f;
                JSONArray moneys = moneyObj.getJSONArray("money");
                JSONArray companyName = moneyObj.getJSONArray("companyName");
                if(companyName.length()>1)
                    System.out.println();
                for(int i=0;i<moneys.length();i++){
                    String moneyStr= (String) moneys.get(i);
                    float money=Float.parseFloat(moneyStr.replace("万元",""));
                    Sum+=money;
                }
                moneyObj.put("sumMoney",Sum+"万元");
                moneyObj.put("partitonId",entry.getKey());
                resultArray.put(moneyObj);
            }
        }
        resultObj.put("Size",resultArray.length());
        resultObj.put("content",resultArray);
        System.out.println();
        CommonTool.printFile(resultObj.toString(),"/home/hadoop/下载/结果数据/担保圈金额数量_"+quarter+".txt",false);
    }
    public void distinctWeightDanbao(String label_com,String label,String relationship,String  quarter){
        Map<Long,List<JSONObject>> tmpMaps=new HashMap<>();
        String sql=null;
        sql="MATCH (n:"+label_com+")-[r:"+relationship+"]->(m:"+label+") " +
                "WHERE EXISTS(r.weight) " +
            "RETURN distinct(n.partition_cc) as partition, collect(distinct id(n)) as ids,collect(distinct n.name) as names,collect((r.weight)) as weights,count(*) as num_of_weight ORDER by num_of_weight DESC";
        int i=0;
        StatementResult execute = neo4jCYDAO.execute(sql);
        while(execute.hasNext()){
            i++;
            Record next = execute.next();
            long partitionId=next.get("partition").asLong();
            List<Object> weightslist=next.get("weights").asList();
            Map<Long,Integer> weightMap=new HashMap<>();

            for(Object weightSize:weightslist){
                if(weightMap.containsKey(weightSize)){
                    Integer integer = weightMap.get(weightSize);
                    weightMap.put((Long) weightSize,integer+1);
                }else{
                    weightMap.put((Long) weightSize,1);
                }
            }
            int numOfWeight=0;
            JSONArray weightSizesInfo=new JSONArray();
            for (Map.Entry<Long,Integer> entry : weightMap.entrySet()){
                Long size = entry.getKey();
                Integer num = entry.getValue();
                numOfWeight+=size*num;
                JSONObject weightInfo=new JSONObject();
                weightInfo.put("size",size);
                weightInfo.put("num",num);
                weightSizesInfo.put(weightInfo);
            }
            List<Object> ids = next.get("ids").asList();
            List<Object> names = next.get("names").asList();
            JSONObject obj=new JSONObject();
            obj.put("weightSize",weightSizesInfo);
            obj.put("numOfWeight",numOfWeight);
            obj.put("companyIds",ids);
            obj.put("companyNames",names);
            List<JSONObject> weightList=null;
            if(ids.size()>1){
                if(tmpMaps.containsKey(partitionId)){
                    weightList=tmpMaps.get(partitionId);
                    weightList.add(obj);
                }else{
                    weightList=new ArrayList<>();
                    weightList.add(obj);
                    tmpMaps.put(partitionId,weightList);
                }
            }


        }

        JSONObject resultObj=new JSONObject();
        JSONArray guaranteeArray=new JSONArray();
        int allguaranteeNum=0;
        for (Map.Entry<Long, List<JSONObject>> entry : tmpMaps.entrySet()){
            JSONObject danbaoObj=new JSONObject();
            List<JSONObject> daobanInfos = entry.getValue();
            int sumOfWeight=0;
            JSONArray danbaoArray=new JSONArray();
            for(JSONObject daobanInfo:daobanInfos){
                int numOfWeight = daobanInfo.getInt("numOfWeight");
                sumOfWeight+=numOfWeight;
                danbaoArray.put(daobanInfo);
            }
            danbaoObj.put("partitionId",entry.getKey());
            danbaoObj.put("sumOfWeight",sumOfWeight);
            danbaoObj.put("info",danbaoArray);
            allguaranteeNum+=sumOfWeight;
            guaranteeArray.put(danbaoObj);
        }
        resultObj.put("guaranteeArray",guaranteeArray);
        resultObj.put("allguaranteeNum",allguaranteeNum);
        resultObj.put("partitonSize",guaranteeArray.length());
        System.out.println(i);
        CommonTool.printFile(resultObj.toString(),"/home/hadoop/下载/结果数据/担保圈担保数量_"+quarter+".txt",false);

    }


    public void testExtersion(String sql){
        StatementResult execute = neo4jCYDAO.execute(sql);
    }

}
