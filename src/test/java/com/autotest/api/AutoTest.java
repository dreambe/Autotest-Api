package com.autotest.api;

import com.autotest.api.bean.TestCase;
import com.autotest.api.utils.ExcelUtil;
import com.autotest.api.utils.ReportUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@SpringBootTest
public class AutoTest extends BaseCase {

    @Value("${test.case.file}")
    private String CASE_FILE;

    @Autowired
    ExcelUtil excelUtil;

    @DataProvider(name = "datasource")
    public Object[][] getDatasource() {
        ExcelUtil excelUtil = new ExcelUtil();
        //读取指定文件或者文件夹下的所有用例
        Object[][] result = excelUtil.readAllFile(CASE_FILE);
        Assert.assertNotNull(result, "待执行的测试用例为0，请检查Excel文件配置（ignore=1表示不执行）");
        return result;
    }

    @Test(description = "执行每条case，进行测试", dataProvider = "datasource")
    public void apiTest(TestCase testCase) {
        if ("1.0".equals(testCase.getIgnoreFlag()) || "1".equals(testCase.getIgnoreFlag())) {
            throw new SkipException("略过本条用例");
        }
        ReportUtil.log("\n❤❤❤❤❤❤❤❤执行测试用例❤❤❤❤❤❤❤❤: ["
                + testCase.getNo() + "] 描述: " + testCase.getDescription());
        this.runTest(testCase);
    }

    // 执行每个测试用例
    private void runTest(TestCase testCase) {
        try {
            if ("profit".equalsIgnoreCase(testCase.getSheetName())) {//收益计算的case
                //准备全流程数据
                /**
                 * 等待雪梅的方法
                 */
            }
            // 1.拼接url
            String api = testCase.getApi();
            String url = getUrl(testCase.getSheetName(), api);
            // 2.准备工作，是否向数据库添加测试数据
            testCase = initTestCase(testCase);
            String parameters = testCase.getParameters();
            // 3.判断请求方式，拼接参数并发送请求
            String method = testCase.getMethod().toLowerCase();
            String response;
            if (!parameters.isEmpty()) {
                parameters = replaceVar(parameters);//判断是否有变量，并替换
                testCase.setParameters(parameters);
            }
            switch (method) {
                case "get":
                    ReportUtil.log("Method       : GET");
                    ReportUtil.log("Request URL  : " + url);
                    ReportUtil.log("Request Param: " + parameters);

                    url = parameters.isEmpty() ? url : url + "?" + parameters;
                    response = httpClientUtil.sendHttpGet(url, testCase);
                    break;
                case "post":
                    ReportUtil.log("Method       : POST");
                    ReportUtil.log("Request  URL : " + url);
                    ReportUtil.log("Request  Body: " + parameters);
                    response = httpClientUtil.sendHttpPost(url, testCase);
                    break;
                default:
                    ReportUtil.log("暂时不支持get和post方式以外的请求");
                    return;
            }

            // 4.校验请求返回数据
            verifyResponse(response, testCase.getExpectationsResponse());

            // 5.校验mysql数据库数据
            if (!testCase.getExpectationsMysql().isEmpty()) {
                String mysqls = testCase.getExpectationsMysql();
                mysqls = replaceVar(mysqls);//判断是否有变量，并替换
                verifyMysql(mysqls);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            clear(testCase);
        }
    }
}