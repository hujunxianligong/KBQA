package com.qdcz;

import com.qdcz.tools.CommonTool;
import com.qdcz.service.InstrDemandService;
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
    @Test
    public void testQuey(){
        instrDemandService.queryF("归还借款本金");//未进行抵押登记//抵押担保//归还借款本金
    }

}
