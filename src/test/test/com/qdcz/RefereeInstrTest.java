package com.qdcz;

import com.qdcz.common.CommonTool;
import com.qdcz.common.LoadConfigListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.ogm.json.JSONException;
import org.neo4j.ogm.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by hadoop on 17-6-30.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringbootSdnEmbeddedApplication.class)
public class RefereeInstrTest {
    @Autowired
    private InstrDemandService instrDemandService;
    @Test
    public void testHehe(){
        List<String> getfile = CommonTool.getfile("/home/hadoop/wnd/usr/leagal/建新/点json");
        for(String str:getfile){
            try {
                JSONObject obj=new JSONObject(str);
                instrDemandService.addVertex(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    @Test
    public void testInitEdge(){
        instrDemandService.addEgde();
    }

    @Before
    public void befor(){
        LoadConfigListener loadConfigListener=new LoadConfigListener();//测试使用
        loadConfigListener.contextInitialized(null);
    }

    @Test
    public void testQuey(){

        String result = instrDemandService.queryF("未进行抵押登记");//与银行签订抵押合同//未进行抵押登记//抵押担保//归还借款本金归还借款本金//注销抵押登记//抵押物未进行抵押登记
        System.out.println(result);
    }

}
