package com.autotest.api;

import com.alibaba.fastjson.*;
import com.autotest.api.bean.TestCase;
import com.autotest.api.db.redis.AutotestSpringBootRedisManager;
import com.autotest.api.listener.RetryListener;
import com.autotest.api.utils.*;
import com.autotest.api.db.jdbc.AutotestSpringBootJdbcManager;
import com.autotest.api.listener.AutoTestListener;
import com.iqianjin.autotest.springboot.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Listeners({AutoTestListener.class, RetryListener.class})
public class BaseCase extends AbstractTestNGSpringContextTests {
    // platInfo 填 WEB
    private final static String WEB_DOMAIN = "xxx";

    // platInfo 填 H5
    private final static String H5_DOMAIN = "xxx";


    public final static Logger LOGGER = LoggerFactory.getLogger(BaseCase.class);

    public HttpClientUtil httpClientUtil = HttpClientUtil.getInstance();

    @Autowired
    AutotestSpringBootJdbcManager autotestSpringBootJdbcManager;

    @Autowired
    AutotestSpringBootRedisManager autotestSpringBootRedisManager;

    @Autowired
    SqlFileExecutor sqlFileExecutor;

    @Autowired
    private Environment env;

    @BeforeSuite
    public void beforeSuite() {
        LOGGER.info("\n❤❤❤❤❤❤❤❤本次测试开始❤❤❤❤❤❤❤❤");
    }

    @AfterSuite
    public void afterSuite() {
        LOGGER.info("\n \n❤❤❤❤❤❤❤❤本次测试结束❤❤❤❤❤❤❤❤");
    }

    /**
     * 拼接url
     *
     * @param api
     * @return
     */
    public String getUrl(String sheetName , String api) throws Exception {
        // 如果mock的开关打开，则请求mock的接口，
        if ("1".equals(env.getProperty("mock.flag"))) {
            return env.getProperty("mock.host") + api;
        } else if (api.startsWith("http://")) {
            return api;
        }

        // 以 '/' 开头的，从配置文件读取host，然后拼接 api 返回
        return env.getProperty(sheetName) + api;
//
//        String host = env.getProperty(sheetName);
//        switch (platInfo) {
//            case "WEB":
//                host = WEB_DOMAIN;
//                break;
//            case "H5":
//                host = H5_DOMAIN;
//                break;
//            case "APP":
//                host = env.getProperty("host.app");
//                break;
//            case "HEXIN":
//                host = env.getProperty("host.hexin");
//                break;
//            case "JIZHANG":
//                host = env.getProperty("host.charge");
//                break;
//            case "CUNGUAN":
//                host = env.getProperty("host.hx");
//                break;
//            case "JIJIN":
//                host = env.getProperty("host.fund");
//                break;
//            default:
//                ReportUtil.log("ERROR: 请在配置文件里配置host，例：host.xxx=");
//                throw new Exception("ERROR: 请在配置文件里配置host，例：host.xxx=");
//        }
//
//        return host + api;
    }

    /**
     * 准备工作(cookie,token,sql等)
     *
     * @param testCase
     * @return
     */
    public TestCase initTestCase(TestCase testCase) throws Exception {
        // 是SQL文件就执行文件；如果是SQL语句，就执行SQL语句
        if (!testCase.getMysql().isEmpty()) {
            if (testCase.getMysql().endsWith(".sql")) {
                String dataDir = env.getProperty("test.data.dir");
                String sqlFile = dataDir + File.separator + testCase.getMysql();
                try {
                    sqlFileExecutor.executeSqlFile(sqlFile);
                } catch (NullPointerException e) {

                }

            }
            operationMysql(testCase.getMysql());
        }

        if (!testCase.getMongoDB().isEmpty()) {//判断是否要执行mongo文件
            //暂时未处理
        }

        if (!testCase.getRedis().isEmpty()) {//判断是否要执行redis文件
            String[] redisArray = testCase.getRedis().split(";");
            for (String str : redisArray) {
                if (str.trim().startsWith("set")) {//修改key
                    String key = str.trim().split(" ")[1];
                    String value = str.trim().split(" ")[2];
                    autotestSpringBootRedisManager.setRedisKeyValue(key, value);
                } else if (str.trim().startsWith("del")) {//删除key
                    String key = str.trim().split(" ")[1];
                    autotestSpringBootRedisManager.removeRedisValueByKey(key);
                }
            }
        }
        return testCase;
    }

    /**
     * 校验接口返回值
     *
     * @param response
     * @param expectations
     * @return
     */
    public void verifyResponse(String response, String expectations) {
        AssertUtil.flag = true;
        AssertUtil.errors.clear();

        if("error".equals(response)){
            AssertUtil.flag = false;
        }
        // TO DO 逻辑放在下面进行判断，不应该根据返回，要根据expctValue 是不是key-value格式
        else if (!isJson(response)) {
            AssertUtil.equals(expectations, response);
        }
        else {
            try {
                JSONObject jsonObject = JSON.parseObject(response);
                Map<String, String> map = str2map(expectations, ";", ":");
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    String expectKey = entry.getKey().trim();
                    String expectValue = entry.getValue().trim();

                    // 检查整个返回是否包含目标字段
                    if (!expectKey.startsWith("$") && ("contain").equals(expectKey)) {

                        AssertUtil.contains(expectValue, response);
                    } else if (!expectKey.startsWith("$")) {
                        ReportUtil.log("用例参数有误: 请检查 [ contain ] 参数拼写是否有误");
                    }

                    try {
                        if (expectKey.startsWith("$")) {
                            String actualValue = JSONPath.eval(jsonObject, expectKey).toString();
                            AssertUtil.equals(expectKey, expectValue, actualValue);
                        }
                    } catch (Exception e) {
                        AssertUtil.flag = false;
                        ReportUtil.log("返回JSON数据没有包含 [ " + expectKey + " ]字段, 严重错误");
                    }
                }

            } catch (JSONException e) {
                //返回值非json格式
                AssertUtil.flag = false;
                e.printStackTrace();
            }
        }

        if (AssertUtil.flag) {
            ReportUtil.log("❤❤❤❤❤❤❤❤测试用例成功❤❤❤❤❤❤❤❤: Pass");
        } else {
            ReportUtil.log("❤❤❤❤❤❤❤❤测试用例失败❤❤❤❤❤❤❤❤: Fail");
        }

        Assert.assertTrue(AssertUtil.flag, "仅做判断所有检查是否通过。如果出现该信息，请看前面输出的校验信息");
    }

    /**
     * 校验数据库
     *
     * @param expectations
     */
    public void verifyMysql(String expectations) throws Exception {
        AssertUtil.flag = true;
        AssertUtil.errors.clear();
        String sql;

        // JSON数组走这个逻辑
        if (isJsonArray(expectations)) {
            JSONArray dataArr = JSONArray.parseArray(expectations);

            // data部分是json数组，直接按照json数组进行解析
            for (Object obj : dataArr) {
                JSONObject jsonObj = (JSONObject) obj;
                sql = jsonObj.getString("sql");
                Map actualMap = autotestSpringBootJdbcManager.queryToMap(sql);

                // 如果sql查询返回为空，则继续执行下一组数据
                if (actualMap == null) {
                    AssertUtil.flag = false;
                    ReportUtil.log("无法连接库: sql: \" + sql + \" 查不到数据! 请检查sql跟数据库!");
                    continue;
                }

                for (Iterator<String> iterator = jsonObj.keySet().iterator(); iterator.hasNext(); ) {
                    String expectKey = iterator.next().trim();
                    if (("sql").equals(expectKey)) {
                        continue;
                    }
                    String expectValue = jsonObj.getString(expectKey).trim();
                    String actualValue = actualMap.get(expectKey).toString();

                    AssertUtil.equals(expectKey, expectValue, actualValue);
                }
            }
        }
        // 非JSON数组走这个逻辑
        else {
            Map<String, String> sqlMap = str2map(expectations, ";", ":");
            if (sqlMap.containsKey("isMultipleCheck")) {
                sql = sqlMap.get("isMultipleCheck");
                Map actualMap = autotestSpringBootJdbcManager.queryToMap(sql);

                for (Map.Entry<String, String> entry : sqlMap.entrySet()) {

                    if (("isMultipleCheck").equals(entry.getKey())) {
                        continue;
                    }

                    String expectKey = entry.getKey().trim();
                    String expectValue = entry.getValue().trim();
                    String actualValue = actualMap.get(expectKey).toString().trim();

                    AssertUtil.equals(expectKey, expectValue, actualValue);
                }
            } else {
                for (Map.Entry<String, String> entry : sqlMap.entrySet()) {
                    sql = entry.getKey();
                    String expectValue = entry.getValue().trim();
                    String actualValue = autotestSpringBootJdbcManager.query(sql).trim();

                    AssertUtil.equals(sql, expectValue, actualValue);
                }
            }
        }

        if (AssertUtil.flag) {
            ReportUtil.log("❤❤❤❤❤❤❤❤测试用例成功❤❤❤❤❤❤❤❤: Pass");
        } else {
            ReportUtil.log("❤❤❤❤❤❤❤❤测试用例失败❤❤❤❤❤❤❤❤: Fail");
        }

        Assert.assertTrue(AssertUtil.flag, "仅做判断所有检查是否通过。如果出现该信息，请看前面输出的校验信息");
    }

    /**
     * 字符串键值对转成map
     *
     * @param paramters
     * @param splitChar1 一级分隔符，将字符串分成多个键值对key-value
     * @param splitChar2 二级分隔符，分割key和value
     * @return
     */
    private Map<String, String> str2map(String paramters, String splitChar1, String splitChar2) {
        if (paramters.isEmpty()) {
            return null;
        }
        Map<String, String> map = new HashMap<>();
        String[] paramArray = paramters.split(splitChar1);
        for (String param : paramArray) {
            String[] array = param.split(splitChar2);
            map.put(array[0], array[1]);
        }
        return map;
    }

    /**
     * 判断字符串是否为Json
     *
     * @param content
     * @return boolen
     */
    private boolean isJson(String content) {
        try {
            JSONObject jsonStr = JSON.parseObject(content);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断字符串是否为JsonArray
     *
     * @param content
     * @return boolen
     */
    private boolean isJsonArray(String content) {
        try {
            JSONArray jsonArr = JSONArray.parseArray(content);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 执行case前执行sql语句
     * PS: Excel配置的为sql语句，以分号隔开
     *
     * @param sqlStr SQL语句
     */
    public void operationMysql(String sqlStr) {
        try {
            //判断是否有变量，并替换
            sqlStr = replaceVar(sqlStr);
            String[] sqlArray = sqlStr.split(";");
            for (String sql : sqlArray) {
                autotestSpringBootJdbcManager.execute(sql);
            }
//            logger.info("\n❤执行SQL语句成功");
        } catch (Exception e) {
            LOGGER.error("\n❤执行SQL语句报错");
            e.printStackTrace();
        }
    }

    /**
     * 清理测试产生的垃圾数据
     * PS: Excel配置的为sql语句，以分号隔开
     *
     * @param testCase
     */
    public void clear(TestCase testCase) {
        try {
            // 清理MySQL数据库
            if (!testCase.getClearMysql().isEmpty()) {
                String clears = testCase.getClearMysql();
                //判断是否有变量，并替换
                clears = replaceVar(clears);
                String[] sqlArray = clears.split(";");
                for (String sql : sqlArray) {
                    autotestSpringBootJdbcManager.execute(sql);
                }
            }
            // 清理Redis数据库
            if (!testCase.getClearRedis().isEmpty()) {
                String[] redisArray = testCase.getClearRedis().split(";");
                for (String key : redisArray) {
                    if (key.trim().startsWith("del")) {
                        key = key.trim().split(" ")[1];
                        autotestSpringBootRedisManager.removeRedisValueByKey(key);
                    }
                }
            }
        } catch (Exception e) {
            ReportUtil.log("清理测试数据报错，请检查报错信息");
            e.printStackTrace();
        }
    }

    /**
     * 替换Excel中的变量
     * $yesterday  2017-12-04
     * $today      2017-12-05
     * $datefortable 20171204
     *
     * @param str
     * @return
     */
    public String replaceVar(String str) {
        if (!str.isEmpty() && str.contains("$yesterday")) {
            String yesterday = DateUtils.getYesterday();
            str = str.replace("$yesterday", yesterday);
        }
        if (!str.isEmpty() && str.contains("$today")) {
            String yesterday = DateUtils.formateDateForToday();
            str = str.replace("$today", yesterday);
        }
        if (!str.isEmpty() && str.contains("$datefortable")) {
            String yesterday = DateUtils.getDatefortable();
            str = str.replace("$datefortable", yesterday);
        }
        return str;
    }
}
