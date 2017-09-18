package com.qdcz.other.parse;

import jxl.Cell;
import jxl.Range;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.*;
import jxl.format.VerticalAlignment;
import jxl.read.biff.BiffException;
import jxl.write.*;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by hadoop on 17-8-30.
 */
public class CreateParse {
    public static void main(String[] args) throws WriteException, IOException, BiffException {
        CreateParse createParse = new CreateParse();
        createParse.testCreate();

    }
    public  void testCreate (){
        try {
// 打开文件
            WritableWorkbook book = Workbook.createWorkbook( new File( "/home/hadoop/wnd/usr/xlsx/test.xls" ));
// 生成名为“第一页”的工作表，参数0表示这是第一页
            WritableSheet sheet = book.createSheet( " 第一页 " , 0 );
// 在Label对象的构造子中指名单元格位置是第一列第一行(0,0)
// 以及单元格内容为test
            Label label = new Label( 0 , 0 , " test " );
// 将定义好的单元格添加到工作表中
            sheet.addCell(label);
        //    jxl.write.Number number = new jxl.write.Number( 1 , 0 , 555.12541 );
        //    sheet.addCell(number);
// 写入数据并关闭文件
            book.write();
            book.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
