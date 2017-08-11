package com.qdcz.conf;

import com.qdcz.common.XMLUtil;
import com.qdcz.entity.Graph;
import com.qdcz.entity.GraphDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 存储所有的数据库名称
 * Created by star on 17-8-8.
 */
public class DatabaseConfiguration extends Properties{
    public static Map<String,Graph> graphs;
    public static Map<String,List<Graph>> projects;

    @Override
    public synchronized void load(InputStream inStream) throws IOException {
        GraphDatabase graphDatabase = ((GraphDatabase) XMLUtil.convertXmlFileToObject(GraphDatabase.class,inStream));
        graphs = graphDatabase.getGraphMap();
        projects = graphDatabase.getProjectMap();
    }


    public static String getRelationshipByLabel(String label) throws Exception {
        for (Graph graph:graphs.values()){
            if(label.equals(graph.getLabel())){
                return graph.getRelationship();
            }
        }
        throw new Exception("未找到对应的边库"+label);
    }

    public static String getLabelByRelationship(String relationship) throws Exception {
        for (Graph graph:graphs.values()){
            if(relationship.equals(graph.getRelationship())){
                return graph.getLabel();
            }
        }
        throw new Exception("未找到对应的点库"+relationship);
    }


    public static Graph getGraph(String graphName) throws Exception {
        if(graphs.containsKey(graphName)){
            return graphs.get(graphName);
        }
        throw new Exception("未找到对应的图谱Graph"+graphName);
    }


    public static List<Graph> getProject(String projectName) throws Exception {
        if(projects.containsKey(projectName)){
            return projects.get(projectName);
        }
        throw new Exception("未找到对应的工程project："+projectName);
    }

}
