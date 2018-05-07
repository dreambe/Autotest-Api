package com.autotest.api.utils;

import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;

public class AssertUtil {

    public static boolean flag = true;

    public static List<Error> errors = new ArrayList<Error>();

    /**
     * 校验JSON字段 相等
     * @param expectKey
     * @param expectValue
     * @param actualValue
     */
    public static void equals(String expectKey, String expectValue, String actualValue) {
        try {
            Assert.assertTrue(actualValue.trim().equals(expectValue.trim()));
        } catch (Error e) {
            errors.add(e);
            flag = false;
            ReportUtil.log(String.format("[ %s ] 期望 [ %s ], 实际返回 [ %s ], 跟期望不等", expectKey, expectValue, actualValue));
        }
    }

    /**
     * 校验JSON字段 不相等
     * @param expectKey
     * @param expectValue
     * @param actualValue
     */
    public static void notEquals(String expectKey, String expectValue, String actualValue) {
        try {
            Assert.assertFalse(actualValue.trim().equals(expectValue.trim()));
        } catch (Error e) {
            errors.add(e);
            flag = false;
            ReportUtil.log(String.format("[ %s ] 期望 [ %s ], 实际返回 [ %s ], 跟期望相等", expectKey, expectValue, actualValue));
        }
    }

    /**
     * 校验整个返回字符串 相等
     * @param expectValue
     * @param actualValue
     */
    public static void equals(String expectValue, String actualValue) {
        try {
            Assert.assertTrue(actualValue.trim().equals(expectValue.trim()));
        } catch (Error e) {
            errors.add(e);
            flag = false;
            ReportUtil.log(String.format("整个返回字符串期望 [ %s ], 实际返回 [ %s ], 跟期望不等", expectValue, actualValue));
        }
    }

    /**
     * 校验整个返回字符串 不相等
     * @param expectValue
     * @param actualValue
     */
    public static void notEquals(String expectValue, String actualValue) {
        try {
            Assert.assertTrue(actualValue.trim().equals(expectValue.trim()));
        } catch (Error e) {
            errors.add(e);
            flag = false;
            ReportUtil.log(String.format("整个返回字符串不期望 [ %s ], 实际返回 [ %s ], 跟期望相等", expectValue, actualValue));
        }
    }

    /**
     * 校验JSON字段 包含
     * @param expectKey
     * @param expectValue
     * @param actualValue
     */
    public static void contains(String expectKey, String expectValue, String actualValue) {
        try {
            Assert.assertTrue(actualValue.trim().contains(expectValue.trim()));
        } catch (Error e) {
            errors.add(e);
            flag = false;
            ReportUtil.log(String.format("[ %s ] 期望 [ %s ] 包含 [ %s ], 实际为不包含", expectKey, actualValue, expectValue));
        }
    }

    /**
     * 校验JSON字段 不包含
     * @param expectKey
     * @param expectValue
     * @param actualValue
     */
    public static void notContains(String expectKey, String expectValue, String actualValue) {
        try {
            Assert.assertFalse(actualValue.trim().contains(expectValue.trim()));
        } catch (Error e) {
            errors.add(e);
            flag = false;
            ReportUtil.log(String.format("[ %s ] 期望 [ %s ] 不包含 [ %s ], 实际为包含", expectKey, actualValue, expectValue));
        }
    }

    /**
     * 校验整个返回字符串 包含
     * @param expectValue
     * @param actualValue
     */
    public static void contains(String expectValue, String actualValue) {
        try {
            Assert.assertTrue(actualValue.trim().contains(expectValue.trim()));
        } catch (Error e) {
            errors.add(e);
            flag = false;
            ReportUtil.log(String.format("整个返回字符串期望 [ %s ] 包含 [ %s ], 实际为不包含", actualValue, expectValue));
        }
    }

    /**
     * 校验整个返回字符串 不包含
     * @param expectValue
     * @param actualValue
     */
    public static void notContains(String expectValue, String actualValue) {
        try {
            Assert.assertFalse(actualValue.trim().contains(expectValue.trim()));
        } catch (Error e) {
            errors.add(e);
            flag = false;
            ReportUtil.log(String.format("整个返回字符串期望 [ %s ] 不包含 [ %s ], 实际为包含", actualValue, expectValue));
        }
    }
}
