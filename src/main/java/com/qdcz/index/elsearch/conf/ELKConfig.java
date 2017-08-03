package com.qdcz.index.elsearch.conf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by hadoop on 17-5-25.
 */
public class ELKConfig  extends Properties {

    public static String ELKcluster;
    public static String ELKuser;
    public static String ELKpasswd;
    public static String ELKhost;
    public static String ELKports;
    public static String ELKindex;


    @Override
    public synchronized void load(InputStream inStream) throws IOException {
        super.load(inStream);
        ELKcluster = this.getProperty("ELKcluster");
        ELKuser = this.getProperty("ELKuser");
        ELKpasswd = this.getProperty("ELKpasswd");
        ELKhost = this.getProperty("ELKhost");
        ELKports = this.getProperty("ELKports");
        ELKindex = this.getProperty("ELKindex");
    }
}
