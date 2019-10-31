package com.tongbanjie.hoop.storage.mysql.repository.jdbc.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author xu.qiang
 * @date 2017/2/28.
 */
public class JdbcSupport {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public JdbcSupport() {
    }

    public JdbcSupport(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
