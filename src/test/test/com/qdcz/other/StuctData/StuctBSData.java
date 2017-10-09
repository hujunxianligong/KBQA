package com.qdcz.other.StuctData;

import com.qdcz.common.CommonTool;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.*;

/**
 * Created by hadoop on 17-10-1.
 */
public class StuctBSData {
    private static String root="上市公司分析";
    private static String dir = "/mnt/vol_0/neo4j-community-3.2.1/import/";
    private static  String inputFile ="part-r-00000";
    private static String ve_csv = dir+"vertex.csv";
    private static String ve_txt = dir+"vertex_before.txt";
    private static String edge_txt = dir+"edges_before.txt";
    public static void main(String[] args) throws Exception {
        StuctBSData instance = new StuctBSData();
       // instance.doIt();
        System.out.println(ChineseNameTest("完那1"));
    }
    public void doIt() throws Exception {
        Map<String,JSONObject> vertexs =  new HashMap<>();
        List<JSONObject> edges =  new ArrayList<>();
        Map<String,String> coms = new HashMap<>();
        StringBuffer sb_neo4j = new StringBuffer();
        StringBuffer sb_ela = new StringBuffer();
        CommonTool.printFile("root,name,type,content,identity,weight\n",ve_csv,false);

        CommonTool.printFile("",ve_txt,false);

        CommonTool.printFile("",edge_txt,false);


        Scanner sc = new Scanner(new File(dir+inputFile));
        while(sc.hasNext()){
           // vertexs.clear();
            edges.clear();

            String line = sc.nextLine();
            JSONObject one_com=null;
            try {
                one_com = new JSONObject(line.split("\t")[1]);
            } catch (JSONException e) {
                System.out.println(line.toString());
            }

            String companyName = one_com.getString("CompanyName");

            if(companyName.endsWith("公")){
                companyName = companyName+"司";
            }

            if(!StructListedCom.verifyName(companyName)){
                continue;
            }


            companyName = StructListedCom.changeName(companyName);
            String identity=null;
            if(!coms.containsKey(companyName)){
                 identity = UUID.randomUUID().toString();
                //公司
                JSONObject node_COM = new JSONObject();
                node_COM.put("identity",identity);
                node_COM.put("root",root);
                node_COM.put("name",companyName.trim());
                node_COM.put("type","com");
                node_COM.put("content",new JSONObject().toString());

                vertexs.put(companyName,node_COM);
                coms.put(companyName,identity);

            }else{
                identity = coms.get(companyName);
            }
            //公司属性
            dealStrInfo( edges,  vertexs,one_com,identity,coms);
            printEdgeData(sb_neo4j,edges);
        }
        printVertexData(sb_neo4j,sb_ela,vertexs);

    }

    private void dealStrInfo(List<JSONObject> edges,  Map<String,JSONObject> vertexs ,
                             JSONObject obj,String identity, Map<String,String> coms){
        String  useType=obj.getString("Type");

        String name = null;
        String type = null;
        switch(useType)
        {
            case "holders":
                if(obj.has("shareholder")){
                    name = obj.getString("shareholder");
                }else {
                    name = "";
                }
                if(obj.has("shareholder_Type")){
                    type = obj.getString("shareholder_Type");
                }else {
                    type = "";
                }
                break;
            case "Branches":
                if(obj.has("BranchesName")){
                    name = obj.getString("BranchesName");
                }else{
                    name = "";
                }
                type = "分支机构";
                break;
            case "KeyPerson":
                if(obj.has("name")){
                    name = obj.getString("name");
                }else{
                    name = "";
                }
                if(obj.has("position")){
                    type = obj.getString("position");
                }else {
                    type = "";
                }
                break;
            case "invested":
                if(obj.has("name")){
                    name = obj.getString("name");
                }else{
                    name = "";
                }
                type = "对外投资";
                break;
            default:
                name= "";
                break;
        }
        if(!"".equals(name)){
            if(vertexs.containsKey(name.hashCode())){
                JSONObject jsonObject = vertexs.get(name.hashCode());
                JSONObject jsonObject1 = new JSONObject(jsonObject.getString("content"));
                if(jsonObject1.has(useType)){
                    JSONArray jsonArray = jsonObject1.getJSONArray(useType);
                    jsonArray.put(obj);
                }else{
                    JSONArray contentArray=new JSONArray();
                    contentArray.put(obj);
                    jsonObject1.put(useType,contentArray);
                }
                String identity_in = coms.get(name);

                JSONObject one_edges = new JSONObject();
                one_edges.put("root",root);
                one_edges.put("from",identity);
                one_edges.put("to",identity_in);
                one_edges.put("name",type);
                if(identity_in==null)
                    System.out.println();
                edges.add(one_edges);
            }else{
                String identity_guanlian = UUID.randomUUID().toString();
                JSONArray contentArray=new JSONArray();
                contentArray.put(obj);
                JSONObject contentObj= new JSONObject();
                contentObj.put(useType,contentArray);
                JSONObject node_COM = new JSONObject();
                node_COM.put("identity",identity_guanlian);
                node_COM.put("root",root);
                node_COM.put("name",name.trim());
                node_COM.put("type",getNameType(name));
                node_COM.put("content",contentObj.toString());
                vertexs.put(name,node_COM);

                coms.put(name,identity_guanlian);

                JSONObject one_edges = new JSONObject();
                one_edges.put("root",root);
                one_edges.put("from",identity);
                one_edges.put("to",identity_guanlian);
                one_edges.put("name",type);
                edges.add(one_edges);
            }

        }else{
            try {
                throw new Exception("没有名字:"+obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }
    private String getNameType(String name){
        if(ChineseNameTest(name)){
            return "per";
        }else{
            return "com";
        }
    }
    static boolean ChineseNameTest(String name) {
        if (!name.matches("[\u4e00-\u9fa5]{2,4}")) {
        //    System.out.println("只能输入2到4个汉字");
            return false;
        }else return true;
    }
    private void printEdgeData(StringBuffer sb_neo4j,List<JSONObject> edges){

        for (int i = 0;i< edges.size();i++){
            try {
                JSONObject obj = edges.get(i);
                String root = obj.getString("root").replace(",", "，");
                String name = obj.getString("name").replace(",", "，");
                String from = obj.getString("from").replace(",", "，");
                String to = obj.getString("to").replace(",", "，");
                int weight = 1;
                obj.put("weight", weight);
                if (from.equals(to)) {
                    continue;
                }

                String identity = UUID.randomUUID().toString();
                obj.put("identity", identity);
                sb_neo4j.append(obj.toString()+"\n");
            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }
        CommonTool.printFile(sb_neo4j.toString(),edge_txt,true);
        sb_neo4j.delete(0,sb_neo4j.length());
    }
    private void printVertexData(StringBuffer sb_neo4j,StringBuffer sb_ela, Map<String,JSONObject> vertexs ) {
        for (int i = 0; i < vertexs.size(); i++) {
            JSONObject obj = vertexs.get(i);
            String type = obj.getString("type").replace(",", "，");
            String root = obj.getString("root").replace(",", "，");
            String name = obj.getString("name").replace(",", "，");
            String content = obj.getString("content").replace(",", "，");
            String identity = obj.getString("identity").replace(",", "，");

//            if(!type.equals("com")){
//                continue;
//            }

            if (type.equals("com")) {
                System.out.println(name);
            }

            sb_neo4j.append(root + "," + name + "," + type + "," + content + "," + identity + "\n");

            sb_ela.append(obj.toString() + "\n");


        }
        CommonTool.printFile(sb_neo4j.toString(), ve_csv, true);
        sb_neo4j.delete(0, sb_neo4j.length());

        CommonTool.printFile(sb_ela.toString(), ve_txt, true);
        sb_ela.delete(0, sb_ela.length());
    }
}
