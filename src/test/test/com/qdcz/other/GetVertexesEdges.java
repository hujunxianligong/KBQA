package com.qdcz.other;

import com.qdcz.common.CommonTool;
import org.json.JSONObject;

import java.io.*;

/**
 * Created by hadoop on 17-7-4.
 */
public class GetVertexesEdges {
    public static String INPUTPATH="/home/hadoop/wnd/usr/leagal/一行三会数据/定义处理后的文本/test/";
    public static String OUTPUTPATH="/home/hadoop/wnd/usr/leagal/一行三会数据/定义处理后的文本/test/";
    public static String OUTFILEPATH= "/home/hadoop/wnd/usr/leagal/一行三会数据/建新给的概念/";

    public static void main(String[] args) {
        GetVertexesEdges test=new GetVertexesEdges();
        test.putAll(INPUTPATH);
//        test.getdefine2("/home/hadoop/wnd/usr/leagal/一行三会数据/建新给的概念");

//
//        test.putAll("/home/hadoop/wnd/usr/leagal/实体关系修改");
//        test.getdefine("/home/hadoop/wnd/usr/leagal/一行三会数据/log");
//        try {
//            test.get("/home/hadoop/wnd/usr/leagal/实体关系提取-06.15/个人贷款管理.txt");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
    private void getdefine(String filePath){//一行三会文本
        String outpath=OUTPUTPATH;
        File dirInput = new File(filePath);
        File[] files = dirInput.listFiles();
        for (File file: files) {
//            System.out.println(file);

            if(file.toString().endsWith("doc"))
                System.out.println();
            FileReader re = null;
            try {
                re = new FileReader(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            boolean flag=false;//判断是否没有一条定义
            String[] splits=file.toString().split("/");
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
                            if(strs.length==2&&(strs[0].endsWith("还应当")||strs[0].endsWith("以上条件")||strs[0].endsWith("本身就")||strs[0].endsWith("往往")||strs[0].endsWith("而")||strs[0].endsWith("假如")||strs[0].endsWith("实事求")||strs[0].endsWith("尤其")||strs[0].endsWith("需要注意的")||strs[0].endsWith("但")||strs[0].endsWith("还")||(strs[1].length()>1&&strs[1].startsWith("否")))){

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
//                                  System.out.println(str);
                            }

                            else {

                                str=str.replaceAll("[ |　]", " ").trim();
                                str=str.replace(" ","").replaceAll("第[1234567890１２３４５６７８９０零一二三四五六七八九十百]{1,5}条","").replaceAll("^（{0,1}\\({0,1}[一二三四五六七八九十壹贰叁肆伍陆柒捌玖拾0-9]{1,2}\\){0,1}）{0,1}[\\.、]{0,1}","").trim();
                                if(str.split("是")[0].length()>40){

                                }else{
                                    String name=str.split("是")[0].trim();
                                    if(name.contains("，")){
                                        String[] split = name.split("，");

                                        if(split.length>1){//有内容
                                            continue;
//                                            System.out.println(line);
//                                            System.out.println(name +"\t \t \t \t "+str.split("是")[1]);
                                        }else{//OK

                                            name = split[0].replace("　", "").trim();
                                        }
                                    }
                              //      System.out.println(str);
                                    String  replace =name;
                                    String[] splitsOne = new String[]{"所称的", "所指的", "所指", "所称"};
                                    for (String eachSplitOne : splitsOne) {
                                        String[] split = replace.split(eachSplitOne);
                                        if (split.length > 1) {
                                            replace = split[1];
                                        }
                                    }

                                    String[] replacesOldWord = new String[]{"的目", "另一目"};
                                    String[] replacesNewWord = new String[]{"的目的", "另一个目的"};
                                    for (int i = 0; i < replacesOldWord.length; i++) {
                                        if (replace.endsWith(replacesOldWord[i])) {
                                            replace = replace.replace(replacesOldWord[i], replacesNewWord[i]);
                                        }
                                    }

                                    String[] replacesWord = new String[]{"本实施细则中规定的","本表填写的","本办法","所谓","本解释中","本规定有关","在本文中","本意见中的","此处", "前款", "“", "”", "上述计算公式中提到的"
                                    , "&nbsp;"};
                                    for (String eachReplaceWord : replacesWord) {
                                        replace = replace.replaceAll(eachReplaceWord, "");
                                    }

                                    String[] startsDel = new String[]{ "•", "．", "、", "该", "除非", "上述", "其中", "不论",
                                            "这些都", "这些", "也就", "无论", "与上述",  "尤其", "本条", "(a)", "\\d+\u0007",
                                            "(b)", "(e)", "(j)", "(c)", "(f)", "附件\\d+", "附件", "这点", "一项", "本办法中",
                                            "即使", "对于", "-", "下面"};
                                    for (String eachStartDel : startsDel) {
                                        if (replace.startsWith(eachStartDel) ) {
                                            replace = replace.substring(eachStartDel.length(), replace.length());
                                        }
                                    }

                                    String[] endsDel = new String[]{ "的", "既", "应", "应当", "主要", "通常", "一般", "应该",
                                            "的含义", "的目的", "既可以", "可以", "不仅", "必须", "都", ",", ".", "特别", "也", "或许",
                                            "只", "或", "附件\\d+", "强调", "的取得", "□3", "本指引", "重要", "在理论上"};
                                    for (String eachEndDel : endsDel) {
                                        if (replace.endsWith(eachEndDel)) {
                                            replace = replace.substring(0, replace.length() - eachEndDel.length());
                                        }
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

                                        replace=replace.replaceAll("[ |　]", " ").replaceAll("^[1234567890]","").trim();

                                        if(str.split("是").length > 1 && replace.length() > 1){
                                            flag=true;
                                            String result=replace+ "\t";
                                            for(int i=1;i<strs.length;i++){
                                                result+=strs[i];
                                            }
                                            result+="\t"+"概念\n";
                                           // System.out.println(result);
                                          //  CommonTool.printFile(result,outpath+filename,true);
                                        }
                                    }
                                    if(replace2!=null){
                                        replace2=replace2.replaceAll("[ |　]", " ").trim();
                                        if (replace2.length() > 1) {
                                            flag=true;
                                            String result=replace+ "\t";
                                            for(int i=1;i<strs.length;i++){
                                                result+=strs[i];
                                            }
                                            result+="\t"+"概念\n";
                                          //  System.out.println(result);
                                         //   CommonTool.printFile(result,outpath+filename,true);
                                        }
                                    }
                                }

                            }

                        }
                        else if(str.contains("包括")){
                            if(str.contains("：")){//有内容

                            }else if(str.split("包括").length==2&&(str.contains("不包括"))){

                            }
                            else if(str.split("包括").length>2){//有内容
                                // System.out.println(str);
                            }
                            else{

                                String name =str.split("包括")[0].replace("　", "").trim();
                                name=name.replaceAll("第[1234567890１２３４５６７８９０零一二三四五六七八九十百]{1,5}条","").replaceAll("^（{0,1}\\({0,1}[一二三四五六七八九十壹贰叁肆伍陆柒捌玖拾0-9]{1,2}\\){0,1}）{0,1}[\\.、]{0,1}","").replace(" ","").trim();
                                name=name.replace("．","");
//                              System.out.println(name);
                                if(name.length()<2){

                                }
                                else if(name.length()>10){//有内容
//                                  System.out.println(name);
                                }
                                else{

                                    if(name.endsWith("至少要")){
                                        name=name.substring(0,name.length()-3);
                                    }else if(name.endsWith("主要")||name.endsWith("至少")||name.endsWith("应当")||name.endsWith("可以")||name.endsWith("应该")){
                                        name=name.substring(0,name.length()-2);
                                    }else if(name.endsWith("应")||name.endsWith("可")){
                                        name=name.substring(0,name.length()-1);
                                    }
                                    if(name.endsWith("，")||name.endsWith(",")){//有内容
                                        name=name.substring(0,name.length()-1);
                                    }
                                    else if(name.endsWith("（")||name.endsWith("(")){//有内容
                                        name=name.substring(0,name.length()-1);
                                    }
                                    if(name.startsWith("这")){

                                    } else if("主要".equals(name)||"内容".equals(name)||"至少".equals(name)){

                                    }else{
                                        //    System.out.println(name);
                                    }

                                }

                            }

                        }



                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
               if(!flag){
                   System.out.println(file.toString());
                }
        }
    }
    private void getdefine2(String inputPath){//小宇哥 建新给的文本
        File dirInput = new File(inputPath);
        File[] files = dirInput.listFiles();
        for (File file: files) {
            try {
                FileReader re = null;
                try {
                    re = new FileReader(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                String[] splits=file.toString().split("/");
                String filename =splits[splits.length-1];
                BufferedReader read = new BufferedReader(re );
                String str = null;
                StringBuffer sb = new StringBuffer();
                StringBuffer sb1 = new StringBuffer();
                while((str=read.readLine())!=null){
                      if(str.contains("：")){

                          String[] split = str.split("：");
                          String name=split[0].replace(" ","").replace(" ","").trim();
                          name=name.replaceAll("^（{0,1}\\({0,1}[一二三四五六七八九十壹贰叁肆伍陆柒捌玖拾0-9]{1,4}\\){0,1}）{0,1}[\\.、]{0,1}","").replace("\t","").replace(" ","").trim();
                          name=name.replaceAll("^（{0,1}\\({0,1}[一二三四五六七八九十壹贰叁肆伍陆柒捌玖拾0-9]{1,4}\\){0,1}）{0,1}[\\.、]{0,1}","");
                          String define="";
                          for(int i=1;i<split.length;i++){
                              define+=split[i];
                          }
                          String result=name+"\t"+define+"\t概念\n";
                          CommonTool.printFile(result,OUTPUTPATH+filename,true);
                       //   System.out.println(name+"\t"+define+"\t概念");
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
                    obj1.put("to",strs[1].replaceAll(" ",""));
                    obj1.put("from",strs[0].replaceAll(" ",""));
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
        write(sb.toString(),OUTFILEPATH+"vertex.txt");
        write(sb1.toString(),OUTFILEPATH+"edges.txt");
        sb=null;
        sb1=null;
    }
    public static void write(String text,String filePath) throws IOException {
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
