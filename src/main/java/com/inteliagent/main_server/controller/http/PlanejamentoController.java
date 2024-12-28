package com.inteliagent.main_server.controller.http;

import com.inteliagent.main_server.dto.*;
import com.inteliagent.main_server.service.PlanejamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
@RestController
@RequestMapping("/api")
public class PlanejamentoController {

    @Autowired
    private PlanejamentoService planejamentoService;

    @GetMapping("/find-by-uuid/{uuid}")
    public ResponseEntity<?> findByUUID(@PathVariable String uuid) {
        try {
            PlanejamentoDTO planejamento = planejamentoService.findByUUID(uuid);

            if (planejamento == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Planejamento não encontrado para o UUID: " + uuid);
            }

            return ResponseEntity.ok(planejamento);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar planejamento: " + e.getMessage());
        }
    }

    @GetMapping("/planejamento-list")
    public ResponseEntity<?> getAllPlanejamentos() {
        try {
            List<PlanejamentoDTO> ultimasVisitas = planejamentoService.getAllPlanejamentos();
            return ResponseEntity.created(new URI("")).body(ultimasVisitas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao syncronizar cargas: " + e.getMessage());
        }
    }

    @PostMapping("/planejamento/salvar")
    public ResponseEntity<?> salvar(@RequestBody PlanejamentoSalvarDTO planejamentoSalvarDTO) {
        try {
            String uuid = planejamentoService.salvar(planejamentoSalvarDTO);
            planejamentoSalvarDTO.setUuid(uuid);
            return ResponseEntity.ok(planejamentoSalvarDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao syncronizar cargas: " + e.getMessage());
        }
    }
}
