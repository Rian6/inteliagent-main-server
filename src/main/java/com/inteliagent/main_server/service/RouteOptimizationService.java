package com.inteliagent.main_server.service;

import com.inteliagent.main_server.configuration.CurrentUser;
import com.inteliagent.main_server.entity.Visita;
import com.inteliagent.main_server.tools.RouterOptimize;
import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

/** Separate CPU-bound routing use case; currently runs within the main process. */
@Service
public class RouteOptimizationService {
    private final RouterOptimize optimizer;
    private final java.util.concurrent.Semaphore slots;

    public RouteOptimizationService(RouterOptimize optimizer, @org.springframework.beans.factory.annotation.Value("${routing.max-concurrent:2}") int concurrency) {
        if (concurrency < 1 || concurrency > 8) throw new IllegalArgumentException("Concorrência de rotas inválida");
        this.optimizer = optimizer;
        this.slots = new java.util.concurrent.Semaphore(concurrency);
    }

    public List<Visita> optimize(List<Visita> visits) {
        if (!CurrentUser.admin()) throw new AccessDeniedException("Administrador necessário");
        if (!slots.tryAcquire()) throw new RoutingBusyException();
        try { return optimizer.optimizeRoute(visits); }
        finally { slots.release(); }
    }
}
