package com.inteliagent.main_server.service;

import com.inteliagent.main_server.dto.*;
import com.inteliagent.main_server.persistence.UsuarioPersistence;
import com.inteliagent.main_server.persistence.VisitaPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioPersistence usuarioPersistence;

    public List<UsuarioDTO> getLocationUsers(){
        try{
            return usuarioPersistence.getLocationUsers();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }
}
