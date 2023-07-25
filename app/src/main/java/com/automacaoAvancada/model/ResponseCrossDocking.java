package com.automacaoAvancada.model;

public class ResponseCrossDocking {

    double velocidadeMediaCarro1;
    double velocidadeMediaCarro2;
    double tempoEstimadoEncontro;
    boolean carro2chegouPrimeiro = false;

    public ResponseCrossDocking(double velocidadeMediaCarro1, double velocidadeMediaCarro2, double tempoEstimadoEncontro, boolean carro2chegouPrimeiro) {
        this.velocidadeMediaCarro1 = velocidadeMediaCarro1;
        this.velocidadeMediaCarro2 = velocidadeMediaCarro2;
        this.tempoEstimadoEncontro = tempoEstimadoEncontro;
        this.carro2chegouPrimeiro = carro2chegouPrimeiro;
    }

    public ResponseCrossDocking() {
    }

    public double getVelocidadeMediaCarro1() {
        return velocidadeMediaCarro1;
    }

    public void setVelocidadeMediaCarro1(double velocidadeMediaCarro1) {
        this.velocidadeMediaCarro1 = velocidadeMediaCarro1;
    }

    public double getVelocidadeMediaCarro2() {
        return velocidadeMediaCarro2;
    }

    public void setVelocidadeMediaCarro2(double velocidadeMediaCarro2) {
        this.velocidadeMediaCarro2 = velocidadeMediaCarro2;
    }

    public double getTempoEstimadoEncontro() {
        return tempoEstimadoEncontro;
    }

    public void setTempoEstimadoEncontro(double tempoEstimadoEncontro) {
        this.tempoEstimadoEncontro = tempoEstimadoEncontro;
    }

    public boolean isCarro2chegouPrimeiro() {
        return carro2chegouPrimeiro;
    }

    public void setCarro2chegouPrimeiro(boolean carro2chegouPrimeiro) {
        this.carro2chegouPrimeiro = carro2chegouPrimeiro;
    }
}
