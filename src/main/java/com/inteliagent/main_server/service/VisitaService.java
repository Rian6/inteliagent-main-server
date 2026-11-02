package com.inteliagent.main_server.service;

import com.inteliagent.main_server.dto.*;
import com.inteliagent.main_server.persistence.VisitaPersistence;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VisitaService {
    public VisitaService(VisitaPersistence visitaPersistence) {
        this.visitaPersistence = visitaPersistence;
    }


    private final VisitaPersistence visitaPersistence;

    @org.springframework.transaction.annotation.Transactional(timeout=15)
    public boolean delete(String id) {
        if (!com.inteliagent.main_server.configuration.CurrentUser.admin()) throw new org.springframework.security.access.AccessDeniedException("Administrador necessário");
        if (id == null) throw new IllegalArgumentException("ID inválido");
        java.util.UUID.fromString(id);
        return visitaPersistence.delete(id);
    }

    public List<VisitaDTO> getUltimasVisitas(){
        return visitaPersistence.getUltimasVisitas();
    }

    public List<VisitasPorRegiaoDTO> getVisitasPorRegião(){
        return visitaPersistence.getVisitasPorRegião();
    }

    @org.springframework.transaction.annotation.Transactional(timeout=15)
    public void salvar(List<VisitaSalvarDTO> visitaSalvarDTOList){
        if (visitaSalvarDTOList == null || visitaSalvarDTOList.size() > 500 || visitaSalvarDTOList.stream().anyMatch(java.util.Objects::isNull)) throw new IllegalArgumentException("Lote inválido");
        visitaSalvarDTOList
                    .stream()
                    .forEach(visitaSalvarDTO ->
                            visitaPersistence.salvar(visitaSalvarDTO));}

    public List<VisitasPorDiaDTO> getVisitasPorDia(){
        return visitaPersistence.getVisitasPorDia();
    }

    public List<VisitaListDTO> getVisitasByPlanejamento(String idPlanejamento){
        return visitaPersistence.getVisitasByPlanejamento(idPlanejamento);
    }
}
