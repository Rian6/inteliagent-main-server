package com.inteliagent.main_server.entity;

public class Visita {
    private Long id;
    private String endereco;
    private String nome;
    private String numero;
    private String cep;
    private Double lat;
    private Double lng;
    private Location location;
    private int ordem;  // Campo para armazenar a posição da visita na rota otimizada

    public Visita(Long id, String endereco, String nome, String numero, String cep, Double lat, Double lng) {
        this.id = id;
        this.endereco = endereco;
        this.nome = nome;
        this.numero = numero;
        this.cep = cep;
        this.lat = lat;
        this.lng = lng;
        this.location = new Location(lat, lng); // Assumindo que você tem uma classe Location
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
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

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getOrdem() {
        return ordem;
    }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }
}
