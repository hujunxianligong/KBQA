package com.qdcz.parse;

import com.qdcz.tools.CommonTool;
import org.neo4j.cypher.internal.frontend.v2_3.ast.functions.E;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by hadoop on 17-7-22.
 */
public class BytesToHexString {

    public static final String TYPE_JPG = "jpg";
    public static final String TYPE_GIF = "gif";
    public static final String TYPE_PNG = "png";
    public static final String TYPE_BMP = "bmp";
    public static final String TYPE_UNKNOWN = "unknown";
    public static final String TYPE_DOC = "doc";
    public static final String TYPE_DOCX = "docx";
    public static final String TYPE_XLS = "xls";
    public static final String TYPE_XLSX = "xlsx";
    public static final String TYPE_PDF = "pdf";
    /**
     * byte数组转换成16进制字符串
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static final String OUT_PATH = "/home/hadoop/wnd/usr/leagal/一行三会数据/log/";
    /**
     * 根据文件流判断图片类型
     * @param fis
     * @return jpg/png/gif/bmp
     */
    public static String getPicType(byte[] fis,String filePath) {

        String[] splits=filePath.split("/");
        String filename =splits[splits.length-1].split("\\.")[0];

        String outFilePath=OUT_PATH+filename+".txt";
        //读取文件的前几个字节来判断图片格式
        byte[] b = new byte[4];
        for(int i=0;i<b.length;i++)
        {
            b[i]=fis[i];
        }
        String type = bytesToHexString(b).toUpperCase();
        System.out.println(type);
        if (type.contains("FFD8FF")) {
            return TYPE_JPG;
        } else if (type.contains("89504E47")) {
            return TYPE_PNG;
        } else if (type.contains("47494638")) {
            return TYPE_GIF;
        } else if (type.contains("424D")) {
            return TYPE_BMP;
        }
        else if (type.contains("D0CF11E0")) {
            try {
                byte[] bytes = GetDocumentInfo.GetXls(fis);
                CommonTool.printFile(bytes,outFilePath);

                return TYPE_XLS;
            } catch (Exception e) {
                try {
                    byte[] bytes = GetDocumentInfo.GetDoc(fis);
//                    String s = new String(bytes,"utf-8");
//                    String[] splits1 = s.split("\r");
//                    for(String split:splits1){
//                        System.out.println(split);
//                    }
                    String s=CommonTool.filterOffUtf8Mb4(bytes);
                    CommonTool.printFile(s,outFilePath,false);
                    return TYPE_DOC;
                } catch (Exception e2) {
                    return TYPE_UNKNOWN;
                }
            }

        }
        else if (type.contains("504B0304")) {
            try {
                byte[] bytes = GetDocumentInfo.GetXlsx(fis);
                CommonTool.printFile(bytes,outFilePath);

                return TYPE_XLSX;
            } catch (Exception e) {
                try {
                    byte[] bytes = GetDocumentInfo.getDocx(fis);
                    CommonTool.printFile(bytes,outFilePath);
                    return TYPE_DOCX;
                } catch (Exception e2) {
                    return TYPE_UNKNOWN;
                }
            }
        }
        else if (type.contains("25504446")) {
            try{
                byte[] bytes = GetDocumentInfo.getTextFromPDF(fis);
                CommonTool.printFile(bytes,outFilePath);
            }catch (Exception e){
                System.out.println(filePath);
                e.printStackTrace();
            }


            return TYPE_PDF;
        }else{
            CommonTool.printFile(new String(fis),outFilePath,false);
            return TYPE_UNKNOWN;
        }
    }

    private void exchangeWord(String Path){
        File dirInput = new File(Path);
        File[] files = dirInput.listFiles();
        for (File file: files) {
            if(file.isDirectory()){
                exchangeWord(file.getAbsolutePath());
                continue;
            }
            Long file_length = new Long(file.length());
            byte[] file_buffer = new byte[file_length.intValue()];
            try {
                FileInputStream in = new FileInputStream(file);
                in.read(file_buffer);
                in.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }


            if(file.toString().contains(".")){
                try{
                    getPicType(file_buffer, file.toString());
                }catch (Exception e){
                    System.out.println(file.toString());
                    e.printStackTrace();
                }

            }else{
                String[] splits=file.toString().split("/");
                String filename =splits[splits.length-1];
                if("log".equals(filename)){
                    continue;
                }

                CommonTool.printFile(new String(file_buffer),OUT_PATH+filename,false);
            }



        }
    }
    public static void main(String[] args) throws IOException {
        BytesToHexString bytesToHexString=new BytesToHexString();
        bytesToHexString.exchangeWord("/home/hadoop/wnd/usr/leagal/一行三会数据/1To3");
//        File file= new File("/home/hadoop/wnd/usr/leagal/银监会所有文件/附件5：按照监管公式法计量信用风险缓释作用示例.doc");
//        Long file_length = new Long(file.length());
//        byte[] file_buffer = new byte[file_length.intValue()];
//        try {
//            FileInputStream in = new FileInputStream(file);
//            in.read(file_buffer);
//            in.close();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//            getPicType(file_buffer, file.toString());
//        附件三：新资本协议市场风险框架的修订稿
//        附件5：按照监管公式法计量信用风险缓释作用示例
    }

}

