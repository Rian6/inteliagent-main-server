package com.inteliagent.main_server.controller.http;

import com.inteliagent.main_server.dto.*;
import com.inteliagent.main_server.entity.Visita;
import com.inteliagent.main_server.service.RouteOptimizationService;
import com.inteliagent.main_server.service.VisitaService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** HTTP adaptation only; authorization and persistence belong to application services. */
@RestController
@RequestMapping("/api")
public class VisitaController {
    private final VisitaService visits;
    private final RouteOptimizationService routes;
    private final com.inteliagent.main_server.persistence.VisitaPersistence persistence;

    public VisitaController(VisitaService visits, RouteOptimizationService routes, com.inteliagent.main_server.persistence.VisitaPersistence persistence) {
        this.visits = visits;
        this.routes = routes;
        this.persistence = persistence;
    }

    @GetMapping("/ultimas-visitas")
    public List<VisitaDTO> latest() { return visits.getUltimasVisitas(); }

    @PostMapping("/visita/salvar")
    public ResponseEntity<Void> save(@RequestBody List<VisitaSalvarDTO> batch) {
        visits.salvar(batch);
        return ResponseEntity.ok().build();
    }

    public record VisitOrder(String idPlanejamento,List<String> ids){}
    @PostMapping("/visita/reordenar")
    public ResponseEntity<Void> reorder(@RequestBody VisitOrder order){persistence.reorder(order.idPlanejamento(),order.ids());return ResponseEntity.ok().build();}

    @GetMapping("/visitas-por-dia")
    public List<VisitasPorDiaDTO> byDay() { return visits.getVisitasPorDia(); }

    @GetMapping("/visitas-por-regiao")
    public List<VisitasPorRegiaoDTO> byRegion() { return visits.getVisitasPorRegião(); }

    @GetMapping("/visitas-list/{uuid}")
    public List<VisitaListDTO> list(@PathVariable String uuid) { return visits.getVisitasByPlanejamento(uuid); }

    @GetMapping("/visita/{id}/ficha")
    public java.util.Map<String,Object> details(@PathVariable String id) { return persistence.details(id); }

    @PostMapping("/optimize-route")
    public List<Visita> optimize(@RequestBody List<Visita> visits) { persistence.requireReorderPermission(); return routes.optimize(visits); }

    @DeleteMapping("/visita/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        return visits.delete(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
