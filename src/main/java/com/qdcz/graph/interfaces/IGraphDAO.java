package com.qdcz.graph.interfaces;

import com.qdcz.graph.entity._Edge;
import com.qdcz.graph.entity._Vertex;

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
