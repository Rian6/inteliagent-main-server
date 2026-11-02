package com.inteliagent.main_server.persistence;

import com.inteliagent.main_server.dto.*;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import com.inteliagent.main_server.configuration.CurrentUser;

@Repository
public class VisitaPersistence {
    public VisitaPersistence(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    private final NamedParameterJdbcTemplate jdbcTemplate;

    @org.springframework.transaction.annotation.Transactional(readOnly=true)
    public java.util.Map<String,Object> details(String id) {
        java.util.UUID.fromString(id);
        var args = java.util.Map.of("id",id,"cede",CurrentUser.cede());
        var rows = jdbcTemplate.queryForList("SELECT v.* FROM visita v JOIN planejamento p ON p.ID=v.ID_PLANEJAMENTO WHERE v.ID=:id AND p.ID_CEDE=:cede AND COALESCE(v.SITUACAO,1)<>0",args);
        if (rows.isEmpty()) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND);
        var result = new java.util.HashMap<String,Object>();
        result.put("visita",canonicalFields(rows.getFirst()));
        for (String table : java.util.List.of("INSPECAO","AMOSTRA","TRATAMENTO")) result.put(table.toLowerCase(),jdbcTemplate.queryForList("SELECT * FROM "+table+" WHERE ID_VISITA=:id AND COALESCE(SITUACAO,1)<>0",args).stream().map(this::canonicalFields).toList());
        return result;
    }
    private java.util.Map<String,Object> canonicalFields(java.util.Map<String,Object> row) {
        var result=new java.util.LinkedHashMap<String,Object>();
        row.forEach((key,value)->result.put(key.toUpperCase(java.util.Locale.ROOT),value));
        return result;
    }

    public boolean delete(String id) {
        return jdbcTemplate.update("UPDATE visita v JOIN planejamento p ON p.id=v.id_planejamento SET v.situacao=0,v.sync_version=v.sync_version+1 WHERE v.id=:id AND p.id_cede=:cede AND COALESCE(v.situacao,1)<>0", java.util.Map.of("id",id,"cede",CurrentUser.cede())) == 1;
    }

    public void salvar(VisitaSalvarDTO visitaSalvarDTO){
        HashMap<String, Object> map = new HashMap<>();
        String uuid = visitaSalvarDTO.getUuid() == null || visitaSalvarDTO.getUuid().equals("0") ? UUID.randomUUID().toString() : visitaSalvarDTO.getUuid();
        var cede = java.util.Map.of("id", visitaSalvarDTO.getIdPlanejamento(), "cede", CurrentUser.cede());
        if (jdbcTemplate.queryForObject("SELECT COUNT(*) FROM planejamento WHERE id = :id AND id_cede = :cede", cede, Integer.class) != 1) throw new org.springframework.security.access.AccessDeniedException("Planejamento inacessível");
        if (jdbcTemplate.queryForObject("SELECT COUNT(*) FROM visita v JOIN planejamento p ON p.id = v.id_planejamento WHERE v.id = :id AND p.id_cede <> :cede", java.util.Map.of("id", uuid, "cede", CurrentUser.cede()), Integer.class) > 0) throw new org.springframework.security.access.AccessDeniedException("Visita inacessível");
        jdbcTemplate.queryForList("SELECT ID FROM planejamento WHERE ID=:id AND ID_CEDE=:cede FOR UPDATE",cede);
        var previous=jdbcTemplate.queryForList("SELECT ID_PLANEJAMENTO,SEQUENCIA FROM visita WHERE ID=:id FOR UPDATE",java.util.Map.of("id",uuid));
        boolean canReorder=com.inteliagent.main_server.configuration.VisitPermission.allowed(jdbcTemplate,true);
        Integer sequence=visitaSalvarDTO.getSequencia();
        if(previous.isEmpty()) {
            if(!com.inteliagent.main_server.configuration.VisitPermission.allowed(jdbcTemplate,false))throw new org.springframework.security.access.AccessDeniedException("Sem permissão para adicionar visitas");
            if(!canReorder || sequence==null)sequence=jdbcTemplate.queryForObject("SELECT COALESCE(MAX(SEQUENCIA),0)+1 FROM visita WHERE ID_PLANEJAMENTO=:id",cede,Integer.class);
        } else {
            if(sequence==null)sequence=(Integer)previous.getFirst().get("SEQUENCIA");
            if(!java.util.Objects.equals(previous.getFirst().get("ID_PLANEJAMENTO"),visitaSalvarDTO.getIdPlanejamento()))throw new org.springframework.security.access.AccessDeniedException("Vínculo imutável");
            if(!CurrentUser.admin())throw new org.springframework.security.access.AccessDeniedException("Use a opção de alterar a ordem para reorganizar visitas");
            if(!canReorder && !java.util.Objects.equals(previous.getFirst().get("SEQUENCIA"),sequence))throw new org.springframework.security.access.AccessDeniedException("Sem permissão para alterar a ordem das visitas");
        }
        map.put("ID", uuid);
        com.inteliagent.main_server.configuration.ReportMetadata.validate(visitaSalvarDTO.getBoletimJson());
        map.put("BOLETIM_JSON", visitaSalvarDTO.getBoletimJson());
        if (visitaSalvarDTO.getNome() == null || visitaSalvarDTO.getNome().isBlank() || visitaSalvarDTO.getNome().length() > 255 || visitaSalvarDTO.getNumero() != null && visitaSalvarDTO.getNumero().length() > 32) throw new IllegalArgumentException("Endereço inválido");
        if (visitaSalvarDTO.getTipoVisita() != null && !java.util.List.of("N","R").contains(visitaSalvarDTO.getTipoVisita())) throw new IllegalArgumentException("Tipo da visita inválido");
        if (visitaSalvarDTO.getLat()!=null && (!Double.isFinite(visitaSalvarDTO.getLat()) || Math.abs(visitaSalvarDTO.getLat())>90) || visitaSalvarDTO.getLng()!=null && (!Double.isFinite(visitaSalvarDTO.getLng()) || Math.abs(visitaSalvarDTO.getLng())>180)) throw new IllegalArgumentException("Coordenadas inválidas");
        map.put("TIPO_VISITA", visitaSalvarDTO.getTipoVisita());
        map.put("COMPLEMENTO", visitaSalvarDTO.getComplemento());
        map.put("LADO", visitaSalvarDTO.getLado());
        map.put("TIPO_IMOVEL", visitaSalvarDTO.getTipoImovel());
        map.put("NOME", visitaSalvarDTO.getNome());
        map.put("CEP", visitaSalvarDTO.getCep());
        map.put("LATITUDE", visitaSalvarDTO.getLat());
        map.put("LONGITUDE", visitaSalvarDTO.getLng());
        map.put("NUMERO", visitaSalvarDTO.getNumero());
        map.put("SEQUENCIA", sequence);
        map.put("IDPLANEJAMENTO", visitaSalvarDTO.getIdPlanejamento());

        String sql = "INSERT INTO VISITA (id, NOME, CEP, NUMERO, SEQUENCIA, ID_PLANEJAMENTO, LATITUDE, LONGITUDE, SITUACAO, TIPO_VISITA, COMPLEMENTO, LADO, TIPO_IMOVEL, SITUACAO_REFERENCIA, BOLETIM_JSON) VALUES (:ID, :NOME, :CEP, :NUMERO, :SEQUENCIA, :IDPLANEJAMENTO, :LATITUDE, :LONGITUDE, 1, COALESCE(:TIPO_VISITA,'N'), :COMPLEMENTO, :LADO, :TIPO_IMOVEL, 5, :BOLETIM_JSON) ON DUPLICATE KEY UPDATE BOLETIM_JSON=COALESCE(:BOLETIM_JSON,BOLETIM_JSON), NOME=:NOME, CEP=:CEP, NUMERO=:NUMERO, SEQUENCIA=:SEQUENCIA, LATITUDE=:LATITUDE, LONGITUDE=:LONGITUDE, TIPO_VISITA=COALESCE(:TIPO_VISITA,TIPO_VISITA), COMPLEMENTO=COALESCE(:COMPLEMENTO,COMPLEMENTO), LADO=COALESCE(:LADO,LADO), TIPO_IMOVEL=COALESCE(:TIPO_IMOVEL,TIPO_IMOVEL), sync_version=sync_version+1";
        jdbcTemplate.update(sql, map);
    }

    public void requireReorderPermission() {
        if(!com.inteliagent.main_server.configuration.VisitPermission.allowed(jdbcTemplate,true))throw new org.springframework.security.access.AccessDeniedException("Sem permissão para alterar a ordem das visitas");
    }
    @org.springframework.transaction.annotation.Transactional(timeout=15)
    public void reorder(String planId,List<String> ids) {
        if(!com.inteliagent.main_server.configuration.VisitPermission.allowed(jdbcTemplate,true))throw new org.springframework.security.access.AccessDeniedException("Sem permissão para alterar a ordem das visitas");
        java.util.UUID.fromString(planId);
        if(ids==null || ids.size()>500 || new java.util.HashSet<>(ids).size()!=ids.size())throw new IllegalArgumentException("Ordem inválida");
        ids.forEach(java.util.UUID::fromString);
        var args=java.util.Map.of("id",planId,"cede",CurrentUser.cede());
        if(jdbcTemplate.queryForList("SELECT ID FROM planejamento WHERE ID=:id AND ID_CEDE=:cede FOR UPDATE",args).isEmpty())throw new org.springframework.security.access.AccessDeniedException("Planejamento inacessível");
        var current=jdbcTemplate.queryForList("SELECT ID FROM visita WHERE ID_PLANEJAMENTO=:id AND COALESCE(SITUACAO,1)<>0 FOR UPDATE",args,String.class);
        if(!new java.util.HashSet<>(current).equals(new java.util.HashSet<>(ids)))throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT,"A lista de visitas mudou. Atualize antes de alterar a ordem.");
        for(int i=0;i<ids.size();i++)jdbcTemplate.update("UPDATE visita SET SEQUENCIA=:sequence,sync_version=sync_version+1 WHERE ID=:id",java.util.Map.of("id",ids.get(i),"sequence",i+1));
    }

    public List<VisitaDTO> getUltimasVisitas(){
        final String sql = "select v.id, p.zona as regiao, v.created_at as data from visita v \n" +
                "inner join planejamento p on p.ID = v.ID_PLANEJAMENTO WHERE COALESCE(v.situacao,1) <> 0 AND p.id_cede = :cede \n" +
                "order by v.updated_at desc\n" +
                "limit 5";

        return jdbcTemplate.query(sql, java.util.Map.of("cede", CurrentUser.cede()), (rs, rowNum) -> {
            VisitaDTO visitaDTO = new VisitaDTO();
            visitaDTO.setId(rs.getString("id"));
            visitaDTO.setRegiao(rs.getString("regiao"));
            visitaDTO.setData(rs.getString("data"));
            return visitaDTO;
        });
    }

    public List<VisitasPorRegiaoDTO> getVisitasPorRegião(){
        final String sql = "select p.zona as regiao, count(1) as quantidade from visita v \n" +
                "inner join planejamento p on p.ID = v.ID_PLANEJAMENTO WHERE COALESCE(v.situacao,1) <> 0 AND p.id_cede = :cede \n" +
                "group by p.ZONA";

        return jdbcTemplate.query(sql, java.util.Map.of("cede", CurrentUser.cede()), (rs, rowNum) -> {
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
                "WHERE COALESCE(v.situacao,1) <> 0 AND p.id_cede = :cede AND v.created_at >= CURRENT_DATE - INTERVAL 90 DAY \n" +
                "GROUP BY \n" +
                "    DATE(v.created_at) ORDER BY data\n";

        return jdbcTemplate.query(sql, java.util.Map.of("cede", CurrentUser.cede()), (rs, rowNum) -> {
            VisitasPorDiaDTO visitasPorDiaDTO = new VisitasPorDiaDTO();
            visitasPorDiaDTO.setData(rs.getString("data"));
            visitasPorDiaDTO.setQuantidade(rs.getInt("quantidade"));
            return visitasPorDiaDTO;
        });
    }

    public List<VisitaListDTO> getVisitasByPlanejamento(String idPlanejamento){
        HashMap<String, Object> sqlMap = new HashMap<>();
        sqlMap.put("IDPLANEJAMENTO", idPlanejamento);
        sqlMap.put("cede", CurrentUser.cede());

        final String sql = "SELECT \n" +
                " v.id," +
                " v.cep," +
                " v.numero,\n" +
                " v.sequencia,\n" +
                " v.latitude,\n" +
                " v.longitude,\n" +
                " v.BOLETIM_JSON, v.TIPO_VISITA, v.COMPLEMENTO, v.LADO, v.TIPO_IMOVEL, v.nome\n" +
                "FROM \n" +
                "    visita v\n" +
                "INNER JOIN \n" +
                "    planejamento p \n" +
                "ON \n" +
                "    p.ID = v.ID_PLANEJAMENTO\n" +
                "   WHERE COALESCE(v.situacao,1) <> 0 AND p.id = :IDPLANEJAMENTO AND p.id_cede = :cede";

        return jdbcTemplate.query(sql, sqlMap, (rs, rowNum) -> {
            VisitaListDTO visitaListDTO = new VisitaListDTO();
            visitaListDTO.setBoletimJson(rs.getString("BOLETIM_JSON"));
            visitaListDTO.setId(rs.getString("ID"));
            visitaListDTO.setCep(rs.getString("CEP"));
            visitaListDTO.setNumero(rs.getString("NUMERO"));
            visitaListDTO.setSequencia(rs.getInt("SEQUENCIA"));
            visitaListDTO.setTipoVisita(rs.getString("TIPO_VISITA"));
            visitaListDTO.setComplemento(rs.getString("COMPLEMENTO"));
            visitaListDTO.setLado(rs.getString("LADO"));
            visitaListDTO.setTipoImovel(rs.getString("TIPO_IMOVEL"));
            visitaListDTO.setLat((Double)rs.getObject("LATITUDE"));
            visitaListDTO.setLng((Double)rs.getObject("LONGITUDE"));
            visitaListDTO.setNome(rs.getString("NOME"));

            return visitaListDTO;
        });
    }
}
