package com.qdcz;

import org.neo4j.ogm.json.JSONObject;

import java.io.*;

/**
 * Created by hadoop on 17-7-4.
 */
public class GetVertexesEdges {
    public static void main(String[] args) {
        GetVertexesEdges test=new GetVertexesEdges();
//        test.putAll("/home/hadoop/wnd/usr/leagal/实体关系修改");
        test.getdefine("/home/hadoop/wnd/usr/leagal/银监会法规释义_人工_机器2 - 150份");
//        try {
//            test.get("/home/hadoop/wnd/usr/leagal/实体关系提取-06.15/个人贷款管理.txt");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
    private void getdefine(String filePath){
        File dirInput = new File(filePath);
        File[] files = dirInput.listFiles();
        for (File file: files) {
//            System.out.println(file);
            FileReader re = null;
            try {
                re = new FileReader(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            String[] splits=filePath.split("/");
            String filename =splits[splits.length-1];
            BufferedReader read = new BufferedReader(re );
            String line = null;
            StringBuffer sb = new StringBuffer();
            StringBuffer sb1 = new StringBuffer();
            try {
                while ((line = read.readLine()) != null) {
                    String[] splitju = line.split("[。；]");
                    for(String str:splitju){
                        if(str.contains("是")){
                            String[] strs = str.split("是");
                            if((strs[0].endsWith("还应当")||strs[0].endsWith("而")||strs[0].endsWith("实事求")||strs[0].endsWith("尤其")||strs[0].endsWith("需要注意的")||strs[0].endsWith("但")||strs[1].startsWith("否"))&&strs.length==2){

                            }else if(strs.length>2&&strs[1].startsWith("否")&&strs[2].startsWith("否")){

                            }
                            else if(strs.length>2&&strs[0].endsWith("尤其")&&strs[1].endsWith("尤其")){

                            }
                            else if(strs.length>5){

                            }
                            else if(str.contains("特别是")&&strs.length==2){

                            }else if(str.contains("？")){

                            }else  if(strs[0].length()<=1){

                            }else if(strs[0].contains("如果")){
                            }

                            else if(str.contains("不是")){

                            }
                            else if(strs[0].length()<4&&strs[0].contains("（")&&strs[0].contains("）")){

                            }
                            else if(str.contains("：")){//有内容，需要高端方法搞定
                                //  System.out.println(str);
                            }

                            else {
                                str=str.replaceAll("第[1234567890零一二三四五六七八九十百]{1,5}条","").replaceAll("^（{0,1}\\({0,1}[一二三四五六七八九十壹贰叁肆伍陆柒捌玖拾0-9]{1,2}\\){0,1}）{0,1}[\\.、]{0,1}","").trim();
                                if(str.split("是")[0].length()>40){

                                }else{
                                    String name=str.split("是")[0].trim();
                                    if(name.contains("，")){
                                        String[] split = name.split("，");
                                        if(split.length>1){//有内容
//                                            System.out.println(name +"\t \t \t \t "+str.split("是")[1]);
                                        }else{//OK
                                            String replace = split[0].trim().replace("　", "");
                                             if(replace.contains("所称的")){
                                                replace = replace.split("所称的")[1];
                                            }else if(replace.contains("所指的")){
                                                replace = replace.split("所指的")[1];
                                            }
                                            else if(replace.contains("所指")){
                                                replace = replace.split("所指")[1];
                                            }else if(replace.contains("所称")){
                                                replace = replace.split("所称")[1];
                                            }
                                            replace= replace.replaceAll("[“”．]","").replace("前款","");
                                            if(replace.endsWith("的")&&!replace.endsWith("的目的")){
                                                replace=replace.replace("的","");
                                            }
                                            else if(replace.endsWith("的")||replace.endsWith("既")||replace.endsWith("应")){
                                                replace=replace.substring(0,replace.length()-1);
                                            }
                                            else if(replace.endsWith("主要")){
                                                replace=replace.substring(0,replace.length()-2);
                                            }
                                            else if(replace.endsWith("的含义")||replace.endsWith("的目的")){
                                                replace=replace.substring(0,replace.length()-3);
                                            }
                                            String replace2=null;
                                            if(replace.contains("也称为")){
                                                String[] 也称为s = replace.split("也称为");
                                                 replace2 = 也称为s[0];
                                                replace = 也称为s[1];

                                            }else if(replace.contains("（以下简称")){
                                                String[] split1 = replace.split("（以下简称");
                                                replace2 = split1[0];
                                                replace= split1[1].substring(0, split1[1].length() - 1);

                                            }

                                            if(!"".equals(replace)){
                                                System.out.println(replace+ "\t \t \t \t "+str.split("是")[1]);
                                            }
                                            if(replace2!=null){
                                                System.out.println(replace2+ "\t \t \t \t "+str.split("是")[1]);
                                            }
                                        }
                                    }
                                    else{
                                        String replace = name.trim().replace("　", "");
                                        if(replace.contains("所称的")){
                                            replace = replace.split("所称的")[1];
                                        }else if(replace.contains("所指的")){
                                            replace = replace.split("所指的")[1];
                                        }
                                        else if(replace.contains("所指")){
                                            replace = replace.split("所指")[1];
                                        }else if(replace.contains("所称")){
                                            replace = replace.split("所称")[1];
                                        }
                                        replace= replace.replaceAll("[“”．]","").replace("前款","");
                                        if(replace.endsWith("的")&&!replace.endsWith("的目的")){
                                            replace=replace.replace("的","");
                                        }
                                        else if(replace.endsWith("的")||replace.endsWith("既")||replace.endsWith("应")){
                                            replace=replace.substring(0,replace.length()-1);
                                        }
                                        else if(replace.endsWith("主要")){
                                            replace=replace.substring(0,replace.length()-2);
                                        }
                                        else if(replace.endsWith("的含义")||replace.endsWith("的目的")){
                                            replace=replace.substring(0,replace.length()-3);
                                        }
                                        String replace2=null;
                                        if(replace.contains("也称为")){
                                            String[] 也称为s = replace.split("也称为");
                                            replace2 = 也称为s[0];
                                            replace = 也称为s[1];

                                        }else if(replace.contains("（以下简称")){
                                            String[] split1 = replace.split("（以下简称");
                                            replace2 = split1[0];
                                            replace= split1[1].substring(0, split1[1].length() - 1);

                                        }

                                        if(!"".equals(replace)){
                                            System.out.println(replace+ "\t \t \t \t "+str.split("是")[1]);
                                        }
                                        if(replace2!=null&&!"".equals(replace2)){
                                            System.out.println(replace2+ "\t \t \t \t "+str.split("是")[1]);
                                        }

                                    }

                                }

                            }

                        }
//                        if(str.contains("指")){
//
//                        }



                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
                    obj1.put("from",strs[1].replaceAll(" ",""));
                    obj1.put("to",strs[0].replaceAll(" ",""));
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
