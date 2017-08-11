package com.qdcz.entity;


import com.qdcz.common.XMLUtil;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlAccessorType(XmlAccessType.FIELD)  
//XML文件中的根标识  
@XmlRootElement(name = "xml")
public class GraphDatabase {
    public static void main(String[] args) {

        GraphDatabase graphDatabase= (GraphDatabase) XMLUtil.convertXmlFileToObject(GraphDatabase.class,"/media/star/Soft/WorkSpace/KBQA/src/main/resources/dev/database.xml");
        System.out.println(XMLUtil.convertToXml(graphDatabase));
    }

    private Graphs graphs;

    private Projects projects;



    public Projects getProjects() {
        return projects;
    }

    public void setProjects(Projects projects) {
        this.projects = projects;
    }


    public Graphs getGraphs() {
        return graphs;
    }

    public void setGraphs(Graphs graphs) {
        this.graphs = graphs;
    }


    /**
     * 获取graphmap,便于检索
     * @return
     */
    public Map<String,Graph> getGraphMap(){
        Map<String,Graph> graphMap =  new HashMap<>();

        for (Graph graph:graphs.getGraph()) {
            graphMap.put(graph.getName(),graph);
        }
        return graphMap;
    }

    /**
     * 获取projectmap，便于检索
     * @return
     */
    public Map<String,List<Graph>> getProjectMap(){
        Map<String,List<Graph>> projectMap =  new HashMap<>();

        for (Project project:projects.getProject()) {
            String project_name = project.getProject_name();
            List<Graph> project_graphs = new ArrayList<>();
            for (String graphName:project.getGraphs().getGraph_name()) {
                for (Graph graph:graphs.getGraph()) {
                    if(graphName.equals(graph.getName())){
                        project_graphs.add(graph);
                        break;
                    }
                }
            }
            projectMap.put(project_name,project_graphs);

        }
        return projectMap;
    }




}
