package com.qdcz.other;

import com.qdcz.common.CommonTool;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

/**
 * Created by hadoop on 17-10-11.
 * 统计1行三会法规条数
 */
public class NumStatistLaws {
    public static void main(String[] args) {
        NumStatistLaws instance=new NumStatistLaws();
        File dirInput = new File(args[0]);
        File[] files = dirInput.listFiles();
        int sum=0;
        for (File file: files) {
            System.out.println(file);
//            if(file.toString().contains("决定")){
//                continue;
//            }
//			FileWriter fileWriter = new FileWriter(file);
            int maxNum=0;
            Scanner scanner = null;
            try {
                scanner = new Scanner(file);
                while (scanner.hasNextLine()) {
                    String strLine = scanner.nextLine();
                    int tiaoNum= instance.parse(strLine);
                    if(tiaoNum>maxNum){
                        maxNum=tiaoNum;
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            finally {
                if(scanner!=null){
                    scanner.close();
                }

//				fileWriter.flush();
//				fileWriter.close();
            }
            sum+=maxNum;
            System.out.println("MaxNum:\t"+maxNum);
        }
        System.out.println("Sum:\t"+sum);

    }
    private int parse(String strline){
        int result=0;
        String regex="第[一二一二三四五六七八九十百零]{1,}条";
        Vector<String> all_match = CommonTool.get_all_match(strline, regex);
        if(all_match.size()>0){
            String s = all_match.get(all_match.size() - 1);
            result=chineseNumber2Int(s);
        }
        return result;
    }



    //将中文数字转化为阿拉伯数字
    private static int chineseNumber2Int(String chineseNumber){
        int result = 0;
        int temp = 1;//存放一个单位的数字如：十万
        int count = 0;//判断是否有chArr
        char[] cnArr = new char[]{'一','二','三','四','五','六','七','八','九'};
        char[] chArr = new char[]{'十','百','千','万','亿'};
        for (int i = 0; i < chineseNumber.length(); i++) {
            boolean b = true;//判断是否是chArr
            char c = chineseNumber.charAt(i);
            for (int j = 0; j < cnArr.length; j++) {//非单位，即数字
                if (c == cnArr[j]) {
                    if(0 != count){//添加下一个单位之前，先把上一个单位值添加到结果中
                        result += temp;
                        temp = 1;
                        count = 0;
                    }
                    // 下标+1，就是对应的值
                    temp = j + 1;
                    b = false;
                    break;
                }
            }
            if(b){//单位{'十','百','千','万','亿'}
                for (int j = 0; j < chArr.length; j++) {
                    if (c == chArr[j]) {
                        switch (j) {
                            case 0:
                                temp *= 10;
                                break;
                            case 1:
                                temp *= 100;
                                break;
                            case 2:
                                temp *= 1000;
                                break;
                            case 3:
                                temp *= 10000;
                                break;
                            case 4:
                                temp *= 100000000;
                                break;
                            default:
                                break;
                        }
                        count++;
                    }
                }
            }
            if (i == chineseNumber.length() - 1) {//遍历到最后一个字符
                result += temp;
            }
        }
        return result;
    }
}
