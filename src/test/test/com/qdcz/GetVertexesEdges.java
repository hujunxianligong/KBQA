package com.qdcz;

import org.neo4j.ogm.json.JSONObject;

import java.io.*;

/**
 * Created by hadoop on 17-7-4.
 */
public class GetVertexesEdges {
    public static void main(String[] args) {
        GetVertexesEdges test=new GetVertexesEdges();
        test.putAll("/home/hadoop/wnd/usr/leagal/实体关系修改");
//        try {
//            test.get("/home/hadoop/wnd/usr/leagal/实体关系提取-06.15/个人贷款管理.txt");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
    private void putAll(String filePath){
        File dirInput = new File(filePath);
        File[] files = dirInput.listFiles();
        for (File file: files) {
            System.out.println(file);

            if(file.toString().endsWith("/73"))
                System.out.println();

//			FileWriter fileWriter = new FileWriter(file);

            try {
                get(file.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void get(String filePath) throws IOException {
        FileReader re = null;
        try {
            re = new FileReader(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String[] splits=filePath.split("/");
        String filename =splits[splits.length-1];
        BufferedReader read = new BufferedReader(re );
        String str = null;
        StringBuffer sb = new StringBuffer();
        StringBuffer sb1 = new StringBuffer();
        try {
            while((str=read.readLine())!=null){
                try {

                    String[] strs=str.split("\t");
                    for(int i=0;i<strs.length-1;i++){
                        JSONObject obj = new JSONObject();

                        obj.put("type","");
                        obj.put("name",strs[i].replaceAll(" ",""));
                        obj.put("identity","");
                        obj.put("root",filename.replace(".txt",""));
                        sb.append(obj+"\n");
                        obj = null;

                    }

                    JSONObject obj1 = new JSONObject();

                    obj1.put("relation",strs[2].replaceAll(" ",""));
                    obj1.put("from",strs[0].replaceAll(" ",""));
                    obj1.put("to",strs[1].replaceAll(" ",""));
                    obj1.put("root",filename.replace(".txt",""));
                    sb1.append(obj1+"\n");
                    obj1 = null;

                } catch (Exception e) {
                    System.out.println("【N】:"+str);
//                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            read.close();
            re.close();
        }
        write(sb.toString(),"/home/hadoop/wnd/usr/leagal/logs/vertex.txt");
        write(sb1.toString(),"/home/hadoop/wnd/usr/leagal/logs/edges.txt");
        sb=null;
        sb1=null;
    }
    private void write(String text,String filePath) throws IOException {
        FileWriter fileWriter=new FileWriter(new File(filePath),true);///home/hadoop/wnd/usr/leagal/logs/log.txt
        try {
            fileWriter.write(text);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fileWriter.flush();
            fileWriter.close();
        }
    }
}
