package com.inteliagent.main_server.service;

import com.inteliagent.main_server.dto.PlanejamentoDTO;
import com.inteliagent.main_server.dto.PlanejamentoSalvarDTO;
import com.inteliagent.main_server.dto.VisitaDTO;
import com.inteliagent.main_server.persistence.PlanejamentoPersistence;
import com.inteliagent.main_server.persistence.VisitaPersistence;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanejamentoService {
    public PlanejamentoService(PlanejamentoPersistence planejamentoPersistence) {
        this.planejamentoPersistence = planejamentoPersistence;
    }

    private final PlanejamentoPersistence planejamentoPersistence;

    public List<PlanejamentoDTO> getAllPlanejamentos(){
        return planejamentoPersistence.getAllPlanejamentos();
    }

    public PlanejamentoDTO findByUUID(String uuid){
        return planejamentoPersistence.findByUUID(uuid);
    }

    public String salvar(PlanejamentoSalvarDTO planejamentoSalvarDTO){
        return planejamentoPersistence.salvar(planejamentoSalvarDTO);
    }
}
