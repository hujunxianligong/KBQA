package com.qdcz.graph.interfaces;

import com.qdcz.entity.Edge;
import com.qdcz.entity.IGraphEntity;
import com.qdcz.entity.Vertex;
import org.json.JSONObject;
import org.neo4j.driver.v1.types.Path;

import java.util.List;
import java.util.Map;

/**
 * 每个图对外提供的操作业务
 * Created by star on 17-8-3.
 */
public interface IGraphBuzi {

    /**
     * 批量文件增点
     * @param label
     * @param filepath
     * @return
     */
    public Map batchInsertVertex(String label,String filepath);


    /**
     * 批量文件增边
     * @param relatinship
     * @param filepath
     * @return
     */
    public Map batchInsertEdge(String relatinship,String filepath);
    /**
     * 添加点
     * @param vertex
     * @return
     */
    public String addVertex(Vertex vertex);

    /**
     * 无向搜索
     * @param vertex
     * @param depth
     * @return
     */
    public List<Path> directedBfExtersion(Vertex vertex, int depth);

    /**
     * 修改点
     * @param vertex
     * @return
     */
    public String changeVertex(Vertex vertex);


    /**
     *删除点
     */
    public List<IGraphEntity> deleteVertex(Vertex vertex);


    /**
     * 新增边
     * @param edge
     * @return
     */
    public String addEdges(Edge edge);


    /**
     * 修改边
     * @param edge
     * @return
     */
    public String changeEdge(Edge edge);

    /**
     * 删除边
     * @param edge
     * @return
     */
    public String deleteEdge(Edge edge);

    /**
     * 根据节点查询广搜查询
     * @param vertex
     * @param depth
     * @return
     */
    public List<Path> bfExtersion(Vertex vertex,int depth);
    /**
     * 深度优先遍历
     * @param fromId
     * @param toId
     * @return
     */
    public Path dfExection(long fromId, long toId, int depth);


    /**
     * 根据identity查询系统点信息
     * @param label
     * @param identity
     */
    public Vertex checkVertexByIdentity(String label,String  identity);
    /**
     * 根据 id与广搜结果
     * @param id
     * @param depth
     * @return
     */
    public List<Path> checkGraphById(long id,int depth);
    /**
     * 根据边id查询边首尾点
     * @param id
     *
     */
    public Map<String,Vertex> checkVertexByEdgeId(long id);
}
