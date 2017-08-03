package com.qdcz.graph.interfaces;

import com.qdcz.graph.entity._Edge;
import com.qdcz.graph.entity._Vertex;

/**
 * 每个图对外提供的操作业务
 * Created by star on 17-8-3.
 */
public interface IGraphBuzi {

    /**
     * 添加点
     * @param vertex
     * @return
     */
    public String addVertex(_Vertex vertex);


    /**
     * 修改点
     * @param vertex
     * @return
     */
    public String changeVertex(_Vertex vertex);


    /**
     *删除点
     */
    public String deleteVertex(_Vertex vertex);


    /**
     * 新增边
     * @param edge
     * @return
     */
    public String addEdges(_Edge edge);


    /**
     * 修改边
     * @param edge
     * @return
     */
    public String changeEdge(_Edge edge);

    /**
     * 删除边
     * @param edge
     * @return
     */
    public String deleteEdge(_Edge edge);
}
