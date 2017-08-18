package com.qdcz.other;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by star on 17-8-18.
 */
public class JustTest {
    public static void main(String[] args) {
        String mm = "柳工机械中东有限公司  2014年 12月 29日  4,270  一般保证 一年 否 是 ";


        System.out.println(replaceAllDataSpace(mm));
    }

    public static String replaceAllDataSpace(String text){
        Set<String> set = getAllMatch("\\d{4}年\\s+\\d{1,2}月\\s+\\d{1,2}日",text);
        for (String one: set) {
            text = text.replace(one,one.replaceAll("\\s+",""));
        }
        return text;
    }

    public static Set getAllMatch(String reg,String str){
        Set<String> set =  new HashSet<>();
        Pattern p=Pattern.compile(reg);
        Matcher m=p.matcher(str);
        while(m.find()) {
            set.add(m.group());
        }
        return set;
    }
}
