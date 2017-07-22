package com.qdcz.neo4jkernel.lucene;

/**
 * Created by hadoop on 17-7-22.
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.SmallFloat;

public class BM25Similarity extends Similarity
{
    private static class BM25Stats extends Similarity.SimWeight
    {

        public float getValueForNormalization()
        {
            float queryWeight = idf.getValue() * queryBoost;
            return queryWeight * queryWeight;
        }

        public void normalize(float queryNorm, float topLevelBoost)
        {
            this.topLevelBoost = topLevelBoost;
            weight = idf.getValue() * queryBoost * topLevelBoost;
        }

        private final Explanation idf;
        private final float avgdl;
        private final float queryBoost;
        private float topLevelBoost;
        private float weight;
        private final String field;
        private final float cache[];

        BM25Stats(String field, Explanation idf, float queryBoost, float avgdl, float cache[])
        {
            this.field = field;
            this.idf = idf;
            this.queryBoost = queryBoost;
            this.avgdl = avgdl;
            this.cache = cache;
        }
    }

    private class BM25DocScorer extends Similarity.SimScorer
    {

        public float score(int doc, float freq)
        {
            float norm = norms != null ? cache[(byte)(int)norms.get(doc) & 255] : k1;
            return (weightValue * freq) / (freq + norm);
        }

        public Explanation explain(int doc, Explanation freq)
        {
            return explainScore(doc, freq, stats, norms);//这是最终打分函数
        }

        public float computeSlopFactor(int distance)
        {
            return sloppyFreq(distance);
        }

        public float computePayloadFactor(int doc, int start, int end, BytesRef payload)
        {
            return scorePayload(doc, start, end, payload);
        }

        private final BM25Stats stats;
        private final float weightValue;
        private final NumericDocValues norms;
        private final float cache[];
        final BM25Similarity this0;
        BM25DocScorer(BM25Stats stats, NumericDocValues norms)       throws IOException
        {
            this0 = BM25Similarity.this;

            this.stats = stats;
            weightValue = stats.weight * (k1 + 1.0F);
            cache = stats.cache;
            this.norms = norms;
        }

    }


    public BM25Similarity(float k1, float b)
    {
        discountOverlaps = true;
        this.k1 = k1;
        this.b = b;
    }

    public BM25Similarity()
    {
        discountOverlaps = true;
        k1 = 1.2F;
        b = 0.75F;
    }

    protected float idf(long docFreq, long numDocs)
    {
        return (float)Math.log(1.0D + ((double)(numDocs - docFreq) + 0.5D) / ((double)docFreq + 0.5D));
    }

    protected float sloppyFreq(int distance)
    {
        return 1.0F / (float)(distance + 1);
    }

    protected float scorePayload(int doc, int start, int end, BytesRef bytesref)
    {
        return 1.0F;
    }

    protected float avgFieldLength(CollectionStatistics collectionStats)
    {
        long sumTotalTermFreq = collectionStats.sumTotalTermFreq();
        if(sumTotalTermFreq <= 0L)
            return 1.0F;
        else
            return (float)((double)sumTotalTermFreq / (double)collectionStats.maxDoc());
    }

    protected byte encodeNormValue(float boost, int fieldLength)
    {
        return SmallFloat.floatToByte315(boost / (float)Math.sqrt(fieldLength));
    }

    protected float decodeNormValue(byte b)
    {
        return NORM_TABLE[b & 255];
    }

    public void setDiscountOverlaps(boolean v)
    {
        discountOverlaps = v;
    }

    public boolean getDiscountOverlaps()
    {
        return discountOverlaps;
    }

    public final long computeNorm(FieldInvertState state)
    {
        int numTerms = discountOverlaps ? state.getLength() - state.getNumOverlap() : state.getLength();
        return (long)encodeNormValue(state.getBoost(), numTerms);
    }



    public Explanation idfExplain(CollectionStatistics collectionStats, TermStatistics termStats)
    {
        long df = termStats.docFreq();
        long max = collectionStats.maxDoc();
        float idf = idf(df, max);

        return Explanation.match(idf, (new StringBuilder()).append("idf(docFreq=").append(df).append(", maxDocs=").append(max).append(")").toString());
    }

    public Explanation idfExplain(CollectionStatistics collectionStats, TermStatistics termStats[])
    {
        long max = collectionStats.maxDoc();
        float idf = 0.0F;
        Explanation exp = null;
        Collection<Explanation> matchs = new ArrayList<Explanation>();
        TermStatistics arr[]= termStats;
        int len =arr.length;
        for(int i=0;i<len;i++){
            TermStatistics stat=arr[i];
            long df = stat.docFreq();
            float termIdf = idf(df, max);
            Explanation match = Explanation.match(termIdf, (new StringBuilder()).append("idf(docFreq=").append(df).append(", maxDocs=").append(max).append(")").toString());
            matchs.add(match);
            idf += termIdf;
         }
        exp= Explanation.match(idf,"idf(), sum of:",matchs);
        return exp;
    }
    @Override
    public SimWeight computeWeight(CollectionStatistics collectionStatistics, TermStatistics... termStatisticss) {
        Explanation idf = termStatisticss.length != 1 ? idfExplain(collectionStatistics, termStatisticss) : idfExplain(collectionStatistics, termStatisticss[0]);
        float avgdl = avgFieldLength(collectionStatistics);
        float cache[] = new float[256];
        for(int i = 0; i < cache.length; i++)
            cache[i] = k1 * ((1.0F - b) + (b * decodeNormValue((byte)i)) / avgdl);

        return new BM25Stats(collectionStatistics.field(), idf, 1.0f, avgdl, cache);
    }

    @Override
    public SimScorer simScorer(SimWeight simWeight, LeafReaderContext leafReaderContext) throws IOException {
        BM25Stats bm25stats = (BM25Stats)simWeight;
        return new BM25DocScorer(bm25stats, leafReaderContext.reader().getNormValues(bm25stats.field));
    }


    private Explanation explainScore(int doc, Explanation freq, BM25Stats stats, NumericDocValues norms)
    {
        Explanation boostExpl = Explanation.match(stats.queryBoost * stats.topLevelBoost, "boost");
        Explanation result = Explanation.noMatch((new StringBuilder()).append("score(doc=").append(doc).append(",freq=").append(freq).append("), product of:").toString());

        if(boostExpl.getValue() != 1.0F)
            result=Explanation.noMatch((new StringBuilder()).append("score(doc=").append(doc).append(",freq=").append(freq).append("), product of:").toString(),boostExpl,stats.idf);
        Explanation tfNormExpl = Explanation.noMatch("tfNorm, computed from:",freq,Explanation.match(k1, "parameter k1"));
        if(norms == null)
        {
            tfNormExpl =Explanation.match((freq.getValue() * (k1 + 1.0F)) / (freq.getValue() + k1),"tfNorm, computed from:",freq,Explanation.match(k1, "parameter k1"),Explanation.match(0.0F, "parameter b (norms omitted for field)"));
        } else
        {

            float doclen = decodeNormValue((byte)(int)norms.get(doc));
            Explanation match = Explanation.match(b, "parameter b");

            Explanation avgFieldLength = Explanation.match(stats.avgdl, "avgFieldLength");

            Explanation fieldLength = Explanation.match(doclen, "fieldLength");

            tfNormExpl =Explanation.match((freq.getValue() * (k1 + 1.0F)) / (freq.getValue() + k1 * ((1.0F - b) + (b * doclen) / stats.avgdl)),"tfNorm, computed from:",freq,Explanation.match(k1, "parameter k1"),match,avgFieldLength,fieldLength);

        }

        result =Explanation.match(boostExpl.getValue() * stats.idf.getValue() * tfNormExpl.getValue(),(new StringBuilder()).append("score(doc=").append(doc).append(",freq=").append(freq).append("), product of:").toString(),boostExpl,stats.idf,tfNormExpl);
        return result;
    }

    public String toString()
    {
        return (new StringBuilder()).append("BM25(k1=").append(k1).append(",b=").append(b).append(")").toString();
    }

    public float getK1()
    {
        return k1;
    }

    public float getB()
    {
        return b;
    }

    private final float k1;
    private final float b;
    protected boolean discountOverlaps;
    private static final float NORM_TABLE[];

    static
    {
        NORM_TABLE = new float[256];
        for(int i = 0; i < 256; i++)
        {
            float f = SmallFloat.byte315ToFloat((byte)i);
            NORM_TABLE[i] = 1.0F / (f * f);
        }

    }

}