package com.qdcz.StuctData;

import com.qdcz.common.CommonTool;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

/**
 * Created by star on 17-7-28.
 */
public class StructNewPaper {
    public static void main(String[] args) throws Exception{
        newStruct();
    }


    public static void newStruct() throws Exception{
        Scanner sc =  new Scanner(new File("/media/star/Doc/工作文档/微信-论文项目/csis2.csv"));
        List<String> nodes = new ArrayList<>();
        List<String> edges = new ArrayList<>();
        String com_id = UUID.randomUUID().toString();





        JSONObject content_com = new JSONObject();
        content_com.put("area","");
        content_com.put("zipcode","");

        JSONObject node_com = new JSONObject();
        node_com.put("identity",com_id);
        node_com.put("root","社会科学知识库");
        node_com.put("name","美国战略国际研究中心");
        node_com.put("type","com");
        node_com.put("content",content_com.toString());


        nodes.add(node_com.toString());

        while(sc.hasNext()){



            String line = sc.nextLine();
            String company = line.split(",")[0];
            String person = line.split(",")[1];

            String pweson_id = UUID.randomUUID().toString();
            JSONObject node_person = new JSONObject();
            node_person.put("identity",pweson_id);
            node_person.put("root","社会科学知识库");
            node_person.put("name",person);
            node_person.put("type","author");
            node_person.put("content",new JSONObject().toString());


//            JSONObject content_com = new JSONObject();
//            content_com.put("area","");
//            content_com.put("zipcode","");
//
//            JSONObject node_com = new JSONObject();
//            node_com.put("identity",com_id);
//            node_com.put("root","社会科学知识库");
//            node_com.put("name",company.trim());
//            node_com.put("type","com");
//            node_com.put("content",content_com);



            JSONObject content_one_edge =  new JSONObject();
            content_one_edge.put("order","1");

            JSONObject one_edges = new JSONObject();
            one_edges.put("root","社会科学知识库");
            one_edges.put("from",pweson_id);
            one_edges.put("to",com_id);
            one_edges.put("name","属于");
            one_edges.put("content",content_one_edge.toString());



            nodes.add(node_person.toString());

            edges.add(one_edges.toString());
        }
        sc.close();








        StringBuffer sb = new StringBuffer();
        for (String line:nodes){
            sb.append(line+"\n");
        }
        CommonTool.printFile(sb.toString().getBytes(),"/media/star/Doc/工作文档/微信-论文项目/vertex3.txt");
        sb.delete(0,sb.length());

        for (String line:edges){
            sb.append(line+"\n");
        }
        CommonTool.printFile(sb.toString().getBytes(),"/media/star/Doc/工作文档/微信-论文项目/edges3.txt");
        sb.delete(0,sb.length());
    }
}
