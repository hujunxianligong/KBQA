package com.qdcz.service;

import com.qdcz.neo4jkernel.LegacyIndexService;
import com.qdcz.neo4jkernel.LoopDataService;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.ogm.json.JSONArray;
import org.neo4j.ogm.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by hadoop on 17-7-13.
 * 对知识库问题的分析获取图谱中的准确信息
 */
@Service
public class QuestionPaserService
{
    @Autowired
    private LegacyIndexService legacyIndexService;

    public  Map<String, Object> getNode(String question){
        String[] fields={"name"};
        float maxScore = 0;
        Map<String, Object> node =null;
        String type="node";
        List<Map<String, Object>> maps = legacyIndexService.selectByFullTextIndex(fields, question,"vertex");
        for(Map<String, Object> map:maps){
            float score = (float) map.get("score");//会出错
            if(maxScore<score){
                maxScore = score;
                node = map;
            }
        }
        fields= new String[]{"relation"};
        maps = legacyIndexService.selectByFullTextIndex(fields, question,"edge");
        for(Map<String, Object> map:maps){
            float score = 0;
            try {
                score = Float.parseFloat( map.get("score").toString());//会出错
            }catch (Exception e){
                System.out.println();
            }
            if(maxScore<score){
                type="edge";
                maxScore = score;
                node = map;
            }
        }

        return node;
    }

}
