package com.inteliagent.main_server.controller.http;

import com.inteliagent.main_server.dto.*;
import com.inteliagent.main_server.service.PlanejamentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
@RestController
@RequestMapping("/api")
public class PlanejamentoController {
    public PlanejamentoController(PlanejamentoService planejamentoService) {
        this.planejamentoService = planejamentoService;
    }


    private final PlanejamentoService planejamentoService;

    @GetMapping("/find-by-uuid/{uuid}")
    public ResponseEntity<?> findByUUID(@PathVariable String uuid) {

            PlanejamentoDTO planejamento = planejamentoService.findByUUID(uuid);

            if (planejamento == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Planejamento não encontrado para o UUID: " + uuid);
            }

            return ResponseEntity.ok(planejamento);

    }

    @GetMapping("/planejamento-list")
    public ResponseEntity<?> getAllPlanejamentos() {

            List<PlanejamentoDTO> ultimasVisitas = planejamentoService.getAllPlanejamentos();
            return ResponseEntity.ok(ultimasVisitas);

    }

    @PostMapping("/planejamento/salvar")
    public ResponseEntity<?> salvar(@RequestBody PlanejamentoSalvarDTO planejamentoSalvarDTO) {

            String uuid = planejamentoService.salvar(planejamentoSalvarDTO);
            planejamentoSalvarDTO.setUuid(uuid);
            return ResponseEntity.ok(planejamentoSalvarDTO);

    }
}
