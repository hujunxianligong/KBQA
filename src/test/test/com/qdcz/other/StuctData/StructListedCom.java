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
    private static String dir = "/home/hadoop/下载/导入数据/";

    private static String ve_csv = dir+"vertex.csv";
    private static String ve_txt = dir+"vertex_before.txt";

    private static String edge_csv = dir+"edges.csv";
    private static String edge_txt = dir+"edges_before.txt";

    private static String[] attr_en = {"lim_pub_date","gua_limit","act_occ_date","act_gua_money","gua_type","gua_date","whe_per_end",
            "whe_rel_guarantee","gua_beg_date","gua_lim_date","gua_whe_overdue","gua_ove_money","whe_rev_guarantee","rel_relation","lis_com_relation"};
    private static String[] attr_cn = {"披露日期","担保额度","协议签署日","实际担保金额","担保类型","担保期","是否履行完毕",
            "是否为关联方担保","担保起始日","担保到期日","担保是否逾期","担保逾期金额","是否存在反担保","关联关系","担保方与上市公司的关系"};


    private Map<String,Integer> danbao_weight = new HashMap<>();


    private static String ed_csv_2 = dir+"edges2.tmp";

    public static void main(String[] args) throws Exception {
        StructListedCom instance = new StructListedCom();
        instance.doIt();
    }




    public String changeName(String name){

        if(name.endsWith("公")){
            name = name+"司";
        }

        if(name.contains("，")){
            name = name.substring(0,name.indexOf("，"));
        }
        name = name.replace("［注］","").replace("（公司全资子公司）","").replace("[注]","").replace("（包括浙江五家子公司）","").replaceAll("（","(").replaceAll("）",")");//
        return name;
    }


    public boolean verifyName(String name){
            boolean flag = true;
//            if(name.length()>35){
//                if(name.contains("、")){
//                    String[] split=name.split("、");
//                    int nameCount = 0;
//                    for(String eachName : split){
//                        if(eachName.endsWith("公司")){
//                            nameCount++;
//                        }
//                    }
//                    if( nameCount ==split.length){
//                        return true;
//                    }
//                }
//            }
            if(name.isEmpty() || name.length()>35|| name.length()<5 || name.matches(".*\\d+.*")
                    || name.contains("√")|| name.contains("□") || name.contains("%") || name.contains("---")
                    || name.contains("\"")){

                return false;
            }
            if(name.replace("有限","").replace("公司","").replace("股份","").length()<4){
                return false;
            }

            return flag;
    }


    public void doIt() throws Exception {
        Scanner sc = new Scanner(new File(dir+"someJson"));
        List<JSONObject> vertexs =  new ArrayList<>();
        List<JSONObject> edges =  new ArrayList<>();
        Map<String,String> coms = new HashMap<>();
        Map<String,String> danbaos = new HashMap<>();
        StringBuffer sb_neo4j = new StringBuffer();
        StringBuffer sb_ela = new StringBuffer();




        CommonTool.printFile("root,name,type,content,identity,weight\n",ve_csv,false);

        CommonTool.printFile("root,name,from_id,to_id,identity,weight\n",ed_csv_2,false);


        CommonTool.printFile("",ve_txt,false);

        CommonTool.printFile("",edge_txt,false);

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


            companyName = changeName(companyName);

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
            getout_cre_guarantee(one_com.getJSONArray("out_cre_guarantee"),companyName,vertexs,edges,coms,danbaos,"out_cre_guarantee");


            //公司对子公司担保情况(com_sub_guarantee)
            getcom_sub_guarantee(one_com.getJSONArray("com_sub_guarantee"),companyName,vertexs,edges,coms,danbaos,"com_sub_guarantee");


            //子公司对子公司担保情况(sub_sub_guarantee)
            getsub_sub_guarantee(one_com.getJSONArray("sub_sub_guarantee"),companyName,vertexs,edges,coms,danbaos,"sub_sub_guarantee");

            //关联担保情况（rel_guarantee)
//            getrel_guarantee(one_com.getJSONArray("rel_guarantee"),companyName,vertexs,edges,coms,danbaos);

            printData(sb_neo4j,sb_ela,vertexs,edges);

        }


        StringBuffer sb = new StringBuffer();
        //权重
        for (String da_we : danbao_weight.keySet()) {
            int weight = danbao_weight.get(da_we);
            String from = da_we.split("_")[0];
            String to = da_we.split("_")[1];
            String type = da_we.split("_")[2];

            String name = "担保";

            if(from.equals(to)){
                continue;
            }

            JSONObject da_edges = new JSONObject();
            da_edges.put("root",root);
            da_edges.put("from",from);
            da_edges.put("to",to);
            da_edges.put("name",name);
            da_edges.put("weight",weight);
            String identity = UUID.randomUUID().toString();
            da_edges.put("identity",identity);

            sb_neo4j.append(root+","+name+","+from+","+to+","+identity+","+weight+"\n");
            sb_ela.append(da_edges.toString()+"\n");

            sb.append(root+","+type+","+from+","+to+","+identity+","+weight+"\n");



        }

        CommonTool.printFile(sb_neo4j.toString(),edge_csv,true);
        sb_neo4j.delete(0,sb_neo4j.length());
        CommonTool.printFile(sb_ela.toString(),edge_txt,true);
        sb_ela.delete(0,sb_ela.length());

        CommonTool.printFile(sb.toString(),ed_csv_2,true);
        sb.delete(0,sb.length());

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

            if(!type.equals("com")){
                continue;
            }

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
            int weight = 1;
            obj.put("weight",weight);
            if(from.equals(to)){
                continue;
            }

//            if(!name.equals("担保")){
//                continue;
//            }

            String identity = UUID.randomUUID().toString();
            obj.put("identity",identity);

            sb_neo4j.append(root+","+name+","+from+","+to+","+identity+","+weight+"\n");
            sb_ela.append(obj.toString()+"\n");
        }


        
        
        
        CommonTool.printFile(sb_neo4j.toString(),edge_csv,true);
        sb_neo4j.delete(0,sb_neo4j.length());
        CommonTool.printFile(sb_ela.toString(),edge_txt,true);
        sb_ela.delete(0,sb_ela.length());
    }


    private void getrel_guarantee(JSONArray rel_guarantee, String companyName, List<JSONObject> vertexs, List<JSONObject> edges, Map<String, String> coms, Map<String, String> danbaos,String type) throws Exception {
        String com_identity = coms.get(companyName);


    }

    private void getsub_sub_guarantee(JSONArray sub_sub_guarantee, String companyName, List<JSONObject> vertexs, List<JSONObject> edges, Map<String, String> coms, Map<String, String> danbaos,String type) throws Exception {
        String com_identity = coms.get(companyName);

        for (int i = 0;i<sub_sub_guarantee.length();i++){

            String danbao_name = "子公司对子公司担保";
            JSONObject obj = sub_sub_guarantee.getJSONObject(i);
            dealWithOne(companyName,vertexs,edges,coms,danbaos,obj,danbao_name,type);
        }
    }

    private void getcom_sub_guarantee(JSONArray com_sub_guarantee, String companyName, List<JSONObject> vertexs, List<JSONObject> edges, Map<String, String> coms, Map<String, String> danbaos,String type) throws Exception {


        for (int i = 0;i<com_sub_guarantee.length();i++){

            String danbao_name = "公司对子公司担保";
            JSONObject obj = com_sub_guarantee.getJSONObject(i);
            dealWithOne(companyName,vertexs,edges,coms,danbaos,obj,danbao_name,type);
        }
    }
    private void getout_cre_guarantee(JSONArray out_cre_guarantee, String companyName, List<JSONObject> vertexs, List<JSONObject> edges, Map<String, String> coms, Map<String, String> danbaos,String type) throws Exception {
        for (int i = 0;i<out_cre_guarantee.length();i++){
            String danbao_name = "对外担保";
            JSONObject obj = out_cre_guarantee.getJSONObject(i);
            dealWithOne(companyName,vertexs,edges,coms,danbaos,obj,danbao_name,type);
        }
    }

    private void dealWithOne(String companyName, List<JSONObject> vertexs,
                        List<JSONObject> edges, Map<String, String> coms, Map<String, String> danbaos,
                        JSONObject obj,String danbao_name,String type) throws Exception {

        String[] dan_fi = null;
        String[] dan_se = null;









        if(obj.has("gua_itself") && verifyName(obj.getString("gua_itself"))){
            String gua_itself = obj.getString("gua_itself");
            gua_itself = changeName(gua_itself);

            companyName = gua_itself;



        }



        if(companyName.contains("、")){
            dan_fi = companyName.split("、");
        }else{
            dan_fi = new String[]{companyName};
        }


        String gua_obj_name = obj.getString("gua_obj_name");

        if(!verifyName(gua_obj_name)){
            return;
        }



        if(gua_obj_name.equals(companyName)){
            return;
        }


        gua_obj_name = changeName(gua_obj_name);

        if(gua_obj_name.contains("、")){
            dan_se = gua_obj_name.split("、");
        }else{
            dan_se = new String[]{gua_obj_name};
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
            node_COM.put("type",type);
//                node_COM.put("content",content_obj.toString());
            node_COM.put("content",new JSONObject().toString());

            vertexs.add(node_COM);




            for (int i = 0;i<attr_en.length;i++){
                String one_attr_cn = attr_cn[i];
                String one_attr_en = attr_en[i];



//                  conyinur;

                if(obj.has(one_attr_en) && !obj.getString(one_attr_en).isEmpty()){
                    String tmp = obj.getString(one_attr_en);
                    JSONObject tmp_ve = new JSONObject();
                    String tmp_id = UUID.randomUUID().toString();
                    tmp_ve.put("identity",tmp_id);
                    tmp_ve.put("root",root);
                    tmp_ve.put("name",tmp.trim());
                    tmp_ve.put("type","attribute");
                    tmp_ve.put("content",new JSONObject().toString());

                    vertexs.add(tmp_ve);

                    JSONObject tmp_ed = new JSONObject();
                    tmp_ed.put("root",root);
                    tmp_ed.put("from",identity_danbao);
                    tmp_ed.put("to",tmp_id);
                    tmp_ed.put("name",one_attr_cn);

                    edges.add(tmp_ed);
                }
            }

//                danbaos.put(danbao_name,identity_danbao);
        }


        for (String fi:dan_fi){
            if(!verifyName(fi)){
                continue;
            }

            String com_identity = coms.get(companyName);

            JSONObject gua_itself_ve = new JSONObject();

            String gua_itself_id = UUID.randomUUID().toString();

            if(coms.containsKey(fi)){
                gua_itself_id = coms.get(fi);
            }else{

                gua_itself_ve.put("identity",gua_itself_id);
                gua_itself_ve.put("root",root);
                gua_itself_ve.put("name",fi.trim());
                gua_itself_ve.put("type","com");
                gua_itself_ve.put("content",new JSONObject().toString());

                vertexs.add(gua_itself_ve);


                coms.put(fi,gua_itself_id);
            }

            com_identity  = gua_itself_id;






            for (String se:dan_se) {
                if(!verifyName(se)){
                    continue;
                }



                String identity = "";
                if(!coms.containsKey(se)){
                    identity = UUID.randomUUID().toString();


                    JSONObject node_COM = new JSONObject();
                    node_COM.put("identity",identity);
                    node_COM.put("root",root);
                    node_COM.put("name",se.trim());
                    node_COM.put("type","com");
                    node_COM.put("content",new JSONObject().toString());

                    vertexs.add(node_COM);

                    coms.put(se,identity);
                }else{
                    identity = coms.get(se);
                }



                //边操作
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


                String we_name = com_identity+"_"+identity+"_"+type;
                if(danbao_weight.containsKey(we_name)){
                    danbao_weight.put(we_name,danbao_weight.get(we_name)+1);
                }else{
                    danbao_weight.put(we_name,1);
                }

            }
        }

    }


}
