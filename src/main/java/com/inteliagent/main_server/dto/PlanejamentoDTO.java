package com.inteliagent.main_server.dto;

public class PlanejamentoDTO {
    private long quantidadeVisitas;
    public long getQuantidadeVisitas(){return quantidadeVisitas;}
    public void setQuantidadeVisitas(long value){quantidadeVisitas=value;}
    private String boletimJson;
    public String getBoletimJson(){return boletimJson;}
    public void setBoletimJson(String value){boletimJson=value;}
    private String catalogoVersao;
    public String getCatalogoVersao() { return catalogoVersao; }
    public void setCatalogoVersao(String value) { catalogoVersao = value; }
    private Integer ciclo;
    public Integer getCiclo() { return ciclo; }
    public void setCiclo(Integer value) { ciclo = value; }

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

    private int situacao;
    public int getSituacao() { return situacao; }
    public void setSituacao(int value) { situacao = value; }
    private String codigo;
    private String descricao;
    private String zona;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }
}
