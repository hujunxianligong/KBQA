package com.qdcz.graph.neo4jkernel.lucene;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.queries.CustomScoreProvider;
import org.apache.lucene.queries.function.FunctionQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.queries.CustomScoreQuery;

import java.io.IOException;

/**
 * Created by hadoop on 17-7-22.
 */
@SuppressWarnings("serial")
public class MyCustomScoreQuery extends CustomScoreQuery {

    public MyCustomScoreQuery(Query subQuery, FunctionQuery valSrcQuery) {
        super(subQuery, valSrcQuery);
    }


    protected CustomScoreProvider getCustomScoreProvider(LeafReaderContext context)
            throws IOException {
        //默认情况实现的评分是通过原有的评分*传入进来的评分域所获取的评分来确定最终评分的
        //为了根据不同的需求进行评分，需要自己进行评分的设定
        /**
         * 自定义评分的步骤
         * 创建一个类继承于CustomScoreProvider
         * 覆盖其customScore方法
         */
        return new MyCustomScoreProvider(context);
    }

}
