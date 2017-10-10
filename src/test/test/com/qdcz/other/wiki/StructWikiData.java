package com.qdcz.other.wiki;

import com.qdcz.common.CommonTool;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

/**
 * Created by star on 17-10-9.
 */
public class StructWikiData {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(new File("/media/star/Doc/工作文档/wiki图项目/filter4.json"));


        String vertex_csv = "/media/star/Doc/工作文档/wiki图项目/vertex.csv";
        String edges_txt = "/media/star/Doc/工作文档/wiki图项目/edges.txt";

        if(new File(vertex_csv).exists()){
            new File(vertex_csv).delete();
        }
        if(new File(edges_txt).exists()){
            new File(edges_txt).delete();
        }

        String root = "wiki数据";

        Writer vertexWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(vertex_csv), true), "utf-8"));
        vertexWriter.write("root,name,type,content,identity\n");

        Writer edgesWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(edges_txt), true), "utf-8"));


        Map<String,String> map = new HashMap<>();
        int m = 0;
        while(sc.hasNext()){
            String line = sc.nextLine();
            JSONObject obj = null;
            try {
                obj = new JSONObject(line);
            }catch (Exception e){
                continue;
            }
            if(!obj.has("label")){
                continue;
            }



            String label = obj.getString("label").replace(",","，").replace("\"","").trim();
            if(label.isEmpty()){
                continue;
            }
//            if(label.replaceAll("(，| |'|:|\\(|\\))","").matches("\\w+") || label.matches(".*\\d+.*")){
//                System.out.println("过滤一条");
//                continue;
//            }


            if(label.matches(".*\\w+.*") || label.matches(".*\\d+.*")){
//                System.out.println("过滤一条");
                continue;
            }
            System.out.println(m++);

            String identity = "";
            if(map.containsKey(label)){
                identity = map.get(label);
            }else{
                identity = UUID.randomUUID().toString();
                map.put(label,identity);
            }


            JSONObject node_label = new JSONObject();
            node_label.put("identity",identity);
            node_label.put("root","wiki数据");
            node_label.put("name",label);
            node_label.put("type","com");
            node_label.put("content",new JSONObject().toString());

            vertexWriter.write(root+","+label+","+"com"+","+new JSONObject().toString()+","+identity+"\n");




            if(obj.has("id")){
                String id = obj.getString("id").replace(",","，").replace("\"","");
                if(id.length() > 2) {
                    JSONObject node_id = new JSONObject();
                    String id_identity = UUID.randomUUID().toString();
                    node_id.put("identity", id_identity);
                    node_id.put("root", "wiki数据");
                    node_id.put("name", id);
                    node_id.put("type", "id");
                    node_id.put("content", new JSONObject().toString());

                    vertexWriter.write(root + "," + id + "," + "id" + "," + new JSONObject().toString() + "," + id_identity + "\n");

                    JSONObject edges = new JSONObject();
                    edges.put("root", root);
                    edges.put("from", identity);
                    edges.put("weight", 1);
                    edges.put("to", id_identity);
                    edges.put("identity", UUID.randomUUID().toString());
                    edges.put("name", "id");


                    edgesWriter.write(edges.toString() + "\n");
                }
            }


            if(obj.has("desc")){
                String desc = obj.getString("desc").replace(",","，").replace("\"","");
                if(desc.length()>2) {
                    JSONObject node_desc = new JSONObject();
                    String desc_identity = UUID.randomUUID().toString();
                    node_desc.put("identity", desc_identity);
                    node_desc.put("root", "wiki数据");
                    node_desc.put("name", desc);
                    node_desc.put("type", "desc");
                    node_desc.put("content", new JSONObject().toString());


                    vertexWriter.write(root + "," + desc + "," + "desc" + "," + new JSONObject().toString() + "," + desc_identity + "\n");


                    JSONObject edges = new JSONObject();
                    edges.put("root", root);
                    edges.put("from", identity);
                    edges.put("weight", 1);
                    edges.put("to", desc_identity);
                    edges.put("identity", UUID.randomUUID().toString());
                    edges.put("name", "desc");


                    edgesWriter.write(edges.toString() + "\n");

                }

            }

            if(obj.has("aliases")){
                JSONArray arr = obj.getJSONArray("aliases");
                for (int i = 0;i<arr.length();i++){
                    JSONObject one_aliases = arr.getJSONObject(i);
                    String value = one_aliases.getString("value").replace(",","，").replace("\"","");
                    if(value.length()>2) {

                        JSONObject node_value = new JSONObject();
                        String value_identity = UUID.randomUUID().toString();
                        node_value.put("identity", value_identity);
                        node_value.put("root", "wiki数据");
                        node_value.put("name", value);
                        node_value.put("type", "aliases");
                        node_value.put("content", new JSONObject().toString());


                        vertexWriter.write(root + "," + value + "," + "aliases" + "," + new JSONObject().toString() + "," + value_identity + "\n");


                        JSONObject edges = new JSONObject();
                        edges.put("root", root);
                        edges.put("from", identity);
                        edges.put("weight", 1);
                        edges.put("to", value_identity);
                        edges.put("identity", UUID.randomUUID().toString());
                        edges.put("name", "aliases");


                        edgesWriter.write(edges.toString() + "\n");
                    }

                }

            }


        }
        sc.close();
        edgesWriter.close();
        vertexWriter.close();
    }
}
