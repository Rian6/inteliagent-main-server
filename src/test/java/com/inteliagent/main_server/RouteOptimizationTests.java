package com.inteliagent.main_server;

import com.inteliagent.main_server.entity.Location;
import com.inteliagent.main_server.entity.Visita;
import com.inteliagent.main_server.service.RouteOptimizationService;
import com.inteliagent.main_server.service.RoutingBusyException;
import com.inteliagent.main_server.tools.RouterOptimize;
import java.util.List;
import java.util.concurrent.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class RouteOptimizationTests {
    @AfterEach void clear() { SecurityContextHolder.clearContext(); }
    private void role(String role) {
        var auth = new TestingAuthenticationToken("user", "none", List.of(new SimpleGrantedAuthority("ROLE_" + role)));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
    private Visita visit(long id, double latitude, double longitude) {
        var visit = new Visita(); visit.setId(id); visit.setLocation(new Location(latitude, longitude)); return visit;
    }
    @Test void rejectsBadCoordinatesEvenWithOnlyOneVisit() {
        var solver = new RouterOptimize();
        assertThrows(IllegalArgumentException.class, () -> solver.optimizeRoute(List.of(new Visita())));
        assertThrows(IllegalArgumentException.class, () -> solver.optimizeRoute(List.of(visit(1, Double.NaN, 0))));
        assertThrows(IllegalArgumentException.class, () -> solver.optimizeRoute(List.of(visit(1, 0, 181))));
    }
    @Test void keepsEveryVisitAndAssignsContiguousOrder() {
        var input = List.of(visit(1, 0, 0), visit(2, 0, 1), visit(3, 0, 2), visit(4, 0, 3));
        var actual = new RouterOptimize().optimizeRoute(input);
        assertEquals(4, actual.size());
        assertEquals(1, actual.getFirst().getId());
        assertEquals(4, actual.stream().map(Visita::getId).distinct().count());
        for (int index = 0; index < actual.size(); index++) assertEquals(index, actual.get(index).getOrdem());
    }
    @Test void onlyAdminCanUseCpuAndBusyRequestsRecover() throws Exception {
        var optimizer = mock(RouterOptimize.class);
        var started = new CountDownLatch(1); var release = new CountDownLatch(1);
        when(optimizer.optimizeRoute(anyList())).thenAnswer(call -> {
            started.countDown();
            if (!release.await(5, TimeUnit.SECONDS)) throw new IllegalStateException("test timeout");
            return call.getArgument(0);
        });
        var service = new RouteOptimizationService(optimizer, 1);
        role("AGENT");
        assertThrows(org.springframework.security.access.AccessDeniedException.class, () -> service.optimize(List.of()));
        verifyNoInteractions(optimizer);
        try (var workers = Executors.newSingleThreadExecutor()) {
            var background = workers.submit(() -> { role("ADMIN"); return service.optimize(List.of()); });
            assertTrue(started.await(5, TimeUnit.SECONDS));
            role("ADMIN");
            assertThrows(RoutingBusyException.class, () -> service.optimize(List.of()));
            release.countDown();
            assertTrue(background.get(5, TimeUnit.SECONDS).isEmpty());
            assertTrue(service.optimize(List.of()).isEmpty());
        }
    }
}
