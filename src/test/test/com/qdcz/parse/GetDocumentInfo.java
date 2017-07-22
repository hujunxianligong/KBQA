package com.qdcz.parse;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.pdfbox.pdfparser.PDFParser;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFTextStripper;

/**
 * Created by hadoop on 17-7-22.
 */
public class GetDocumentInfo {

    public static void main(String[] args) {

    }


    public static byte[] getTextFromPDF(byte[] data)
    {

        String result = null;
        InputStream is = new ByteArrayInputStream(data);
        PDDocument document = null;
        try {
            PDFParser parser = new PDFParser(is);
            try {
                parser.parse();
            } catch (Exception e) {
                e.printStackTrace();
                return data;
            }
            document = parser.getPDDocument();
            PDFTextStripper stripper = new PDFTextStripper();
            result = stripper.getText(document);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return result.getBytes();
    }

    public static byte[] GetXlsx(byte[] data ) throws IOException {
        StringBuffer sb = new StringBuffer();

        Workbook wb = null;
        wb = new XSSFWorkbook(new ByteArrayInputStream(data));// 解析xlsx格式
        Sheet sheet = wb.getSheetAt(0);// 第一个工作表

        int firstRowIndex = sheet.getFirstRowNum();
        int lastRowIndex = sheet.getLastRowNum();

        for (int rIndex = firstRowIndex; rIndex <= lastRowIndex; rIndex++) {
            Row row = sheet.getRow(rIndex);
            if (row != null) {
                int firstCellIndex = row.getFirstCellNum();
                int lastCellIndex = row.getLastCellNum();
                for (int cIndex = firstCellIndex; cIndex < lastCellIndex; cIndex++) {
                    Cell cell = row.getCell(cIndex);
                    String value = "";
                    if (cell != null) {
                        value = cell.toString();
                        sb.append(value + " ");
                    }
                }
                sb.append("\n");
            }
        }
        wb=null;

        String temp=sb.toString();

        return temp.getBytes();
    }

    public static byte[] GetDoc(byte[] data ) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        String result = "";
        try {
            HWPFDocument doc = new HWPFDocument(bais);
            Range rang = doc.getRange();
            result += rang.text();
            bais.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        bais.close();
        return result.getBytes();
    }

    public static byte[] getDocx(byte[] data ) throws IOException {

        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        XWPFDocument doc = null;
        try {
            doc = new XWPFDocument(bais);
        } catch (IOException e) {
            e.printStackTrace();
        }
        XWPFWordExtractor extractor = new XWPFWordExtractor(doc);
        String text = extractor.getText();
        doc=null;
        extractor=null;
        bais.close();
        return text.getBytes();
    }

    public static byte[] GetXls(byte[] data ) throws IOException {

        POIFSFileSystem poifsFileSystem = null;
        HSSFWorkbook hssfWorkbook = null;
        poifsFileSystem = new POIFSFileSystem(new ByteArrayInputStream(data));
        hssfWorkbook = new HSSFWorkbook(poifsFileSystem);
        HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);
        StringBuffer sb = new StringBuffer();
        int firstRowIndex = hssfSheet.getFirstRowNum();
        int lastRowIndex = hssfSheet.getLastRowNum();
        for (int rIndex = firstRowIndex; rIndex <= lastRowIndex; rIndex++) {
            Row row = hssfSheet.getRow(rIndex);
            if (row != null) {
                int firstCellIndex = row.getFirstCellNum();
                int lastCellIndex = row.getLastCellNum();
                for (int cIndex = firstCellIndex; cIndex < lastCellIndex; cIndex++) {
                    Cell cell = row.getCell(cIndex);
                    String value = "";
                    if (cell != null) {
                        value = cell.toString();
                        sb.append(value + " ");
                    }
                }
                sb.append("\n");
            }
        }
        hssfWorkbook=null;
        poifsFileSystem=null;
        String tmp=sb.toString();
        return tmp.getBytes();
    }
}
