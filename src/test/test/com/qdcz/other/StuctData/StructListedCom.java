package com.qdcz.other.StuctData;

import com.qdcz.common.CommonTool;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by star on 17-8-16.
 */
public class StructListedCom {
    private static String root="上市公司分析";
    private static String dir = "/media/star/Doc/工作文档/上市公司担保关系分析/";

    private static String ve_csv = dir+"vertex.csv";
    private static String ve_txt = dir+"vertex.txt";

    private static String edge_csv = dir+"edges.csv";
    private static String edge_txt = dir+"edges.txt";

    private static String[] attr_en = {"lim_pub_date","gua_limit","act_occ_date","act_gua_money","gua_type","gua_date","whe_per_end",
            "whe_rel_guarantee","gua_beg_date","gua_lim_date","gua_whe_overdue","gua_ove_money","whe_rev_guarantee","rel_relation","lis_com_relation"};
    private static String[] attr_cn = {"披露日期","担保额度","协议签署日","实际担保金额","担保类型","担保期","是否履行完毕",
            "是否为关联方担保","担保起始日","担保到期日","担保是否逾期","担保逾期金额","是否存在反担保","关联关系","担保方与上市公司的关系"};



    public static void main(String[] args) throws Exception {
        StructListedCom instance = new StructListedCom();
        instance.doIt();
    }

public boolean verifyName(String name){

        if(name.isEmpty() || name.length()>35 || name.length()<5 || name.matches(".*\\d+.*")
                || name.contains("√")|| name.contains("□") || name.contains("%") || name.contains("---")){

            return false;
        }else{
            return true;
        }
}


    public void doIt() throws Exception {
        Scanner sc = new Scanner(new File("/media/star/Doc/工作文档/上市公司担保关系分析/someJson"));
        List<JSONObject> vertexs =  new ArrayList<>();
        List<JSONObject> edges =  new ArrayList<>();
        Map<String,String> coms = new HashMap<>();
        Map<String,String> danbaos = new HashMap<>();
        StringBuffer sb_neo4j = new StringBuffer();
        StringBuffer sb_ela = new StringBuffer();




        CommonTool.printFile("root,name,type,content,identity\n",ve_csv,true);

        CommonTool.printFile("root,name,from_id,to_id,identity\n",edge_csv,true);

        while(sc.hasNext()){
            vertexs.clear();
            edges.clear();

            String line = sc.nextLine();

            JSONObject one_com = new JSONObject(line);

            String companyName = one_com.getString("cre_subject");

            if(companyName.endsWith("公")){
                companyName = companyName+"司";
            }

            if(!verifyName(companyName)){
                continue;
            }


            if(!coms.containsKey(companyName)){
                String identity = UUID.randomUUID().toString();


                JSONObject node_COM = new JSONObject();
                node_COM.put("identity",identity);
                node_COM.put("root",root);
                node_COM.put("name",companyName.trim());
                node_COM.put("type","com");
                node_COM.put("content",new JSONObject().toString());

                vertexs.add(node_COM);

                coms.put(companyName,identity);
            }


            //对外担保情况(out_cre_guarantee)
            getout_cre_guarantee(one_com.getJSONArray("out_cre_guarantee"),companyName,vertexs,edges,coms,danbaos);


            //公司对子公司担保情况(com_sub_guarantee)
            getcom_sub_guarantee(one_com.getJSONArray("com_sub_guarantee"),companyName,vertexs,edges,coms,danbaos);


            //子公司对子公司担保情况(sub_sub_guarantee)
            getsub_sub_guarantee(one_com.getJSONArray("sub_sub_guarantee"),companyName,vertexs,edges,coms,danbaos);

            //关联担保情况（rel_guarantee)
//            getrel_guarantee(one_com.getJSONArray("rel_guarantee"),companyName,vertexs,edges,coms,danbaos);

            printData(sb_neo4j,sb_ela,vertexs,edges);

        }
        sc.close();
    }

    private void printData(StringBuffer sb_neo4j,StringBuffer sb_ela,List<JSONObject> vertexs,List<JSONObject> edges){
        for (int i = 0;i< vertexs.size();i++){
            JSONObject obj = vertexs.get(i);
            String type = obj.getString("type").replace(",","，");
            String root =obj.getString("root").replace(",","，");
            String name = obj.getString("name").replace(",","，");
            String content = obj.getString("content").replace(",","，");
            String identity = obj.getString("identity").replace(",","，");

            if(type.equals("com")){
                System.out.println(name);
            }

            sb_neo4j.append(root+","+name+","+type+","+content+","+identity+"\n");

            sb_ela.append(obj.toString()+"\n");


        }
        CommonTool.printFile(sb_neo4j.toString(),ve_csv,true);
        sb_neo4j.delete(0,sb_neo4j.length());

        CommonTool.printFile(sb_ela.toString(),ve_txt,true);
        sb_ela.delete(0,sb_ela.length());


        for (int i = 0;i< edges.size();i++){
            JSONObject obj = edges.get(i);
            String root = obj.getString("root").replace(",","，");
            String name = obj.getString("name").replace(",","，");
            String from = obj.getString("from").replace(",","，");
            String to = obj.getString("to").replace(",","，");
            String identity = UUID.randomUUID().toString();
            obj.put("identity",identity);

            sb_neo4j.append(root+","+name+","+from+","+to+","+identity+"\n");
            sb_ela.append(obj.toString()+"\n");

        }
        CommonTool.printFile(sb_neo4j.toString(),edge_csv,true);
        sb_neo4j.delete(0,sb_neo4j.length());
        CommonTool.printFile(sb_ela.toString(),edge_txt,true);
        sb_ela.delete(0,sb_ela.length());
    }


    private void getrel_guarantee(JSONArray rel_guarantee, String companyName, List<JSONObject> vertexs, List<JSONObject> edges, Map<String, String> coms, Map<String, String> danbaos) throws Exception {
        String com_identity = coms.get(companyName);


    }

    private void getsub_sub_guarantee(JSONArray sub_sub_guarantee, String companyName, List<JSONObject> vertexs, List<JSONObject> edges, Map<String, String> coms, Map<String, String> danbaos) throws Exception {
        String com_identity = coms.get(companyName);

        for (int i = 0;i<sub_sub_guarantee.length();i++){

            String danbao_name = "子公司对子公司担保";
            JSONObject obj = sub_sub_guarantee.getJSONObject(i);
            dealWithOne(companyName,vertexs,edges,coms,danbaos,obj,danbao_name);
        }
    }

    private void getcom_sub_guarantee(JSONArray com_sub_guarantee, String companyName, List<JSONObject> vertexs, List<JSONObject> edges, Map<String, String> coms, Map<String, String> danbaos) throws Exception {


        for (int i = 0;i<com_sub_guarantee.length();i++){

            String danbao_name = "公司对子公司担保";
            JSONObject obj = com_sub_guarantee.getJSONObject(i);
            dealWithOne(companyName,vertexs,edges,coms,danbaos,obj,danbao_name);
        }
    }
    private void getout_cre_guarantee(JSONArray out_cre_guarantee, String companyName, List<JSONObject> vertexs, List<JSONObject> edges, Map<String, String> coms, Map<String, String> danbaos) throws Exception {
        for (int i = 0;i<out_cre_guarantee.length();i++){
            String danbao_name = "对外担保";
            JSONObject obj = out_cre_guarantee.getJSONObject(i);
            dealWithOne(companyName,vertexs,edges,coms,danbaos,obj,danbao_name);
        }
    }

    private void dealWithOne(String companyName, List<JSONObject> vertexs,
                        List<JSONObject> edges, Map<String, String> coms, Map<String, String> danbaos,
                        JSONObject obj,String danbao_name) throws Exception {
        String com_identity = coms.get(companyName);

        String gua_obj_name = obj.getString("gua_obj_name");
        if(gua_obj_name.endsWith("公")){
            gua_obj_name = gua_obj_name+"司";
        }

        if(!verifyName(gua_obj_name)){
            return;
        }


        String identity = "";
        if(!coms.containsKey(gua_obj_name)){
            identity = UUID.randomUUID().toString();


            JSONObject node_COM = new JSONObject();
            node_COM.put("identity",identity);
            node_COM.put("root",root);
            node_COM.put("name",gua_obj_name.trim());
            node_COM.put("type","com");
            node_COM.put("content",new JSONObject().toString());

            vertexs.add(node_COM);

            coms.put(gua_obj_name,identity);
        }else{
            identity = coms.get(gua_obj_name);
        }





        String identity_danbao = "";
        if(danbaos.containsKey(danbao_name)){
            throw new Exception("两个担保:"+danbao_name);
        }else{

            identity_danbao = UUID.randomUUID().toString();



            JSONObject node_COM = new JSONObject();
            node_COM.put("identity",identity_danbao);
            node_COM.put("root",root);
            node_COM.put("name",danbao_name.trim());
            node_COM.put("type","out_cre_guarantee");
//                node_COM.put("content",content_obj.toString());
            node_COM.put("content",new JSONObject().toString());

            vertexs.add(node_COM);




//            for (int i = 0;i<attr_en.length;i++){
//                String one_attr_cn = attr_cn[i];
//                String one_attr_en = attr_en[i];
//
//                if(obj.has(one_attr_en) && !obj.getString(one_attr_en).isEmpty()){
//                    String tmp = obj.getString(one_attr_en);
//                    JSONObject tmp_ve = new JSONObject();
//                    String tmp_id = UUID.randomUUID().toString();
//                    tmp_ve.put("identity",tmp_id);
//                    tmp_ve.put("root",root);
//                    tmp_ve.put("name",tmp.trim());
//                    tmp_ve.put("type","attribute");
//                    tmp_ve.put("content",new JSONObject().toString());
//
//                    vertexs.add(tmp_ve);
//
//                    JSONObject tmp_ed = new JSONObject();
//                    tmp_ed.put("root",root);
//                    tmp_ed.put("from",identity_danbao);
//                    tmp_ed.put("to",tmp_id);
//                    tmp_ed.put("name",one_attr_cn);
//
//                    edges.add(tmp_ed);
//                }
//            }

//                danbaos.put(danbao_name,identity_danbao);
        }


        if(obj.has("gua_itself") && verifyName(obj.getString("gua_itself"))){
            String gua_itself = obj.getString("gua_itself");




            JSONObject gua_itself_ve = new JSONObject();
            String gua_itself_id = UUID.randomUUID().toString();
            if(coms.containsKey(gua_itself)){
                gua_itself_id = coms.get(gua_itself);
            }else{
                coms.put(gua_itself,gua_itself_id);
            }


            gua_itself_ve.put("identity",gua_itself_id);
            gua_itself_ve.put("root",root);
            gua_itself_ve.put("name",gua_itself.trim());
            gua_itself_ve.put("type","com");
            gua_itself_ve.put("content",new JSONObject().toString());

            vertexs.add(gua_itself_ve);


            com_identity  = gua_itself_id;

        }


        JSONObject one_edges = new JSONObject();
        one_edges.put("root",root);
        one_edges.put("from",com_identity);
        one_edges.put("to",identity_danbao);
        one_edges.put("name","担保方");

        edges.add(one_edges);


        JSONObject two_edges = new JSONObject();
        two_edges.put("root",root);
        two_edges.put("from",identity);
        two_edges.put("to",identity_danbao);
        two_edges.put("name","被担保方");

        edges.add(two_edges);


        if(danbao_name.contains("子公司")){
            JSONObject rel_edges = new JSONObject();
            rel_edges.put("root",root);
            rel_edges.put("from",com_identity);
            rel_edges.put("to",identity);
            rel_edges.put("name","子公司");

            edges.add(rel_edges);
        }


    }


}
