package com.autotest.api.db.jdbc;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class AutotestSpringBootJdbcManager {
    private final static Logger LOGGER = LoggerFactory.getLogger(AutotestSpringBootJdbcManager.class);

    @Autowired
    JdbcTemplate jdbcTemplate;

    /**
     * 执行sql语句
     *
     * @param sql ：sql语句
     */
    public void execute(String sql) {
        jdbcTemplate.execute(sql);
    }

    /**
     * 执行查询语句，获取单个字段的返回
     *
     * @param sql
     * @return
     */
    public String query(String sql) {
        String result = jdbcTemplate.queryForObject(sql, String.class);
        return result;
    }

    public Map queryToMap(String sql) {
        try {
            Map result = jdbcTemplate.queryForMap(sql);
            return result;
        } catch (EmptyResultDataAccessException e) {
            // queryForMap这个方法查不到结果时会返回null，因此做个健壮性处理
            return null;
        }
    }

    public List queryToList(String sql) {
        List result = jdbcTemplate.queryForList(sql);
        return result;
    }
}
