package com.qdcz.other;

import com.qdcz.common.CommonTool;

import java.io.IOException;

/**
 * Created by star on 17-8-14.
 */
public class ImportData {
    //bluckaddedges   bluckaddvertex
    private static String host_port = "http://localhost:14000/bluckaddvertex?";
    public static void main(String[] args) throws IOException {

        String dir = "/home/hadoop/下载/导入数据/";

        String vertexsPath =dir;
        String graph = "licom";//licom
        String edgesPath = dir+ "edges.txt";

        importXZ(graph,vertexsPath,edgesPath);
    }


    public static void importXZ(String graph,String vertexsPath,
                                  String edgesPath) throws IOException {

        String url = host_port+"label="+graph+"_label&vertexsPath="+java.net.URLEncoder.encode(vertexsPath,"utf-8")+"&relationship="+graph+"_relationship&edgesPath="+java.net.URLEncoder.encode(edgesPath,"utf-8");

        CommonTool.query("",url);
    }
}
