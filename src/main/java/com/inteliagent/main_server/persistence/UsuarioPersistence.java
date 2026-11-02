package com.inteliagent.main_server.persistence;

import com.inteliagent.main_server.dto.*;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Repository
public class UsuarioPersistence {
    public UsuarioPersistence(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    private final NamedParameterJdbcTemplate jdbcTemplate;
    public List<UsuarioDTO> getLocationUsers(){
        HashMap<String, Object> sqlMap = new HashMap<>();

        final String sql = "SELECT \n" +
                " u.longitude," +
                " u.latitude,\n" +
                " u.nome\n" +
                "FROM \n" +
                "    usuario u WHERE u.id_cede = :cede AND u.situacao = 1 LIMIT 500";

        return jdbcTemplate.query(sql, java.util.Map.of("cede", com.inteliagent.main_server.configuration.CurrentUser.cede()), (rs, rowNum) -> {
            UsuarioDTO usuarioDTO = new UsuarioDTO();
            usuarioDTO.setNome(rs.getString("NOME"));
            usuarioDTO.setLatitude(rs.getString("LATITUDE"));
            usuarioDTO.setLongitude(rs.getString("LONGITUDE"));

            return usuarioDTO;
        });
    }
}
