package com.inteliagent.main_server.tools;

import com.google.ortools.constraintsolver.*;
import com.inteliagent.main_server.entity.Location;
import com.inteliagent.main_server.entity.Visita;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/** Bounded native route calculation with no HTTP or database dependencies. */
@Component
public class RouterOptimize {
    static { com.google.ortools.Loader.loadNativeLibraries(); }

    public List<Visita> optimizeRoute(List<Visita> visits) {
        if (visits == null || visits.size() > 200) throw new IllegalArgumentException("Máximo 200 visitas");
        for (Visita visit : visits) {
            Location point = visit == null ? null : visit.getLocation();
            if (point == null || point.getLatitude() == null || point.getLongitude() == null
                    || !Double.isFinite(point.getLatitude()) || !Double.isFinite(point.getLongitude())
                    || Math.abs(point.getLatitude()) > 90 || Math.abs(point.getLongitude()) > 180) {
                throw new IllegalArgumentException("Coordenadas inválidas");
            }
        }
        if (visits.size() < 2) {
            if (!visits.isEmpty()) visits.getFirst().setOrdem(0);
            return List.copyOf(visits);
        }
        int count = visits.size();
        // Compute each geographic distance once, rather than inside every solver callback.
        long[][] distances = new long[count][count];
        for (int from = 0; from < count; from++) {
            for (int to = from + 1; to < count; to++) {
                distances[from][to] = distances[to][from] = distance(visits.get(from).getLocation(), visits.get(to).getLocation());
            }
        }
        RoutingIndexManager manager = new RoutingIndexManager(count, 1, 0);
        RoutingModel routing = null;
        Assignment solution = null;
        try {
            routing = new RoutingModel(manager);
            int callback = routing.registerTransitCallback((from, to) -> distances[manager.indexToNode(from)][manager.indexToNode(to)]);
            routing.setArcCostEvaluatorOfAllVehicles(callback);
            RoutingSearchParameters parameters = main.defaultRoutingSearchParameters().toBuilder()
                    .setTimeLimit(com.google.protobuf.Duration.newBuilder().setSeconds(2).build())
                    .setFirstSolutionStrategy(FirstSolutionStrategy.Value.PATH_CHEAPEST_ARC).build();
            solution = routing.solveWithParameters(parameters);
            List<Visita> ordered = new ArrayList<>(count);
            if (solution == null) ordered.addAll(visits);
            else {
                for (long index = routing.start(0); !routing.isEnd(index); index = solution.value(routing.nextVar(index))) {
                    ordered.add(visits.get(manager.indexToNode(index)));
                }
            }
            for (int index = 0; index < ordered.size(); index++) ordered.get(index).setOrdem(index);
            return ordered;
        } finally {
            if (solution != null) solution.delete();
            if (routing != null) routing.delete();
            manager.delete();
        }
    }

    private long distance(Location from, Location to) {
        double latitude = Math.toRadians(to.getLatitude() - from.getLatitude());
        double longitude = Math.toRadians(to.getLongitude() - from.getLongitude());
        double a = Math.pow(Math.sin(latitude / 2), 2) + Math.cos(Math.toRadians(from.getLatitude()))
                * Math.cos(Math.toRadians(to.getLatitude())) * Math.pow(Math.sin(longitude / 2), 2);
        a = Math.max(0, Math.min(1, a));
        return Math.round(6371000 * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
    }
}
