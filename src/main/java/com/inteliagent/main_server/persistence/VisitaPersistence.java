package com.inteliagent.main_server.persistence;

import com.inteliagent.main_server.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Repository
public class VisitaPersistence {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public void salvar(VisitaSalvarDTO visitaSalvarDTO){
        HashMap<String, Object> map = new HashMap<>();
        String uuid = visitaSalvarDTO.getUuid() == null || visitaSalvarDTO.getUuid().equals("0") ? UUID.randomUUID().toString() : visitaSalvarDTO.getUuid();
        map.put("ID", uuid);
        map.put("NOME", visitaSalvarDTO.getNome());
        map.put("CEP", visitaSalvarDTO.getCep());
        map.put("NUMERO", visitaSalvarDTO.getNumero());
        map.put("SEQUENCIA", visitaSalvarDTO.getSequencia());
        map.put("IDPLANEJAMENTO", visitaSalvarDTO.getIdPlanejamento());

        String sql = "replace into VISITA (id, NOME, CEP, NUMERO, SEQUENCIA, ID_PLANEJAMENTO) VALUES (:ID, :NOME, :CEP, :NUMERO, :SEQUENCIA, :IDPLANEJAMENTO)";
        jdbcTemplate.update(sql, map);
    }

    public List<VisitaDTO> getUltimasVisitas(){
        final String sql = "select v.id, p.zona as regiao, v.created_at as data from visita v \n" +
                "inner join planejamento p on p.ID = v.ID_PLANEJAMENTO \n" +
                "order by v.updated_at desc\n" +
                "limit 5";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            VisitaDTO visitaDTO = new VisitaDTO();
            visitaDTO.setId(rs.getString("id"));
            visitaDTO.setRegiao(rs.getString("regiao"));
            visitaDTO.setData(rs.getString("data"));
            return visitaDTO;
        });
    }

    public List<VisitasPorRegiaoDTO> getVisitasPorRegião(){
        final String sql = "select p.zona as regiao, count(1) as quantidade from visita v \n" +
                "inner join planejamento p on p.ID = v.ID_PLANEJAMENTO \n" +
                "group by p.ZONA";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            VisitasPorRegiaoDTO visitasPorRegiaoDTO = new VisitasPorRegiaoDTO();
            visitasPorRegiaoDTO.setRegiao(rs.getString("regiao"));
            visitasPorRegiaoDTO.setQuantidade(rs.getDouble("quantidade"));
            return visitasPorRegiaoDTO;
        });
    }

    public List<VisitasPorDiaDTO> getVisitasPorDia(){
        final String sql = "SELECT \n" +
                "    DATE(v.created_at) AS data, \n" +
                "    COUNT(1) AS quantidade\n" +
                "FROM \n" +
                "    visita v\n" +
                "INNER JOIN \n" +
                "    planejamento p \n" +
                "ON \n" +
                "    p.ID = v.ID_PLANEJAMENTO\n" +
                "GROUP BY \n" +
                "    DATE(v.created_at)\n";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            VisitasPorDiaDTO visitasPorDiaDTO = new VisitasPorDiaDTO();
            visitasPorDiaDTO.setData(rs.getString("data"));
            visitasPorDiaDTO.setQuantidade(rs.getInt("quantidade"));
            return visitasPorDiaDTO;
        });
    }

    public List<VisitaListDTO> getVisitasByPlanejamento(String idPlanejamento){
        HashMap<String, Object> sqlMap = new HashMap<>();
        sqlMap.put("IDPLANEJAMENTO", idPlanejamento);

        final String sql = "SELECT \n" +
                " v.id," +
                " v.cep," +
                " v.numero,\n" +
                " v.sequencia,\n" +
                " v.nome\n" +
                "FROM \n" +
                "    visita v\n" +
                "INNER JOIN \n" +
                "    planejamento p \n" +
                "ON \n" +
                "    p.ID = v.ID_PLANEJAMENTO\n" +
                "   WHERE p.id = :IDPLANEJAMENTO";

        return jdbcTemplate.query(sql, sqlMap, (rs, rowNum) -> {
            VisitaListDTO visitaListDTO = new VisitaListDTO();
            visitaListDTO.setId(rs.getString("ID"));
            visitaListDTO.setCep(rs.getString("CEP"));
            visitaListDTO.setNumero(rs.getString("NUMERO"));
            visitaListDTO.setSequencia(rs.getInt("SEQUENCIA"));
            visitaListDTO.setNome(rs.getString("NOME"));

            return visitaListDTO;
        });
    }
}
