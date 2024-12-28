package com.inteliagent.main_server.service;

import com.inteliagent.main_server.dto.PlanejamentoDTO;
import com.inteliagent.main_server.dto.PlanejamentoSalvarDTO;
import com.inteliagent.main_server.dto.VisitaDTO;
import com.inteliagent.main_server.persistence.PlanejamentoPersistence;
import com.inteliagent.main_server.persistence.VisitaPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanejamentoService {
    @Autowired
    private PlanejamentoPersistence planejamentoPersistence;

    public List<PlanejamentoDTO> getAllPlanejamentos(){
        try{
            return planejamentoPersistence.getAllPlanejamentos();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public PlanejamentoDTO findByUUID(String uuid){
        try{
            return planejamentoPersistence.findByUUID(uuid);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public String salvar(PlanejamentoSalvarDTO planejamentoSalvarDTO){
        try{
            return planejamentoPersistence.salvar(planejamentoSalvarDTO);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return "";
    }
}
