package com.qdcz.graph.neo4jcypher.service;

import com.qdcz.common.CommonTool;
import com.qdcz.conf.LoadConfigListener;
import com.qdcz.graph.neo4jcypher.connect.Neo4jClientFactory;
import com.qdcz.graph.neo4jcypher.dao.Neo4jCYDAO;
import jxl.Workbook;
import jxl.format.VerticalAlignment;
import jxl.read.biff.BiffException;
import jxl.write.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by hadoop on 17-9-1.
 */
public class AnalysisListedCoService {
    public static void main(String[] args) {

        LoadConfigListener loadConfigListener=new LoadConfigListener();
        loadConfigListener.setSource_dir("/dev/");
        loadConfigListener.contextInitialized(null);
        AnalysisListedCoService instance =new AnalysisListedCoService();
        String [] provinceStrs= new String[]{"HeiLongJiang", "JiLin", "LiaoNing", "HeBei", "HeNan", "ShanDong", "JiangSu"
                , "ShanXi", "Shan_Xi", "GanSu", "SiChuan", "QingHai", "HuNan", "HuBei"
                , "JiangXi", "AnHui", "ZheJiang", "FuJian", "GuangDong", "GuangXi", "GuiZhou"
                , "YunNan", "HaiNan", "BeiJing", "TianJin", "ShangHai", "ChongQing","XinJiang"
                // , "AoMen", "XiangGang", "TaiWan"
                , "XiZang", "NeiMengGu", "NingXia",
        };
        String [] quartStrs=new String[]{"up_2013","down_2013","up_2014","down_2014","down_2015","down_2016","up_2015","up_2016","up_2017"};//"down_2012","down_2014",,"down_2015","down_2016","up_2015","up_2016","up_2017"
//        for(String quarter:quartStrs){
//            instance.setPartition(quarter);
//        instance.setProvince(quarter+"_label",quarter+"_relationship",provinceStrs);
////            for(String province:provinceStrs){
////                instance.unionFindStream(province,quarter+"_danbao_relationship",quarter);
////                instance.distinctWeightDanbao(province,quarter+"_label",quarter+"_relationship",quarter);
////                instance.getDanbaoMoney(province,quarter+"_label",quarter+"_relationship",quarter);
////            }
//
//        }
    //    Map info = instance.setMoneyStatistics("HuNan");
        instance.setMoneyStatistics(provinceStrs);
        //   instance.distinctWeightDanbao("XinJiang","down_2014_label","down_2014_relationship","down_2014");
        //    instance.unionFindStream("ShangHai","down_2015_danbao_relationship",null);
        //     instance.setProvince("down_2015_label","down_2015_relationship",provinceStrs);
    }
    private Neo4jCYDAO neo4jCYDAO;

    private Driver driver;

    public AnalysisListedCoService(){
        driver =  Neo4jClientFactory.create();
        neo4jCYDAO = new Neo4jCYDAO(driver);
    }
    /*******************************************************/
    /************统计查询********/
    /**
     * 图数据建立联通图　强联通图的partition
     * @param quartStrs
     */
    public void setPartition(String quartStrs){
        String sql="CALL algo.unionFind('"+quartStrs+"_label', '"+quartStrs+"_danbao_relationship', {write:true, partitionProperty:\"partition_cc\", defaultValue:0.0, threshold:1.0}) " +
                "YIELD nodes, setCount, loadMillis, computeMillis, writeMillis ";
        neo4jCYDAO.execute(sql);
        sql="CALL algo.unionFind('"+quartStrs+"_label', '"+quartStrs+"_relationship', {write:true, partitionProperty:\"partition_cc_Info\", defaultValue:0.0, threshold:1.0}) " +
                "YIELD nodes, setCount, loadMillis, computeMillis, writeMillis";
        neo4jCYDAO.execute(sql);
        sql="CALL algo.scc('"+quartStrs+"_label','"+quartStrs+"_danbao_relationship', {write:true,partitionProperty:'partition_scc'}) " +
                "YIELD loadMillis, computeMillis, writeMillis, setCount, maxSetSize, minSetSize";
        neo4jCYDAO.execute(sql);
    }
    public void setProvince(String label,String relationship,String[] provinceStrs){
        for(String province :provinceStrs){
            String sql="MATCH (n:"+label+" )-[r:"+relationship+" {name:'地域'}]->(m) " +
                    "WHERE n.type = 'com' and m.name = '"+province+"' " +
                    "with n " +
                    "set n:"+province+" " +
                    "return n";
            neo4jCYDAO.execute(sql);
        }

    }

    private void setJson(Map< String,Map<String ,Map<String,JSONObject>>> Info){

        List<String> conTentCp = CommonTool.getfile("/home/hadoop/wnd/usr/RiskTransfer/ST公司名全称");
        for (Map.Entry< String,Map<String ,Map<String,JSONObject>>> entry : Info.entrySet()){
            Map<String, Map<String, JSONObject>> companyInfos = entry.getValue();
            for(Map.Entry< String ,Map<String,JSONObject>> entry_company : companyInfos.entrySet()){
                String companyName = entry_company.getKey();
                if(!conTentCp.contains(companyName)){
                    continue;
                }
                JSONObject cpinfo=new JSONObject();
                JSONArray infos=new JSONArray();
                Map<String, JSONObject> value = entry_company.getValue();
                for(Map.Entry< String,JSONObject> entry_guarantees : value.entrySet()){
                    String yearInfo = entry_guarantees.getKey();
                    JSONObject value1 = entry_guarantees.getValue();
                    value1.put("quarter",yearInfo);
                    infos.put(value1);
                }
                cpinfo.put("companyName",companyName);
                cpinfo.put("infos",infos);
                CommonTool.printFile(cpinfo.toString()+"\n","/home/hadoop/wnd/usr/RiskTransfer/InfoJsonYear",true);

            }

        }
    }
    /**
     * 统计结果集合写入xlsx列表
     * @param Info
     * @throws IOException
     * @throws BiffException
     * @throws WriteException
     */
    private   void testxlsx(Map< String,Map<String ,Map<String,JSONObject>>> Info) throws IOException, BiffException, WriteException {
        try {
            List<String> conTentCp = CommonTool.getfile("/home/hadoop/wnd/usr/RiskTransfer/ST公司名全称");
            // Excel获得文件
            Workbook wb = Workbook.getWorkbook( new File( "/home/hadoop/wnd/usr/xlsx/test.xls" ));
            // 打开一个文件的副本，并且指定数据写回到原文件
            WritableWorkbook book = Workbook.createWorkbook( new File( "/home/hadoop/wnd/usr/xlsx/test.xls" ),
                    wb);
            WritableFont bold = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD);//设置字体种类和黑体显示,字体为Arial,字号大小为10,采用黑体显示
            WritableCellFormat titleFormate = new WritableCellFormat(bold);//生成一个单元格样式控制对象
            titleFormate.setAlignment(jxl.format.Alignment.CENTRE);//单元格中的内容水平方向居中
            titleFormate.setVerticalAlignment(VerticalAlignment.CENTRE);//单元格的内容垂直方向居中
            // 添加一个工作表
            WritableSheet sheet = book.createSheet( "第一页",0 );
            String[] labels=new String[]{"地域","上市公司","指标","2012下半年","2013上半年","2013下半年", "2014上半年" ,"2014年下","2015年上","2015年下","2016年上","2016年下","2017年上"};
            for(int i=0;i<labels.length;i++){
                Label cell = new Label(i,0, labels[i], titleFormate);
                sheet.addCell( cell);
            }

            String[] propertyNames=  new  String[]{"资产负债率","担保额度占净资产的比重","净资产额","逾期担保数量","接受担保总额","对外担保数量","对外担保总额","接受担保总额","总资产额","逾期担保总额"};
            int setpropertyNum=propertyNames.length;
            int currentRow=1;
            for (Map.Entry< String,Map<String ,Map<String,JSONObject>>> entry : Info.entrySet()){
                String province = entry.getKey();
                Map<String, Map<String, JSONObject>> companyInfos = entry.getValue();
                int size = companyInfos.size();
                for(int i=0;i<size;i++){
                    sheet.mergeCells(1, i * setpropertyNum+currentRow, 1, (i + 1) * setpropertyNum+currentRow-1);
                    for(int j=0;j<setpropertyNum;j++){
                        Label cell3 = new Label(2,i * setpropertyNum+currentRow+j, propertyNames[j], titleFormate);
                        sheet.addCell( cell3);
                    }
                }
                int i=0;
                for(Map.Entry< String ,Map<String,JSONObject>> entry_company : companyInfos.entrySet()){
                    String companyName = entry_company.getKey();
                    if(!conTentCp.contains(companyName)){
                        continue;
                    }
                    Label cell1 = new Label(1, setpropertyNum*i+currentRow, companyName, titleFormate);
                    sheet.addCell( cell1);
                    Map<String, JSONObject> value = entry_company.getValue();
                    for(Map.Entry< String,JSONObject> entry_guarantees : value.entrySet()){
                        String yearInfo = entry_guarantees.getKey();
                        int column=0;
                        if(yearInfo.startsWith("down_2012")){
                            column=3;
                        }else if(yearInfo.startsWith("up_2013")){
                            column=4;
                        }else if(yearInfo.startsWith("down_2013")){
                            column=5;
                        }else if(yearInfo.startsWith("up_2014")){
                            column=6;
                        } else if(yearInfo.startsWith("down_2014")){
                            column=7;
                        }else if(yearInfo.startsWith("up_2015")){
                            column=8;
                        }else if(yearInfo.startsWith("down_2015")){
                            column=9;
                        }else if(yearInfo.startsWith("up_2016")){
                            column=10;
                        }else if(yearInfo.startsWith("down_2016")){
                            column=11;
                        }else if(yearInfo.startsWith("up_2017")){
                            column=12;
                        }
                        JSONObject value1 = entry_guarantees.getValue();
                        Iterator keys = value1.keys();
                        int m=0;
                        while(keys.hasNext()){
                            String string = value1.getString((String) keys.next());
                            Label cell2 = new Label(column, currentRow+i*setpropertyNum+m, string, titleFormate);
                            sheet.addCell( cell2);
                            m++;
                        }
                    }
                    i++;
                }
                sheet.mergeCells(0, currentRow, 0, currentRow+size * setpropertyNum-1);
                Label cell4 = new Label(0, currentRow, province, titleFormate);
                sheet.addCell( cell4);
                currentRow+= size*setpropertyNum;
            }

            book.write();
            book.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 组织各个省份数据
     * @param provinceStrs
     */
    public void setMoneyStatistics(String[] provinceStrs){
        Map<String,Map<String ,Map<String,JSONObject>>> allInfo=new HashMap<>();
        for(String province:provinceStrs) {
            Map info = setMoneyStatistics(province);
            allInfo.put(province,info);
        }
        setJson(allInfo);
//        try {
//            testxlsx(allInfo);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (BiffException e) {
//            e.printStackTrace();
//        } catch (WriteException e) {
//            e.printStackTrace();
//        }
        System.out.println();
    }

    /**
     * 结果中筛选有问题的上市公司
     * @param guaranteesMap
     * @return
     */
    private Map parseGuaranteesInfo(Map<String ,Map<String,JSONObject>> guaranteesMap){
        Map<String ,Map<String,JSONObject>> guaranteesParseMap=new HashMap<>();
        for (Map.Entry< String ,Map<String,JSONObject>> entry : guaranteesMap.entrySet()){
            String companyName = entry.getKey();
            Map<String, JSONObject> guaranteesInfos = entry.getValue();
            boolean flag=false;//判定是否需要挑选出

            NavigableMap<Integer,String> externalGuartees=new TreeMap<>();
            NavigableMap<Integer,String> netAssetList=new TreeMap<>();
            for (Map.Entry< String,JSONObject> yearInfo : guaranteesInfos.entrySet()){
                JSONObject guaranteesInfo = yearInfo.getValue();
                String re1="^([+-]?\\d*\\.?\\d*)$";
                Float debtRatio = 0f;
                String debtRatioStr=guaranteesInfo.getString("debtRatio").replace("%", "");
                debtRatioStr= CommonTool.get_one_match(debtRatioStr,re1);
                if(!"".equals(debtRatioStr)) {
                    debtRatio = Float.parseFloat(debtRatioStr);
                }

                Float amount_guarantee_proportion_netAssets =  0f;
                String amount_guarantee_proportion_netAssetsStr=guaranteesInfo.getString("amount_Guarantee_Proportion_NetAssets").replace("%", "");
                amount_guarantee_proportion_netAssetsStr= CommonTool.get_one_match(amount_guarantee_proportion_netAssetsStr,re1);
                if(!"".equals(amount_guarantee_proportion_netAssetsStr)){
                    amount_guarantee_proportion_netAssets = Float.parseFloat(amount_guarantee_proportion_netAssetsStr);
                }

                if(debtRatio>70 ||amount_guarantee_proportion_netAssets>70){
                    flag=true;
                }
                String total_external_guarantees = guaranteesInfo.getString("total_External_guarantees");
                String netAsset = guaranteesInfo.getString("netAsset");
                String yearQuarter=yearInfo.getKey();
                String[] split = yearQuarter.split("_");
                int add =0;
                int num=0;
                if("down".equals(split[0])){
                    add=1;
                }
                if("2015".equals(split[1])){
                    num=1;
                }else if("2016".equals(split[1])){
                    num=3;
                }else if("2017".equals(split[1])){
                    num=5;
                }
                num=num+add;
                externalGuartees.put(num,total_external_guarantees);
                netAssetList.put(num,netAsset);

            }
            if(!flag){
                if(externalGuartees.size()>1){
                    boolean flagSmall=true;//是否持续增加
                    for (   Map.Entry<Integer, String> Entry : externalGuartees.entrySet()){
                        Map.Entry<Integer, String> integerStringEntry = externalGuartees.higherEntry(Entry.getKey());
                        Map.Entry<Integer, String> lastEntry = integerStringEntry;
                        Map.Entry<Integer, String> firstEntry = Entry;
                        if(lastEntry==null)
                            continue;
                        Float firstMoney=0f;
                        String firstMoneyStr=firstEntry.getValue().replace("万元","");
                        if(!"".equals(firstMoneyStr)) {
                            firstMoney =    Float.parseFloat(firstMoneyStr);
                        }
                        Float lastMoney=0f;
                        String lastMoneyStr=lastEntry.getValue().replace("万元","");
                        if(!"".equals(lastMoneyStr)) {
                            lastMoney = Float.parseFloat(lastMoneyStr);
                        }
                        if(firstMoney>lastMoney){//如果不是持续增加
                            flagSmall=false;
                        }

                    }
                    if(flagSmall){
                        flag = true;
                    }
                }
                if(!flag&&netAssetList.size()>1){
                    boolean flagSmall=true;//是否持续下降
                    for (   Map.Entry<Integer, String> Entry : netAssetList.entrySet()){
                        Map.Entry<Integer, String> integerStringEntry = netAssetList.higherEntry(Entry.getKey());
                        Map.Entry<Integer, String> lastEntry = integerStringEntry;
                        Map.Entry<Integer, String> firstEntry = Entry;
                        if(lastEntry==null)
                            continue;
                        Float firstMoney=0f;
                        String firstMoneyStr=firstEntry.getValue().replace("万元","");
                        if(!"".equals(firstMoneyStr)) {
                            firstMoney =    Float.parseFloat(firstMoneyStr);
                        }
                        Float lastMoney=0f;
                        String lastMoneyStr=lastEntry.getValue().replace("万元","");
                        if(!"".equals(lastMoneyStr)) {
                            lastMoney = Float.parseFloat(lastMoneyStr);
                        }
                        if(firstMoney<lastMoney){//如果不是持续增加
                            flagSmall=false;
                        }

                    }
                    if(flagSmall){
                        flag = true;
                    }
                }
            }
            if(flag){
               // System.out.println(companyName);
                guaranteesParseMap.put(companyName,guaranteesInfos);
            }
        }
        return guaranteesParseMap;
    }

    /**
     * 获取图中公司　所需分析参数结果
     * @param province
     * @return
     */
    public Map setMoneyStatistics(String province){

        String sql ="MATCH (n:"+province+") with n as company MATCH(n)-[r {name:\"总资产\"}]->(m) " +
                "where id(n)=id(company) with company,m as moneys MATCH(n)-[r {name:\"净资产\"}]->(m) " +
                "where id(n)=id(company) with company,moneys,m as netAsset  MATCH(n)-[r {name:\"资产负债率\"}]->(m) " +
                "where id(n)=id(company) " +
                "return company.name,collect(moneys.name) as allmoney,collect(netAsset.name) as netAsset,collect(m.name) as debtRatio ,collect(labels(moneys)) as years";
        StatementResult execute = neo4jCYDAO.execute(sql);
        Map<String ,Map<String,JSONObject>> guaranteesMap=new HashMap<>();
        while(execute.hasNext()) {
            Record next = execute.next();
            String companyName = next.get("company.name").asString();

            Map<String,JSONObject> oneCPresult=new HashMap<>();
            List<Object> allmoneys = next.get("allmoney").asList();

            List<Object> netAssets = next.get("netAsset").asList();
            List<Object> debtRatios = next.get("debtRatio").asList();
            List<Object> years = next.get("years").asList();

            for(int i=0;i<years.size();i++){
                JSONObject json=new JSONObject();
                List<String> year = (List<String>) years.get(i);
                String yearStr=year.get(0);
                Object netAsset = netAssets.get(i);
                Object allmoney = allmoneys.get(i);
                Object debtRatio = debtRatios.get(i);
                json.put("allmoney",allmoney);
                json.put("netAsset",netAsset);
                json.put("debtRatio",debtRatio);
                json.put("total_External_guarantees","");
                json.put("total_External_guarantees_size","");
                json.put("total_Accept_guarantees_size","");
                json.put("total_Accept_guarantees","");
                json.put("total_Overdue_guarantee","");
                json.put("total_Overdue_guarantee_size","");
                json.put("amount_Guarantee_Proportion_NetAssets","");
                if(!oneCPresult.containsKey(yearStr))
                    oneCPresult.put(yearStr,json);
            }
            if(!guaranteesMap.containsKey(companyName)){
                guaranteesMap.put(companyName,oneCPresult);
            }
        }
        sql="MATCH(n:"+province+")-[*1{name:\"担保方\"}]->()-[r {name:\"实际担保金额\"}]->(m) " +
                "return n.name as company,collect(m.name) as moneys,collect(labels(m)) as years";
        execute = neo4jCYDAO.execute(sql);
        while(execute.hasNext()) {
            Record next = execute.next();
            String companyName = next.get("company").asString();
            List<Object> moneys = next.get("moneys").asList();
            List<Object> years = next.get("years").asList();
            Map<String,Float> yearMoneyMap =new HashMap<>();
            Map<String,Integer> yearCountMap =new HashMap<>();
            for(int i=0;i<years.size();i++){
                List<String> year = (List<String>) years.get(i);
                String yearStr=year.get(0);
                String danbaoMoney = (String) moneys.get(i);
                Float moneyFloat= Float.parseFloat(danbaoMoney.replace("万元",""));
                if(yearMoneyMap.containsKey(yearStr)){
                    yearMoneyMap.put(yearStr,yearMoneyMap.get(yearStr)+moneyFloat);
                    yearCountMap.put(yearStr,yearCountMap.get(yearStr)+1);
                }else{
                    yearMoneyMap.put(yearStr,moneyFloat);
                    yearCountMap.put(yearStr,1);
                }
            }
            for (Map.Entry<String,Float> entry : yearMoneyMap.entrySet()){
                String year = entry.getKey();
                Map<String, JSONObject> stringJSONObjectMap = guaranteesMap.get(companyName);
                if(stringJSONObjectMap!=null) {
                    JSONObject jsonObject = stringJSONObjectMap.get(year);
                    if(jsonObject!=null){
                        jsonObject.put("total_External_guarantees", entry.getValue() + "万元");
                        jsonObject.put("total_External_guarantees_size", yearCountMap.get(year) + "");
                        float netAsset = Float.parseFloat(jsonObject.getString("netAsset").replace("万元", ""));
                        float num = entry.getValue() / netAsset*100;
                        DecimalFormat df = new DecimalFormat("0.00");//格式化小数
                        String s = df.format(num);
                        jsonObject.put("amount_Guarantee_Proportion_NetAssets", s + "%");
                    }
                }
            }
        }

        sql="MATCH(n:"+province+")-[*1{name:\"被担保方\"}]->()-[r {name:\"实际担保金额\"}]->(m) " +
                "return n.name as company,collect(m.name) as moneys,collect(labels(m)) as years";
        execute = neo4jCYDAO.execute(sql);
        while(execute.hasNext()) {
            Record next = execute.next();
            String companyName = next.get("company").asString();

            List<Object> moneys = next.get("moneys").asList();

            List<Object> years = next.get("years").asList();
            Map<String,Float> yearMoneyMap =new HashMap<>();
            Map<String,Integer> yearCountMap =new HashMap<>();
            for(int i=0;i<years.size();i++){
                List<String> year = (List<String>) years.get(i);
                String yearStr=year.get(0);
                String danbaoMoney = (String) moneys.get(i);
                Float moneyFloat= Float.parseFloat(danbaoMoney.replace("万元",""));
                if(yearMoneyMap.containsKey(yearStr)){
                    yearMoneyMap.put(yearStr,yearMoneyMap.get(yearStr)+moneyFloat);
                    yearCountMap.put(yearStr,yearCountMap.get(yearStr)+1);
                }else{
                    yearMoneyMap.put(yearStr,moneyFloat);
                    yearCountMap.put(yearStr,1);
                }
            }
            for (Map.Entry<String,Float> entry : yearMoneyMap.entrySet()){
                String year = entry.getKey();
                Map<String, JSONObject> stringJSONObjectMap = guaranteesMap.get(companyName);
                if(stringJSONObjectMap!=null) {
                    JSONObject jsonObject = stringJSONObjectMap.get(year);
                    if (jsonObject != null) {
                        jsonObject.put("total_Accept_guarantees", entry.getValue() + "万元");
                        jsonObject.put("total_Accept_guarantees_size", yearCountMap.get(year) + "");
                    }
                }
            }

        }

        sql="MATCH(n:"+province+")-[*1{name:\"担保方\"}]->()-[r {name:\"担保是否逾期\"}]->(m) where m.name=\"是\" with n as company,m as yuqi " +
                "MATCH(n:"+province+")-[*1{name:\"担保方\"}]->()-[r {name:\"担保逾期金额\"}]->(m) " +
                "return n.name as company,collect(m.name) as moneys,collect(labels(m)) as years";
        execute = neo4jCYDAO.execute(sql);
        while(execute.hasNext()) {
            Record next = execute.next();
            String companyName = next.get("company").asString();
            List<Object> moneys = next.get("moneys").asList();
            List<Object> years = next.get("years").asList();
            Map<String,Float> yearMoneyMap =new HashMap<>();
            Map<String,Integer> yearCountMap =new HashMap<>();
            for(int i=0;i<years.size();i++){
                List<String> year = (List<String>) years.get(i);
                String yearStr=year.get(0);
                String danbaoMoney = (String) moneys.get(i);
                Float moneyFloat= Float.parseFloat(danbaoMoney.replace("万元",""));
                if(yearMoneyMap.containsKey(yearStr)){
                    yearMoneyMap.put(yearStr,yearMoneyMap.get(yearStr)+moneyFloat);
                    yearCountMap.put(yearStr,yearCountMap.get(yearStr)+1);
                }else{
                    yearMoneyMap.put(yearStr,moneyFloat);
                    yearCountMap.put(yearStr,1);
                }
            }
            for (Map.Entry<String,Float> entry : yearMoneyMap.entrySet()){
                String year = entry.getKey();
                Map<String, JSONObject> stringJSONObjectMap = guaranteesMap.get(companyName);
                if(stringJSONObjectMap!=null) {
                    JSONObject jsonObject = stringJSONObjectMap.get(year);
                    if(jsonObject!=null){
                        jsonObject.put("total_Overdue_guarantee",entry.getValue()+"万元");
                        jsonObject.put("total_Overdue_guarantee_size",yearCountMap.get(year)+"");
                    }
                }

            }
        }
        guaranteesMap=parseGuaranteesInfo(guaranteesMap);
        return guaranteesMap;
    }

    /**
     * 获取联通图公司统计
     * @param label_com
     * @param relationship
     * @param quarter
     */
    public void unionFindStream(String label_com,String relationship,String  quarter){
        Map<Integer,List<JSONObject>> tmpMaps=new HashMap<>();
        String sql=null;
        Map<Integer,Integer> quantityMap=new HashMap<>();
        //CALL algo.unionFind.stream(label:String, relationship:String, {weightProperty:'weight', threshold:0.42, defaultValue:1.0)YIELD nodeId, setId

        //    sql="CALL algo.unionFind.stream('"+label+"', '"+relationship+"', { defaultValue:0.0, threshold:1.0}) YIELD nodeId,setId";
        sql= "CALL algo.unionFind.stream('"+label_com+"', '"+relationship+"', { defaultValue:0.0, threshold:1.0}) YIELD nodeId,setId " +
                "match(n)-[r:"+relationship+"]->(m) where id(n)=nodeId and m.type = 'com' " +
                "return setId,collect(m.name)+n.name as companyName,collect(id(m))+id(n) as companyId";
        StatementResult execute = neo4jCYDAO.execute(sql);

        int sum=0;
        while(execute.hasNext()){
            Record next = execute.next();
            sum++;
            long setId = next.get("setId").asLong();
            List<Object> companyNames = next.get("companyName").asList();
            List<Object> companyIds = next.get("companyId").asList();
            JSONObject obj=new JSONObject();
            obj.put("partitonId",setId);
            obj.put("companyIds",companyIds);
            obj.put("companyNames",companyNames);
            int size=companyNames.size();
            List<JSONObject> namelist=null;
            if(tmpMaps.containsKey(size)){
                namelist=tmpMaps.get(size);
                namelist.add(obj);
                Integer integer = quantityMap.get(size);
                quantityMap.put(size,integer+1);
            }else {
                namelist = new ArrayList<>();
                namelist.add(obj);
                tmpMaps.put(size, namelist);
                quantityMap.put(size, 1);
            }
        }

        JSONObject result=new JSONObject();
        JSONArray jsonArray=new JSONArray();
        result.put("担保圈数量",sum);
        result.put("分布坐标",quantityMap.toString());
        for (Map.Entry<Integer, List<JSONObject>> entry : tmpMaps.entrySet()){
            List<JSONObject> partitionContents = entry.getValue();
            JSONObject obj= new JSONObject();
            JSONArray partitionContentArray=new JSONArray();
            for(JSONObject partitionContent:partitionContents){
                partitionContentArray.put(partitionContent);
            }
            obj.put("size",entry.getKey());
            obj.put("content",partitionContentArray);
            jsonArray.put(obj);
        }
        result.put("partition",jsonArray);
        if(sum>0)
            CommonTool.printFile(result.toString(),"/home/hadoop/下载/结果数据/"+quarter+"/担保圈公司数量_"+quarter+"_"+label_com+".txt",false);
    }

    /**
     * 获取担圈担保金额统计
     * @param label_com
     * @param label
     * @param relationship
     * @param quarter
     */
    public void getDanbaoMoney(String label_com,String label,String relationship,String  quarter){
        Map<Long,List<JSONObject>> tmpMaps=new HashMap<>();
        String sql="MATCH (x:"+label_com+")-[v:"+relationship+"*1{name:\"担保方\"}]->(n:"+label+")-[r*1{name:\"实际担保金额\"}]->(m:"+label+") " +
                "with distinct(x.partition_cc)as partition,collect(distinct id(x)) as companyId,collect(distinct x.name) as companyName,collect(m.name) as money " +
                "return partition,companyId,companyName,money";
        StatementResult execute = neo4jCYDAO.execute(sql);
        while(execute.hasNext()){
            Record next = execute.next();
            long partitionId=next.get("partition").asLong();
            List<Object> companyId = next.get("companyId").asList();
            List<Object> companyName = next.get("companyName").asList();
            List<Object> money = next.get("money").asList();
            JSONObject obj=new JSONObject();
            obj.put("companyId",companyId);
            obj.put("companyName",companyName);
            obj.put("money",money);
            List<JSONObject> weightList=null;
            if(tmpMaps.containsKey(partitionId)){
                weightList=tmpMaps.get(partitionId);
                weightList.add(obj);
            }else{
                weightList=new ArrayList<>();
                weightList.add(obj);
                tmpMaps.put(partitionId,weightList);
            }

        }
        JSONObject resultObj=new JSONObject();
        JSONArray resultArray=new JSONArray();
        for (Map.Entry<Long, List<JSONObject>> entry : tmpMaps.entrySet()){
            List<JSONObject> value = entry.getValue();
            for(JSONObject moneyObj:value){
                float Sum=0f;
                JSONArray moneys = moneyObj.getJSONArray("money");
                JSONArray companyName = moneyObj.getJSONArray("companyName");
                if(companyName.length()>1)
                    System.out.println();
                for(int i=0;i<moneys.length();i++){
                    String moneyStr= (String) moneys.get(i);
                    float money=Float.parseFloat(moneyStr.replace("万元",""));
                    Sum+=money;
                }
                moneyObj.put("sumMoney",Sum+"万元");
                moneyObj.put("partitonId",entry.getKey());
                resultArray.put(moneyObj);
            }
        }
        resultObj.put("Size",resultArray.length());
        resultObj.put("content",resultArray);
        if(resultArray.length()>0)
            CommonTool.printFile(resultObj.toString(),"/home/hadoop/下载/结果数据/"+quarter+"/担保圈金额数量_"+quarter+"_"+label_com+".txt",false);
    }

    /**
     * 担保圈担保数量统计
     * @param label_com
     * @param label
     * @param relationship
     * @param quarter
     */
    public void distinctWeightDanbao(String label_com,String label,String relationship,String  quarter){
        Map<Long,List<JSONObject>> tmpMaps=new HashMap<>();
        String sql=null;
        sql="MATCH (n:"+label_com+")-[r:"+relationship+"]->(m:"+label+") " +
                "WHERE EXISTS(r.weight) " +
                "RETURN distinct(n.partition_cc) as partition, collect(distinct id(n)) as ids,collect(distinct n.name) as names,collect((r.weight)) as weights,count(*) as num_of_weight ORDER by num_of_weight DESC";
        int i=0;
        StatementResult execute = neo4jCYDAO.execute(sql);
        while(execute.hasNext()){
            i++;
            Record next = execute.next();
            long partitionId=next.get("partition").asLong();
            List<Object> weightslist=next.get("weights").asList();
            Map<Long,Integer> weightMap=new HashMap<>();

            for(Object weightSize:weightslist){
                if(weightMap.containsKey(weightSize)){
                    Integer integer = weightMap.get(weightSize);
                    weightMap.put((Long) weightSize,integer+1);
                }else{
                    weightMap.put((Long) weightSize,1);
                }
            }
            int numOfWeight=0;
            JSONArray weightSizesInfo=new JSONArray();
            for (Map.Entry<Long,Integer> entry : weightMap.entrySet()){
                Long size = entry.getKey();
                Integer num = entry.getValue();
                numOfWeight+=size*num;
                JSONObject weightInfo=new JSONObject();
                weightInfo.put("size",size);
                weightInfo.put("num",num);
                weightSizesInfo.put(weightInfo);
            }
            List<Object> ids = next.get("ids").asList();
            List<Object> names = next.get("names").asList();
            JSONObject obj=new JSONObject();
            obj.put("weightSize",weightSizesInfo);
            obj.put("numOfWeight",numOfWeight);
            obj.put("companyIds",ids);
            obj.put("companyNames",names);
            List<JSONObject> weightList=null;

            if(tmpMaps.containsKey(partitionId)){
                weightList=tmpMaps.get(partitionId);
                weightList.add(obj);
            }else{
                weightList=new ArrayList<>();
                weightList.add(obj);
                tmpMaps.put(partitionId,weightList);
            }
        }
        JSONObject resultObj=new JSONObject();
        JSONArray guaranteeArray=new JSONArray();
        int allguaranteeNum=0;
        for (Map.Entry<Long, List<JSONObject>> entry : tmpMaps.entrySet()){
            JSONObject danbaoObj=new JSONObject();
            List<JSONObject> daobanInfos = entry.getValue();
            int sumOfWeight=0;
            JSONArray danbaoArray=new JSONArray();
            for(JSONObject daobanInfo:daobanInfos){
                int numOfWeight = daobanInfo.getInt("numOfWeight");
                sumOfWeight+=numOfWeight;
                danbaoArray.put(daobanInfo);
            }
            danbaoObj.put("partitionId",entry.getKey());
            danbaoObj.put("sumOfWeight",sumOfWeight);
            danbaoObj.put("info",danbaoArray);
            allguaranteeNum+=sumOfWeight;
            guaranteeArray.put(danbaoObj);
        }
        resultObj.put("guaranteeArray",guaranteeArray);
        resultObj.put("allguaranteeNum",allguaranteeNum);
        resultObj.put("partitonSize",guaranteeArray.length());
        if(guaranteeArray.length()>0)
            CommonTool.printFile(resultObj.toString(),"/home/hadoop/下载/结果数据/"+quarter+"/担保圈担保数量_"+quarter+"_"+label_com+".txt",false);

    }
}
