package com.inteliagent.main_server.service;

import com.inteliagent.main_server.entity.Sync;
import com.inteliagent.main_server.persistence.SyncPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SyncService {

    @Autowired
    private SyncPersistence syncPersistence;

    public List<String> sync(List<Sync> syncList){

        List<String> confirmTables = new ArrayList<>();

        for(Sync sync: syncList) {
            try{
                String load = mountSQL(sync);
                syncPersistence.genericSave(load);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

        return confirmTables;
    }

    private String mountSQL(Sync sync){
        String sql = "REPLACE INTO "+sync.getTableName()+" ("+sync.getCabecalho()+") VALUES";

        StringBuilder rotinaBuilder = new StringBuilder();
        for(int i=0; i<sync.getRotina().size(); i++){
            String rotina = sync.getRotina().get(i);
            rotinaBuilder.append(" (").append(rotina).append(")");

            String virgula = i<sync.getRotina().size()-1 ? ",\n" : "\n";
            rotinaBuilder.append(virgula);
        }
        sql = sql + rotinaBuilder;
        return sql;
    }
}
