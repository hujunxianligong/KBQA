package com.qdcz.other.hanlp;

/**
 * Created by hadoop on 17-7-18.
 */

import com.hankcs.hanlp.HanLP;

import java.util.List;

/**
 * 依存句法分析（神经网络句法模型需要-Xms1g -Xmx1g -Xmn512m）
 * @author hankcs
 */
public class DemoDependencyParser
{
    public static void main(String[] args)

    {
        String content = " 回购协议市场的定义";
        List<String> keywordList = HanLP.extractKeyword(content, 2);
        System.out.println(keywordList.toString());
//        CommonSynonymDictionary.SynonymItem synonymItem = CoreSynonymDictionary.get("职责");
//        List<Synonym> synonymList = synonymItem.synonymList;
//        for(Synonym synonym:synonymList){
//            System.out.println(synonym.getIdString());
//            System.out.println(synonym.getRealWord());
//            System.out.println(synonym.getId());
//            System.out.println( synonym.toString());
//            Synonym.Type type = synonym.type;
//        }
//        Suggester suggester = new Suggester();
//        String[] titleArray =
//                (
//                        "威廉王子发表演说 呼吁保护野生动物\n" +
//                                "《时代》年度人物最终入围名单出炉 普京马云入选\n" +
//                                "“黑格比”横扫菲：菲吸取“海燕”经验及早疏散\n" +
//                                "日本保密法将正式生效 日媒指其损害国民知情权\n" +
//                                "英报告说空气污染带来“公共健康危机”\n"+
//                                "关于银团贷款的定义是什么？\n"+
//                                "代理行负责哪些事情？\n"
//                ).split("\\n");
//        for (String title : titleArray)
//        {
//            suggester.addSentence(title);
//        }
//
//        System.out.println(suggester.suggest("发言", 1));       // 语义
//        System.out.println(suggester.suggest("危机公共", 1));   // 字符
//        System.out.println(suggester.suggest("mayun", 1));      // 拼音
//        System.out.println(suggester.suggest("职责", 1));

//        String content = "程序员(英文Programmer)是从事程序开发、维护的专业人员。一般将程序员分为程序设计人员和程序编码人员，但两者的界限并不非常清楚，特别是在中国。软件从业人员分为初级程序员、高级程序员、系统分析员和项目经理四大类。";
//        List<String> keywordList = HanLP.extractKeyword(content, 10);
//        System.out.println(keywordList);
//        System.out.printf("%-5s\t\t%-5s\t\t%-10s\t\t%-5s\n", "词A", "词B", "语义距离", "语义相似度");
//        for (String a : keywordList)
//        {
//
//            for (String b : keywordList)
//            {
//                System.out.printf("%-5s\t\t%-5s\t%-15d\t%-5.10f\n", a, b, CoreSynonymDictionary.distance(a, b), CoreSynonymDictionary.similarity(a, b));
//            }
//        }
//        //徐先生还具体帮助他确定了把画雄鹰、松鼠和麻雀作为主攻目标。
//        CoNLLSentence sentence = HanLP.parseDependency("股权转让的定义");
//        System.out.println(sentence);
//        // 可以方便地遍历它
//        for (CoNLLWord word : sentence)
//        {
//            System.out.printf("%s --(%s)--> %s\n", word.LEMMA, word.DEPREL, word.HEAD.LEMMA);
//        }
//        // 也可以直接拿到数组，任意顺序或逆序遍历
//        CoNLLWord[] wordArray = sentence.getWordArray();
//        for (int i = wordArray.length - 1; i >= 0; i--)
//        {
//            CoNLLWord word = wordArray[i];
//            System.out.printf("%s --(%s)--> %s\n", word.LEMMA, word.DEPREL, word.HEAD.LEMMA);
//        }
//        // 还可以直接遍历子树，从某棵子树的某个节点一路遍历到虚根
//        CoNLLWord head = wordArray[wordArray.length-1];
//        while ((head = head.HEAD) != null)
//        {
//            if (head == CoNLLWord.ROOT) System.out.println(head.LEMMA);
//            else System.out.printf("%s --(%s)--> ", head.LEMMA, head.DEPREL);
//        }
    }
}