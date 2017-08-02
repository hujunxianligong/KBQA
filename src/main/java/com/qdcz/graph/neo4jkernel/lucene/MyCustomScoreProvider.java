package com.qdcz.graph.neo4jkernel.lucene;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.queries.CustomScoreProvider;

import java.io.IOException;

/**
 * Created by hadoop on 17-7-22.
 */
public class MyCustomScoreProvider extends CustomScoreProvider {

    public MyCustomScoreProvider(LeafReaderContext context) {
        super(context);
    }

    /**
     * subQueryScore 原有的评分
     * valSrcScore 评分域的分值
     */
    @Override
    public float customScore(int doc, float subQueryScore, float valSrcScore)
            throws IOException {
        return subQueryScore/valSrcScore;
    }

}
