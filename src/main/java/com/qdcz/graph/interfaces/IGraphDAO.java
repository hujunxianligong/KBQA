package com.qdcz.graph.interfaces;

import com.qdcz.graph.entity.Edge;
import com.qdcz.graph.entity.Vertex;

/**
 * 图谱操作类接口
 * Created by star on 17-8-2.
 */
public interface IGraphDAO {

    /**
     * 添加点
     * @param vertex
     * @return
     */
    public String addVertex(Vertex vertex);


    /**
     * 修改点
     * @param vertex
     * @return
     */
    public String changeVertex(Vertex vertex);


    /**
     *删除点
     */
    public String deleteVertex(Vertex vertex);


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


}
