
package com.qdcz.graph.service;

import com.qdcz.graph.entity.Vertex;
import com.qdcz.common.BuildReresult;

import com.qdcz.graph.entity.Edge;
import com.qdcz.service.bean.RequestParameter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.traversal.Traverser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


/**
 * Created by hadoop on 17-6-22.
 * main service
 */
//@Service
public class TransactionService {



    /**
     *批量导入或删除数据节点
     */
    @Transactional

    public long addVertexsByPath(RequestParameter requestParameter, String filePath, String type){
        return 0;
    }

    /**
     * //批量导入边
     * @param filePath
     * @return
     */
    @Transactional
    public long addEdgesByPath(RequestParameter requestParameter,String filePath){
        return 0;
    }

    /**
     * 创建节点
     * @param vertex
     * @return
     */
    @Transactional

    public long addVertex(RequestParameter requestParameter,Vertex vertex){
        return 0;
    }
    /**
     *删除节点
     */
    @Transactional
    public void deleteVertex(RequestParameter requestParameter,String name){
    }

    /**
     * 通过
     * @param id
     */
    @Transactional
    public void deleteVertex(RequestParameter requestParameter,long id){
    }

    /**
     * 修改点信息
     * @param id
     * @param newVertex
     * @return
     */
    @Transactional
    public long changeVertex(RequestParameter requestParameter,long id,Vertex newVertex){
        return 0;
    }

    /**
     * 索引匹配查询
     * @param keyword
     * @return
     */
    @Transactional
    public JSONObject indexMatchingQuery(String keyword){
        return null;
    }
    @Transactional
    public JSONObject exactMatchQuery(RequestParameter requestParameter, String name){
        return null;
    }

    /**
     * 根据精准name查图
     * @param name
     * @param depth
     * @return
     */
    @Transactional
    public JSONObject exactMatchQuery(RequestParameter requestParameter,String name,int depth){
        return null;
    }

    /**
     * 根据边relation 查询相关
     * @param relationship
     * @return
     */
    @Transactional
    public JSONObject getInfoByRname(String relationship){
        return null;
    }

    /**
     * 根据 id与深度返回结果
     * @param id
     * @param depth
     * @return
     */
    @Transactional
    public JSONObject getGraphById(Long id,int depth){
        return null;
    }


    /**
     * 插入关系
     * @return
     */
    @Transactional
    public long addEgde(RequestParameter requestParameter,Edge edge){
        return 0;
    }

    /**
     * 插入关系
     */
    @Transactional
    public long addEgde(RequestParameter requestParameter,long fromId,long toId,String relation,JSONObject content){
        return 0;
    }

    /**
     *
     * @param id
     * @return
     */
    @Transactional
    public Edge deleteEgde(RequestParameter requestParameter,Long id){
        return null;
    }
    @Transactional
    public long changeEgde(RequestParameter requestParameter,Long id,Long fromId,Long toId,JSONObject newEgdeInfo){
        return 0;
    }

    @Transactional
    public void changeEgde(RequestParameter requestParameter,Long id,JSONObject newEgdeInfo){
        return ;
    }

}
