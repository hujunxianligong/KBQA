package com.qdcz.other.StuctData;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by star on 17-8-24.
 */
public class cutFile {

    public static void main(String[] args) throws FileNotFoundException {
        xx();
    }

    private static void xx() throws FileNotFoundException {
        String[] yearsArr = new String[]{"2012", "2013", "2014", "2015", "2016", "2017"};
        Map<String,List<String>> map = new HashMap<>();


        String filePath = "";
        Scanner scanner = null;
        scanner = new Scanner(new File(filePath));
        while (scanner.hasNext()) {
            String strLine = scanner.nextLine();
            JSONObject jsonObject = new JSONObject(strLine);
            String year = jsonObject.getString("year");
            String quarterStr = jsonObject.getString("quarterStr");
            for (int i = 0; i < yearsArr.length; i++) {

            }
        }
    }
}
