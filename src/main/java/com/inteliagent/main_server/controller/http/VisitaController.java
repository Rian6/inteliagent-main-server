package com.inteliagent.main_server.controller.http;

import com.inteliagent.main_server.dto.*;
import com.inteliagent.main_server.entity.Location;
import com.inteliagent.main_server.entity.Visita;
import com.inteliagent.main_server.service.VisitaService;
import com.inteliagent.main_server.tools.RouterOptimize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.google.ortools.constraintsolver.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class VisitaController {
    //@Value("${google.maps.api.key}")
    private String googleMapsApiKey;

    @Autowired
    private VisitaService visitaService;

    private RouterOptimize routeOptimizer;
    public VisitaController() {
        this.routeOptimizer = new RouterOptimize();
    }
    @GetMapping("/ultimas-visitas")
    public ResponseEntity<?> getUltimasVisitas() {
        try {
            List<VisitaDTO> ultimasVisitas = visitaService.getUltimasVisitas();
            return ResponseEntity.created(new URI("")).body(ultimasVisitas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao syncronizar cargas: " + e.getMessage());
        }
    }

    @PostMapping("/visita/salvar")
    public ResponseEntity<?> salvar(@RequestBody List<VisitaSalvarDTO> visitaSalvarDTO) {
        try {
            visitaService.salvar(visitaSalvarDTO);
            return ResponseEntity.ok("");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao syncronizar cargas: " + e.getMessage());
        }
    }

    @GetMapping("/visitas-por-dia")
    public ResponseEntity<?> getVisitasPorDia() {
        try {
            List<VisitasPorDiaDTO> visitas = visitaService.getVisitasPorDia();
            return ResponseEntity.created(new URI("")).body(visitas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao syncronizar cargas: " + e.getMessage());
        }
    }

    @GetMapping("/visitas-por-regiao")
    public ResponseEntity<?> getVisitasPorRegião() {
        try {
            List<VisitasPorRegiaoDTO> visitas = visitaService.getVisitasPorRegião();
            return ResponseEntity.created(new URI("")).body(visitas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao syncronizar cargas: " + e.getMessage());
        }
    }

    @GetMapping("/visitas-list/{uuid}")
    public ResponseEntity<?> getAllPlanejamentos(@PathVariable String uuid) {
        try {
            List<VisitaListDTO> visitas = visitaService.getVisitasByPlanejamento(uuid);
            return ResponseEntity.created(new URI("")).body(visitas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao syncronizar cargas: " + e.getMessage());
        }
    }

    @PostMapping("/optimize-route")
    public List<Visita> optimizeRoute(@RequestBody List<Visita> visitas) {
        return routeOptimizer.optimizeRoute(visitas);
    }
}
