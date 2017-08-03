package com.qdcz.common;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 概念规则匹配处理
 * @author hzy
 *
 */
public class ConceptRuler {
    private static List<ConceptRule> ruler = null;

    public static void main(String[] args) throws Exception {
        test1();
    }

    public static void test2(){
        Pattern pattern = Pattern.compile("何(谓|为)(.*)");
        Matcher matcher = pattern.matcher("何为银团贷款");
        System.out.println(matcher.find()+matcher.group(2));
    }

    public static void test1() throws Exception{
        loadRules();
  //      LineNumberReader reader = new LineNumberReader(new FileReader("test.txt"));
        String line = "何为银团贷款";
  //      while((line=reader.readLine())!=null){
            System.out.println(line+" find matcher...");
            for(ConceptRule rule:ruler){
                //System.out.println(rule.pattern.pattern());
                Matcher matcher = rule.pattern.matcher(line);
                if(matcher.find()){
                    System.out.println(rule.pattern.pattern()+" match:"+matcher.group(rule.index));
                    break;
                }
            }
 //       }
  //      reader.close();
    }
    public static String RegexKey(String question) throws Exception {
        loadRules();
        String result=null;
        String line =question.replaceAll("[。？?.]","");
        System.out.println(line+" find matcher...");
        for(ConceptRule rule:ruler){
            //System.out.println(rule.pattern.pattern());
            Matcher matcher = rule.pattern.matcher(line);
            if(matcher.find()){
                System.out.println(rule.pattern.pattern()+" match:"+matcher.group(rule.index));
                result=matcher.group(rule.index);
                break;
            }
        }
        return result;
    }

    public static void loadRules() throws Exception{
        ruler = new ArrayList<>();
        LineNumberReader reader = new LineNumberReader(new FileReader("/home/hadoop/wnd/concept_rule_simple.txt"));
        String line = null;
        while((line=reader.readLine())!=null){
            //System.out.println(line);
            if(!"".equals(line) && !line.startsWith("#")){
                String[] arr = line.split(" ");
                ruler.add(new ConceptRule(arr[0].trim(), Integer.parseInt(arr[1])+1));
            }
        }
        reader.close();
    }
}

class ConceptRule{
    public Pattern pattern;
    public int index;

    public ConceptRule(String reg, int index){
        this.pattern = Pattern.compile(reg);
        this.index = index;
    }
}
