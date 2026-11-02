package com.inteliagent.main_server.dto;

public class PlanejamentoSalvarDTO {
    private String boletimJson;
    public String getBoletimJson(){return boletimJson;}
    public void setBoletimJson(String value){boletimJson=value;}
    private String catalogoVersao;
    public String getCatalogoVersao() { return catalogoVersao; }
    public void setCatalogoVersao(String value) { catalogoVersao = value; }
    private Integer ciclo;
    public Integer getCiclo() { return ciclo; }
    private boolean cicloInformado;
    public void setCiclo(Integer value) { ciclo = value; cicloInformado = true; }
    public boolean hasCiclo() { return cicloInformado; }

    private Integer idCidade;
    public Integer getIdCidade() { return idCidade; }
    public void setIdCidade(Integer value) { idCidade = value; }
    private Integer idCategoria;
    public Integer getIdCategoria() { return idCategoria; }
    public void setIdCategoria(Integer value) { idCategoria = value; }
    private Integer atividade;
    public Integer getAtividade() { return atividade; }
    public void setAtividade(Integer value) { atividade = value; }
    private Integer tipo;
    public Integer getTipo() { return tipo; }
    public void setTipo(Integer value) { tipo = value; }
    private String ano;
    public String getAno() { return ano; }
    public void setAno(String value) { ano = value; }
    private String dataUltVisita;
    public String getDataUltVisita() { return dataUltVisita; }
    public void setDataUltVisita(String value) { dataUltVisita = value; }

    private String uuid;
    private String nomeRegiao;
    private String zona;
    private int situacao;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getNomeRegiao() {
        return nomeRegiao;
    }

    public void setNomeRegiao(String nomeRegiao) {
        this.nomeRegiao = nomeRegiao;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }

    public int getSituacao() {
        return situacao;
    }

    public void setSituacao(int situacao) {
        this.situacao = situacao;
    }
}
