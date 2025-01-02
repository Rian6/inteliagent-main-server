package com.inteliagent.main_server.controller.http;

import com.inteliagent.main_server.dto.PlanejamentoDTO;
import com.inteliagent.main_server.dto.UsuarioDTO;
import com.inteliagent.main_server.service.UsuarioService;
import com.inteliagent.main_server.service.VisitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/usuario-location")
    public ResponseEntity<?> getAllPlanejamentos() {
        try {
            List<UsuarioDTO> usuarioDTOS = usuarioService.getLocationUsers();
            return ResponseEntity.created(new URI("")).body(usuarioDTOS);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao syncronizar cargas: " + e.getMessage());
        }
    }
}
