package com.inteliagent.main_server.controller.http;
import com.inteliagent.main_server.configuration.CurrentUser;
import org.springframework.web.bind.annotation.*;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import java.util.*;
@RestController @RequestMapping("/api")
public class ReportController {
 private final NamedParameterJdbcTemplate db;
 public ReportController(NamedParameterJdbcTemplate db){this.db=db;}
 private Map<String,Object> upper(Map<String,Object> row){var result=new LinkedHashMap<String,Object>();row.forEach((k,v)->result.put(k.toUpperCase(Locale.ROOT),v));return result;}
 @GetMapping("/planejamento/{id}/boletim") @org.springframework.transaction.annotation.Transactional(readOnly=true)
 public Map<String,Object> report(@PathVariable String id){
 UUID.fromString(id);var args=Map.<String,Object>of("id",id,"cede",CurrentUser.cede());
 var plans=db.queryForList("SELECT * FROM PLANEJAMENTO WHERE ID=:id AND ID_CEDE=:cede AND COALESCE(SITUACAO,1)<>0",args);
 if(plans.isEmpty())throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND);
 var visits=db.queryForList("SELECT * FROM VISITA WHERE ID_PLANEJAMENTO=:id AND COALESCE(SITUACAO,1)<>0 ORDER BY PRIMEIRA_VISITA,SEQUENCIA,ID LIMIT 5001",args).stream().map(this::upper).toList();
 if(visits.size()>5000)throw new IllegalArgumentException("Use planejamentos menores para emitir o boletim (máximo 5000 atendimentos).");
 for(String table:List.of("INSPECAO","AMOSTRA","TRATAMENTO")){
 var children=db.queryForList("SELECT t.* FROM "+table+" t JOIN VISITA v ON v.ID=t.ID_VISITA WHERE v.ID_PLANEJAMENTO=:id AND COALESCE(v.SITUACAO,1)<>0 AND COALESCE(t.SITUACAO,1)<>0 ORDER BY t.ID",args).stream().map(this::upper).toList();
 var grouped=children.stream().collect(java.util.stream.Collectors.groupingBy(r->String.valueOf(r.get("ID_VISITA"))));
 for(var v:visits)v.put(table.toLowerCase(Locale.ROOT),grouped.getOrDefault(String.valueOf(v.get("ID")),List.of()));
 }
 return Map.of("plan",upper(plans.getFirst()),"visits",visits);
 }
 @GetMapping("/indicadores")
 @org.springframework.transaction.annotation.Transactional(readOnly=true)
 public Map<String,Object> indicators(){
  var args=Map.of("cede",CurrentUser.cede());
  var result=new LinkedHashMap<String,Object>(db.queryForMap("""
    SELECT COUNT(*) AS planejamentos,
      COALESCE(SUM(CASE WHEN STATUS=3 THEN 1 ELSE 0 END),0) AS emAndamento,
      COALESCE(SUM(CASE WHEN STATUS=2 THEN 1 ELSE 0 END),0) AS planosPendentes,
      COALESCE(SUM(CASE WHEN STATUS=1 THEN 1 ELSE 0 END),0) AS planosConcluidos
    FROM PLANEJAMENTO WHERE ID_CEDE=:cede AND COALESCE(SITUACAO,1)<>0
    """,args));
  result.putAll(db.queryForMap("""
    SELECT COUNT(*) AS totalVisitas,
      COALESCE(SUM(CASE WHEN v.SITUACAO_REFERENCIA=3 THEN 1 ELSE 0 END),0) AS realizadas,
      COALESCE(SUM(CASE WHEN DATE(v.PRIMEIRA_VISITA)=CURRENT_DATE AND v.SITUACAO_REFERENCIA=3 THEN 1 ELSE 0 END),0) AS hoje,
      COALESCE(SUM(CASE WHEN v.PRIMEIRA_VISITA>=CURRENT_DATE-INTERVAL 6 DAY AND v.PRIMEIRA_VISITA<CURRENT_DATE+INTERVAL 1 DAY AND v.SITUACAO_REFERENCIA=3 THEN 1 ELSE 0 END),0) AS realizadas7Dias,
      COALESCE(SUM(CASE WHEN v.SITUACAO_REFERENCIA=1 THEN 1 ELSE 0 END),0) AS fechados,
      COALESCE(SUM(CASE WHEN v.SITUACAO_REFERENCIA=2 THEN 1 ELSE 0 END),0) AS recusados,
      COALESCE(SUM(CASE WHEN COALESCE(v.SITUACAO_REFERENCIA,5)=5 THEN 1 ELSE 0 END),0) AS aVisitar,
      COALESCE(SUM(CASE WHEN v.CONTEM_AMOSTRA=1 AND v.SITUACAO_REFERENCIA=3 THEN 1 ELSE 0 END),0) AS comAmostra,
      COALESCE(SUM(CASE WHEN v.CONTEM_TRATAMENTO=1 AND v.SITUACAO_REFERENCIA=3 THEN 1 ELSE 0 END),0) AS comTratamento
    FROM VISITA v JOIN PLANEJAMENTO p ON p.ID=v.ID_PLANEJAMENTO
    WHERE p.ID_CEDE=:cede AND COALESCE(p.SITUACAO,1)<>0 AND COALESCE(v.SITUACAO,1)<>0
    """,args));
  return result;
 }
}
