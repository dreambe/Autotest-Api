package com.autotest.api.bean;

public class TestCase {

    private String sheetName;

    private String platInfo;

    private String featureModule;

    private String no;

    private String description;

    private String api;

    private String method;

    private String header;

    private String parameters;

    private String user;

    private String mysql;

    private String mongoDB;

    private String redis;

    private String expectationsMysql;

    private String expectationsResponse;

    private String clearMysql;

    private String clearRedis;

    private String ignoreFlag;

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public String getPlatInfo() {
        return platInfo;
    }

    public void setPlatInfo(String platInfo) {
        this.platInfo = platInfo;
    }

    public String getFeatureModule() {
        return featureModule;
    }

    public void setFeatureModule(String featureModule) {
        this.featureModule = featureModule;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public String getMysql() {
        return mysql;
    }

    public void setMysql(String mysql) {
        this.mysql = mysql;
    }

    public String getMongoDB() {
        return mongoDB;
    }

    public void setMongoDB(String mongoDB) {
        this.mongoDB = mongoDB;
    }

    public String getRedis() {
        return redis;
    }

    public void setRedis(String redis) {
        this.redis = redis;
    }

    public String getExpectationsMysql() {
        return expectationsMysql;
    }

    public void setExpectationsMysql(String expectationsMysql) {
        this.expectationsMysql = expectationsMysql;
    }

    public String getExpectationsResponse() {
        return expectationsResponse;
    }

    public void setExpectationsResponse(String expectationsResponse) {
        this.expectationsResponse = expectationsResponse;
    }

    public String getClearMysql() {
        return clearMysql;
    }

    public void setClearMysql(String clearMysql) {
        this.clearMysql = clearMysql;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getClearRedis() {
        return clearRedis;
    }

    public void setClearRedis(String clearRedis) {
        this.clearRedis = clearRedis;
    }

    public String getIgnoreFlag() {
        return ignoreFlag;
    }

    public void setIgnoreFlag(String ignoreFlag) {
        this.ignoreFlag = ignoreFlag;
    }

    @Override
    public String toString() {
        return String.format("[%s], Description:%s, Method:%s, Url:%s, Param:%s", this.no, this.description,
                this.method, this.api, this.parameters);
    }
}
