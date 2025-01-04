package com.inteliagent.main_server.dto;

public class VisitaSalvarDTO {
    private String uuid;
    private String cep;
    private String nome;
    private String numero;
    private double lat;
    private double lng;
    private int sequencia;
    private String idPlanejamento;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public int getSequencia() {
        return sequencia;
    }

    public void setSequencia(int sequencia) {
        this.sequencia = sequencia;
    }

    public String getIdPlanejamento() {
        return idPlanejamento;
    }

    public void setIdPlanejamento(String idPlanejamento) {
        this.idPlanejamento = idPlanejamento;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
