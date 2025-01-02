package com.inteliagent.main_server.tools;

import com.google.ortools.constraintsolver.*;
import com.inteliagent.main_server.entity.Location;
import com.inteliagent.main_server.entity.Visita;

import java.util.List;
import java.util.ArrayList;
import java.util.function.LongBinaryOperator;

public class RouterOptimize {

    static {
        // Carregar a biblioteca nativa do OR-Tools
        //System.loadLibrary("or-tools");
    }

    public List<Visita> optimizeRoute(List<Visita> visitas) {
        // Número de visitas (incluindo a origem e destino)
        int n = visitas.size();

        // Criar o gerenciador de índices de roteamento
        RoutingIndexManager manager = new RoutingIndexManager(n, 1, 0);

        // Criar o modelo de roteamento
        RoutingModel routing = new RoutingModel(manager);

        // Definir a função de custo (distância) entre dois pontos
        LongBinaryOperator distanceCallback = (left, right) -> {
            Location fromLocation = visitas.get(manager.indexToNode(left)).getLocation();
            Location toLocation = visitas.get(manager.indexToNode(right)).getLocation();
            return computeDistance(fromLocation, toLocation);
        };

        // Registrar o cálculo de distâncias
        int transitCallbackIndex = routing.registerTransitCallback(distanceCallback);

        // Definir o custo de transição
        routing.setArcCostEvaluatorOfAllVehicles(transitCallbackIndex);

        // Definir parâmetros de busca (busca primeira solução viável)
        RoutingSearchParameters searchParameters = RoutingSearchParameters.newBuilder()
                .setFirstSolutionStrategy(FirstSolutionStrategy.Value.PATH_CHEAPEST_ARC)
                .build();

        // Resolver o problema de roteamento
        Assignment solution = routing.solveWithParameters(searchParameters);

        // Organizar a ordem das visitas
        List<Visita> orderedVisitas = new ArrayList<>();
        if (solution != null) {
            // Obter a ordem das visitas otimizadas
            long index = routing.start(0);
            int ordem = 0;  // Contador para a ordem das visitas
            while (!routing.isEnd(index)) {
                int nodeIndex = manager.indexToNode(index);
                Visita visita = visitas.get(nodeIndex);
                visita.setOrdem(ordem);  // Atualiza a ordem da visita
                orderedVisitas.add(visita);
                index = solution.value(routing.nextVar(index));
                ordem++;  // Incrementa a ordem
            }
        }

        return orderedVisitas;
    }

    // Método que calcula a distância entre dois pontos
    private long computeDistance(Location from, Location to) {
        double lat1 = from.getLatitude();
        double lon1 = from.getLongitude();
        double lat2 = to.getLatitude();
        double lon2 = to.getLongitude();

        // Fórmula de Haversine (distância em metros)
        double R = 6371000; // Raio da Terra em metros
        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double deltaPhi = Math.toRadians(lat2 - lat1);
        double deltaLambda = Math.toRadians(lon2 - lon1);

        double a = Math.sin(deltaPhi / 2) * Math.sin(deltaPhi / 2) +
                Math.cos(phi1) * Math.cos(phi2) *
                        Math.sin(deltaLambda / 2) * Math.sin(deltaLambda / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Distância em metros
        return (long) (R * c);
    }
}
