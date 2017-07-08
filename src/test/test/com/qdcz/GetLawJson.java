package com.qdcz;

import org.neo4j.ogm.json.JSONArray;
import org.neo4j.ogm.json.JSONException;
import org.neo4j.ogm.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by hadoop on 17-6-29.
 * 根据建新给的数据做场景的JSon化
 */
public class GetLawJson {
    public static void main(String[] args) throws Exception {
        GetLawJson test=new GetLawJson();
//        test.getLawScenesJson("/home/hadoop/wnd/usr/leagal/建新/场景属性");
        test.getVertexEdge("/home/hadoop/wnd/usr/leagal/建新/点边关系");
    }

    public  void getLawScenesJson(String filePath) throws Exception{
        FileReader re =  new FileReader(filePath);
        BufferedReader read = new BufferedReader(re );
        String str = null;
        while((str=read.readLine())!=null){
            JSONObject obj=new JSONObject();
            JSONObject oneScenes=new JSONObject();

            try {
//                System.out.println(str);
                String[] split = str.split("--");
                String nodeName=split[0];
                oneScenes.put("name",nodeName);
                String attributes=split[1];
                String[] split1 = attributes.split("；");
                if(split1.length>1){//场景
                    String identity=split1[0];
                    oneScenes.put("identity",identity.split("：")[1]);
                    String  type=split1[1];
                    oneScenes.put("type",type.split("：")[1]);

                    String contract =split1[2];
                    oneScenes.put("contract",contract.split("：")[1]);
                    String[] split3 = split1[3].split("：")[1].split("）（");
                    JSONArray jsonArray=new JSONArray();
                    for(int i=0;i<split3.length;i++){
                        jsonArray.put(split3[i].replace("（","").replace("）",""));
                    }
                    oneScenes.put("showKeyWord",jsonArray);
                    String[] split2 = split1[4].split("：")[1].split("）（");
                    JSONArray jsonArray1=new JSONArray();
                    for(int i=0;i<split2.length;i++){
                        jsonArray1.put(split2[i].replace("（","").replace("）",""));
                    }
                    oneScenes.put("resultKeyWord",jsonArray1);
                    obj.put("type","lawScenes");
                    obj.put("lawScenes",oneScenes);
                }else{//问题
                    String[] split2 = split1[0].split("回答：");
                    if(split2.length==2){

                        oneScenes.put("problem",   split2[0].replace("问题：","").trim());
                        oneScenes.put("answer",split2[1].trim());
                    }
                    obj.put("type","lawQuestion");
                    obj.put("lawQuestion",oneScenes);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println(obj);
        }
        read.close();
     //   System.out.println(result);
    }
    public void getVertexEdge(String filePath) throws IOException, JSONException {
        FileReader re =  new FileReader(filePath);
        BufferedReader read = new BufferedReader(re);
        String str = null;
        Set<String> caseReasonSet=new HashSet<>();
        Set<String> regulationSet=new HashSet<>();
        Set<String> caseExampleSet=new HashSet<>();
        Set<String> lawScenesSet=new HashSet<>();
        Set<String> lawQuestionSet=new HashSet<>();
        while((str=read.readLine())!=null){
            try {
                if(str.contains("表现")){
                    String[] split = str.split("\t");
                    caseReasonSet.add(split[0]);
                    lawScenesSet.add(split[1]);

                }else
                if(str.contains("提取")){
                    String[] split = str.split("\t");
                    lawScenesSet.add(split[0]);
                    lawQuestionSet.add(split[1]);
                }else
                if(str.contains("举例")){
                    String[] split = str.split("\t");
                    lawScenesSet.add(split[0]);
                    caseExampleSet.add(split[1]);
                }else
                if(str.contains("涉及")&&!str.contains("主要涉及")){
                    String[] split = str.split("\t");
                    caseExampleSet.add(split[0]);
                    regulationSet.add(split[1]);
                }else
                if(str.contains("主要涉及")){
                    String[] split = str.split("\t");
                    lawScenesSet.add(split[0]);
                    regulationSet.add(split[1]);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }
        read.close();
        for (String str1 : caseReasonSet) {
            JSONObject object = new JSONObject();
            object.put("type","caseReason");
            JSONObject object1= new JSONObject();
            object1.put("name",str1);
            object.put("caseReason",object1);
            System.out.println(object);
        }
        for (String str1 : regulationSet) {
            JSONObject object = new JSONObject();
            object.put("type","regulations");
            JSONObject object1= new JSONObject();
            String[] split = str1.replace("《", "").split("》");
            object1.put("code",split[0]);
            if(split.length==1)
                object1.put("provisions","");
                else
            object1.put("provisions",split[1]);
            object.put("regulations",object1);
            System.out.println(object);
        }
        for (String str1 : caseExampleSet) {
            JSONObject object = new JSONObject();
            object.put("type","caseExample");
            JSONObject object1= new JSONObject();
            object1.put("mongoId",str1);
            object.put("caseExample",object1);
            System.out.println(object);
        }
    }
}
