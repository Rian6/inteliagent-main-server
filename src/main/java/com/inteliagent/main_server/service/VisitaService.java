package com.inteliagent.main_server.service;

import com.inteliagent.main_server.dto.*;
import com.inteliagent.main_server.persistence.VisitaPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VisitaService {

    @Autowired
    private VisitaPersistence visitaPersistence;

    public List<VisitaDTO> getUltimasVisitas(){
        try{
            return visitaPersistence.getUltimasVisitas();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public List<VisitasPorRegiaoDTO> getVisitasPorRegião(){
        try{
            return visitaPersistence.getVisitasPorRegião();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return null;
    }

    public void salvar(List<VisitaSalvarDTO> visitaSalvarDTOList){
        try{
            visitaSalvarDTOList
                    .parallelStream()
                    .forEach(visitaSalvarDTO ->
                            visitaPersistence.salvar(visitaSalvarDTO));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public List<VisitasPorDiaDTO> getVisitasPorDia(){
        try{
            return visitaPersistence.getVisitasPorDia();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return null;
    }
}
