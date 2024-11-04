package com.inteliagent.main_server.entity;

import java.util.List;

public class Sync {
    private String tableName;
    private String cabecalho;
    private List<String> rotina;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<String> getRotina() {
        return rotina;
    }

    public void setRotina(List<String> rotina) {
        this.rotina = rotina;
    }

    public String getCabecalho() {
        return cabecalho;
    }

    public void setCabecalho(String cabecalho) {
        this.cabecalho = cabecalho;
    }
}
