package com.qdcz.other;

import com.qdcz.common.CommonTool;
import com.qdcz.other.StuctData.StructListedCom;

import java.io.IOException;

/**
 * Created by star on 17-8-14.
 */
public class ImportData {
    //bluckaddedges   bluckaddvertex
    private static String host_port = "http://localhost:14000/bluckaddvertex?";
    public static void main(String[] args) throws Exception {

        String dir = "/mnt/vol_0/neo4j-community-3.2.1/import/";
        StructListedCom instance = new StructListedCom();
        instance.inputFile="2016下半年";//2012下半年 2013上半年 2013下半年 2014上半年 2014下半年 2015下半年 2016下半年  2015上半年　2016上半年 2017上半年
        String re1="^\\d+";
        String debtRatioStr= CommonTool.get_one_match(instance.inputFile,re1);
        String graph =null;
        if(instance.inputFile.contains("上")){
             graph = "up_"+debtRatioStr;
        }else{
            graph = "down_"+debtRatioStr;
        }
        String vertexsPath =dir;
        if(graph!=null){
            if(host_port.contains("bluckaddvertex")){
//                instance.doIt();
                importXZ(graph,vertexsPath,"vertex.csv");
            }
            if(host_port.contains("bluckaddedges")){
                importXZ(graph,vertexsPath,"edges.csv");
                importXZ(graph+"_danbao",vertexsPath,"edges2.csv");
            }
        }

    }


    public static void importXZ(String graph,String vertexsPath,
                                  String edgesfile) throws IOException {

        String url = host_port+"label="+graph+"_label&vertexsPath="+java.net.URLEncoder.encode(vertexsPath,"utf-8")+"&relationship="+graph+"_relationship&edgesfile="+java.net.URLEncoder.encode(edgesfile,"utf-8");

        CommonTool.query("",url);
    }
}
