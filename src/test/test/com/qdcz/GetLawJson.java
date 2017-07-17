package com.qdcz;

import com.qdcz.tools.CommonTool;
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
//        test.getVertexEdge("/home/hadoop/wnd/usr/leagal/建新/点边关系");
        test.test();
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

    public  void test() throws Exception{
        String str = new String(CommonTool.readFileOneTime("/home/hadoop/wnd/usr/leagal/建新/银团贷款知识图谱.pos"));
        JSONObject obj = new JSONObject(str);
        JSONObject elements = obj.getJSONObject("diagram").getJSONObject("elements").getJSONObject("elements");
        Set<String> set = elements.toMap().keySet();
        JSONArray nodes = new JSONArray();
        JSONArray edges = new JSONArray();
        String root = "银团贷款业务";
        for (String key:set){
            JSONObject one = elements.getJSONObject(key);
            String name = one.getString("name");
//            System.out.println(one);
            if("linker".equals(name)){
                //边
                try {
                    String text = one.getString("text");
                    String from_text = elements.getJSONObject(one.getJSONObject("from").getString("id")).getJSONArray("textBlock").getJSONObject(0).getString("text");
                    String to_text = elements.getJSONObject(one.getJSONObject("to").getString("id")).getJSONArray("textBlock").getJSONObject(0).getString("text");
                    String[] from_splits = from_text.split("\n");
                    for(String from_split:from_splits){
                        if(from_split.length()<2){
                            continue;
                        }
                        String[] to_splits = to_text.split("\n");

                        for(String to_split:to_splits){
                            if(to_split.length()<2){
                                continue;
                            }
                            JSONObject tmp = new JSONObject();
                            tmp.put("root", root);
                            tmp.put("from", from_split);
                            tmp.put("to", to_split);
                            tmp.put("relation", text);
                            edges.put(tmp);
                        }

                    }


//                    System.out.println(text);
                }catch (Exception e){
                    System.out.println("error:"+one);
//                    System.out.println(elements.getJSONObject(one.getJSONObject("from").getString("id")).getJSONArray("textBlock").getJSONObject(0).getString("text"));
                }
            }else{
                //点
                String text = one.getJSONArray("textBlock").getJSONObject(0).getString("text");
                String[] splits = text.split("\n");
                for(String split:splits){
                    if(split.length()<2){
                        continue;
                    }
                    JSONObject tmp = new JSONObject();
                    tmp.put("identity","");
                    tmp.put("root",root);
                    tmp.put("name",split);
                    tmp.put("type","");
                    nodes.put(tmp);
                }

//                System.out.println(tmp);


//                System.out.println(one);
            }
        }


        for (int i=0;i<nodes.length();i++){
            System.out.println(nodes.getJSONObject(i));
        }

        System.out.println("\n\n\n");

        for (int i=0;i<edges.length();i++){
            System.out.println(edges.getJSONObject(i));
        }

    }

}
