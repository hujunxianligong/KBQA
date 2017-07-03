package com.qdcz.tools;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.neo4j.ogm.json.JSONException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hadoop on 17-6-30.
 */
public class CommonTool {
    public static List<String> getfile(String filePath) {
        FileReader re = null;
        try {
            re = new FileReader(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader read = new BufferedReader(re );
        String str = null;
        List<String> result=new ArrayList<>();
        try {
            while(null != (str = read.readLine())){
                result.add(str);


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            read.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    public static String query(String content, String url) throws JSONException, ClientProtocolException, IOException {
//        long qTest_st = System.currentTimeMillis();
        CloseableHttpClient httpclient = HttpClients.createDefault();
//        long qTest_end = System.currentTimeMillis();
        StringBuffer sb = new StringBuffer();
//        FileWriter fileWriter=new FileWriter(new File("/home/hadoop/wnd/usr/cmb/招行程序运行结果.txt"),true);
        try {
            HttpPost httpPost = new HttpPost(url);
            HttpEntity entity = new ByteArrayEntity(content.getBytes());
            httpPost.setEntity(entity);
//            httpPost.setHeader("type","0");
            CloseableHttpResponse response = httpclient.execute(httpPost);
            try {
//                System.out.println("提交返回的状态:"+response.getStatusLine());
                HttpEntity entity2 = response.getEntity();
                BufferedReader reader = new BufferedReader(new InputStreamReader(entity2.getContent()));
                String line = null;
                while ((line = reader.readLine()) != null) {
//                    System.out.println(line);
//                    fileWriter.write(line+"\n");
                    sb.append(line+"\n");
                }
                EntityUtils.consume(entity2);
            } finally {
//                fileWriter.flush();
//                fileWriter.close();
                response.close();
            }
        } finally {
            httpclient.close();
        }
//        System.out.print("qTest:");
//        System.out.println(qTest_end - qTest_st);
        return sb.toString();
    }
}
