package com.inteliagent.main_server.persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.HashMap;

@Repository
public class SyncPersistence {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public void genericSave(String sql) throws Exception{
        jdbcTemplate.update(sql, new HashMap<>());
    }
}
