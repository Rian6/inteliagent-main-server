package com.inteliagent.main_server.controller.http;

import com.inteliagent.main_server.entity.Sync;
import com.inteliagent.main_server.service.SyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class SyncController {

    @Autowired
    private SyncService syncService;

    @PostMapping("/sync")
    public ResponseEntity<?> sync(@RequestBody List<Sync> syncList) {
        try {
            List<String> confirmLoads = syncService.sync(syncList);
            return ResponseEntity.created(new URI("")).body(confirmLoads);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao syncronizar cargas: " + e.getMessage());
        }
    }
}
