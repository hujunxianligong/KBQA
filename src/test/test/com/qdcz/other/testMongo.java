package com.qdcz.other;

import com.qdcz.conf.LoadConfigListener;
import com.qdcz.mongo.MyMongo;
import com.qdcz.mongo.conf.MongoConfiguration;
import org.json.JSONArray;

import java.io.IOException;

/**
 * Created by hadoop on 17-8-29.
 */
public class testMongo {
    public static void main(String[] args) throws IOException {

        MongoConfiguration mongoConf = new MongoConfiguration();
        mongoConf.load(LoadConfigListener.class.getResourceAsStream("/dev/mongo.properties"));
        MyMongo myMongo=new MyMongo("Trade","businessinfo");
        JSONArray onePage = myMongo.getOnePage(1, 2);
        System.out.println(onePage);

    }
}
