package com.qdcz.other;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by hadoop on 17-10-9.
 */
public class testRead {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner sc = new Scanner(new File("/mnt/vol_0/neo4j-community-3.2.1/import/vertex.csv"));
        int i=0;
        while(sc.hasNext()){
            String line = sc.nextLine();
            String[] split = line.split(",");
            if(split.length<5){
                System.out.println(line);
            }else{
                if("".equals(split[split.length-1])){
                    System.out.println(line);
                }
            }

//            try{
//                JSONObject objEdge=new JSONObject(line);
//                System.out.println(i++);
//                if(!line.contains("from")){
//                    System.out.println(line);
//                }
//            }catch (Exception e){
//                e.printStackTrace();
//            }

        }
    }
}
