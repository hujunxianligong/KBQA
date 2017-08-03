package com.qdcz.index.elsearch.elk;

import com.qdcz.index.elsearch.conf.ELKConfig;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient;
import org.elasticsearch.xpack.security.authc.support.SecuredString;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;

import static org.elasticsearch.xpack.security.authc.support.UsernamePasswordToken.basicAuthHeaderValue;

/**
 * Created by star on 17-8-3.
 */
@Service
public class ElasearchClientFactory {
    public static TransportClient create(){
        TransportClient client = null;

        String[] portStr= ELKConfig.ELKports.split(",");
        int portNum=portStr.length;
        int[] port = new int[portNum] ;
        for(int i=0;i<portNum;i++){
            port[i]=Integer.parseInt(portStr[i]);
        }
        try{
            client = new PreBuiltXPackTransportClient(Settings.builder()
                    .put("cluster.name", ELKConfig.ELKcluster)
                    .put("xpack.security.user", ELKConfig.ELKuser+":"+ELKConfig.ELKpasswd)
                    .build())
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ELKConfig.ELKhost), port[0]))
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ELKConfig.ELKhost), port[port.length-1]));
            String token = basicAuthHeaderValue(ELKConfig.ELKuser, new SecuredString(ELKConfig.ELKpasswd.toCharArray()));
            client.filterWithHeader(Collections.singletonMap("Authorization", token))
                    .prepareSearch().get();
        }catch(UnknownHostException e){
            e.printStackTrace();
        }
        return client;
    }


}
