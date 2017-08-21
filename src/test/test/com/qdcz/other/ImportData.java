package com.qdcz.other;

import com.qdcz.common.CommonTool;

import java.io.IOException;

/**
 * Created by star on 17-8-14.
 */
public class ImportData {
    //bluckaddedges   bluckaddvertex
    private static String host_port = "http://localhost:14001/bluckaddedges?";
    public static void main(String[] args) throws IOException {

        String dir = "/media/star/Doc/工作文档/上市公司担保关系分析/";

        String vertexsPath =dir;
        String graph = "danbao";//licom
        String edgesPath = dir+ "edges.txt";

        importXZ(graph,vertexsPath,edgesPath);
    }


    public static void importXZ(String graph,String vertexsPath,
                                  String edgesPath) throws IOException {

        String url = host_port+"label="+graph+"_label&vertexsPath="+java.net.URLEncoder.encode(vertexsPath,"utf-8")+"&relationship="+graph+"_relationship&edgesPath="+java.net.URLEncoder.encode(edgesPath,"utf-8");

        CommonTool.query("",url);
    }
}
