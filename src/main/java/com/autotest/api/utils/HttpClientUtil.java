package com.autotest.api.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.autotest.api.bean.TestCase;
import com.iqianjin.autotest.springboot.utils.auth.TokenUtil;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * http请求工具类
 *
 * @author haoxiaosha
 */
public class HttpClientUtil {

    // platInfo 填 WEB
    private final static String WEB_DOMAIN = "xxxx";

    // platInfo 填 H5
    private final static String H5_DOMAIN = "xxxx";

    private final static String DEFAULT_DOMAIN = "xxx";

    private final static Logger LOGGER = LoggerFactory.getLogger(HttpClientUtil.class);

    private RequestConfig requestConfig = RequestConfig.custom()
            .setSocketTimeout(3000)
            .setConnectTimeout(1000)
            .setConnectionRequestTimeout(2000)
            .build();

    private static HttpClientUtil instance = null;

    private static BasicCookieStore cookieStore = new BasicCookieStore();

    private HttpClientUtil() {
    }

    public static HttpClientUtil getInstance() {
        if (instance == null) {
            instance = new HttpClientUtil();
        }
        return instance;
    }


    /**
     * 发送 post请求
     *
     * @param httpUrl  地址
     * @param testCase 参数(格式1:key1=value1&key2=value2；格式2：json)
     */
    public String sendHttpPost(String httpUrl, TestCase testCase) {
        String params = testCase.getParameters();
        HttpPost httpPost = new HttpPost(httpUrl);// 创建httpPost
        try {
            if (!testCase.getUser().isEmpty()) {
                // 添加Cookie
                this.addCookieStore(testCase.getUser(), testCase.getPlatInfo());
            } else {
                cookieStore.clear();
            }

            if (!params.isEmpty()) {

                // 以 K-V 形式发送POST请求
                if (!isJson(params) && !isJsonArray(params)) {
                    List<NameValuePair> pairList = new ArrayList<NameValuePair>();
                    String[] paramArray = params.split("&");
                    for (String param : paramArray) {
                        String name = param.split("=")[0];
                        String value, encodValue;
                        try {
                            value = param.split("=")[1];
                        } catch (ArrayIndexOutOfBoundsException e) {
                            value = "";
                        }
                        pairList.add(new BasicNameValuePair(name, value));
                    }
                    UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(pairList);

                    httpPost.setEntity(formEntity);
                    formEntity.setContentType("application/x-www-form-urlencoded");
                }
                // 以 JSON body 发送POST请求
                else {
                    StringEntity stringEntity = new StringEntity(params, "UTF-8");
                    JSONObject jsonObject = (JSONObject) JSONObject.parse(params);
                    if (!testCase.getUser().isEmpty()) {
                        // 添加Token
                        String token = TokenUtil.getToken(testCase.getUser().split(";")[0]);
                        jsonObject.put("token", token);
                    }
                    params = jsonObject.toJSONString();
                    stringEntity = new StringEntity(params, "UTF-8");
                    stringEntity.setContentType("application/json");
                    httpPost.setEntity(stringEntity);
                }
            }

            // header 表格列 以key1:val1;key2:val2;key3:val3 的格式
            if (!testCase.getHeader().isEmpty()) {
                String[] headerGroup = testCase.getHeader().split(";");
                for (String header : headerGroup) {
                    httpPost.addHeader(header.split(":")[0], header.split(":")[1]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sendHttp(httpPost, null);
    }


    /**
     * 发送get请求
     *
     * @param httpUrl
     */
    public String sendHttpGet(String httpUrl, TestCase testCase) {
        HttpGet httpGet = new HttpGet(httpUrl);
        if (!testCase.getUser().isEmpty()) {
            // 添加Cookie
            this.addCookieStore(testCase.getUser(), testCase.getPlatInfo());
        } else {
            cookieStore.clear();
        }
        // header列 以key1:val1;key2:val2;key3:val3 的格式
        if (!testCase.getHeader().isEmpty()) {
            String[] headerGroup = testCase.getHeader().split(";");
            for (String header : headerGroup) {
                httpGet.addHeader(header.split(":")[0], header.split(":")[1]);
            }
        }
        return sendHttp(null, httpGet);
    }


    /**
     * 发送请求，获取返回值
     *
     * @param httpPost 不为空时，表示post请求
     * @param httpGet  不为空时，表示get请求
     * @return
     */
    private String sendHttp(HttpPost httpPost, HttpGet httpGet) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).build();
        CloseableHttpResponse response = null;
        String responseContent = null;
        try {
            if (httpPost != null) {
                httpPost.setConfig(requestConfig);
                response = httpClient.execute(httpPost);
            } else {
                httpGet.setConfig(requestConfig);
                response = httpClient.execute(httpGet);
            }

            responseContent = EntityUtils.toString(response.getEntity(), "UTF-8");

            ReportUtil.log("Response Code: " + response.getStatusLine().getStatusCode());
            ReportUtil.log("Response Body: " + responseContent);
            if (response.getStatusLine().getStatusCode() != 200) {
                responseContent = "error";
                return responseContent;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // 关闭连接,释放资源
                if (response != null) {
                    response.close();
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return responseContent;
    }

    /**
     * 将testcase中的Cookie添加到CookieStore中
     *
     * @param userInfo
     */
    private void addCookieStore(String userInfo, String platInfo) {
        // user不为空时，直接生成cookie跟token。user格式为: userId;nickName
        if (!userInfo.isEmpty()) {
            String userId = userInfo.split(";")[0];
            String nickName = userInfo.split(";")[1];
            String currentTimeMillis = Long.toString(System.currentTimeMillis());

            String cLm = CookieUtils.generateCookieLm(nickName, userId, currentTimeMillis);
            String cLp = CookieUtils.generateCookieLp(nickName, userId, currentTimeMillis);
//            LOGGER.info("\n****\nlm={};\nlp={}", cLm, cLp);

            BasicClientCookie cookieLm = new BasicClientCookie("lm", cLm);
            BasicClientCookie cookieLp = new BasicClientCookie("lp", cLp);

            switch (platInfo) {
                case "WEB":
                    cookieLm.setDomain(WEB_DOMAIN);
                    cookieLp.setDomain(WEB_DOMAIN);
                    break;
                case "H5":
                    cookieLm.setDomain(H5_DOMAIN);
                    cookieLp.setDomain(H5_DOMAIN);
                    break;
                default:
                    cookieLm.setDomain(DEFAULT_DOMAIN);
                    cookieLp.setDomain(DEFAULT_DOMAIN);
            }
            cookieLm.setPath("/");
            cookieLp.setPath("/");
            cookieStore.addCookie(cookieLm);
            cookieStore.addCookie(cookieLp);
        }
    }

    /**
     * 手动增加cookie
     *
     * @param name
     * @param value
     * @param domain
     * @param path
     */
    public static void addCookie(String name, String value, String domain, String path) {
        BasicClientCookie cookie = new BasicClientCookie(name, value);
        cookie.setDomain(domain);
        cookie.setPath(path);
        cookieStore.addCookie(cookie);
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
}
