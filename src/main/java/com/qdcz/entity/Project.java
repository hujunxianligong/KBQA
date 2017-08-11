package com.qdcz.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by star on 17-8-11.
 */

@XmlAccessorType(XmlAccessType.FIELD)
//XML文件中的根标识
@XmlRootElement(name = "projects")
class Projects{
    private List<Project> project;

    public List<Project> getProject() {
        return project;
    }

    public void setProjects(List<Project> projects) {
        this.project = projects;
    }
}


@XmlAccessorType(XmlAccessType.FIELD)
//XML文件中的根标识
@XmlRootElement(name = "project")
public class Project {
    private String project_name;
    private ProjectName graphs;

    public ProjectName getGraphs() {
        return graphs;
    }

    public void setGraphs(ProjectName graphs) {
        this.graphs = graphs;
    }

    public String getProject_name() {
        return project_name;
    }

    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }




}


@XmlAccessorType(XmlAccessType.FIELD)
//XML文件中的根标识
@XmlRootElement(name = "graphs")
class ProjectName{
    public List<String> getGraph_name() {
        return graph_name;
    }

    public void setGraph_name(List<String> graph_name) {
        this.graph_name = graph_name;
    }

    private List<String> graph_name;
}