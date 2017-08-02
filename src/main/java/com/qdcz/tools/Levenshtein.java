package com.qdcz.tools;

/**
 * Created by hadoop on 17-4-20.
 */
public class Levenshtein {
    // 字符串相似度匹配
    private int compare(String str, String target) {
        int d[][]; // 矩阵
        int n = str.length();
        int m = target.length();
        int i; // 遍历str的
        int j; // 遍历target的
        char ch1; // str的
        char ch2; // target的
        int temp; // 记录相同字符,在某个矩阵位置值的增量,不是0就是1

        if (n == 0) {
            return m;
        }

        if (m == 0) {
            return n;
        }

        d = new int[n + 1][m + 1];

        for (i = 0; i <= n; i++) { // 初始化第一列
            d[i][0] = i;
        }

        for (j = 0; j <= m; j++) { // 初始化第一行
            d[0][j] = j;
        }

        for (i = 1; i <= n; i++) { // 遍历str
            ch1 = str.charAt(i - 1);
            // 去匹配target
            for (j = 1; j <= m; j++) {
                ch2 = target.charAt(j - 1);
                if (ch1 == ch2) {
                    temp = 0;
                } else {
                    temp = 1;
                }

                // 左边+1,上边+1, 左上角+temp取最小
                d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1]
                        + temp);
            }
        }
//        int min=d[n][0];
//        for(int x=0;x<m;x++){
//            if(d[n][x]<min){
//                min =d[n][x];
//            }
//        }
        return d[n][m];
    }

    //获取两个字符串开始不同字符的位置
    public static int getCharLocation(String str, String target){
        int n = str.length();
        int m = target.length();
        int i; // 遍历str的
        int j; // 遍历target的
        char ch1; // str的
        char ch2; // target的
        int temp=n<m?n:m; // 记录不同字符开始的位置

        if (n == 0||m == 0) {
            return 0;
        }


        for (i = 0; i < n; i++) { // 遍历str
            // 去匹配target
            for (j = 0; j < m&&i<n; j++) {
                ch1 = str.charAt(i);
                ch2 = target.charAt(j);
                if (ch1 == ch2) {
                    i++;
                } else {
                    temp=i;
                    break;
                }
            }
            break;
        }




        return temp;
    }
    private int min(int one, int two, int three) {
        return (one = one < two ? one : two) < three ? one : three;
    }

    /**
     * 获取两字符串的相似度
     *
     * @param str
     * @param target
     *
     * @return
     */

    public float getSimilarityRatio(String str, String target) {
        return 1 - (float) compare(str, target)
                / Math.max(str.length(), target.length());

    }

//    public static void main(String[] args) {
//        Levenshtein lt = new Levenshtein();
//        String str = "申请人的订单变化情况";
//        String target = "担保人的订单变化情况";
//        System.out.println("similarityRatio="
//                + lt.getSimilarityRatio(str, target));
//        System.out.println("Location="
//                + lt.getCharLocation(str, target));
//    }
}
