package com.inteliagent.main_server.persistence;

import com.inteliagent.main_server.dto.PlanejamentoDTO;
import com.inteliagent.main_server.dto.PlanejamentoSalvarDTO;
import com.inteliagent.main_server.dto.VisitasPorDiaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Repository
public class PlanejamentoPersistence {
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public List<PlanejamentoDTO> getAllPlanejamentos(){
        final String sql = "select id as codigo, nome as descricao, zona  from planejamento\n";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            PlanejamentoDTO planejamentoDTO = new PlanejamentoDTO();
            planejamentoDTO.setCodigo(rs.getString("codigo"));
            planejamentoDTO.setDescricao(rs.getString("descricao"));
            planejamentoDTO.setZona(rs.getString("zona"));
            return planejamentoDTO;
        });
    }

    public PlanejamentoDTO findByUUID(String uuid){
        final String sql = "select id as codigo, nome as descricao, zona from planejamento where id = :UUID\n";
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("UUID",uuid);

        return jdbcTemplate.queryForObject(sql, hashMap,(rs, rowNum) -> {
            PlanejamentoDTO planejamentoDTO = new PlanejamentoDTO();
            planejamentoDTO.setCodigo(rs.getString("codigo"));
            planejamentoDTO.setDescricao(rs.getString("descricao"));
            planejamentoDTO.setZona(rs.getString("zona"));
            return planejamentoDTO;
        });
    }

    public String salvar(PlanejamentoSalvarDTO planejamentoSalvarDTO){
        HashMap<String, Object> map = new HashMap<>();
        String uuid = planejamentoSalvarDTO.getUuid().equals("0") ? UUID.randomUUID().toString() : planejamentoSalvarDTO.getUuid();
        map.put("ID", uuid);
        map.put("NOME", planejamentoSalvarDTO.getNomeRegiao());
        map.put("ZONA", planejamentoSalvarDTO.getZona());
        map.put("STATUS", planejamentoSalvarDTO.getSituacao());

        String sql = "replace into PLANEJAMENTO (id, NOME, ZONA, STATUS, DATA_ULT_VISITA, SITUACAO) VALUES (:ID, :NOME, :ZONA, :STATUS, NOW(), 1)";
        jdbcTemplate.update(sql, map);

        return uuid;
    }
}
