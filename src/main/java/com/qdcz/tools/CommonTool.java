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
import java.nio.ByteBuffer;
import java.util.*;

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


    public static String readFileOneTime(String inpath) {

        File file = new File(inpath);
        Long file_length = new Long(file.length());
        byte[] file_buffer = new byte[file_length.intValue()];
        String content = "";

        try {
            FileInputStream in = new FileInputStream(file);
            in.read(file_buffer);
            in.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            content = new String(file_buffer, "utf-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return content;
    }

    public static void printFile(byte[] image,  String outPath) {
        FileOutputStream fout = null;
        DataOutputStream dout = null;
        try {
            //			System.out.println(filename);
            fout = new FileOutputStream(outPath);
            dout = new DataOutputStream(fout);
            dout.write(image);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fout != null)
                    fout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (dout != null)
                    dout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void printFile(String s, String outPath, Boolean append) {

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(outPath, append);
            fileOutputStream.write(s.getBytes());
//            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
//            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
//            bufferedWriter.write(s);
//            bufferedWriter.close();
//            outputStreamWriter.close();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static  String filterOffUtf8Mb4(byte[] bytes) throws UnsupportedEncodingException {
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        int i = 0;
        while (i < bytes.length) {
            short b = bytes[i];
            if (b > 0) {
                buffer.put(bytes[i++ ]);
                continue;
            }
            b += 256;
            if ((b ^ 0xC0) >> 4 == 0) {
                buffer.put(bytes, i, 2);
                i += 2;
            }
            else if ((b ^ 0xE0) >> 4 == 0) {
                buffer.put(bytes, i, 3);
                i += 3;
            }
            else if ((b ^ 0xF0) >> 4 == 0) {
                i += 4;
            } else { //解決遇到特殊字符時死循環的問題
                buffer.put(bytes[i++ ]);
                continue;
            }
        }
        buffer.flip();
        String result = new String(buffer.array(),0, buffer.limit(), "utf-8");
        return result;
    }
    public   static   void  removeDuplicateWithOrder(List list)  {
        Set set  =   new  HashSet();
        List newList  =   new  ArrayList();
        for  (Iterator iter  =  list.iterator(); iter.hasNext();)  {
            Object element  =  iter.next();
            if  (set.add(element.toString()))
                newList.add(element);
        }
        list.clear();
        list.addAll(newList);
  //      System.out.println( " remove duplicate "   +  list);
    }
}
