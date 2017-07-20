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
import java.util.HashMap;
import java.util.Iterator;
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


    public static String readFileOneTime(String inpath) {
        File file = new File(inpath);
        Long file_length = new Long(file.length());
        byte[] file_buffer = new byte[file_length.intValue()];
        String content = "";

        try {
            FileInputStream e = new FileInputStream(file);
            e.read(file_buffer);
            e.close();
        } catch (Exception var7) {
            var7.printStackTrace();
        }

        try {
            content = new String(file_buffer, "utf8");
        } catch (UnsupportedEncodingException var6) {
            var6.printStackTrace();
        }

        return content;
    }
    public static String getNode(String question){
        String[] arr = "银团贷款合同示范文本|银团贷款的发起和筹组|自行协商、自主定价|银团贷款信息备忘录|“谁借款、谁付费”|参与银团贷款的银行|大额流动资金融资|银团贷款转让交易|尽最大努力推销|大于等于20%|银团贷款委任书|转让标的、市场|大于等于50%|分销银团贷款|大型集团客户|借款人/银行|银团贷款收费|银团贷款业务|大型项目融资|银团贷款管理|银团贷款合同|银行业协会|联合牵头行|银团会议|单家银行|银团贷款|部分包销|银团费用|副牵头行|交易双方|全额包销|大额贷款|银团成员|受让方|代理行|代理费|借款人|参加行|承诺费|出让方|牵头行|安排费|违约|定价".split("\\|");
        for(String one:arr){
            if(question.contains(one)){
                return one;
            }
        }
        return "";
    }


    public static String getEdge(String question){
        String[] arr = "鼓励进行银团借款对象|审阅/签署申明|包含主要条款|权利义务关系|收费管理原则|贷前尽职调查|授信原则|依据对象|支付方式|承担责任|包含事项|享有对象|贷款信息|付款对象|交易前提|风险行为|组成数量|承贷份额|收费项目|追究责任|收费原则|借款对象|主要职能|分销份额|注意防范|评审方式|贷款人|监督者|负责人|发起人|对象|召开|制定|权力|签署|规则|职责|包含|原则|概念|内容|编制|报送|角色|义务|约束|依据|类型".split("\\|");
        for(String one:arr){
            if(question.contains(one)){
                return one;
            }
        }
        return "";
    }
}
