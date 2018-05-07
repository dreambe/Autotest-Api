package com.autotest.api.utils;

import com.autotest.api.bean.TestCase;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * 解析execel数据
 */
@Service
public class ExcelUtil {

    private final static Logger LOGGER = LoggerFactory.getLogger(HttpClientUtil.class);

    /**
     * 获取Excel文件的sheet页
     *
     * @param fileName
     * @return
     */
    public List<Sheet> getSheets(String fileName) {
        List<Sheet> sheets = new ArrayList<>();
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                System.out.println("文件不存在！");
                return null;
            }
            Workbook wb = null;
            if (fileName.endsWith(".xlsx")) {// 2007
                wb = new XSSFWorkbook(new FileInputStream(file));
            } else if (fileName.endsWith(".xls")) {// 2003
                wb = new HSSFWorkbook(new FileInputStream(file));
            }
            int sheetCount = wb.getNumberOfSheets(); // Sheet的数量
            Sheet[] array = new Sheet[sheetCount];
            for (int i = 0; i < sheetCount; i++) {
                Sheet sheet = wb.getSheetAt(i);
                if (!isNullSheet(sheet)) {//sheet页内容非空时，进行读取
                    sheets.add(sheet);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sheets;
    }

    /**
     * 读取所有测试用例Excel的内容
     *
     * @param fileName 目录（不支持层级读取） or 文件
     * @return
     */
    public Object[][] readAllFile(String fileName) {
        try {
            File file = new File(fileName);
            List<TestCase> list = new ArrayList<>();
            if (file.isDirectory()) { //判断传入的是目录 还是文件名
                File[] fileArray = file.listFiles();
                for (File temp : fileArray) {
                    //忽略Excel打开时~$开头的临时文件
                    if (temp.getName().startsWith("~$")) {
                        continue;
                    }
                    List<TestCase> caseList = readFileAsList(temp.getAbsolutePath());
                    list.addAll(caseList);
                }
            } else {
                list = readFileAsList(file.getAbsolutePath());
            }
            return format2array(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 读取测试用例Excel的所有内容（以二维数组的方式返回，作为testng的dataprovider）
     *
     * @param fileName
     * @return
     */
    public Object[][] readFile(String fileName) {
        List<TestCase> list = readFileAsList(fileName);
        return format2array(list);
    }

    /**
     * 读取测试用例，以list返回
     *
     * @param fileName
     * @return
     */
    public List<TestCase> readFileAsList(String fileName) {
        List<TestCase> list = new ArrayList<>();
        List<Sheet> sheets = getSheets(fileName);
        for (Sheet sheet : sheets) {
            List<TestCase> sheetList = this.readSheetAsList(sheet);
            list.addAll(sheetList);
        }
        return list;
    }

    /**
     * 读取每个sheet页的内容（以二维数组的方式返回，作为testng的dataprovider）
     *
     * @param sheet
     * @return
     */
    public Object[][] readSheetAsArray(Sheet sheet) {
        List<TestCase> caselist = readSheetAsList(sheet);
        return format2array(caselist);
    }


    /**
     * 读取每个sheet页的内容,以list返回（忽略ignore=1的用例，表示不执行）
     *
     * @param sheet
     * @return
     */
    public List<TestCase> readSheetAsList(Sheet sheet) {

        List<TestCase> list = new ArrayList<TestCase>();
        Row head = sheet.getRow(0);
        int maxCol = head.getLastCellNum();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);

            // 若行第一格为空，判断这行内容为空，用例结束
            if (isNullRow(row)) {
                break;
            }

            TestCase testCase = initTestCase(head, row, maxCol);
            testCase.setSheetName(sheet.getSheetName());
            list.add(testCase);
        }
        return list;
    }

    /**
     * 获取单元格内容，转成String类型
     *
     * @param row
     * @param column
     * @return
     */
    private String getCellValue(Row row, int column) {
        try {
            return row.getCell(column) == null ? "" : row.getCell(column).toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
//        return row.getCell(column) == null ? "" : row.getCell(column).toString().trim();
    }

    /**
     * 根据当前单元格内容获取所在列
     *
     * @param row
     * @param cellValue
     * @return
     */
    private Integer getCellColumnByValue(Row row, String cellValue) {
        int totalCol = row.getLastCellNum();
        for (int i = 0; i < totalCol; i++) {
            if (cellValue.equalsIgnoreCase(getCellValue(row, i))) {
                return i;
            }
        }
        return -1;
    }


    /**
     * 判断sheet页是否为空
     * 标准：若第一行有数据，则认为不为空，否则为空
     *
     * @param sheet
     * @return
     */
    private boolean isNullSheet(Sheet sheet) {
        return isNullRow(sheet.getRow(0));
    }

    /**
     * 判断行row内容是否为空
     * 标准：若该行第一个cell不为空，则认为row不是空，否则为空
     *
     * @param row
     * @return
     */
    private boolean isNullRow(Row row) {
        return row.getCell(0) == null;
    }

    /**
     * list转成二维数组，testng data
     *
     * @param list
     * @return
     */
    private Object[][] format2array(List<TestCase> list) {
        if (!list.isEmpty()) { //将list转换成二维数组
            Object[][] result = new Object[list.size()][1];
            for (int i = 0; i < list.size(); i++) {
                result[i][0] = list.get(i);
            }
            return result;
        }

        return null;
    }

    /**
     * 根据表头，读取当前行每个单元格内容，并写入TestCase Bean
     *
     * @param head
     * @param row
     * @param totalCol
     * @return
     */
    private TestCase initTestCase(Row head, Row row, Integer totalCol) {
        TestCase testCase = new TestCase();

        for (int j = 0; j <= totalCol; j++) {
            String cellValue = getCellValue(row, j);
            String headRowValue = getCellValue(head, j).toLowerCase();
            switch (headRowValue) {
                case "platinfo":
                    testCase.setPlatInfo(cellValue);
                    break;
                case "featuremodule":
                    testCase.setFeatureModule(cellValue);
                    break;
                case "no":
                    testCase.setNo(cellValue);
                    break;
                case "description":
                    testCase.setDescription(cellValue);
                    break;
                case "api":
                    testCase.setApi(cellValue);
                    break;
                case "method":
                    testCase.setMethod(cellValue);
                    break;
                case "header":
                    testCase.setHeader(cellValue);
                    break;
                case "parameters":
                    testCase.setParameters(cellValue);
                    break;
                case "user":
                    testCase.setUser(cellValue);
                    break;
                case "mysql":
                    testCase.setMysql(cellValue);
                    break;
                case "mongodb":
                    testCase.setMongoDB(cellValue);
                    break;
                case "redis":
                    testCase.setRedis(cellValue);
                    break;
                case "expectationsmysql":
                    testCase.setExpectationsMysql(cellValue);
                    break;
                case "expectationsresponse":
                    testCase.setExpectationsResponse(cellValue);
                    break;
                case "clearmysql":
                    testCase.setClearMysql(cellValue);
                    break;
                case "clearredis":
                    testCase.setClearRedis(cellValue);
                    break;
                case "ignore" :
                    testCase.setIgnoreFlag(cellValue);
                default:
//                    LOGGER.info("\n❤❤❤❤❤❤❤❤❤❤目前该字段无用，请在Excel内删除该列❤❤❤❤❤❤❤❤❤❤: {}", headRowValue);
                    break;
            }
        }
        return testCase;
    }

}
