package com.qdcz.entity;


import com.qdcz.common.XMLUtil;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlAccessorType(XmlAccessType.FIELD)  
//XML文件中的根标识  
@XmlRootElement(name = "xml")
public class GraphDatabase {
    public static void main(String[] args) {
//        Graph graph = new Graph();
//        graph.setName("dfd");
//
//        Map<String,Graph> graphs = new HashMap<>();
//        graphs.put("mm",graph);
//
//        GraphDatabase graphDatabase =  new GraphDatabase();
//        graphDatabase.setGraphs(graphs);

        GraphDatabase graphDatabase= (GraphDatabase) XMLUtil.convertXmlFileToObject(GraphDatabase.class,"/media/star/Soft/WorkSpace/KBQA/src/main/resources/database.xml");
        System.out.println(XMLUtil.convertToXml(graphDatabase));
    }

    private Map<String,Graph> graphs;

    public Map<String, Graph> getGraphs() {
        return graphs;
    }

    public void setGraphs(Map<String, Graph> graphs) {
        this.graphs = graphs;
    }


    //    private List<Graph> graph;
//
//    public List<Graph> getGraph() {
//        return graph;
//    }
//
//    public void setGraph(List<Graph> graph) {
//        this.graph = graph;
//    }
}
