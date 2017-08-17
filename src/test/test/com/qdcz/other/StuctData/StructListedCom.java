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
    public static void main(String[] args) throws Exception {
        StructListedCom instance = new StructListedCom();
        instance.doIt();
    }


    public void doIt() throws Exception {
        Scanner sc = new Scanner(new File("/media/star/Doc/工作文档/上市公司担保关系分析/大北json"));
        List<JSONObject> vertexs =  new ArrayList<>();
        List<JSONObject> edges =  new ArrayList<>();
        Map<String,String> coms = new HashMap<>();
        Map<String,String> danbaos = new HashMap<>();
        StringBuffer sb_neo4j = new StringBuffer();
        StringBuffer sb_ela = new StringBuffer();


        CommonTool.printFile("root,name,type,content,identity\n","/media/star/Doc/工作文档/上市公司担保关系分析/vertex.csv",true);

        CommonTool.printFile("root,name,from_id,to_id,identity\n","/media/star/Doc/工作文档/上市公司担保关系分析/edges.csv",true);

        while(sc.hasNext()){
            vertexs.clear();
            edges.clear();

            String line = sc.nextLine();

            JSONObject one_com = new JSONObject(line);

            String companyName = one_com.getString("cre_subject");

            if(companyName.endsWith("公")){
                companyName = companyName+"司";
            }

            if(companyName.isEmpty()){
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




            for (int i = 0;i< vertexs.size();i++){
                JSONObject obj = vertexs.get(i);
                String type = obj.getString("type").replace(",","，");
                String root =obj.getString("root").replace(",","，");
                String name = obj.getString("name").replace(",","，");
                String content = obj.getString("content").replace(",","，");
                String identity = obj.getString("identity").replace(",","，");

                sb_neo4j.append(root+","+name+","+type+","+content+","+identity+"\n");

                sb_ela.append(obj.toString()+"\n");


            }
            CommonTool.printFile(sb_neo4j.toString(),"/media/star/Doc/工作文档/上市公司担保关系分析/vertex.csv",true);
            sb_neo4j.delete(0,sb_neo4j.length());

            CommonTool.printFile(sb_ela.toString(),"/media/star/Doc/工作文档/上市公司担保关系分析/vertex.txt",true);
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
            CommonTool.printFile(sb_neo4j.toString(),"/media/star/Doc/工作文档/上市公司担保关系分析/edges.csv",true);
            sb_neo4j.delete(0,sb_neo4j.length());
            CommonTool.printFile(sb_ela.toString(),"/media/star/Doc/工作文档/上市公司担保关系分析/edges.txt",true);
            sb_ela.delete(0,sb_ela.length());


        }
        sc.close();
    }

    private void getrel_guarantee(JSONArray rel_guarantee, String companyName, List<JSONObject> vertexs, List<JSONObject> edges, Map<String, String> coms, Map<String, String> danbaos) throws Exception {
        String com_identity = coms.get(companyName);


        for (int i = 0;i<rel_guarantee.length();i++){
            JSONObject obj = rel_guarantee.getJSONObject(i);

            String gua_obj_name = obj.getString("gua_obj_name");
            String lim_pub_date = obj.getString("lim_pub_date");
            String gua_limit = obj.getString("gua_limit");
            String act_occ_date = obj.getString("act_occ_date");
            String act_gua_money = obj.getString("act_gua_money");
            String gua_type = obj.getString("gua_type");
            String gua_date = obj.getString("gua_date");
            String whe_per_end = obj.getString("whe_per_end");
            String whe_rel_guarantee = obj.getString("whe_rel_guarantee");




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


            String danbao_name = companyName+"_关联担保_"+act_occ_date+"_"+gua_limit+"_"+gua_obj_name+"_"+act_gua_money;


            String identity_danbao = "";
            if(danbaos.containsKey(danbao_name)){
                throw new Exception("两个担保");
            }else{

                identity_danbao = UUID.randomUUID().toString();

                JSONObject content_obj = new JSONObject();
                content_obj.put("lim_pub_date",lim_pub_date);
                content_obj.put("gua_limit",gua_limit);
                content_obj.put("act_occ_date",act_occ_date);
                content_obj.put("act_gua_money",act_gua_money);
                content_obj.put("gua_type",gua_type);
                content_obj.put("gua_date",gua_date);
                content_obj.put("whe_per_end",whe_per_end);
                content_obj.put("whe_rel_guarantee",whe_rel_guarantee);


                JSONObject node_COM = new JSONObject();
                node_COM.put("identity",identity_danbao);
                node_COM.put("root",root);
                node_COM.put("name",danbao_name.trim());
                node_COM.put("type","out_cre_guarantee");
//                node_COM.put("content",content_obj.toString());
                node_COM.put("content",new JSONObject().toString());

                vertexs.add(node_COM);



                if(!lim_pub_date.isEmpty()){
                    JSONObject lim_pub_date_ve = new JSONObject();
                    String lim_pub_date_id = UUID.randomUUID().toString();
                    lim_pub_date_ve.put("identity",lim_pub_date_id);
                    lim_pub_date_ve.put("root",root);
                    lim_pub_date_ve.put("name",lim_pub_date.trim());
                    lim_pub_date_ve.put("type","attribute");
                    lim_pub_date_ve.put("content",new JSONObject().toString());

                    vertexs.add(lim_pub_date_ve);

                    JSONObject lim_pub_date_ed = new JSONObject();
                    lim_pub_date_ed.put("root",root);
                    lim_pub_date_ed.put("from",identity_danbao);
                    lim_pub_date_ed.put("to",lim_pub_date_id);
                    lim_pub_date_ed.put("name","披露日期");

                    edges.add(lim_pub_date_ed);
                }




                if(!gua_limit.isEmpty()){
                    JSONObject gua_limit_ve = new JSONObject();
                    String gua_limit_id = UUID.randomUUID().toString();
                    gua_limit_ve.put("identity",gua_limit_id);
                    gua_limit_ve.put("root",root);
                    gua_limit_ve.put("name",gua_limit.trim());
                    gua_limit_ve.put("type","attribute");
                    gua_limit_ve.put("content",new JSONObject().toString());

                    vertexs.add(gua_limit_ve);

                    JSONObject gua_limit_ed = new JSONObject();
                    gua_limit_ed.put("root",root);
                    gua_limit_ed.put("from",identity_danbao);
                    gua_limit_ed.put("to",gua_limit_id);
                    gua_limit_ed.put("name","担保额度");

                    edges.add(gua_limit_ed);
                }


                if(!act_occ_date.isEmpty()){
                    JSONObject act_occ_date_ve = new JSONObject();
                    String act_occ_date_id = UUID.randomUUID().toString();
                    act_occ_date_ve.put("identity",act_occ_date_id);
                    act_occ_date_ve.put("root",root);
                    act_occ_date_ve.put("name",act_occ_date.trim());
                    act_occ_date_ve.put("type","attribute");
                    act_occ_date_ve.put("content",new JSONObject().toString());

                    vertexs.add(act_occ_date_ve);

                    JSONObject act_occ_date_ed = new JSONObject();
                    act_occ_date_ed.put("root",root);
                    act_occ_date_ed.put("from",identity_danbao);
                    act_occ_date_ed.put("to",act_occ_date_id);
                    act_occ_date_ed.put("name","协议签署日");

                    edges.add(act_occ_date_ed);
                }


                if(!act_gua_money.isEmpty()){
                    JSONObject act_gua_money_ve = new JSONObject();
                    String act_gua_money_id = UUID.randomUUID().toString();
                    act_gua_money_ve.put("identity",act_gua_money_id);
                    act_gua_money_ve.put("root",root);
                    act_gua_money_ve.put("name",act_gua_money.trim());
                    act_gua_money_ve.put("type","attribute");
                    act_gua_money_ve.put("content",new JSONObject().toString());

                    vertexs.add(act_gua_money_ve);

                    JSONObject act_gua_money_ed = new JSONObject();
                    act_gua_money_ed.put("root",root);
                    act_gua_money_ed.put("from",identity_danbao);
                    act_gua_money_ed.put("to",act_gua_money_id);
                    act_gua_money_ed.put("name","实际担保金额");

                    edges.add(act_gua_money_ed);
                }


                if(!gua_type.isEmpty()){
                    JSONObject gua_type_ve = new JSONObject();
                    String gua_type_id = UUID.randomUUID().toString();
                    gua_type_ve.put("identity",gua_type_id);
                    gua_type_ve.put("root",root);
                    gua_type_ve.put("name",gua_type.trim());
                    gua_type_ve.put("type","attribute");
                    gua_type_ve.put("content",new JSONObject().toString());

                    vertexs.add(gua_type_ve);

                    JSONObject gua_type_ed = new JSONObject();
                    gua_type_ed.put("root",root);
                    gua_type_ed.put("from",identity_danbao);
                    gua_type_ed.put("to",gua_type_id);
                    gua_type_ed.put("name","担保类型");

                    edges.add(gua_type_ed);
                }


                if(!gua_date.isEmpty()){
                    JSONObject gua_date_ve = new JSONObject();
                    String gua_date_id = UUID.randomUUID().toString();
                    gua_date_ve.put("identity",gua_date_id);
                    gua_date_ve.put("root",root);
                    gua_date_ve.put("name",gua_date.trim());
                    gua_date_ve.put("type","attribute");
                    gua_date_ve.put("content",new JSONObject().toString());

                    vertexs.add(gua_date_ve);

                    JSONObject gua_date_ed = new JSONObject();
                    gua_date_ed.put("root",root);
                    gua_date_ed.put("from",identity_danbao);
                    gua_date_ed.put("to",gua_date_id);
                    gua_date_ed.put("name","担保期");

                    edges.add(gua_date_ed);
                }




                if(!whe_per_end.isEmpty()){
                    JSONObject whe_per_end_ve = new JSONObject();
                    String whe_per_end_id = UUID.randomUUID().toString();
                    whe_per_end_ve.put("identity",whe_per_end_id);
                    whe_per_end_ve.put("root",root);
                    whe_per_end_ve.put("name",whe_per_end.trim());
                    whe_per_end_ve.put("type","attribute");
                    whe_per_end_ve.put("content",new JSONObject().toString());

                    vertexs.add(whe_per_end_ve);

                    JSONObject whe_per_end_ed = new JSONObject();
                    whe_per_end_ed.put("root",root);
                    whe_per_end_ed.put("from",identity_danbao);
                    whe_per_end_ed.put("to",whe_per_end_id);
                    whe_per_end_ed.put("name","是否履行完毕");

                    edges.add(whe_per_end_ed);
                }


                if(!whe_rel_guarantee.isEmpty()){
                    JSONObject whe_rel_guarantee_ve = new JSONObject();
                    String whe_rel_guarantee_id = UUID.randomUUID().toString();
                    whe_rel_guarantee_ve.put("identity",whe_rel_guarantee_id);
                    whe_rel_guarantee_ve.put("root",root);
                    whe_rel_guarantee_ve.put("name",whe_rel_guarantee.trim());
                    whe_rel_guarantee_ve.put("type","attribute");
                    whe_rel_guarantee_ve.put("content",new JSONObject().toString());

                    vertexs.add(whe_rel_guarantee_ve);

                    JSONObject whe_rel_guarantee_ed = new JSONObject();
                    whe_rel_guarantee_ed.put("root",root);
                    whe_rel_guarantee_ed.put("from",identity_danbao);
                    whe_rel_guarantee_ed.put("to",whe_rel_guarantee_id);
                    whe_rel_guarantee_ed.put("name","是否为关联方担保");

                    edges.add(whe_rel_guarantee_ed);
                }




//                danbaos.put(danbao_name,identity_danbao);
            }



            JSONObject one_edges = new JSONObject();
            one_edges.put("root",root);
            one_edges.put("from",identity_danbao);
            one_edges.put("to",com_identity);
            one_edges.put("name","担保方");

            edges.add(one_edges);


            JSONObject two_edges = new JSONObject();
            two_edges.put("root",root);
            two_edges.put("from",identity_danbao);
            two_edges.put("to",identity);
            two_edges.put("name","被担保方");

            edges.add(two_edges);

        }
    }

    private void getsub_sub_guarantee(JSONArray sub_sub_guarantee, String companyName, List<JSONObject> vertexs, List<JSONObject> edges, Map<String, String> coms, Map<String, String> danbaos) throws Exception {
        String com_identity = coms.get(companyName);


        for (int i = 0;i<sub_sub_guarantee.length();i++){
            JSONObject obj = sub_sub_guarantee.getJSONObject(i);

            String gua_obj_name = obj.getString("gua_obj_name");

            if(gua_obj_name.endsWith("公")){
                gua_obj_name = gua_obj_name+"司";
            }

            String lim_pub_date = obj.getString("lim_pub_date");
            String gua_limit = obj.getString("gua_limit");
            String act_occ_date = obj.getString("act_occ_date");
            String act_gua_money = obj.getString("act_gua_money");
            String gua_type = obj.getString("gua_type");
            String gua_date = obj.getString("gua_date");
            String whe_per_end = obj.getString("whe_per_end");
            String whe_rel_guarantee = obj.getString("whe_rel_guarantee");




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


            String danbao_name = companyName+"_子公司对子公司担保_"+act_occ_date+"_"+gua_limit+"_"+gua_obj_name+"_"+act_gua_money;

            danbao_name = "子公司对子公司担保";

            String identity_danbao = "";
            if(danbaos.containsKey(danbao_name)){
                throw new Exception("两个担保");
            }else{

                identity_danbao = UUID.randomUUID().toString();

                JSONObject content_obj = new JSONObject();
                content_obj.put("lim_pub_date",lim_pub_date);
                content_obj.put("gua_limit",gua_limit);
                content_obj.put("act_occ_date",act_occ_date);
                content_obj.put("act_gua_money",act_gua_money);
                content_obj.put("gua_type",gua_type);
                content_obj.put("gua_date",gua_date);
                content_obj.put("whe_per_end",whe_per_end);
                content_obj.put("whe_rel_guarantee",whe_rel_guarantee);


                JSONObject node_COM = new JSONObject();
                node_COM.put("identity",identity_danbao);
                node_COM.put("root",root);
                node_COM.put("name",danbao_name.trim());
                node_COM.put("type","out_cre_guarantee");
//                node_COM.put("content",content_obj.toString());
                node_COM.put("content",new JSONObject().toString());

                vertexs.add(node_COM);



                if(!lim_pub_date.isEmpty()){
                    JSONObject lim_pub_date_ve = new JSONObject();
                    String lim_pub_date_id = UUID.randomUUID().toString();
                    lim_pub_date_ve.put("identity",lim_pub_date_id);
                    lim_pub_date_ve.put("root",root);
                    lim_pub_date_ve.put("name",lim_pub_date.trim());
                    lim_pub_date_ve.put("type","attribute");
                    lim_pub_date_ve.put("content",new JSONObject().toString());

                    vertexs.add(lim_pub_date_ve);

                    JSONObject lim_pub_date_ed = new JSONObject();
                    lim_pub_date_ed.put("root",root);
                    lim_pub_date_ed.put("from",identity_danbao);
                    lim_pub_date_ed.put("to",lim_pub_date_id);
                    lim_pub_date_ed.put("name","披露日期");

                    edges.add(lim_pub_date_ed);
                }




                if(!gua_limit.isEmpty()){
                    JSONObject gua_limit_ve = new JSONObject();
                    String gua_limit_id = UUID.randomUUID().toString();
                    gua_limit_ve.put("identity",gua_limit_id);
                    gua_limit_ve.put("root",root);
                    gua_limit_ve.put("name",gua_limit.trim());
                    gua_limit_ve.put("type","attribute");
                    gua_limit_ve.put("content",new JSONObject().toString());

                    vertexs.add(gua_limit_ve);

                    JSONObject gua_limit_ed = new JSONObject();
                    gua_limit_ed.put("root",root);
                    gua_limit_ed.put("from",identity_danbao);
                    gua_limit_ed.put("to",gua_limit_id);
                    gua_limit_ed.put("name","担保额度");

                    edges.add(gua_limit_ed);
                }


                if(!act_occ_date.isEmpty()){
                    JSONObject act_occ_date_ve = new JSONObject();
                    String act_occ_date_id = UUID.randomUUID().toString();
                    act_occ_date_ve.put("identity",act_occ_date_id);
                    act_occ_date_ve.put("root",root);
                    act_occ_date_ve.put("name",act_occ_date.trim());
                    act_occ_date_ve.put("type","attribute");
                    act_occ_date_ve.put("content",new JSONObject().toString());

                    vertexs.add(act_occ_date_ve);

                    JSONObject act_occ_date_ed = new JSONObject();
                    act_occ_date_ed.put("root",root);
                    act_occ_date_ed.put("from",identity_danbao);
                    act_occ_date_ed.put("to",act_occ_date_id);
                    act_occ_date_ed.put("name","协议签署日");

                    edges.add(act_occ_date_ed);
                }


                if(!act_gua_money.isEmpty()){
                    JSONObject act_gua_money_ve = new JSONObject();
                    String act_gua_money_id = UUID.randomUUID().toString();
                    act_gua_money_ve.put("identity",act_gua_money_id);
                    act_gua_money_ve.put("root",root);
                    act_gua_money_ve.put("name",act_gua_money.trim());
                    act_gua_money_ve.put("type","attribute");
                    act_gua_money_ve.put("content",new JSONObject().toString());

                    vertexs.add(act_gua_money_ve);

                    JSONObject act_gua_money_ed = new JSONObject();
                    act_gua_money_ed.put("root",root);
                    act_gua_money_ed.put("from",identity_danbao);
                    act_gua_money_ed.put("to",act_gua_money_id);
                    act_gua_money_ed.put("name","实际担保金额");

                    edges.add(act_gua_money_ed);
                }


                if(!gua_type.isEmpty()){
                    JSONObject gua_type_ve = new JSONObject();
                    String gua_type_id = UUID.randomUUID().toString();
                    gua_type_ve.put("identity",gua_type_id);
                    gua_type_ve.put("root",root);
                    gua_type_ve.put("name",gua_type.trim());
                    gua_type_ve.put("type","attribute");
                    gua_type_ve.put("content",new JSONObject().toString());

                    vertexs.add(gua_type_ve);

                    JSONObject gua_type_ed = new JSONObject();
                    gua_type_ed.put("root",root);
                    gua_type_ed.put("from",identity_danbao);
                    gua_type_ed.put("to",gua_type_id);
                    gua_type_ed.put("name","担保类型");

                    edges.add(gua_type_ed);
                }


                if(!gua_date.isEmpty()){
                    JSONObject gua_date_ve = new JSONObject();
                    String gua_date_id = UUID.randomUUID().toString();
                    gua_date_ve.put("identity",gua_date_id);
                    gua_date_ve.put("root",root);
                    gua_date_ve.put("name",gua_date.trim());
                    gua_date_ve.put("type","attribute");
                    gua_date_ve.put("content",new JSONObject().toString());

                    vertexs.add(gua_date_ve);

                    JSONObject gua_date_ed = new JSONObject();
                    gua_date_ed.put("root",root);
                    gua_date_ed.put("from",identity_danbao);
                    gua_date_ed.put("to",gua_date_id);
                    gua_date_ed.put("name","担保期");

                    edges.add(gua_date_ed);
                }




                if(!whe_per_end.isEmpty()){
                    JSONObject whe_per_end_ve = new JSONObject();
                    String whe_per_end_id = UUID.randomUUID().toString();
                    whe_per_end_ve.put("identity",whe_per_end_id);
                    whe_per_end_ve.put("root",root);
                    whe_per_end_ve.put("name",whe_per_end.trim());
                    whe_per_end_ve.put("type","attribute");
                    whe_per_end_ve.put("content",new JSONObject().toString());

                    vertexs.add(whe_per_end_ve);

                    JSONObject whe_per_end_ed = new JSONObject();
                    whe_per_end_ed.put("root",root);
                    whe_per_end_ed.put("from",identity_danbao);
                    whe_per_end_ed.put("to",whe_per_end_id);
                    whe_per_end_ed.put("name","是否履行完毕");

                    edges.add(whe_per_end_ed);
                }


                if(!whe_rel_guarantee.isEmpty()){
                    JSONObject whe_rel_guarantee_ve = new JSONObject();
                    String whe_rel_guarantee_id = UUID.randomUUID().toString();
                    whe_rel_guarantee_ve.put("identity",whe_rel_guarantee_id);
                    whe_rel_guarantee_ve.put("root",root);
                    whe_rel_guarantee_ve.put("name",whe_rel_guarantee.trim());
                    whe_rel_guarantee_ve.put("type","attribute");
                    whe_rel_guarantee_ve.put("content",new JSONObject().toString());

                    vertexs.add(whe_rel_guarantee_ve);

                    JSONObject whe_rel_guarantee_ed = new JSONObject();
                    whe_rel_guarantee_ed.put("root",root);
                    whe_rel_guarantee_ed.put("from",identity_danbao);
                    whe_rel_guarantee_ed.put("to",whe_rel_guarantee_id);
                    whe_rel_guarantee_ed.put("name","是否为关联方担保");

                    edges.add(whe_rel_guarantee_ed);
                }




//                danbaos.put(danbao_name,identity_danbao);
            }



//            JSONObject one_edges = new JSONObject();
//            one_edges.put("root",root);
//            one_edges.put("from",identity_danbao);
//            one_edges.put("to",com_identity);
//            one_edges.put("name","担保方");
//
//            edges.add(one_edges);


            JSONObject two_edges = new JSONObject();
            two_edges.put("root",root);
            two_edges.put("from",identity_danbao);
            two_edges.put("to",identity);
            two_edges.put("name","担保方");

            edges.add(two_edges);


            JSONObject rel_edges = new JSONObject();
            rel_edges.put("root",root);
            rel_edges.put("from",com_identity);
            rel_edges.put("to",identity);
            rel_edges.put("name","子公司");

            edges.add(rel_edges);

        }
    }

    private void getcom_sub_guarantee(JSONArray com_sub_guarantee, String companyName, List<JSONObject> vertexs, List<JSONObject> edges, Map<String, String> coms, Map<String, String> danbaos) throws Exception {
        String com_identity = coms.get(companyName);


        for (int i = 0;i<com_sub_guarantee.length();i++){
            JSONObject obj = com_sub_guarantee.getJSONObject(i);

            String gua_obj_name = obj.getString("gua_obj_name");

            if(gua_obj_name.endsWith("公")){
                gua_obj_name = gua_obj_name+"司";
            }

            String lim_pub_date = obj.getString("lim_pub_date");
            String gua_limit = obj.getString("gua_limit");
            String act_occ_date = obj.getString("act_occ_date");
            String act_gua_money = obj.getString("act_gua_money");
            String gua_type = obj.getString("gua_type");
            String gua_date = obj.getString("gua_date");
            String whe_per_end = obj.getString("whe_per_end");
            String whe_rel_guarantee = obj.getString("whe_rel_guarantee");




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


            String danbao_name = companyName+"_公司对子公司担保_"+act_occ_date+"_"+gua_limit+"_"+gua_obj_name+"_"+act_gua_money;

            danbao_name = "公司对子公司担保";

            String identity_danbao = "";
            if(danbaos.containsKey(danbao_name)){
                throw new Exception("两个担保:"+danbao_name );
            }else{

                identity_danbao = UUID.randomUUID().toString();

                JSONObject content_obj = new JSONObject();
                content_obj.put("lim_pub_date",lim_pub_date);
                content_obj.put("gua_limit",gua_limit);
                content_obj.put("act_occ_date",act_occ_date);
                content_obj.put("act_gua_money",act_gua_money);
                content_obj.put("gua_type",gua_type);
                content_obj.put("gua_date",gua_date);
                content_obj.put("whe_per_end",whe_per_end);
                content_obj.put("whe_rel_guarantee",whe_rel_guarantee);


                JSONObject node_COM = new JSONObject();
                node_COM.put("identity",identity_danbao);
                node_COM.put("root",root);
                node_COM.put("name",danbao_name.trim());
                node_COM.put("type","out_cre_guarantee");
//                node_COM.put("content",content_obj.toString());
                node_COM.put("content",new JSONObject().toString());

                vertexs.add(node_COM);



                if(!lim_pub_date.isEmpty()){
                    JSONObject lim_pub_date_ve = new JSONObject();
                    String lim_pub_date_id = UUID.randomUUID().toString();
                    lim_pub_date_ve.put("identity",lim_pub_date_id);
                    lim_pub_date_ve.put("root",root);
                    lim_pub_date_ve.put("name",lim_pub_date.trim());
                    lim_pub_date_ve.put("type","attribute");
                    lim_pub_date_ve.put("content",new JSONObject().toString());

                    vertexs.add(lim_pub_date_ve);

                    JSONObject lim_pub_date_ed = new JSONObject();
                    lim_pub_date_ed.put("root",root);
                    lim_pub_date_ed.put("from",identity_danbao);
                    lim_pub_date_ed.put("to",lim_pub_date_id);
                    lim_pub_date_ed.put("name","披露日期");

                    edges.add(lim_pub_date_ed);
                }




                if(!gua_limit.isEmpty()){
                    JSONObject gua_limit_ve = new JSONObject();
                    String gua_limit_id = UUID.randomUUID().toString();
                    gua_limit_ve.put("identity",gua_limit_id);
                    gua_limit_ve.put("root",root);
                    gua_limit_ve.put("name",gua_limit.trim());
                    gua_limit_ve.put("type","attribute");
                    gua_limit_ve.put("content",new JSONObject().toString());

                    vertexs.add(gua_limit_ve);

                    JSONObject gua_limit_ed = new JSONObject();
                    gua_limit_ed.put("root",root);
                    gua_limit_ed.put("from",identity_danbao);
                    gua_limit_ed.put("to",gua_limit_id);
                    gua_limit_ed.put("name","担保额度");

                    edges.add(gua_limit_ed);
                }


                if(!act_occ_date.isEmpty()){
                    JSONObject act_occ_date_ve = new JSONObject();
                    String act_occ_date_id = UUID.randomUUID().toString();
                    act_occ_date_ve.put("identity",act_occ_date_id);
                    act_occ_date_ve.put("root",root);
                    act_occ_date_ve.put("name",act_occ_date.trim());
                    act_occ_date_ve.put("type","attribute");
                    act_occ_date_ve.put("content",new JSONObject().toString());

                    vertexs.add(act_occ_date_ve);

                    JSONObject act_occ_date_ed = new JSONObject();
                    act_occ_date_ed.put("root",root);
                    act_occ_date_ed.put("from",identity_danbao);
                    act_occ_date_ed.put("to",act_occ_date_id);
                    act_occ_date_ed.put("name","协议签署日");

                    edges.add(act_occ_date_ed);
                }


                if(!act_gua_money.isEmpty()){
                    JSONObject act_gua_money_ve = new JSONObject();
                    String act_gua_money_id = UUID.randomUUID().toString();
                    act_gua_money_ve.put("identity",act_gua_money_id);
                    act_gua_money_ve.put("root",root);
                    act_gua_money_ve.put("name",act_gua_money.trim());
                    act_gua_money_ve.put("type","attribute");
                    act_gua_money_ve.put("content",new JSONObject().toString());

                    vertexs.add(act_gua_money_ve);

                    JSONObject act_gua_money_ed = new JSONObject();
                    act_gua_money_ed.put("root",root);
                    act_gua_money_ed.put("from",identity_danbao);
                    act_gua_money_ed.put("to",act_gua_money_id);
                    act_gua_money_ed.put("name","实际担保金额");

                    edges.add(act_gua_money_ed);
                }


                if(!gua_type.isEmpty()){
                    JSONObject gua_type_ve = new JSONObject();
                    String gua_type_id = UUID.randomUUID().toString();
                    gua_type_ve.put("identity",gua_type_id);
                    gua_type_ve.put("root",root);
                    gua_type_ve.put("name",gua_type.trim());
                    gua_type_ve.put("type","attribute");
                    gua_type_ve.put("content",new JSONObject().toString());

                    vertexs.add(gua_type_ve);

                    JSONObject gua_type_ed = new JSONObject();
                    gua_type_ed.put("root",root);
                    gua_type_ed.put("from",identity_danbao);
                    gua_type_ed.put("to",gua_type_id);
                    gua_type_ed.put("name","担保类型");

                    edges.add(gua_type_ed);
                }


                if(!gua_date.isEmpty()){
                    JSONObject gua_date_ve = new JSONObject();
                    String gua_date_id = UUID.randomUUID().toString();
                    gua_date_ve.put("identity",gua_date_id);
                    gua_date_ve.put("root",root);
                    gua_date_ve.put("name",gua_date.trim());
                    gua_date_ve.put("type","attribute");
                    gua_date_ve.put("content",new JSONObject().toString());

                    vertexs.add(gua_date_ve);

                    JSONObject gua_date_ed = new JSONObject();
                    gua_date_ed.put("root",root);
                    gua_date_ed.put("from",identity_danbao);
                    gua_date_ed.put("to",gua_date_id);
                    gua_date_ed.put("name","担保期");

                    edges.add(gua_date_ed);
                }




                if(!whe_per_end.isEmpty()){
                    JSONObject whe_per_end_ve = new JSONObject();
                    String whe_per_end_id = UUID.randomUUID().toString();
                    whe_per_end_ve.put("identity",whe_per_end_id);
                    whe_per_end_ve.put("root",root);
                    whe_per_end_ve.put("name",whe_per_end.trim());
                    whe_per_end_ve.put("type","attribute");
                    whe_per_end_ve.put("content",new JSONObject().toString());

                    vertexs.add(whe_per_end_ve);

                    JSONObject whe_per_end_ed = new JSONObject();
                    whe_per_end_ed.put("root",root);
                    whe_per_end_ed.put("from",identity_danbao);
                    whe_per_end_ed.put("to",whe_per_end_id);
                    whe_per_end_ed.put("name","是否履行完毕");

                    edges.add(whe_per_end_ed);
                }


                if(!whe_rel_guarantee.isEmpty()){
                    JSONObject whe_rel_guarantee_ve = new JSONObject();
                    String whe_rel_guarantee_id = UUID.randomUUID().toString();
                    whe_rel_guarantee_ve.put("identity",whe_rel_guarantee_id);
                    whe_rel_guarantee_ve.put("root",root);
                    whe_rel_guarantee_ve.put("name",whe_rel_guarantee.trim());
                    whe_rel_guarantee_ve.put("type","attribute");
                    whe_rel_guarantee_ve.put("content",new JSONObject().toString());

                    vertexs.add(whe_rel_guarantee_ve);

                    JSONObject whe_rel_guarantee_ed = new JSONObject();
                    whe_rel_guarantee_ed.put("root",root);
                    whe_rel_guarantee_ed.put("from",identity_danbao);
                    whe_rel_guarantee_ed.put("to",whe_rel_guarantee_id);
                    whe_rel_guarantee_ed.put("name","是否为关联方担保");

                    edges.add(whe_rel_guarantee_ed);
                }




//                danbaos.put(danbao_name,identity_danbao);
            }



            JSONObject one_edges = new JSONObject();
            one_edges.put("root",root);
            one_edges.put("from",identity_danbao);
            one_edges.put("to",com_identity);
            one_edges.put("name","担保方");

            edges.add(one_edges);


            JSONObject two_edges = new JSONObject();
            two_edges.put("root",root);
            two_edges.put("from",identity_danbao);
            two_edges.put("to",identity);
            two_edges.put("name","被担保方");

            edges.add(two_edges);





            JSONObject rel_edges = new JSONObject();
            rel_edges.put("root",root);
            rel_edges.put("from",com_identity);
            rel_edges.put("to",identity);
            rel_edges.put("name","子公司");

            edges.add(rel_edges);




        }
    }


    private void getout_cre_guarantee(JSONArray out_cre_guarantee, String companyName, List<JSONObject> vertexs, List<JSONObject> edges, Map<String, String> coms, Map<String, String> danbaos) throws Exception {
        String com_identity = coms.get(companyName);


        for (int i = 0;i<out_cre_guarantee.length();i++){
            JSONObject obj = out_cre_guarantee.getJSONObject(i);

            String gua_obj_name = obj.getString("gua_obj_name");


            if(gua_obj_name.endsWith("公")){
                gua_obj_name = gua_obj_name+"司";
            }



            String lim_pub_date = obj.getString("lim_pub_date");
            String gua_limit = obj.getString("gua_limit");
            String act_occ_date = obj.getString("act_occ_date");
            String act_gua_money = obj.getString("act_gua_money");
            String gua_type = obj.getString("gua_type");
            String gua_date = obj.getString("gua_date");
            String whe_per_end = obj.getString("whe_per_end");
            String whe_rel_guarantee = obj.getString("whe_rel_guarantee");




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


            String danbao_name = companyName+"_对外担保_"+act_occ_date+"_"+gua_limit+"_"+gua_obj_name+"_"+act_gua_money;

            danbao_name = "对外担保";

            String identity_danbao = "";
            if(danbaos.containsKey(danbao_name)){
                throw new Exception("两个担保:"+danbao_name);
            }else{

                identity_danbao = UUID.randomUUID().toString();

                JSONObject content_obj = new JSONObject();
                content_obj.put("lim_pub_date",lim_pub_date);
                content_obj.put("gua_limit",gua_limit);
                content_obj.put("act_occ_date",act_occ_date);
                content_obj.put("act_gua_money",act_gua_money);
                content_obj.put("gua_type",gua_type);
                content_obj.put("gua_date",gua_date);
                content_obj.put("whe_per_end",whe_per_end);
                content_obj.put("whe_rel_guarantee",whe_rel_guarantee);


                JSONObject node_COM = new JSONObject();
                node_COM.put("identity",identity_danbao);
                node_COM.put("root",root);
                node_COM.put("name",danbao_name.trim());
                node_COM.put("type","out_cre_guarantee");
//                node_COM.put("content",content_obj.toString());
                node_COM.put("content",new JSONObject().toString());

                vertexs.add(node_COM);



                if(!lim_pub_date.isEmpty()){
                    JSONObject lim_pub_date_ve = new JSONObject();
                    String lim_pub_date_id = UUID.randomUUID().toString();
                    lim_pub_date_ve.put("identity",lim_pub_date_id);
                    lim_pub_date_ve.put("root",root);
                    lim_pub_date_ve.put("name",lim_pub_date.trim());
                    lim_pub_date_ve.put("type","attribute");
                    lim_pub_date_ve.put("content",new JSONObject().toString());

                    vertexs.add(lim_pub_date_ve);

                    JSONObject lim_pub_date_ed = new JSONObject();
                    lim_pub_date_ed.put("root",root);
                    lim_pub_date_ed.put("from",identity_danbao);
                    lim_pub_date_ed.put("to",lim_pub_date_id);
                    lim_pub_date_ed.put("name","披露日期");

                    edges.add(lim_pub_date_ed);
                }




                if(!gua_limit.isEmpty()){
                    JSONObject gua_limit_ve = new JSONObject();
                    String gua_limit_id = UUID.randomUUID().toString();
                    gua_limit_ve.put("identity",gua_limit_id);
                    gua_limit_ve.put("root",root);
                    gua_limit_ve.put("name",gua_limit.trim());
                    gua_limit_ve.put("type","attribute");
                    gua_limit_ve.put("content",new JSONObject().toString());

                    vertexs.add(gua_limit_ve);

                    JSONObject gua_limit_ed = new JSONObject();
                    gua_limit_ed.put("root",root);
                    gua_limit_ed.put("from",identity_danbao);
                    gua_limit_ed.put("to",gua_limit_id);
                    gua_limit_ed.put("name","担保额度");

                    edges.add(gua_limit_ed);
                }


                if(!act_occ_date.isEmpty()){
                    JSONObject act_occ_date_ve = new JSONObject();
                    String act_occ_date_id = UUID.randomUUID().toString();
                    act_occ_date_ve.put("identity",act_occ_date_id);
                    act_occ_date_ve.put("root",root);
                    act_occ_date_ve.put("name",act_occ_date.trim());
                    act_occ_date_ve.put("type","attribute");
                    act_occ_date_ve.put("content",new JSONObject().toString());

                    vertexs.add(act_occ_date_ve);

                    JSONObject act_occ_date_ed = new JSONObject();
                    act_occ_date_ed.put("root",root);
                    act_occ_date_ed.put("from",identity_danbao);
                    act_occ_date_ed.put("to",act_occ_date_id);
                    act_occ_date_ed.put("name","协议签署日");

                    edges.add(act_occ_date_ed);
                }


                if(!act_gua_money.isEmpty()){
                    JSONObject act_gua_money_ve = new JSONObject();
                    String act_gua_money_id = UUID.randomUUID().toString();
                    act_gua_money_ve.put("identity",act_gua_money_id);
                    act_gua_money_ve.put("root",root);
                    act_gua_money_ve.put("name",act_gua_money.trim());
                    act_gua_money_ve.put("type","attribute");
                    act_gua_money_ve.put("content",new JSONObject().toString());

                    vertexs.add(act_gua_money_ve);

                    JSONObject act_gua_money_ed = new JSONObject();
                    act_gua_money_ed.put("root",root);
                    act_gua_money_ed.put("from",identity_danbao);
                    act_gua_money_ed.put("to",act_gua_money_id);
                    act_gua_money_ed.put("name","实际担保金额");

                    edges.add(act_gua_money_ed);
                }


                if(!gua_type.isEmpty()){
                    JSONObject gua_type_ve = new JSONObject();
                    String gua_type_id = UUID.randomUUID().toString();
                    gua_type_ve.put("identity",gua_type_id);
                    gua_type_ve.put("root",root);
                    gua_type_ve.put("name",gua_type.trim());
                    gua_type_ve.put("type","attribute");
                    gua_type_ve.put("content",new JSONObject().toString());

                    vertexs.add(gua_type_ve);

                    JSONObject gua_type_ed = new JSONObject();
                    gua_type_ed.put("root",root);
                    gua_type_ed.put("from",identity_danbao);
                    gua_type_ed.put("to",gua_type_id);
                    gua_type_ed.put("name","担保类型");

                    edges.add(gua_type_ed);
                }


                if(!gua_date.isEmpty()){
                    JSONObject gua_date_ve = new JSONObject();
                    String gua_date_id = UUID.randomUUID().toString();
                    gua_date_ve.put("identity",gua_date_id);
                    gua_date_ve.put("root",root);
                    gua_date_ve.put("name",gua_date.trim());
                    gua_date_ve.put("type","attribute");
                    gua_date_ve.put("content",new JSONObject().toString());

                    vertexs.add(gua_date_ve);

                    JSONObject gua_date_ed = new JSONObject();
                    gua_date_ed.put("root",root);
                    gua_date_ed.put("from",identity_danbao);
                    gua_date_ed.put("to",gua_date_id);
                    gua_date_ed.put("name","担保期");

                    edges.add(gua_date_ed);
                }




                if(!whe_per_end.isEmpty()){
                    JSONObject whe_per_end_ve = new JSONObject();
                    String whe_per_end_id = UUID.randomUUID().toString();
                    whe_per_end_ve.put("identity",whe_per_end_id);
                    whe_per_end_ve.put("root",root);
                    whe_per_end_ve.put("name",whe_per_end.trim());
                    whe_per_end_ve.put("type","attribute");
                    whe_per_end_ve.put("content",new JSONObject().toString());

                    vertexs.add(whe_per_end_ve);

                    JSONObject whe_per_end_ed = new JSONObject();
                    whe_per_end_ed.put("root",root);
                    whe_per_end_ed.put("from",identity_danbao);
                    whe_per_end_ed.put("to",whe_per_end_id);
                    whe_per_end_ed.put("name","是否履行完毕");

                    edges.add(whe_per_end_ed);
                }


                if(!whe_rel_guarantee.isEmpty()){
                    JSONObject whe_rel_guarantee_ve = new JSONObject();
                    String whe_rel_guarantee_id = UUID.randomUUID().toString();
                    whe_rel_guarantee_ve.put("identity",whe_rel_guarantee_id);
                    whe_rel_guarantee_ve.put("root",root);
                    whe_rel_guarantee_ve.put("name",whe_rel_guarantee.trim());
                    whe_rel_guarantee_ve.put("type","attribute");
                    whe_rel_guarantee_ve.put("content",new JSONObject().toString());

                    vertexs.add(whe_rel_guarantee_ve);

                    JSONObject whe_rel_guarantee_ed = new JSONObject();
                    whe_rel_guarantee_ed.put("root",root);
                    whe_rel_guarantee_ed.put("from",identity_danbao);
                    whe_rel_guarantee_ed.put("to",whe_rel_guarantee_id);
                    whe_rel_guarantee_ed.put("name","是否为关联方担保");

                    edges.add(whe_rel_guarantee_ed);
                }




//                danbaos.put(danbao_name,identity_danbao);
            }



            JSONObject one_edges = new JSONObject();
            one_edges.put("root",root);
            one_edges.put("from",identity_danbao);
            one_edges.put("to",com_identity);
            one_edges.put("name","担保方");

            edges.add(one_edges);


            JSONObject two_edges = new JSONObject();
            two_edges.put("root",root);
            two_edges.put("from",identity_danbao);
            two_edges.put("to",identity);
            two_edges.put("name","被担保方");

            edges.add(two_edges);

        }
    }
}
