package com.arun.jenkins.jenkins.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.springframework.util.StringUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

@Slf4j
public class MyExcelUtils {

    private final static String EXCEL2003 = "xls";
    private final static String EXCEL2007 = "xlsx";
    private final static List<String> key = Arrays.asList("entName", "socialNo", "industryphy", "historyDate");

    public static HSSFWorkbook createExcel(String[] headerName, String[] headerKey, String sheetName, List dataList) {
        try {
            if (headerKey.length <= 0) {
                return null;
            }
            if (dataList.size() <= 0) {
                return null;
            }
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet;
            if ((sheetName == null) || (sheetName.equals(""))) {
                sheet = wb.createSheet("Sheet1");
            } else {
                sheet = wb.createSheet(sheetName);
            }
            HSSFRow row = sheet.createRow(0);
            HSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(HorizontalAlignment.forInt((short) 2));
            HSSFCell cell = null;
            if (headerName.length > 0) {
                for (int i = 0; i < headerName.length; i++) {
                    cell = row.createCell(i);
                    cell.setCellValue(headerName[i]);
                    cell.setCellStyle(style);
                }
            }
            int n = 0;
            HSSFCellStyle contextstyle = wb.createCellStyle();
            contextstyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00_);(#,##0.00)"));

            HSSFCellStyle contextstyle1 = wb.createCellStyle();
            HSSFDataFormat format = wb.createDataFormat();
            contextstyle1.setDataFormat(format.getFormat("@"));

            HSSFCell cell0 = null;
            HSSFCell cell1 = null;

            for (Iterator localIterator = dataList.iterator(); localIterator.hasNext(); ) {
                Object obj = localIterator.next();
                Field[] fields = obj.getClass().getDeclaredFields();
                row = sheet.createRow(n + 1);
                for (int j = 0; j < headerKey.length; j++) {
                    if (headerName.length <= 0) {
                        cell0 = row.createCell(j);
                        cell0.setCellValue(headerKey[j]);
                        cell0.setCellStyle(style);

                    }
                    for (int i = 0; i < fields.length; i++) {
                        if (fields[i].getName().equals(headerKey[j])) {
                            fields[i].setAccessible(true);
                            if (fields[i].get(obj) == null) {
                                row.createCell(j).setCellValue("");
                                break;
                            }
                            if ((fields[i].get(obj) instanceof Number)) {
                                cell1 = row.createCell(j);
                                cell1.setCellType(CellType.forInt(0));
                                cell1.setCellStyle(contextstyle);
                                cell1.setCellValue(Double.parseDouble(fields[i].get(obj).toString()));
                                break;
                            }
                            if ("".equals(fields[i].get(obj))) {
                                cell1 = row.createCell(j);
                                cell1.setCellStyle(contextstyle1);
                                row.createCell(j).setCellValue("");
                                cell1.setCellType(CellType.forInt(1));
                                break;
                            }
                            row.createCell(j).setCellValue(fields[i].get(obj).toString());
                            break;
                        }

                    }
                }
                n++;
            }
            for (int i = 0; i < headerKey.length; i++) {
                sheet.setColumnWidth(i, headerKey[i].getBytes().length * 2 * 256);
            }
            HSSFWorkbook localHSSFWorkbook1 = wb;
            return localHSSFWorkbook1;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {

        }
    }

    //excel2003版
    public static HSSFWorkbook createExcel(List<String> headerName, List<String> headerKey, String sheetName, List<Map<String, String>> maps) {
        try {
            if (headerKey.size() <= 0) {
                return null;
            }
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet;
            if ((sheetName == null) || (sheetName.equals(""))) {
                sheet = wb.createSheet("Sheet1");
            } else {
                sheet = wb.createSheet(sheetName);
            }
            HSSFRow row = sheet.createRow(0);
            HSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(HorizontalAlignment.forInt((short) 2));
            HSSFCell cell = null;
            if (headerName.size() > 0) {
                for (int i = 0; i < headerName.size(); i++) {
                    cell = row.createCell(i);
                    cell.setCellValue(headerName.get(i));
                    cell.setCellStyle(style);
                }
            }
            int n = 0;
            HSSFCellStyle contextstyle = wb.createCellStyle();
            contextstyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00_);(#,##0.00)"));

            HSSFCellStyle contextstyle1 = wb.createCellStyle();
            HSSFDataFormat format = wb.createDataFormat();
            contextstyle1.setDataFormat(format.getFormat("@"));

            HSSFCell cell0 = null;
            HSSFCell cell1 = null;

            List<String> strings = new ArrayList<>();
            Set<String> keys = maps.get(0).keySet();
            for (String key : keys) {
                strings.add(key);
            }

            for (Map<String, String> map : maps) {
                row = sheet.createRow(n + 1);
                for (int j = 0; j < headerKey.size(); j++) {
                    if (headerName.size() <= 0) {
                        cell0 = row.createCell(j);
                        cell0.setCellValue(headerKey.get(j));
                        cell0.setCellStyle(style);
                    }
                    for (int i = 0; i < strings.size(); i++) {
                        if (strings.get(i).equals(headerKey.get(j))) {
                            if (map.get(strings.get(i)) == null) {
                                row.createCell(j).setCellValue("暂无数据");
                                break;
                            }
                            if ("".equals(map.get(strings.get(i)))) {
                                cell1 = row.createCell(j);
                                cell1.setCellStyle(contextstyle1);
                                row.createCell(j).setCellValue("暂无数据");
                                cell1.setCellType(CellType.forInt(1));
                                break;
                            }
                            row.createCell(j).setCellValue(map.get(strings.get(i)));
                            break;
                        }

                    }
                }
                n++;
            }
            for (int i = 0; i < headerKey.size(); i++) {
                sheet.setColumnWidth(i, headerKey.get(i).getBytes().length * 2 * 256);
            }
            HSSFWorkbook localHSSFWorkbook1 = wb;
            return localHSSFWorkbook1;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {

        }
    }

    public static HSSFWorkbook createExcel(List<String> headerName, List<String> headerKey, String sheetName, List<Map<String, String>> maps, List<Integer> integers, List<String> indexKeys) {
        try {
            if (headerKey.size() <= 0) {
                return null;
            }
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet;
            if ((sheetName == null) || (sheetName.equals(""))) {
                sheet = wb.createSheet("Sheet1");
            } else {
                sheet = wb.createSheet(sheetName);
            }
            HSSFRow row = sheet.createRow(0);
            HSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(HorizontalAlignment.forInt((short) 2));
            HSSFCell cell = null;
            if (headerName.size() > 0) {
                for (int i = 0; i < headerName.size(); i++) {
                    cell = row.createCell(i);
                    cell.setCellValue(headerName.get(i));
                    cell.setCellStyle(style);
                }
            }
            int n = 0;
            HSSFCellStyle contextstyle = wb.createCellStyle();
            contextstyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00_);(#,##0.00)"));

            HSSFCellStyle contextstyle1 = wb.createCellStyle();
            HSSFDataFormat format = wb.createDataFormat();
            contextstyle1.setDataFormat(format.getFormat("@"));

            HSSFCell cell0 = null;
            HSSFCell cell1 = null;

            List<String> strings = new ArrayList<>();
            Set<String> keys = maps.get(0).keySet();
            for (String key : keys) {
                strings.add(key);
            }

            for (Map<String, String> map : maps) {
                row = sheet.createRow(n + 1);
                for (int j = 0; j < headerKey.size(); j++) {
                    if (headerName.size() <= 0) {
                        cell0 = row.createCell(j);
                        cell0.setCellValue(headerKey.get(j));
                        cell0.setCellStyle(style);
                    }
                    for (int i = 0; i < strings.size(); i++) {

                        if (strings.get(i).equals(headerKey.get(j))) {
                            if (map.get(strings.get(i)) == null) {
                                row.createCell(j).setCellValue("暂无数据");
                                break;
                            }
                            if ("".equals(map.get(strings.get(i)))) {
                                cell1 = row.createCell(j);
                                cell1.setCellStyle(contextstyle1);
                                row.createCell(j).setCellValue("暂无数据");
                                cell1.setCellType(CellType.forInt(1));
                                break;
                            }
                            row.createCell(j).setCellValue(map.get(strings.get(i)));
                            break;
                        }

                    }
                }
                n++;
            }
            for (int i = 0; i < headerKey.size(); i++) {
                sheet.setColumnWidth(i, headerKey.get(i).getBytes().length * 2 * 256);
            }
            addMergedRegionUtil(sheet, integers, headerKey, indexKeys);
            //设置水平居中
            style.setAlignment(HorizontalAlignment.CENTER);
            //设置垂直居中
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            cell.setCellStyle(style);
            if (cell0 != null) {
                cell0.setCellStyle(style);
            }
            if (cell1 != null) {
                cell1.setCellStyle(style);
            }
            HSSFWorkbook localHSSFWorkbook1 = wb;
            return localHSSFWorkbook1;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {

        }
    }

    //传入头名字，传入每列的key值，传入对象名称，传入插入的数据，传入从上至下需要合并单元格的行数，合并的列
    public static HSSFWorkbook createExcel(String[] headerName, String[] headerKey, String sheetName, List dataList, List<Integer> totals, String[] keys) {
        try {
            if (headerKey.length <= 0) {
                return null;
            }
            /*if (dataList.size() <= 0) {
                return null;
            }*/
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet;
            if ((sheetName == null) || (sheetName.equals(""))) {
                sheet = wb.createSheet("Sheet1");
            } else {
                sheet = wb.createSheet(sheetName);
            }
            HSSFRow row = sheet.createRow(0);
            HSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(HorizontalAlignment.forInt((short) 2));
            HSSFCell cell = null;
            if (headerName.length > 0) {
                for (int i = 0; i < headerName.length; i++) {
                    cell = row.createCell(i);
                    cell.setCellValue(headerName[i]);
                    cell.setCellStyle(style);
                }
            }
            int n = 0;
            HSSFCellStyle contextstyle = wb.createCellStyle();
            contextstyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00_);(#,##0.00)"));

            HSSFCellStyle contextstyle1 = wb.createCellStyle();
            HSSFDataFormat format = wb.createDataFormat();
            contextstyle1.setDataFormat(format.getFormat("@"));

            HSSFCell cell0 = null;
            HSSFCell cell1 = null;

            for (Iterator localIterator = dataList.iterator(); localIterator.hasNext(); ) {
                Object obj = localIterator.next();
                Field[] fields = obj.getClass().getDeclaredFields();
                row = sheet.createRow(n + 1);
                for (int j = 0; j < headerKey.length; j++) {
                    if (headerName.length <= 0) {
                        cell0 = row.createCell(j);
                        cell0.setCellValue(headerKey[j]);
                        cell0.setCellStyle(style);

                    }
                    for (int i = 0; i < fields.length; i++) {
                        if (fields[i].getName().equals(headerKey[j])) {
                            fields[i].setAccessible(true);
                            if (fields[i].get(obj) == null) {
                                row.createCell(j).setCellValue("");
                                break;
                            }
                            if ((fields[i].get(obj) instanceof Number)) {
                                cell1 = row.createCell(j);
                                cell1.setCellType(CellType.forInt(0));
                                cell1.setCellStyle(contextstyle);
                                cell1.setCellValue(Double.parseDouble(fields[i].get(obj).toString()));
                                break;
                            }
                            if ("".equals(fields[i].get(obj))) {
                                cell1 = row.createCell(j);
                                cell1.setCellStyle(contextstyle1);
                                row.createCell(j).setCellValue("");
                                cell1.setCellType(CellType.forInt(1));
                                break;
                            }
                            row.createCell(j).setCellValue(fields[i].get(obj).toString());
                            break;
                        }

                    }
                }
                n++;
            }
            for (int i = 0; i < headerKey.length; i++) {
                sheet.setColumnWidth(i, headerKey[i].getBytes().length * 2 * 256);
            }
            addMergedRegionUtil(sheet, totals, keys, headerKey);
            HSSFWorkbook localHSSFWorkbook1 = wb;
            return localHSSFWorkbook1;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {

        }
    }

    public static void addMergedRegionUtil(HSSFSheet sheet, List<Integer> totals, String[] key, String[] StringKey) {
        int overRow;
        int colrow = 0;
        for (int j = 0; j < key.length; j++) {
            Integer firstRow = 1;
            for (int k = 0; k < StringKey.length; k++) {
                if (StringKey[k].equals(key[j])) {
                    colrow = k;
                    for (int i = 0; i < totals.size(); i++) {
                        overRow = firstRow + totals.get(i) - 1;
                        if (firstRow != overRow) {
                            System.out.println(firstRow + "，" + overRow + "，" + colrow + "，" + colrow);
                            //CellRangeAddress region = new CellRangeAddress(firstRow, overRow, colrow, colrow);
                            //sheet.addMergedRegion(region);
                        }
                        firstRow = firstRow + totals.get(i);
                    }
                }
            }
        }
    }


    public static void addMergedRegionUtil(HSSFSheet sheet, List<Integer> totals, List<String> key, List<String> StringKey) {
        int overRow;
        int colrow = 0;
        for (int j = 0; j < key.size(); j++) {
            Integer firstRow = 1;
            for (int k = 0; k < StringKey.size(); k++) {
                if (StringKey.get(k).equals(key.get(j))) {
                    colrow = j;
                    for (int i = 0; i < totals.size(); i++) {
                        overRow = firstRow + totals.get(i) - 1;
                        if (firstRow != overRow) {
                            System.out.println("maps" + firstRow + "，" + overRow + "，" + colrow + "，" + colrow);
                            CellRangeAddress region = new CellRangeAddress(firstRow, overRow, colrow, colrow);
                            sheet.addMergedRegion(region);
                        }
                        firstRow = firstRow + totals.get(i);
                    }
                }
            }
        }
    }

    //创建表头sheet
    public static HSSFWorkbook createHeadExcel(HSSFWorkbook workbook, List<String> headName1, List<String> headName2, List<Integer> mergedRegion) {
        Integer rowCount = 0;
        Sheet sheet = workbook.createSheet("sheet");
        Row row = sheet.createRow(rowCount++);
        for (int i = 0; i < mergedRegion.size(); i++) {
            if (i == 0) {
                Cell cell = row.createCell(i);
                cell.setCellValue(headName1.get(i));
            }
            if (i == 1) {
                Cell cell = row.createCell(mergedRegion.get(i - 1));
                cell.setCellValue(headName1.get(i));
            }
            if (i > 1) {
                int before = 0;
                for (int index = 0; index < i; index++) {
                    before += mergedRegion.get(index);
                }
                Cell cell = row.createCell(before + mergedRegion.get(i) - 1);
                cell.setCellValue(headName1.get(i));
            }
        }
        for (int i = 0; i < mergedRegion.size(); i++) {
            CellRangeAddress region;
            if (i == 0) {
                System.out.println("maps" + 0 + "，" + 0 + "，" + 0 + "，" + (mergedRegion.get(i) - 1));
                region = new CellRangeAddress(0, 0, 0, (mergedRegion.get(i) - 1));
                sheet.addMergedRegion(region);
            } else {
                int before = 0;
                for (int index = 0; index < i; index++) {
                    before += mergedRegion.get(index);
                }
                System.out.println("maps" + 0 + "，" + 0 + "，" + before + "，" + (before + mergedRegion.get(i) - 1));
                region = new CellRangeAddress(0, 0, before, (before + mergedRegion.get(i)) - 1);
                if (before != (before + mergedRegion.get(i) - 1)) {
                    sheet.addMergedRegion(region);
                }
            }
        }
        Row row2 = sheet.createRow(rowCount++);
        for (int i = 0; i < headName2.size(); i++) {
            Cell cell = row2.createCell(i);
            cell.setCellValue(headName2.get(i));
        }
        return workbook;
    }

    public static HSSFWorkbook addExcelData(HSSFWorkbook workbook, List<Map<String, Object>> maps, List<String> keys) {
        Sheet sheet = workbook.getSheet("sheet");
        int beginRow = sheet.getLastRowNum() + 1;
        for (int i = 0; i < maps.size(); i++) {
            Row row = sheet.createRow(i + beginRow);
            for (int j = 0; j < keys.size(); j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue(maps.get(i).get(keys.get(j)) + "");
            }
        }
        return workbook;
    }

    public static HSSFWorkbook addExcelSheetData(List<String> headerName, List<String> headerKey, String sheetName, List<Map<String, String>> maps, HSSFWorkbook wb) {
        try {
            if (headerKey.size() <= 0) {
                return null;
            }
            HSSFSheet sheet;
            if ((sheetName == null) || (sheetName.equals(""))) {
                sheet = wb.createSheet("Sheet1");
            } else {
                sheet = wb.createSheet(sheetName);
            }
            HSSFRow row = sheet.createRow(0);
            HSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(HorizontalAlignment.forInt((short) 2));
            HSSFCell cell = null;
            if (headerName.size() > 0) {
                for (int i = 0; i < headerName.size(); i++) {
                    cell = row.createCell(i);
                    cell.setCellValue(headerName.get(i));
                    cell.setCellStyle(style);
                }
            }
            int n = 0;
            HSSFCellStyle contextstyle = wb.createCellStyle();
            contextstyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00_);(#,##0.00)"));

            HSSFCellStyle contextstyle1 = wb.createCellStyle();
            HSSFDataFormat format = wb.createDataFormat();
            contextstyle1.setDataFormat(format.getFormat("@"));

            HSSFCell cell0 = null;
            HSSFCell cell1 = null;

            List<String> strings = new ArrayList<>();
            Set<String> keys = maps.get(0).keySet();
            for (String key : keys) {
                strings.add(key);
            }

            for (Map<String, String> map : maps) {
                row = sheet.createRow(n + 1);
                for (int j = 0; j < headerKey.size(); j++) {
                    if (headerName.size() <= 0) {
                        cell0 = row.createCell(j);
                        cell0.setCellValue(headerKey.get(j));
                        cell0.setCellStyle(style);
                    }
                    for (int i = 0; i < strings.size(); i++) {
                        if (strings.get(i).equals(headerKey.get(j))) {
                            if (map.get(strings.get(i)) == null) {
                                row.createCell(j).setCellValue("暂无数据");
                                break;
                            }
                            if ("".equals(map.get(strings.get(i)))) {
                                cell1 = row.createCell(j);
                                cell1.setCellStyle(contextstyle1);
                                row.createCell(j).setCellValue("暂无数据");
                                cell1.setCellType(CellType.forInt(1));
                                break;
                            }
                            row.createCell(j).setCellValue(map.get(strings.get(i)));
                            break;
                        }

                    }
                }
                n++;
            }
            for (int i = 0; i < headerKey.size(); i++) {
                sheet.setColumnWidth(i, headerKey.get(i).getBytes().length * 2 * 256);
            }
            HSSFWorkbook localHSSFWorkbook1 = wb;
            return localHSSFWorkbook1;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {

        }
    }

    //excel2007版
    public static SXSSFWorkbook createSXSSFExcel(List<String> headerName, List<String> headerKey, String sheetName, List<Map<String, String>> maps) {
        try {
            if (headerKey.size() <= 0) {
                return null;
            }
            SXSSFWorkbook wb = new SXSSFWorkbook();
            SXSSFSheet sheet;
            if ((sheetName == null) || (sheetName.equals(""))) {
                sheet = wb.createSheet("Sheet1");
            } else {
                sheet = wb.createSheet(sheetName);
            }
            SXSSFRow row = sheet.createRow(0);
            CellStyle style = wb.createCellStyle();
            style.setAlignment(HorizontalAlignment.forInt(2));
            SXSSFCell cell = null;
            if (headerName.size() > 0) {
                for (int i = 0; i < headerName.size(); i++) {
                    cell = row.createCell(i);
                    cell.setCellValue(headerName.get(i));
                    cell.setCellStyle(style);
                }
            }
            int n = 0;
            CellStyle contextstyle = wb.createCellStyle();
            contextstyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00_);(#,##0.00)"));

            CellStyle contextstyle1 = wb.createCellStyle();
            DataFormat dataFormat = wb.createDataFormat();
            contextstyle1.setDataFormat(dataFormat.getFormat("@"));

            SXSSFCell cell0 = null;
            SXSSFCell cell1 = null;

            List<String> strings = new ArrayList<>();
            Set<String> keys = maps.get(0).keySet();
            for (String key : keys) {
                strings.add(key);
            }

            for (Map<String, String> map : maps) {
                row = sheet.createRow(n + 1);
                for (int j = 0; j < headerKey.size(); j++) {
                    if (headerName.size() <= 0) {
                        cell0 = row.createCell(j);
                        cell0.setCellValue(headerKey.get(j));
                        cell0.setCellStyle(style);
                    }
                    for (int i = 0; i < strings.size(); i++) {
                        if (strings.get(i).equals(headerKey.get(j))) {
                            if (map.get(strings.get(i)) == null) {
                                row.createCell(j).setCellValue("暂无数据");
                                break;
                            }
                            if ("".equals(map.get(strings.get(i)))) {
                                cell1 = row.createCell(j);
                                cell1.setCellStyle(contextstyle1);
                                row.createCell(j).setCellValue("暂无数据");
                                cell1.setCellType(CellType.forInt(1));
                                break;
                            }
                            String value = map.get(strings.get(i));
                            row.createCell(j).setCellValue(value);
                            break;
                        }

                    }
                }
                n++;
            }
            for (int i = 0; i < headerKey.size(); i++) {
                sheet.setColumnWidth(i, headerKey.get(i).getBytes().length * 2 * 256);
            }
            SXSSFWorkbook localHSSFWorkbook1 = wb;
            return localHSSFWorkbook1;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {

        }
    }

    public static SXSSFWorkbook addSXSSFExcelSheetData(List<String> headerName, List<String> headerKey, String sheetName, List<Map<String, String>> maps, SXSSFWorkbook wb) {
        try {
            if (headerKey.size() <= 0) {
                return null;
            }
            SXSSFSheet sheet;
            if ((sheetName == null) || (sheetName.equals(""))) {
                sheet = wb.createSheet("Sheet1");
            } else {
                sheet = wb.createSheet(sheetName);
            }
            SXSSFRow row = sheet.createRow(0);
            CellStyle style = wb.createCellStyle();
            style.setAlignment(HorizontalAlignment.forInt((short) 2));
            SXSSFCell cell = null;
            if (headerName.size() > 0) {
                for (int i = 0; i < headerName.size(); i++) {
                    cell = row.createCell(i);
                    cell.setCellValue(headerName.get(i));
                    cell.setCellStyle(style);
                }
            }
            int n = 0;
            CellStyle contextstyle = wb.createCellStyle();
            contextstyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00_);(#,##0.00)"));

            CellStyle contextstyle1 = wb.createCellStyle();
            DataFormat format = wb.createDataFormat();
            contextstyle1.setDataFormat(format.getFormat("@"));

            SXSSFCell cell0 = null;
            SXSSFCell cell1 = null;

            List<String> strings = new ArrayList<>();
            Set<String> keys = maps.get(0).keySet();
            for (String key : keys) {
                strings.add(key);
            }

            for (Map<String, String> map : maps) {
                row = sheet.createRow(n + 1);
                for (int j = 0; j < headerKey.size(); j++) {
                    if (headerName.size() <= 0) {
                        cell0 = row.createCell(j);
                        cell0.setCellValue(headerKey.get(j));
                        cell0.setCellStyle(style);
                    }
                    for (int i = 0; i < strings.size(); i++) {
                        if (strings.get(i).equals(headerKey.get(j))) {
                            if (map.get(strings.get(i)) == null) {
                                row.createCell(j).setCellValue("暂无数据");
                                break;
                            }
                            if ("".equals(map.get(strings.get(i)))) {
                                cell1 = row.createCell(j);
                                cell1.setCellStyle(contextstyle1);
                                row.createCell(j).setCellValue("暂无数据");
                                cell1.setCellType(CellType.forInt(1));
                                break;
                            }
                            row.createCell(j).setCellValue(map.get(strings.get(i)));
                            break;
                        }

                    }
                }
                n++;
            }
            for (int i = 0; i < headerKey.size(); i++) {
                sheet.setColumnWidth(i, headerKey.get(i).getBytes().length * 2 * 256);
            }
            SXSSFWorkbook localHSSFWorkbook1 = wb;
            return localHSSFWorkbook1;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {

        }
    }


//    public static Map<String, Object> readExcel(MultipartFile file){
//
//        String fileName = file.getName();
//        if (!fileName.matches("^.+\\.(?i)(xls)$") && !fileName.matches("^.+\\.(?i)(xlsx)$")) {
//            log.error("上传文件格式不正确");
//        }
//        Map<String, Object> map = new HashMap<>();
//        List<String> entNames = new ArrayList<>();
//        List<String> entIds = new ArrayList<>();
//        Workbook workbook = null;
//        try {
//            InputStream is = file.getInputStream();
//            if (fileName.endsWith(EXCEL2007)) {
////                FileInputStream is = new FileInputStream(new File(path));
//                workbook = new XSSFWorkbook(is);
//            }
//            if (fileName.endsWith(EXCEL2003)) {
////                FileInputStream is = new FileInputStream(new File(path));
//                workbook = new HSSFWorkbook(is);
//            }
//            if (workbook != null) {
//                Sheet sheet = workbook.getSheetAt(0);
//                int rowSize = sheet.getLastRowNum();
//                for (int rowNum = 0; rowNum <= rowSize; rowNum++) {
//                    if (rowNum != 0) {
//                        Row row = sheet.getRow(rowNum);
//                        Integer cellSize = Integer.parseInt(row.getLastCellNum() + "");
//                        for (int cellNum = 0; cellNum < cellSize; cellNum++) {
//                            if (cellNum == 0) {
//                                entNames.add(row.getCell(cellNum) + "");
//                            }
//                            if (cellNum == 1) {
//                                entIds.add(row.getCell(cellNum) + "");
//                            }
//                        }
//                    }
//                }
//            }
//            map.put("entNames", entNames);
//            map.put("entIds", entIds);
//        } catch (Exception e) {
//            log.error("读取Excel出错");
//        }
//        return map;
//    }

//    public static List<ExcelDto> readExcel(MultipartFile file) {
//        List<ExcelDto> excelDtos=new ArrayList<>();
//        String fileName = file.getOriginalFilename();
//        if (!fileName.matches("^.+\\.(?i)(xls)$") && !fileName.matches("^.+\\.(?i)(xlsx)$")) {
//            log.error("上传文件格式不正确");
//        }
//        List<T> dataList = new ArrayList<>();
//        Workbook workbook = null;
//        try {
//            InputStream is = file.getInputStream();
//            if (fileName.endsWith(EXCEL2007)) {
////                FileInputStream is = new FileInputStream(new File(path));
//                workbook = new XSSFWorkbook(is);
//            }
//            if (fileName.endsWith(EXCEL2003)) {
////                FileInputStream is = new FileInputStream(new File(path));
//                workbook = new HSSFWorkbook(is);
//            }
//            if (workbook != null) {
//                Sheet sheet = workbook.getSheetAt(0);
//                for (int i = 1; i < sheet.getLastRowNum()+1; i++) {
//                    Row row=sheet.getRow(i);
//                    if (StringUtils.isBlank(row.getCell(0).toString())){
//                        continue;
//                    }
//                    ExcelDto dto=new ExcelDto();
//                    if (row.getCell(0) != null && row.getCell(0).toString() != ""){
//                        dto.setEntName(row.getCell(0).toString().replaceAll("（","(").replaceAll("）",")"));
//                    }
//                    if (row.getCell(1) != null && row.getCell(1).toString() != ""){
//                        dto.setEntRegNo(row.getCell(1).toString());
//                    }
//                    excelDtos.add(dto);
//                }
//            }
//        } catch (Exception e) {
//            log.error("异常",e);
//        }
//        return excelDtos;
//    }
//
//    public static List<ImportScore> readExcel(MultipartFile file, String uuid) {
//        List<ImportScore> list=new ArrayList<>();
//        String fileName = file.getOriginalFilename();
//        if (!fileName.matches("^.+\\.(?i)(xls)$") && !fileName.matches("^.+\\.(?i)(xlsx)$")) {
//            log.error("上传文件格式不正确");
//        }
//        List<T> dataList = new ArrayList<>();
//        Workbook workbook = null;
//        try {
//            InputStream is = file.getInputStream();
//            if (fileName.endsWith(EXCEL2007)) {
//                workbook = new XSSFWorkbook(is);
//            }
//            if (fileName.endsWith(EXCEL2003)) {
//                workbook = new HSSFWorkbook(is);
//            }
//            if (workbook != null) {
//                Sheet sheet = workbook.getSheetAt(0);
//                for (int i = 1; i < sheet.getLastRowNum()+1; i++) {
//                    Row row=sheet.getRow(i);
//                    if (StringUtils.isBlank(row.getCell(0).toString())){
//                        continue;
//                    }
//                    ImportScore importScore=new ImportScore();
//                    importScore.setKey(uuid);
//                    if (row.getCell(0) != null && !row.getCell(0).toString().equals("")){
//                        importScore.setEntUid(row.getCell(0).toString());
//                    }
//                    if (row.getCell(7) != null && !row.getCell(7).toString().equals("")){
//                        importScore.setScore(new Double(row.getCell(7).toString()));
//                    }
//                    list.add(importScore);
//                }
//            }
//        } catch (Exception e) {
//            log.error("导入数据异常",e);
//        }
//        return list;
//    }
//
//    public static List<String> readExcelTitle(MultipartFile file) {
//        List<String> list=new ArrayList<>();
//        String fileName = file.getOriginalFilename();
//        if (!fileName.matches("^.+\\.(?i)(xls)$") && !fileName.matches("^.+\\.(?i)(xlsx)$")) {
//            log.error("上传文件格式不正确");
//        }
//        List<T> dataList = new ArrayList<>();
//        Workbook workbook = null;
//        try {
//            InputStream is = file.getInputStream();
//            if (fileName.endsWith(EXCEL2007)) {
//                workbook = new XSSFWorkbook(is);
//            }
//            if (fileName.endsWith(EXCEL2003)) {
//                workbook = new HSSFWorkbook(is);
//            }
//            if (workbook != null) {
//                Sheet sheet = workbook.getSheetAt(0);
//                Row row=sheet.getRow(0);
//                Iterator<Cell> iterator = row.cellIterator();
//                //跳过前两行固定的企业名称和统一社会信用代码
//                iterator.next();
//                iterator.next();
//                while (iterator.hasNext()){
//                    list.add(iterator.next().getStringCellValue());
//                }
//            }
//        } catch (Exception e) {
//            log.error("导入数据异常",e);
//        }
//        return list;
//    }
//
//    public static List<AnsIndexBasic> readExcel(File file,Map<String,String> map) {
//        Map<String,Integer> dict = new HashMap<>();
//        List<AnsIndexBasic> list = new ArrayList<>();
//        String fileName = file.getName();
//        Workbook workbook = null;
//        try {
//            InputStream is = new FileInputStream(file);
//            if (fileName.endsWith(EXCEL2007)) {
//                workbook = new XSSFWorkbook(is);
//            }
//            if (fileName.endsWith(EXCEL2003)) {
//                workbook = new HSSFWorkbook(is);
//            }
//            if (workbook != null) {
//                Sheet sheet = workbook.getSheetAt(0);
//                Row row=sheet.getRow(0);
//                Iterator<Cell> iterator = row.cellIterator();
//                int i = 0;
//                //构建字典记录字段位置
//                while (iterator.hasNext()){
//                    dict.put(iterator.next().getStringCellValue(),i);
//                    i++;
//                }
//                Iterator<Row> rowIterator = sheet.iterator();
//                rowIterator.next();
//                while (rowIterator.hasNext()){
//                    AnsIndexBasic ansIndexBasic = new AnsIndexBasic();
//                    ansIndexBasic.setTenantId(BaseContextHandler.getTenantID());
//                    Row data = rowIterator.next();
//                    for (Map.Entry<String,String> entry : map.entrySet()){
//                        Field field = ansIndexBasic.getClass().getDeclaredField(entry.getKey());
//                        field.setAccessible(true);
//                        if (!key.contains(entry.getKey())){
//                            field.set(ansIndexBasic,(float)data.getCell(dict.get(entry.getValue())).getNumericCellValue());
//                        }else {
//                            field.set(ansIndexBasic,data.getCell(dict.get(entry.getValue())).getStringCellValue());
//                        }
//                    }
//                    list.add(ansIndexBasic);
//                }
//            }
//        } catch (Exception e) {
//            log.error("导入数据异常",e);
//        }
//        return list;
//    }

    public static void main(String[] args) throws IOException {
        int count = 100000;
        List<String> headName = new ArrayList<>();
        headName.add("columnName");
        headName.add("dataType");
        headName.add("disabled");
        headName.add("isVisible");
        headName.add("label");
        headName.add("required");
        headName.add("rules");
        headName.add("tableName");
        headName.add("type");
        headName.add("value");
        List<String> headKey = new ArrayList<>();
        headKey.add("columnName");
        headKey.add("dataType");
        headKey.add("disabled");
        headKey.add("isVisible");
        headKey.add("label");
        headKey.add("required");
        headKey.add("rules");
        headKey.add("tableName");
        headKey.add("type");
        headKey.add("value");
        String json = "[{\n" +
                "\t\"columnName\": \"startUseDate\",\n" +
                "\t\"dataType\": \"DATE\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"入网时间\",\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [],\n" +
                "\t\"tableName\": \"T_C_OBJECTS\",\n" +
                "\t\"type\": \"date\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"installTime\",\n" +
                "\t\"dataType\": \"DATE\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"安装时间\",\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [],\n" +
                "\t\"tableName\": \"T_C_OBJECTS\",\n" +
                "\t\"type\": \"date\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"devType\",\n" +
                "\t\"dataType\": \"NUMBER\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"设备类型\",\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [],\n" +
                "\t\"selectDatas\": [{\n" +
                "\t\t\"label\": \"请选择\",\n" +
                "\t\t\"value\": \"\"\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"纯光伏供电系统\",\n" +
                "\t\t\"value\": 50040001\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"风光互补供电系统\",\n" +
                "\t\t\"value\": 50040002\n" +
                "\t}],\n" +
                "\t\"tableName\": \"T_C_OBJECTS\",\n" +
                "\t\"type\": \"select\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"mntMfrId\",\n" +
                "\t\"dataType\": \"NUMBER\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"维保厂家\",\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [],\n" +
                "\t\"selectDatas\": [{\n" +
                "\t\t\"label\": \"请选择\",\n" +
                "\t\t\"value\": \"\"\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"newland\",\n" +
                "\t\t\"value\": 1\n" +
                "\t}],\n" +
                "\t\"tableName\": \"T_C_OBJECTS\",\n" +
                "\t\"type\": \"select\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"resName\",\n" +
                "\t\"dataType\": \"VARCHAR2(256)\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"资源名称\",\n" +
                "\t\"length\": 256,\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [],\n" +
                "\t\"tableName\": \"T_C_OBJECTS\",\n" +
                "\t\"type\": \"input\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"resState\",\n" +
                "\t\"dataType\": \"NUMBER\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"资源状态\",\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [],\n" +
                "\t\"selectDatas\": [{\n" +
                "\t\t\"label\": \"请选择\",\n" +
                "\t\t\"value\": \"\"\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"在网\",\n" +
                "\t\t\"value\": 10003005\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"待注销\",\n" +
                "\t\t\"value\": 10003006\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"已注销\",\n" +
                "\t\t\"value\": 10003007\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"在建\",\n" +
                "\t\t\"value\": 10003004\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"选址成功\",\n" +
                "\t\t\"value\": 10003003\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"需求\",\n" +
                "\t\t\"value\": 10003002\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"规划\",\n" +
                "\t\t\"value\": 10003001\n" +
                "\t}],\n" +
                "\t\"tableName\": \"T_C_OBJECTS\",\n" +
                "\t\"type\": \"select\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"resCode\",\n" +
                "\t\"dataType\": \"CHAR(18)\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"资源编码\",\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [],\n" +
                "\t\"tableName\": \"T_C_OBJECTS\",\n" +
                "\t\"type\": \"input\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"mfrId\",\n" +
                "\t\"dataType\": \"NUMBER\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"生产厂家\",\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [],\n" +
                "\t\"selectDatas\": [{\n" +
                "\t\t\"label\": \"请选择\",\n" +
                "\t\t\"value\": \"\"\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"泰兴通信电器厂\",\n" +
                "\t\t\"value\": 12924\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"潍坊远程数控\",\n" +
                "\t\t\"value\": 12925\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"业达\",\n" +
                "\t\t\"value\": 12922\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"上海第一开工厂\",\n" +
                "\t\t\"value\": 12923\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"北京电源厂\",\n" +
                "\t\t\"value\": 12928\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"莱州金通电气设备公司\",\n" +
                "\t\t\"value\": 12929\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"深圳华海力超\",\n" +
                "\t\t\"value\": 12926\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"潍坊北方网络通信\",\n" +
                "\t\t\"value\": 12927\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"WISHING\",\n" +
                "\t\t\"value\": 12921\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"烟台力源电力成套设备有限公司\",\n" +
                "\t\t\"value\": 12930\n" +
                "\t}],\n" +
                "\t\"tableName\": \"T_C_OBJECTS\",\n" +
                "\t\"type\": \"select\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"dwMfrId\",\n" +
                "\t\"dataType\": \"NUMBER\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"代维厂家\",\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [],\n" +
                "\t\"selectDatas\": [{\n" +
                "\t\t\"label\": \"请选择\",\n" +
                "\t\t\"value\": \"\"\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"newland\",\n" +
                "\t\t\"value\": 1\n" +
                "\t}],\n" +
                "\t\"tableName\": \"T_C_OBJECTS\",\n" +
                "\t\"type\": \"select\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"propId\",\n" +
                "\t\"dataType\": \"NUMBER\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"产权单位\",\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [],\n" +
                "\t\"selectDatas\": [{\n" +
                "\t\t\"label\": \"请选择\",\n" +
                "\t\t\"value\": \"\"\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"广电\",\n" +
                "\t\t\"value\": 10007005\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"业主\",\n" +
                "\t\t\"value\": 10007006\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"铁塔\",\n" +
                "\t\t\"value\": 10007001\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"北京正通\",\n" +
                "\t\t\"value\": 10007008\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"电信\",\n" +
                "\t\t\"value\": 10007004\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"联通\",\n" +
                "\t\t\"value\": 10007003\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"移动\",\n" +
                "\t\t\"value\": 10007002\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"其他\",\n" +
                "\t\t\"value\": 10007007\n" +
                "\t}],\n" +
                "\t\"tableName\": \"T_C_OBJECTS\",\n" +
                "\t\"type\": \"select\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"business\",\n" +
                "\t\"dataType\": \"NUMBER\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"所属业务\",\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [],\n" +
                "\t\"selectDatas\": [{\n" +
                "\t\t\"label\": \"请选择\",\n" +
                "\t\t\"value\": \"\"\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"智联\",\n" +
                "\t\t\"value\": 10004003\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"能源\",\n" +
                "\t\t\"value\": 10004002\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"通信\",\n" +
                "\t\t\"value\": 10004001\n" +
                "\t}],\n" +
                "\t\"tableName\": \"T_C_OBJECTS\",\n" +
                "\t\"type\": \"select\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"devModel\",\n" +
                "\t\"dataType\": \"NUMBER\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"设备型号\",\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [],\n" +
                "\t\"selectDatas\": [{\n" +
                "\t\t\"label\": \"请选择\",\n" +
                "\t\t\"value\": \"\"\n" +
                "\t}],\n" +
                "\t\"tableName\": \"T_C_OBJECTS\",\n" +
                "\t\"type\": \"select\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"warrantyExpirationDate\",\n" +
                "\t\"dataType\": \"DATE\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"出保日期\",\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [],\n" +
                "\t\"tableName\": \"T_C_OBJECTS\",\n" +
                "\t\"type\": \"date\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"stationId\",\n" +
                "\t\"dataType\": \"CHAR(24)\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"所属站址\",\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [],\n" +
                "\t\"tableName\": \"T_C_OBJECTS\",\n" +
                "\t\"type\": \"input\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"sourceSystem\",\n" +
                "\t\"dataType\": \"NUMBER\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"来源系统\",\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [],\n" +
                "\t\"selectDatas\": [{\n" +
                "\t\t\"label\": \"请选择\",\n" +
                "\t\t\"value\": \"\"\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"运维监控\",\n" +
                "\t\t\"value\": 10006002\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"资源系统\",\n" +
                "\t\t\"value\": 10006001\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"能源监控\",\n" +
                "\t\t\"value\": 10006003\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"智控云\",\n" +
                "\t\t\"value\": 10006004\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"北京正通\",\n" +
                "\t\t\"value\": 10006005\n" +
                "\t}],\n" +
                "\t\"tableName\": \"T_C_OBJECTS\",\n" +
                "\t\"type\": \"select\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"roomId\",\n" +
                "\t\"dataType\": \"CHAR(18)\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"机房编码\",\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [],\n" +
                "\t\"tableName\": \"T_C_OBJECTS\",\n" +
                "\t\"type\": \"input\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"useUnit\",\n" +
                "\t\"dataType\": \"NUMBER\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"使用单位\",\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [],\n" +
                "\t\"selectDatas\": [{\n" +
                "\t\t\"label\": \"请选择\",\n" +
                "\t\t\"value\": \"\"\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"业主\",\n" +
                "\t\t\"value\": 10008006\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"其他\",\n" +
                "\t\t\"value\": 10008007\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"北京正通\",\n" +
                "\t\t\"value\": 10008008\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"广电\",\n" +
                "\t\t\"value\": 10008005\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"铁塔\",\n" +
                "\t\t\"value\": 10008001\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"移动\",\n" +
                "\t\t\"value\": 10008002\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"联通\",\n" +
                "\t\t\"value\": 10008003\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"电信\",\n" +
                "\t\t\"value\": 10008004\n" +
                "\t}],\n" +
                "\t\"tableName\": \"T_C_OBJECTS\",\n" +
                "\t\"type\": \"select\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"subBusiness\",\n" +
                "\t\"dataType\": \"NUMBER\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"所属子业务\",\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [],\n" +
                "\t\"selectDatas\": [{\n" +
                "\t\t\"label\": \"请选择\",\n" +
                "\t\t\"value\": \"\"\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"换电\",\n" +
                "\t\t\"value\": 10005103\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"发电\",\n" +
                "\t\t\"value\": 10005102\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"备电\",\n" +
                "\t\t\"value\": 10005101\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"传输类\",\n" +
                "\t\t\"value\": 10005005\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"楼宇室分类\",\n" +
                "\t\t\"value\": 10005004\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"隧道室分类\",\n" +
                "\t\t\"value\": 10005003\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"智控\",\n" +
                "\t\t\"value\": 10005203\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"宏站类\",\n" +
                "\t\t\"value\": 10005001\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"充电\",\n" +
                "\t\t\"value\": 10005104\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"其他\",\n" +
                "\t\t\"value\": 10005105\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"智联\",\n" +
                "\t\t\"value\": 10005201\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"智享\",\n" +
                "\t\t\"value\": 10005202\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"微站类\",\n" +
                "\t\t\"value\": 10005002\n" +
                "\t}],\n" +
                "\t\"tableName\": \"T_C_OBJECTS\",\n" +
                "\t\"type\": \"select\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"provinceId\",\n" +
                "\t\"dataType\": \"NUMBER\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"省份ID\",\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [],\n" +
                "\t\"selectDatas\": [{\n" +
                "\t\t\"label\": \"请选择\",\n" +
                "\t\t\"value\": \"\"\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"青海省\",\n" +
                "\t\t\"value\": 63\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"西藏自治区\",\n" +
                "\t\t\"value\": 54\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"四川省\",\n" +
                "\t\t\"value\": 51\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"广东省\",\n" +
                "\t\t\"value\": 44\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"北京市\",\n" +
                "\t\t\"value\": 11\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"福建省\",\n" +
                "\t\t\"value\": 35\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"山东省\",\n" +
                "\t\t\"value\": 37\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"江苏省\",\n" +
                "\t\t\"value\": 32\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"宁夏回族自治区\",\n" +
                "\t\t\"value\": 64\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"重庆市\",\n" +
                "\t\t\"value\": 50\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"黑龙江省\",\n" +
                "\t\t\"value\": 23\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"广西壮族自治区\",\n" +
                "\t\t\"value\": 45\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"上海市\",\n" +
                "\t\t\"value\": 31\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"陕西省\",\n" +
                "\t\t\"value\": 61\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"河北省\",\n" +
                "\t\t\"value\": 13\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"江西省\",\n" +
                "\t\t\"value\": 36\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"浙江省\",\n" +
                "\t\t\"value\": 33\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"吉林省\",\n" +
                "\t\t\"value\": 22\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"山西省\",\n" +
                "\t\t\"value\": 14\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"天津市\",\n" +
                "\t\t\"value\": 12\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"河南省\",\n" +
                "\t\t\"value\": 41\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"湖南省\",\n" +
                "\t\t\"value\": 43\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"甘肃省\",\n" +
                "\t\t\"value\": 62\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"海南省\",\n" +
                "\t\t\"value\": 46\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"安徽省\",\n" +
                "\t\t\"value\": 34\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"湖北省\",\n" +
                "\t\t\"value\": 42\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"新疆维吾尔自治区\",\n" +
                "\t\t\"value\": 65\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"云南省\",\n" +
                "\t\t\"value\": 53\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"贵州省\",\n" +
                "\t\t\"value\": 52\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"辽宁省\",\n" +
                "\t\t\"value\": 21\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"内蒙古自治区\",\n" +
                "\t\t\"value\": 15\n" +
                "\t}],\n" +
                "\t\"tableName\": \"T_C_OBJECTS\",\n" +
                "\t\"type\": \"select\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"resTypeId\",\n" +
                "\t\"dataType\": \"NUMBER\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"资源类型\",\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [],\n" +
                "\t\"selectDatas\": [{\n" +
                "\t\t\"label\": \"请选择\",\n" +
                "\t\t\"value\": \"\"\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"站址\",\n" +
                "\t\t\"value\": 201\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"机房\",\n" +
                "\t\t\"value\": 205\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"铁塔\",\n" +
                "\t\t\"value\": 8843\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"蓄电池组\",\n" +
                "\t\t\"value\": 3521\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"动力及环境监控单元\",\n" +
                "\t\t\"value\": 5003\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"开关电源\",\n" +
                "\t\t\"value\": 3510\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"发电主设备\",\n" +
                "\t\t\"value\": 3482\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"能源电池\",\n" +
                "\t\t\"value\": 1509\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"新能源供电系统\",\n" +
                "\t\t\"value\": 5004\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"基站天线\",\n" +
                "\t\t\"value\": 5001\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"无线回传设备\",\n" +
                "\t\t\"value\": 5002\n" +
                "\t}, {\n" +
                "\t\t\"label\": \"油田机房监控\",\n" +
                "\t\t\"value\": 5005\n" +
                "\t}],\n" +
                "\t\"tableName\": \"T_C_OBJECTS\",\n" +
                "\t\"type\": \"select\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"dccount\",\n" +
                "\t\"dataType\": \"NUMBER\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"DC/DC转换模块数量（个）\",\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"trigger\": \"blur\"\n" +
                "\t}],\n" +
                "\t\"tableName\": \"T_C_PWR_NENG_SUP\",\n" +
                "\t\"type\": \"input\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"devType\",\n" +
                "\t\"dataType\": \"NUMBER\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"设备类型\",\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"trigger\": \"blur\"\n" +
                "\t}],\n" +
                "\t\"tableName\": \"T_C_PWR_NENG_SUP\",\n" +
                "\t\"type\": \"input\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"mntMfrId\",\n" +
                "\t\"dataType\": \"NUMBER\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"维保厂家\",\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"trigger\": \"blur\"\n" +
                "\t}],\n" +
                "\t\"tableName\": \"T_C_PWR_NENG_SUP\",\n" +
                "\t\"type\": \"input\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"createOp\",\n" +
                "\t\"dataType\": \"NUMBER\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"创建人\",\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"trigger\": \"blur\"\n" +
                "\t}],\n" +
                "\t\"tableName\": \"T_C_PWR_NENG_SUP\",\n" +
                "\t\"type\": \"input\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"longitude\",\n" +
                "\t\"dataType\": \"NUMBER\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"x坐标\",\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"trigger\": \"blur\"\n" +
                "\t}],\n" +
                "\t\"tableName\": \"T_C_PWR_NENG_SUP\",\n" +
                "\t\"type\": \"input\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"startUseTime\",\n" +
                "\t\"dataType\": \"DATE\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"开始使用时间\",\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [],\n" +
                "\t\"tableName\": \"T_C_PWR_NENG_SUP\",\n" +
                "\t\"type\": \"date\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"devModel\",\n" +
                "\t\"dataType\": \"NUMBER\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"设备型号\",\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"trigger\": \"blur\"\n" +
                "\t}],\n" +
                "\t\"tableName\": \"T_C_PWR_NENG_SUP\",\n" +
                "\t\"type\": \"input\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"countyId\",\n" +
                "\t\"dataType\": \"NUMBER\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"区县ID\",\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"trigger\": \"blur\"\n" +
                "\t}],\n" +
                "\t\"tableName\": \"T_C_PWR_NENG_SUP\",\n" +
                "\t\"type\": \"input\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"dwMfrIdOld\",\n" +
                "\t\"dataType\": \"NUMBER\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"原代维公司\",\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"trigger\": \"blur\"\n" +
                "\t}],\n" +
                "\t\"tableName\": \"T_C_PWR_NENG_SUP\",\n" +
                "\t\"type\": \"input\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"maintenancePeriod\",\n" +
                "\t\"dataType\": \"VARCHAR2(16)\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"维保期限\",\n" +
                "\t\"length\": 16,\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [],\n" +
                "\t\"tableName\": \"T_C_PWR_NENG_SUP\",\n" +
                "\t\"type\": \"input\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"modifyOp\",\n" +
                "\t\"dataType\": \"NUMBER\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"修改人\",\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"trigger\": \"blur\"\n" +
                "\t}],\n" +
                "\t\"tableName\": \"T_C_PWR_NENG_SUP\",\n" +
                "\t\"type\": \"input\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"mainDevId\",\n" +
                "\t\"dataType\": \"NUMBER\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"主设备id\",\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"trigger\": \"blur\"\n" +
                "\t}],\n" +
                "\t\"tableName\": \"T_C_PWR_NENG_SUP\",\n" +
                "\t\"type\": \"input\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"dccapa\",\n" +
                "\t\"dataType\": \"NUMBER\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"单模块DC/DC转换模块标称容量\",\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"trigger\": \"blur\"\n" +
                "\t}],\n" +
                "\t\"tableName\": \"T_C_PWR_NENG_SUP\",\n" +
                "\t\"type\": \"input\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"provinceId\",\n" +
                "\t\"dataType\": \"NUMBER\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"省份ID\",\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"trigger\": \"blur\"\n" +
                "\t}],\n" +
                "\t\"tableName\": \"T_C_PWR_NENG_SUP\",\n" +
                "\t\"type\": \"input\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"createTime\",\n" +
                "\t\"dataType\": \"DATE\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"创建日期\",\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [],\n" +
                "\t\"tableName\": \"T_C_PWR_NENG_SUP\",\n" +
                "\t\"type\": \"date\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"remark\",\n" +
                "\t\"dataType\": \"VARCHAR2(256)\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"备注\",\n" +
                "\t\"length\": 256,\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [],\n" +
                "\t\"tableName\": \"T_C_PWR_NENG_SUP\",\n" +
                "\t\"type\": \"input\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"fardevcount\",\n" +
                "\t\"dataType\": \"NUMBER\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"远端设备容量\",\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"trigger\": \"blur\"\n" +
                "\t}],\n" +
                "\t\"tableName\": \"T_C_PWR_NENG_SUP\",\n" +
                "\t\"type\": \"input\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"resState\",\n" +
                "\t\"dataType\": \"NUMBER\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"资源状态\",\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"trigger\": \"blur\"\n" +
                "\t}],\n" +
                "\t\"tableName\": \"T_C_PWR_NENG_SUP\",\n" +
                "\t\"type\": \"input\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"resCode\",\n" +
                "\t\"dataType\": \"CHAR(18)\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"资源编码\",\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [],\n" +
                "\t\"tableName\": \"T_C_PWR_NENG_SUP\",\n" +
                "\t\"type\": \"input\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"relatedTower\",\n" +
                "\t\"dataType\": \"NUMBER\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"关联铁塔\",\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"trigger\": \"blur\"\n" +
                "\t}],\n" +
                "\t\"tableName\": \"T_C_PWR_NENG_SUP\",\n" +
                "\t\"type\": \"input\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"mfrId\",\n" +
                "\t\"dataType\": \"NUMBER\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"生产厂家\",\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"trigger\": \"blur\"\n" +
                "\t}],\n" +
                "\t\"tableName\": \"T_C_PWR_NENG_SUP\",\n" +
                "\t\"type\": \"input\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"devName\",\n" +
                "\t\"dataType\": \"VARCHAR2(256)\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"设备名称\",\n" +
                "\t\"length\": 256,\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [],\n" +
                "\t\"tableName\": \"T_C_PWR_NENG_SUP\",\n" +
                "\t\"type\": \"input\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"ifsensor\",\n" +
                "\t\"dataType\": \"NUMBER\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"是否有传感器，1有   0无\",\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"trigger\": \"blur\"\n" +
                "\t}],\n" +
                "\t\"tableName\": \"T_C_PWR_NENG_SUP\",\n" +
                "\t\"type\": \"input\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"warrantyExpirationDate\",\n" +
                "\t\"dataType\": \"DATE\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"出保日期\",\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [],\n" +
                "\t\"tableName\": \"T_C_PWR_NENG_SUP\",\n" +
                "\t\"type\": \"date\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"stationId\",\n" +
                "\t\"dataType\": \"CHAR(18)\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"站址编码\",\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [],\n" +
                "\t\"tableName\": \"T_C_PWR_NENG_SUP\",\n" +
                "\t\"type\": \"input\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"devcount\",\n" +
                "\t\"dataType\": \"NUMBER\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"局端设备满架容量\",\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"trigger\": \"blur\"\n" +
                "\t}],\n" +
                "\t\"tableName\": \"T_C_PWR_NENG_SUP\",\n" +
                "\t\"type\": \"input\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"sourceSystem\",\n" +
                "\t\"dataType\": \"NUMBER\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"来源系统\",\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"trigger\": \"blur\"\n" +
                "\t}],\n" +
                "\t\"tableName\": \"T_C_PWR_NENG_SUP\",\n" +
                "\t\"type\": \"input\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"sourceDn\",\n" +
                "\t\"dataType\": \"VARCHAR2(255)\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"资源编号\",\n" +
                "\t\"length\": 128,\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [],\n" +
                "\t\"tableName\": \"T_C_PWR_NENG_SUP\",\n" +
                "\t\"type\": \"input\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"cityId\",\n" +
                "\t\"dataType\": \"NUMBER\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"市ID\",\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"trigger\": \"blur\"\n" +
                "\t}],\n" +
                "\t\"tableName\": \"T_C_PWR_NENG_SUP\",\n" +
                "\t\"type\": \"input\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"ifoutmaintenance\",\n" +
                "\t\"dataType\": \"NUMBER\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"是否出保\",\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"trigger\": \"blur\"\n" +
                "\t}],\n" +
                "\t\"tableName\": \"T_C_PWR_NENG_SUP\",\n" +
                "\t\"type\": \"input\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"roomId\",\n" +
                "\t\"dataType\": \"CHAR(18)\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"机房编码\",\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [],\n" +
                "\t\"tableName\": \"T_C_PWR_NENG_SUP\",\n" +
                "\t\"type\": \"input\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"latitude\",\n" +
                "\t\"dataType\": \"NUMBER\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"y坐标\",\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"trigger\": \"blur\"\n" +
                "\t}],\n" +
                "\t\"tableName\": \"T_C_PWR_NENG_SUP\",\n" +
                "\t\"type\": \"input\",\n" +
                "\t\"value\": \"\"\n" +
                "}, {\n" +
                "\t\"columnName\": \"modifyTime\",\n" +
                "\t\"dataType\": \"DATE\",\n" +
                "\t\"disabled\": \"0\",\n" +
                "\t\"isVisible\": 1,\n" +
                "\t\"label\": \"修改时间\",\n" +
                "\t\"required\": \"0\",\n" +
                "\t\"rules\": [],\n" +
                "\t\"tableName\": \"T_C_PWR_NENG_SUP\",\n" +
                "\t\"type\": \"date\",\n" +
                "\t\"value\": \"\"\n" +
                "}]";
        List<Map<String, String>> maps = JSONObject.parseObject(json, ArrayList.class);
        List<Map<String, String>> maps1 = JSON.parseObject(json, ArrayList.class);

        /*
        OutputStream outXls = new FileOutputStream("E://a.xls");
        System.out.println("正在导出xls....");
        Date d = new Date();
        ExcelUtil.exportExcel(title,headMap,ja,null,outXls);
        System.out.println("共"+count+"条数据,执行"+(new Date().getTime()-d.getTime())+"ms");
        outXls.close();*/
        //
        OutputStream outXlsx = new FileOutputStream("C:\\document\\1.xlsx");
        System.out.println("正在导出xlsx....");
        Date d2 = new Date();
        MyExcelUtils.createSXSSFExcel(headName, headKey, "测试", maps);
        System.out.println("共" + count + "条数据,执行" + (new Date().getTime() - d2.getTime()) + "ms");
        outXlsx.close();
    }
}
