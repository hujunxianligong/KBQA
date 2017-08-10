package com.qdcz.conf;

import com.qdcz.common.XMLUtil;
import com.qdcz.entity.Graph;
import com.qdcz.entity.GraphDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * 存储所有的数据库名称
 * Created by star on 17-8-8.
 */
public class DatabaseConfiguration extends Properties{
    public static Map<String,Graph> Graphs;
    @Override
    public synchronized void load(InputStream inStream) throws IOException {
        Graphs = ((GraphDatabase) XMLUtil.convertXmlFileToObject(GraphDatabase.class,inStream)).getGraphs();
    }
}
