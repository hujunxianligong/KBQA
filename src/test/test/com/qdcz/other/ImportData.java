package com.qdcz.other;

import com.qdcz.common.CommonTool;
import com.qdcz.other.StuctData.StructListedCom;

import java.io.IOException;

/**
 * Created by star on 17-8-14.
 */
public class ImportData {
    //bluckaddedges   bluckaddvertex
    private static String host_port = "http://localhost:14000/bluckaddedges?";
    public static void main(String[] args) throws Exception {

        String dir = "/mnt/vol_0/neo4j-community-3.2.1/import/";
        StructListedCom instance = new StructListedCom();
        instance.inputFile="2014下半年";
        String vertexsPath =dir;
        String graph = "down_2014";//licom

        if(host_port.contains("bluckaddvertex")){
            instance.doIt();
            importXZ(graph,vertexsPath,"vertex.csv");
        }
        if(host_port.contains("bluckaddedges")){
            importXZ(graph,vertexsPath,"edges.csv");
            importXZ(graph+"_danbao",vertexsPath,"edges2.csv");
        }
    }


    public static void importXZ(String graph,String vertexsPath,
                                  String edgesfile) throws IOException {

        String url = host_port+"label="+graph+"_label&vertexsPath="+java.net.URLEncoder.encode(vertexsPath,"utf-8")+"&relationship="+graph+"_relationship&edgesfile="+java.net.URLEncoder.encode(edgesfile,"utf-8");

        CommonTool.query("",url);
    }
}
