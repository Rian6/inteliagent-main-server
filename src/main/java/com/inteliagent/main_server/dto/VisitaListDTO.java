package com.inteliagent.main_server.dto;

public class VisitaListDTO {
    private String boletimJson;
    public String getBoletimJson(){return boletimJson;}
    public void setBoletimJson(String value){boletimJson=value;}
    private String tipoVisita;
    public String getTipoVisita() { return tipoVisita; }
    public void setTipoVisita(String value) { tipoVisita = value; }
    private String complemento;
    public String getComplemento() { return complemento; }
    public void setComplemento(String value) { complemento = value; }
    private String lado;
    public String getLado() { return lado; }
    public void setLado(String value) { lado = value; }
    private String tipoImovel;
    public String getTipoImovel() { return tipoImovel; }
    public void setTipoImovel(String value) { tipoImovel = value; }

    private String id;
    private String cep;
    private String numero;
    private int sequencia;
    private String nome;
    private Double lat;
    private Double lng;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}
