package com.qdcz.service.high;

import com.qdcz.config.MongoConfigure;
import com.qdcz.service.bottom.RefereeInstrService;
import com.qdcz.service.mongo.BaseMongoDAL;
import com.qdcz.service.mongo.MyMongo;
import com.qdcz.tools.BuildReresult;
import com.qdcz.tools.CommonTool;
import com.qdcz.neo4jkernel.LegacyIndexService;
import com.qdcz.neo4jkernel.LoopDataService;
import com.qdcz.sdn.entity.instruments.*;
import org.apache.http.client.ClientProtocolException;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.ogm.json.JSONArray;
import org.neo4j.ogm.json.JSONException;
import org.neo4j.ogm.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

/**
 * Created by hadoop on 17-6-30.
 */
@Service
public class InstrDemandService {
    @Autowired
    private RefereeInstrService refereeInstrService;
    @Autowired
    private LegacyIndexService legacyIndexService;
    @Autowired
    private LoopDataService loopDataService;
    @Transactional
    public void addEgde(){
//        List<String> scenesJson = getfile("/home/hadoop/wnd/usr/leagal/建新/场景属性json");
//        for(String str:scenesJson){
//            try {
//                JSONArray array=new JSONArray(str);
//                for(int i=0;i<array.length();i++){
//                    JSONObject obj=array.getJSONObject(i);
//                    LawScenes lawScenes = new LawScenes(obj.getString("name"), obj.getString("identity"), obj.getString("type"), obj.getString("contract"), obj.getString("showKeyWord"), obj.getString("resultKeyWord"));
//                    refereeInstrService.addLawScenes(lawScenes);
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        List<String> questionList=getfile("/home/hadoop/wnd/usr/leagal/建新/问题");
//        for(String str:questionList){
//            String[] split = str.split("\t");
//            LawQuestion lawQuestion = new LawQuestion(split[0], split[1], split[2]);
//            refereeInstrService.addLawQuestion(lawQuestion);
//
//        }
//        List<String> regulationList=getfile("/home/hadoop/wnd/usr/leagal/建新/法令");
//        for(String str:regulationList){
//            String[] split = str.replace("《", "").split("》");
//            Regulations regulations = new Regulations(split[0], split[1]);
//            refereeInstrService.addRegulations(regulations);
//
//        }
//        List<String> caseReasonList=getfile("/home/hadoop/wnd/usr/leagal/建新/案由");
//        for(String str:caseReasonList){
//            CaseReason caseReason = new CaseReason(str);
//            refereeInstrService.addCaseReason(caseReason);
//        }
//        List<String> caseExampleList=getfile("/home/hadoop/wnd/usr/leagal/建新/案例");
//        for(String str:caseExampleList){
//            CaseExample caseExample = new CaseExample(str);
//            refereeInstrService.addCaseExample(caseExample);
//        }
        List<String> edgeList= CommonTool.getfile("/home/hadoop/wnd/usr/leagal/建新/点边关系");
        for(String str:edgeList){
            if(str.contains("表现")){
                String[] split = str.split("\t");
                CaseReason reasByName = refereeInstrService.getReasByName(split[0].toString());
                LawScenes scenByName = refereeInstrService.getScenByName(split[1]);
                Show show = new Show(scenByName,reasByName,split[2]);
                refereeInstrService.addShow(show);
            }else
            if(str.contains("提取")){
                String[] split = str.split("\t");
                LawScenes scenByName = refereeInstrService.getScenByName(split[0]);
                LawQuestion quesByName = refereeInstrService.getQuesByName(split[1]);
                Extract extract=new Extract(scenByName,quesByName,split[2]);
                refereeInstrService.addExtract(extract);
            }else
            if(str.contains("举例")){
                String[] split = str.split("\t");
                LawScenes scenByName = refereeInstrService.getScenByName(split[0]);
                CaseExample expByName = refereeInstrService.getExpByName(split[1]);
                ForExample forExample=new ForExample(scenByName,expByName,split[2]);
                refereeInstrService.addForExample(forExample);
            }else
            if(str.contains("涉及")&&!str.contains("主要涉及")){
                String[] split = str.split("\t");
                CaseExample expByName = refereeInstrService.getExpByName(split[0]);
                String[] split1 = split[1].replace("《", "").split("》");
                String name = null;
                if(split1.length==2) {
                    name = split1[0] + "-" + split1[1];
                }else{
                    name = split1[0] + "-" +"";
                }
                if(name.contains("关于适用〈中华人")) {
                    System.out.println();
                }
                Regulations regByName = refereeInstrService.getRegByName(name);
                Involved involved=new Involved(expByName,regByName,split[2]);
                refereeInstrService.addInvloved(involved);
            }else
            if(str.contains("主要涉及")){
                String[] split = str.split("\t");
                LawScenes scenByName = refereeInstrService.getScenByName(split[0]);
                String[] split1 = split[1].replace("《", "").split("》");
                String name = null;
                if(split1.length==2) {
                    name = split1[0] + "-" + split1[1];
                }else{
                    name = split1[0] + "-" +"";
                }
                Regulations regByName = refereeInstrService.getRegByName(name);
                MainlyInvolved mainlyInvolved=new MainlyInvolved(scenByName,regByName,split[2]);
                refereeInstrService.addMainlyInvolved(mainlyInvolved);
            }
        }
    }
    @Transactional
    public void delVertex(JSONObject object) throws JSONException {
        String type=object.getString("type");
        JSONObject obj=null;
        if("lawScenes".equals(type)){
            obj = object.getJSONObject("lawScenes");
            LawScenes lawScenes =new LawScenes(obj);
            LawScenes scenByName = refereeInstrService.getScenByName(lawScenes.getName());
            List<String > propKeys=new ArrayList<>();
            propKeys.add("resultKeyWord");
            propKeys.add("showKeyWord");
            propKeys.add("problem");
            legacyIndexService.deleteFullTextIndex(scenByName.getId(),propKeys,"vertex");
            refereeInstrService.deleteLawScenes(scenByName);
        }else if("caseExample".equals(type)){
            obj=object.getJSONObject("caseExample");
            CaseExample caseExample=new CaseExample(obj);
            CaseExample expByName = refereeInstrService.getExpByName(caseExample.mongo_id);
            refereeInstrService.deleteCaseExample(expByName);
        }else if("lawQuestion".equals(type)){
            obj=object.getJSONObject("lawQuestion");
            LawQuestion lawQuestion=new LawQuestion(obj);
            LawQuestion quesByName = refereeInstrService.getQuesByName(lawQuestion.name);
            List<String > propKeys=new ArrayList<>();
            propKeys.add("resultKeyWord");
            propKeys.add("showKeyWord");
            propKeys.add("problem");
            legacyIndexService.deleteFullTextIndex(quesByName.getId(),propKeys,"vertex");
            refereeInstrService.deleteLawQuestion(quesByName);
        }else if("caseReason".equals(type)){
            obj=object.getJSONObject("caseReason");
            CaseReason caseReason=new CaseReason(obj);
            CaseReason reasByName = refereeInstrService.getReasByName(caseReason.name);
            refereeInstrService.deleteCaseReason(reasByName);
        }else if("regulations".equals(type)){
            obj=object.getJSONObject("regulations");
            Regulations regulations=new Regulations(obj);
            Regulations regByName = refereeInstrService.getRegByName(regulations.getName());
            refereeInstrService.deleteRegulations(regByName);
        }
        System.out.println("end");
    }
    @Transactional
    public void addVertex(JSONObject object) throws JSONException {//创建节点
        String type=object.getString("type");
        JSONObject obj=null;
        if("lawScenes".equals(type)){
            obj = object.getJSONObject("lawScenes");
            LawScenes lawScenes =new LawScenes(obj);
            long l = refereeInstrService.addLawScenes(lawScenes);
            System.out.println();
            List<String > propKeys=new ArrayList<>();
            propKeys.add("resultKeyWord");
            propKeys.add("showKeyWord");
            propKeys.add("problem");
            legacyIndexService.createFullTextIndex(l,propKeys,"vertex");
        }else if("caseExample".equals(type)){
            obj=object.getJSONObject("caseExample");
            CaseExample caseExample=new CaseExample(obj);
            refereeInstrService.addCaseExample(caseExample);
        }else if("lawQuestion".equals(type)){
            obj=object.getJSONObject("lawQuestion");
            LawQuestion lawQuestion=new LawQuestion(obj);
            long l = refereeInstrService.addLawQuestion(lawQuestion);
            List<String > propKeys=new ArrayList<>();
            propKeys.add("resultKeyWord");
            propKeys.add("showKeyWord");
            propKeys.add("problem");
            legacyIndexService.createFullTextIndex(l,propKeys,"vertex");
        }else if("caseReason".equals(type)){
            obj=object.getJSONObject("caseReason");
            CaseReason caseReason=new CaseReason(obj);
            refereeInstrService.addCaseReason(caseReason);
        }else if("regulations".equals(type)){
            obj=object.getJSONObject("regulations");
            Regulations regulations=new Regulations(obj);
            refereeInstrService.addRegulations(regulations);
        }
    }
    @Transactional
    public String queryF(String question){

        String[] fields={"problem"};

        List<Map<String, Object>> problemMaps = legacyIndexService.selectByFullTextIndex(fields, question, "vertex");
        for(Map<String, Object> map:problemMaps){
            System.out.println((Long) map.get("id")+"\t"+map.get("score"));
        }
        fields= new String[]{"resultKeyWord", "showKeyWord"};
        List<Map<String, Object>> keyWordmaps = legacyIndexService.selectByFullTextIndex(fields, question, "vertex");
        for(Map<String, Object> map:keyWordmaps){
            System.out.println((Long) map.get("id")+"\t"+map.get("score"));
        }
        problemMaps.addAll(keyWordmaps);
        List<Map<String, Object>> maps= problemMaps;
        MyComparetor mc = new MyComparetor();
        Collections.sort(maps,mc);
        Collections.reverse(maps);
        mc=null;

        //取最高相似度的场景进行遍历
        int num=1;
        if(num>maps.size())
            num=maps.size();
        JSONObject result =new JSONObject();
        BuildReresult buildReresult = new BuildReresult();
        for(int i=0;i<num;i++){
            Map<String, Object> map=maps.get(i);
            long id = Long.parseLong(map.get("id").toString());
            Traverser paths = loopDataService.associatedNodeSearch(id);
            JSONObject jsonObject = buildReresult.graphResult(paths);
            result=buildReresult.MergeResult(result,buildReresult.graphResult(paths));
        }
        result= buildReresult.cleanRestult(result);
        //遍历结果去mongo查文书
        BaseMongoDAL mongo = new MyMongo(MongoConfigure.dbOnline,"law_details");
        JSONArray newCaseExamples= new JSONArray();
        try {
            JSONArray nodes = result.getJSONArray("nodes");
            for(int i=0;i<nodes.length();i++){
                JSONObject jsonObject = nodes.getJSONObject(i);
                if(jsonObject.has("mongoId")){
                    String mongoId=jsonObject.getString("mongoId");
                    JSONObject oneDocument = mongo.getOneDocument(mongoId);
                    String title = oneDocument.getString("title");
                    jsonObject.put("title",title);
                    newCaseExamples.put(jsonObject);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }finally {

            if(mongo!=null){
                mongo.close();
            }
        }
        //去ＥＬＫ拿法令内容
        JSONArray newRegulations= new JSONArray();
        try {
            JSONArray nodes = result.getJSONArray("nodes");
            for(int i=0;i<nodes.length();i++){
                JSONObject jsonObject = nodes.getJSONObject(i);
                try{
                    if(jsonObject.has("code")){
                        String code=jsonObject.getString("code");
                        String provisions=jsonObject.getString("provisions");
                        if(provisions.length()==0){
                            jsonObject.put("title", "无条款并未查询");
                        }else {
                            String query = CommonTool.query(code + "-" + provisions, "http://h133:12000/api/shitcheck");
                            jsonObject.put("title", new JSONArray(query));
                        }
                        newRegulations.put(jsonObject);
                    }
                }catch (ClientProtocolException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(result);
        return  result.toString();
    }
    class MyComparetor implements Comparator
    {
        @Override
        public int compare(Object o1, Object o2)
        {
            Map sdto1= (Map )o1;
            Map sdto2= (Map )o2;
            return Float.compare(Float.parseFloat(sdto1.get("score").toString()),Float.parseFloat(sdto2.get("score").toString()));
        }
    }

}
