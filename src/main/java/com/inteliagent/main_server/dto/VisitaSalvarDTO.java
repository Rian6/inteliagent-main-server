package com.inteliagent.main_server.dto;

public class VisitaSalvarDTO {
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

    private String uuid;
    private String cep;
    private String nome;
    private String numero;
    private Double lat;
    private Double lng;
    private Integer sequencia;
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

    public Integer getSequencia() {
        return sequencia;
    }

    public void setSequencia(Integer sequencia) {
        this.sequencia = sequencia;
    }

    public String getIdPlanejamento() {
        return idPlanejamento;
    }

    public void setIdPlanejamento(String idPlanejamento) {
        this.idPlanejamento = idPlanejamento;
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
