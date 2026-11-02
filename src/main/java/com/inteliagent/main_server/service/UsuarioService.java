package com.inteliagent.main_server.service;

import com.inteliagent.main_server.dto.*;
import com.inteliagent.main_server.persistence.UsuarioPersistence;
import com.inteliagent.main_server.persistence.VisitaPersistence;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {
    public UsuarioService(UsuarioPersistence usuarioPersistence) {
        this.usuarioPersistence = usuarioPersistence;
    }


    private final UsuarioPersistence usuarioPersistence;

    public List<UsuarioDTO> getLocationUsers(){
        return usuarioPersistence.getLocationUsers();
    }
}
