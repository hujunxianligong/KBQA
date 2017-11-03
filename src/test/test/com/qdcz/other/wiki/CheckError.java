package com.qdcz.other.wiki;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by star on 17-10-9.
 */
public class CheckError {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(new File("/media/star/Doc/工作文档/wiki图项目/vertex.csv"));
        int m = 0;
        while (sc.hasNext()){
            String line =  sc.nextLine();
            String[]  splits = line.split(",");
            System.out.println(m++);
            if(splits.length!=5){
                System.out.println(line);
                break;
            }
        }
        sc.close();

    }
}
