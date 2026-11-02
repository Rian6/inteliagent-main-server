package com.inteliagent.main_server.persistence;

import com.inteliagent.main_server.dto.PlanejamentoDTO;
import com.inteliagent.main_server.dto.PlanejamentoSalvarDTO;
import com.inteliagent.main_server.dto.VisitasPorDiaDTO;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import com.inteliagent.main_server.configuration.CurrentUser;

@Repository
public class PlanejamentoPersistence {
    public PlanejamentoPersistence(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<PlanejamentoDTO> getAllPlanejamentos(){
        final String sql = "select (SELECT COUNT(*) FROM VISITA v WHERE v.ID_PLANEJAMENTO=planejamento.ID AND COALESCE(v.SITUACAO,1)<>0) AS quantidade_visitas, id as codigo, nome as descricao, status, zona, ID_CIDADE, ID_CATEGORIA, ATIVIDADE, TIPO, ANO, CICLO, CATALOGO_VERSAO, BOLETIM_JSON, DATA_ULT_VISITA  from planejamento where id_cede = :cede ORDER BY updated_at DESC LIMIT 500\n";

        return jdbcTemplate.query(sql, java.util.Map.of("cede", CurrentUser.cede()), (rs, rowNum) -> {
            PlanejamentoDTO planejamentoDTO = new PlanejamentoDTO();
            planejamentoDTO.setQuantidadeVisitas(rs.getLong("quantidade_visitas"));
            planejamentoDTO.setBoletimJson(rs.getString("BOLETIM_JSON"));
            planejamentoDTO.setCodigo(rs.getString("codigo"));
            planejamentoDTO.setDescricao(rs.getString("descricao"));
            planejamentoDTO.setZona(rs.getString("zona"));
            planejamentoDTO.setSituacao(rs.getInt("status"));
            planejamentoDTO.setIdCidade((Integer)rs.getObject("ID_CIDADE"));
            planejamentoDTO.setIdCategoria((Integer)rs.getObject("ID_CATEGORIA"));
            planejamentoDTO.setAtividade((Integer)rs.getObject("ATIVIDADE"));
            planejamentoDTO.setTipo((Integer)rs.getObject("TIPO"));
            planejamentoDTO.setAno(rs.getString("ANO"));
            planejamentoDTO.setDataUltVisita(rs.getString("DATA_ULT_VISITA"));
            planejamentoDTO.setCiclo((Integer)rs.getObject("CICLO"));
            planejamentoDTO.setCatalogoVersao(rs.getString("CATALOGO_VERSAO"));
            return planejamentoDTO;
        });
    }

    public PlanejamentoDTO findByUUID(String uuid){
        final String sql = "select (SELECT COUNT(*) FROM VISITA v WHERE v.ID_PLANEJAMENTO=planejamento.ID AND COALESCE(v.SITUACAO,1)<>0) AS quantidade_visitas, id as codigo, nome as descricao, status, zona, ID_CIDADE, ID_CATEGORIA, ATIVIDADE, TIPO, ANO, CICLO, CATALOGO_VERSAO, BOLETIM_JSON, DATA_ULT_VISITA from planejamento where id = :UUID AND id_cede = :cede\n";
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("UUID",uuid);
        hashMap.put("cede", String.valueOf(CurrentUser.cede()));

        return jdbcTemplate.queryForObject(sql, hashMap,(rs, rowNum) -> {
            PlanejamentoDTO planejamentoDTO = new PlanejamentoDTO();
            planejamentoDTO.setQuantidadeVisitas(rs.getLong("quantidade_visitas"));
            planejamentoDTO.setBoletimJson(rs.getString("BOLETIM_JSON"));
            planejamentoDTO.setCodigo(rs.getString("codigo"));
            planejamentoDTO.setDescricao(rs.getString("descricao"));
            planejamentoDTO.setZona(rs.getString("zona"));
            planejamentoDTO.setSituacao(rs.getInt("status"));
            planejamentoDTO.setIdCidade((Integer)rs.getObject("ID_CIDADE"));
            planejamentoDTO.setIdCategoria((Integer)rs.getObject("ID_CATEGORIA"));
            planejamentoDTO.setAtividade((Integer)rs.getObject("ATIVIDADE"));
            planejamentoDTO.setTipo((Integer)rs.getObject("TIPO"));
            planejamentoDTO.setAno(rs.getString("ANO"));
            planejamentoDTO.setDataUltVisita(rs.getString("DATA_ULT_VISITA"));
            planejamentoDTO.setCiclo((Integer)rs.getObject("CICLO"));
            planejamentoDTO.setCatalogoVersao(rs.getString("CATALOGO_VERSAO"));
            return planejamentoDTO;
        });
    }

    public String salvar(PlanejamentoSalvarDTO planejamentoSalvarDTO){
        HashMap<String, Object> map = new HashMap<>();
        String uuid = (planejamentoSalvarDTO.getUuid() == null || planejamentoSalvarDTO.getUuid().equals("0")) ? UUID.randomUUID().toString() : planejamentoSalvarDTO.getUuid();
        if (planejamentoSalvarDTO.getNomeRegiao() == null || planejamentoSalvarDTO.getNomeRegiao().isBlank() || planejamentoSalvarDTO.getNomeRegiao().length() > 255) throw new IllegalArgumentException("Nome inválido");
        var owner = jdbcTemplate.queryForList("SELECT id_cede FROM planejamento WHERE id = :id", java.util.Map.of("id", uuid));
        if (!owner.isEmpty() && !java.util.Objects.equals(owner.get(0).get("id_cede"), CurrentUser.cede())) throw new org.springframework.security.access.AccessDeniedException("Sede diferente");
        if(!com.inteliagent.main_server.configuration.PlanningPermission.allowed(jdbcTemplate,!owner.isEmpty())) throw new org.springframework.security.access.AccessDeniedException("Sem permissão para este planejamento");
        java.util.UUID.fromString(uuid);
        com.inteliagent.main_server.configuration.ReportMetadata.validate(planejamentoSalvarDTO.getBoletimJson());
        map.put("boletimJson", com.inteliagent.main_server.configuration.PlanningMetadata.prepare(jdbcTemplate,uuid,planejamentoSalvarDTO.getBoletimJson(),owner.isEmpty()));
        map.put("CEDE", CurrentUser.cede());
        map.put("ID", uuid);
        map.put("NOME", planejamentoSalvarDTO.getNomeRegiao());
        map.put("ZONA", planejamentoSalvarDTO.getZona());
        map.put("STATUS", planejamentoSalvarDTO.getSituacao());
        if (!java.util.List.of(1,2,3).contains(planejamentoSalvarDTO.getSituacao())) throw new IllegalArgumentException("Situação inválida");
        if (planejamentoSalvarDTO.getAno()!=null) java.time.LocalDate.parse(planejamentoSalvarDTO.getAno());
        if (planejamentoSalvarDTO.getDataUltVisita()!=null) java.time.LocalDateTime.parse(planejamentoSalvarDTO.getDataUltVisita().replace(' ','T'));

        com.inteliagent.main_server.catalog.PlanningCatalog.validate(planejamentoSalvarDTO.getCatalogoVersao(),planejamentoSalvarDTO.getIdCidade(),planejamentoSalvarDTO.getIdCategoria(),planejamentoSalvarDTO.getAtividade(),planejamentoSalvarDTO.getTipo(),planejamentoSalvarDTO.getCiclo());
        map.put("ciclo",planejamentoSalvarDTO.getCiclo());
        map.put("cicloInformado",planejamentoSalvarDTO.hasCiclo());
        map.put("catalogoVersao",planejamentoSalvarDTO.getCatalogoVersao());
        map.put("idCidade", planejamentoSalvarDTO.getIdCidade());
        map.put("idCategoria", planejamentoSalvarDTO.getIdCategoria());
        map.put("atividade", planejamentoSalvarDTO.getAtividade());
        map.put("tipo", planejamentoSalvarDTO.getTipo());
        map.put("ano", planejamentoSalvarDTO.getAno());
        map.put("dataUltVisita", planejamentoSalvarDTO.getDataUltVisita());
        if(owner.isEmpty()){
          var required=new java.util.HashMap<String,Object>();
          required.put("ID_CIDADE",planejamentoSalvarDTO.getIdCidade());required.put("ID_CATEGORIA",planejamentoSalvarDTO.getIdCategoria());required.put("ATIVIDADE",planejamentoSalvarDTO.getAtividade());required.put("TIPO",planejamentoSalvarDTO.getTipo());required.put("CICLO",planejamentoSalvarDTO.getCiclo());required.put("NOME",planejamentoSalvarDTO.getNomeRegiao());required.put("ZONA",planejamentoSalvarDTO.getZona());required.put("ANO",planejamentoSalvarDTO.getAno());required.put("BOLETIM_JSON",planejamentoSalvarDTO.getBoletimJson());
          com.inteliagent.main_server.configuration.PlanningReportRequirements.validate(required);
        }

        String sql = "INSERT INTO PLANEJAMENTO (id, ID_CEDE, NOME, ZONA, STATUS, DATA_ULT_VISITA, SITUACAO, ID_CIDADE, ID_CATEGORIA, ATIVIDADE, TIPO, ANO, CICLO, CATALOGO_VERSAO, BOLETIM_JSON) VALUES (:ID, :CEDE, :NOME, :ZONA, :STATUS, COALESCE(:dataUltVisita,NOW()), 1, :idCidade, :idCategoria, :atividade, :tipo, :ano, :ciclo, :catalogoVersao, :boletimJson) ON DUPLICATE KEY UPDATE BOLETIM_JSON=COALESCE(:boletimJson,BOLETIM_JSON), NOME=:NOME, ZONA=:ZONA, STATUS=:STATUS, ID_CIDADE=COALESCE(:idCidade,ID_CIDADE), ID_CATEGORIA=COALESCE(:idCategoria,ID_CATEGORIA), ATIVIDADE=COALESCE(:atividade,ATIVIDADE), TIPO=COALESCE(:tipo,TIPO), ANO=COALESCE(:ano,ANO), CICLO=CASE WHEN :cicloInformado THEN :ciclo ELSE CICLO END, CATALOGO_VERSAO=COALESCE(:catalogoVersao,CATALOGO_VERSAO), DATA_ULT_VISITA=COALESCE(:dataUltVisita,DATA_ULT_VISITA), sync_version=sync_version+1";
        jdbcTemplate.update(sql, map);

        return uuid;
    }
}
