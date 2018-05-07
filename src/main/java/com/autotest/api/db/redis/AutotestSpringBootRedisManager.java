package com.autotest.api.db.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;



@Component
public class AutotestSpringBootRedisManager {
    private final static Logger LOGGER = LoggerFactory.getLogger(AutotestSpringBootRedisManager.class);

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    /**
     * 设置 key-value 具备过期时间属性
     *
     * @param key
     * @param seconds 以秒为单位
     * @param value
     */
    public void setRedisKeyValueTimeOut(String key, String value, int seconds) {

        try {
            value = StringUtils.isEmpty(value) ? "" : value;
            stringRedisTemplate.opsForValue().set(key, value, seconds, TimeUnit.SECONDS);
            //throw new RuntimeException();
        } catch (Exception e) {
            LOGGER.error("Set keyex error :", e);
        }

    }

    /**
     * 设定某个key的value值
     *
     * @param key
     * @param value
     */
    public void setRedisKeyValue(String key, String value) {

        try {
            stringRedisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            LOGGER.error("Set keyex error :", e);
        }
    }

    /**
     * 获取指定Key的value String值
     *
     * @param key
     */
    public String getRedisValueByKey(String key) {

        return (stringRedisTemplate.opsForValue().get(key));
    }


    /**
     * 用 value 参数覆写(overwrite)给定 key 所储存的字符串值，从偏移量 offset 开始
     *
     * @param key
     * @param value
     * @param offset 偏移量
     * @commnet 如下：
     * setRedisKeyValue("key","hello world");
     * setRedisKeyValueOffset("key","redis", 6);
     * System.out.println("***************"+getRedisKeyValue("key"));
     * 结果：***************hello redis
     */
    public void setRedisKeyValueOffset(String key, String value, long offset) {

        try {
            stringRedisTemplate.opsForValue().set(key, value, offset);
        } catch (Exception e) {
            LOGGER.error("Set keyex error :", e);
        }
    }

    /**
     * 获取指定Key的value String值
     *
     * @param key
     */
    public void removeRedisValueByKey(String key) {

        try {
            stringRedisTemplate.delete(key);
            //throw new RuntimeException();
        } catch (Exception e) {
            LOGGER.error("Get keyex error :", e);
        }
    }


}
