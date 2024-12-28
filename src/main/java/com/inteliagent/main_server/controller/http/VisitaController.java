package com.inteliagent.main_server.controller.http;

import com.inteliagent.main_server.dto.*;
import com.inteliagent.main_server.service.VisitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class VisitaController {

    @Autowired
    private VisitaService visitaService;

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
}
