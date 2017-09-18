package com.qdcz.other.StuctData;

import com.qdcz.common.CommonTool;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Created by hadoop on 17-9-12.
 */
public class StructCheckCon {
    private static String dir = "/mnt/vol_0/neo4j-community-3.2.1/import/";
    public static String inputPath = "/home/hadoop/下载/反馈qp结果/";
    private static String[] inputFiles ={ "2013上半年", "2013下半年", "2014上半年", "2014下半年", "2015下半年", "2016下半年",  "2015上半年","2016上半年", "2017上半年"};
    public static  String inputFile ="2016下半年";
    private static String[] attr_en = {"lim_pub_date","gua_limit","act_occ_date","act_gua_money","gua_type","gua_date","whe_per_end",
            "whe_rel_guarantee","gua_beg_date","gua_lim_date","gua_whe_overdue","gua_ove_money","whe_rev_guarantee","rel_relation","lis_com_relation"};
    private static String[] attr_cn = {"披露日期","担保额度","协议签署日","实际担保金额","担保类型","担保期","是否履行完毕",
            "是否为关联方担保","担保起始日","担保到期日","担保是否逾期","担保逾期金额","是否存在反担保","关联关系","担保方与上市公司的关系"};//,"年度","季度","总资产","净资产"

    public static void main(String[] args) throws Exception {
        StructCheckCon instance=new StructCheckCon();
        for(String input:inputFiles) {
            inputFile=input;
            instance.doIt();
        }
    }
    private static String[] label_en = {"year","quarterStr","address","allMoney","netAsset","debtRatio"};
    private static String[] label_cn = {"年度","季度","地域","总资产","净资产","资产负债率"};
    private  void dealStrInfo(JSONObject obj){
        for (int i = 0;i<attr_en.length;i++){
            String one_attr_cn = attr_cn[i];
            String one_attr_en = attr_en[i];
            if(obj.has(one_attr_en) && !obj.getString(one_attr_en).isEmpty()){
                String string = obj.getString(one_attr_en);
                switch (one_attr_en){
                    case "lim_pub_date":
                    case "act_occ_date":
                    case "gua_beg_date":
                    case "gua_lim_date":
                        string=string.replace("年","/").replace("月","/").replace("日","").replace("至","-") ;
                        String pattern = "((^((1[8-9]\\d{2})|([2-9]\\d{3}))([-\\/\\._])(10|12|0?[13578])([-\\/\\._])(3[01]|[12][0-9]|0?[1-9])$)|(^((1[8-9]\\d{2})|([2-9]\\d{3}))([-\\/\\._])(11|0?[469])([-\\/\\._])(30|[12][0-9]|0?[1-9])$)|(^((1[8-9]\\d{2})|([2-9]\\d{3}))([-\\/\\._])(0?2)([-\\/\\._])(2[0-8]|1[0-9]|0?[1-9])$)|(^([2468][048]00)([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([3579][26]00)([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([1][89][0][48])([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([2-9][0-9][0][48])([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([1][89][2468][048])([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([2-9][0-9][2468][048])([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([1][89][13579][26])([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([2-9][0-9][13579][26])([-\\/\\._])(0?2)([-\\/\\._])(29)$))";
                        boolean isMatch = Pattern.matches(pattern, string);
                        if(isMatch){

                        }else{
                            CommonTool.printFile(one_attr_cn+"\t"+one_attr_en+"\t"+string+"            ｈａｓ problem \t"+obj+"\n",inputPath+inputFile,true);


                        }
                        break;
                    case "gua_limit":
                    case "act_gua_money":
                    case "gua_ove_money":
                        string=string.replace("万元","");
                        Float money=Float.parseFloat(string);
                        if(money>=0){

                        }else{
                            CommonTool.printFile(one_attr_cn+"\t"+one_attr_en+"\t"+string+"            ｈａｓ problem \t"+obj+"\n",inputPath+inputFile,true);

                        }
                        break;
                    case "gua_type":
                        if(string.contains("押")||string.contains("保证")||string.contains("担保")|string.contains("责任保")){

                        }else {
                            CommonTool.printFile(one_attr_cn+"\t"+one_attr_en+"\t"+string+"            ｈａｓ problem \t"+obj+"\n",inputPath+inputFile,true);

                        }
                        break;
                    case "gua_date":
                        string=string .replace("至","-").replace("~","-");
                        if(string.contains("-")){
                            pattern = "^[0-9]{1,2}-[0-9]{1,2}[个月|年]";
                            isMatch = Pattern.matches(pattern, string);
                            if(isMatch){

                            }else{
                                string=string.replace("年","/").replace("月","/").replace("日","") ;
                                String[] split = string.split("-");
                                if(split.length==2){
                                    //进入日期判断
                                    for(String date:split){
                                        date=date.replace("年","/").replace("月","/").replace("日","") ;
                                        pattern = "^(^(\\d{4}|\\d{2})(\\-|\\/|\\.)\\d{1,2}$)|((^((1[8-9]\\d{2})|([2-9]\\d{3}))([-\\/\\._])(10|12|0?[13578])([-\\/\\._])(3[01]|[12][0-9]|0?[1-9])$)|(^((1[8-9]\\d{2})|([2-9]\\d{3}))([-\\/\\._])(11|0?[469])([-\\/\\._])(30|[12][0-9]|0?[1-9])$)|(^((1[8-9]\\d{2})|([2-9]\\d{3}))([-\\/\\._])(0?2)([-\\/\\._])(2[0-8]|1[0-9]|0?[1-9])$)|(^([2468][048]00)([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([3579][26]00)([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([1][89][0][48])([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([2-9][0-9][0][48])([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([1][89][2468][048])([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([2-9][0-9][2468][048])([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([1][89][13579][26])([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([2-9][0-9][13579][26])([-\\/\\._])(0?2)([-\\/\\._])(29)$))";
                                        isMatch = Pattern.matches(pattern, date);
                                        if(isMatch){

                                        }else{

                                            CommonTool.printFile(one_attr_cn+"\t"+one_attr_en+"\t"+string+"            ｈａｓ problem \t"+obj+"\n",inputPath+inputFile,true);
                                            break;
                                        }
                                    }
                                } else {
                                    CommonTool.printFile(one_attr_cn+"\t"+one_attr_en+"\t"+string+"            ｈａｓ problem \t"+obj+"\n",inputPath+inputFile,true);

                                }
                            }
                        }else{
                            pattern= "[0-9]{1,2}$|^[0-9一两三四五六七八九十半]{1,3}[年|个月|日]{1,2}|";
                            isMatch = Pattern.matches(pattern, string);

                            if(isMatch){

                            }else{
                                String string1=string.replace("年","/").replace("月","/").replace("日","") ;
                                pattern = "^(^(\\d{4}|\\d{2})(\\-|\\/|\\.)\\d{1,2}$)|((^((1[8-9]\\d{2})|([2-9]\\d{3}))([-\\/\\._])(10|12|0?[13578])([-\\/\\._])(3[01]|[12][0-9]|0?[1-9])$)|(^((1[8-9]\\d{2})|([2-9]\\d{3}))([-\\/\\._])(11|0?[469])([-\\/\\._])(30|[12][0-9]|0?[1-9])$)|(^((1[8-9]\\d{2})|([2-9]\\d{3}))([-\\/\\._])(0?2)([-\\/\\._])(2[0-8]|1[0-9]|0?[1-9])$)|(^([2468][048]00)([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([3579][26]00)([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([1][89][0][48])([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([2-9][0-9][0][48])([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([1][89][2468][048])([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([2-9][0-9][2468][048])([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([1][89][13579][26])([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([2-9][0-9][13579][26])([-\\/\\._])(0?2)([-\\/\\._])(29)$))";
                                isMatch = Pattern.matches(pattern, string1);
                                if(isMatch){

                                }else{
                                    if(string.equals("长期")||string.equals("到期后又展期半年")||string.equals("借款到期后两年")||string.equals("1年到期后又展期半年")||string.equals("半年到期后又展期半年"))
                                    {

                                    }else{
                                        CommonTool.printFile(one_attr_cn+"\t"+one_attr_en+"\t"+string+"            ｈａｓ problem \t"+obj+"\n",inputPath+inputFile,true);
                                    }
                                }
                            }
                        }
                        break;
                    case "whe_per_end":
                    case "whe_rel_guarantee":
                    case "gua_whe_overdue":
                    case "whe_rev_guarantee":
                        if("是".equals(string)||"否".equals(string)){

                        }else{
                            CommonTool.printFile(one_attr_cn+"\t"+one_attr_en+"\t"+string+"            ｈａｓ problem \t"+obj+"\n",inputPath+inputFile,true);

                        }
                        break;
                    case "rel_relation":

                        break;
                    case "lis_com_relation":

                        break;

                    default:
                        CommonTool.printFile(one_attr_cn+"\t"+one_attr_en+"\t"+string+"            ｈａｓ problem \t"+obj+"\n",inputPath+inputFile,true);

                        break;
                }
            }else{
           //     System.out.println();
            }
        }
    }
    public void doIt() throws Exception {
        Scanner sc = new Scanner(new File(dir + inputFile));
        while(sc.hasNext()) {

            String line = sc.nextLine();

            JSONObject one_com = new JSONObject(line);
            String companyName = one_com.getString("cre_subject");



            if(!verifyName(companyName)){
                System.out.println();
                continue;
            }
            JSONArray out_cre_guarantee = one_com.getJSONArray("out_cre_guarantee");
            for (int i = 0;i<out_cre_guarantee.length();i++) {
                JSONObject obj = out_cre_guarantee.getJSONObject(i);
                dealStrInfo(obj);
            }
            JSONArray com_sub_guarantee = one_com.getJSONArray("com_sub_guarantee");
            for (int i = 0;i<com_sub_guarantee.length();i++) {
                JSONObject obj = com_sub_guarantee.getJSONObject(i);
                dealStrInfo(obj);
            }
            JSONArray sub_sub_guarantee = one_com.getJSONArray("sub_sub_guarantee");
            for (int i = 0;i<sub_sub_guarantee.length();i++) {
                JSONObject obj = sub_sub_guarantee.getJSONObject(i);
                dealStrInfo(obj);
            }
        }
    }

    public boolean verifyName(String name){
        boolean flag = true;
        if(name.isEmpty() || name.length()>35|| name.length()<5 || name.matches(".*\\d+.*")
                || name.contains("√")|| name.contains("□") || name.contains("%") || name.contains("---")
                || name.contains("\"")){

            return false;
        }
        if(name.replace("有限","").replace("公司","").replace("股份","").length()<4){
            return false;
        }

        return flag;
    }

}
