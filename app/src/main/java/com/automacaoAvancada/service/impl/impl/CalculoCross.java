package com.automacaoAvancada.service.impl.impl;

import com.automacaoAvancada.model.ResponseCrossDocking;

import Jama.Matrix;

public class CalculoCross implements  Runnable{

    private final double distanciaCarro1;
    private final double velocidadeCarro1;
    private int c1;
    private final double distInicial1;
    private final double tEstimado;


    public CalculoCross(double distanciaCarro1, double velocidadeCarro1, int c1, double distInicial1, double tEstimado){
        this.distanciaCarro1 = distanciaCarro1;
        this.velocidadeCarro1 = velocidadeCarro1;
        this.c1 = c1;
        this.distInicial1 = distInicial1;
        this.tEstimado = tEstimado;
    }

    @Override
    public void run(){
        ResponseCrossDocking response = new ResponseCrossDocking();

        System.out.println("Distancia do carro ao destino: "+distanciaCarro1 + " km");
        System.out.println("Velocidade inicial do carro: "+velocidadeCarro1+ " km/h");
        System.out.println("Tempo estimado para o encontro: "+tEstimado +" horas");

        response.setTempoEstimadoEncontro(tEstimado);

        //Reconcilia os dados
        double[][] y1 = {{distInicial1}, {distanciaCarro1}, {distanciaCarro1-c1-c1}, {distanciaCarro1-c1-c1}, {distanciaCarro1-c1-c1}};
        double[][] a1 = {{1}, {-1}, {-1}, {-1}, {-1}};
        double[][] v1 = {{1,0,0,0,0},{0,1,0,0,0},{0,0,1,0,0},{0,0,0,1,0},{0,0,0,0,1}};
        Matrix Y1 = new Matrix(y1);
        Matrix A1 = new Matrix(a1);
        Matrix V1 = new Matrix(v1);
        Matrix dadosRec1 = reconciliation(Y1, A1, V1);

        // Velocidade indicada para o carro
        double dadoRec1 = dadosRec1.get(2,1);
        double distAjustada1;
        if (distanciaCarro1>dadoRec1){
            distAjustada1 = distanciaCarro1-dadoRec1;
        }
        else{
            distAjustada1 = distanciaCarro1+dadoRec1;
        }

        double velocidadeMediaCarro1 = distAjustada1/tEstimado;

        response.setVelocidadeMediaCarro1(velocidadeMediaCarro1);

        // Imprime a velocidade inidcada
        System.out.println("Velocidade indicada para o carro: " + velocidadeMediaCarro1 + " km/h");

        c1++;

        double intervalo = ((3.6e+6)/velocidadeMediaCarro1);
        try {
            Thread.sleep((long) intervalo);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public static Matrix reconciliation(Matrix y, Matrix a, Matrix v){

        Matrix At = a.transpose();
        Matrix AV = a.times(v);
        Matrix AVAt = AV.times(At);
        Matrix inv = AVAt.inverse();
        Matrix VAt = v.times(At);
        Matrix X = inv.times(VAt);
        Matrix XA = X.times(a);
        Matrix XAY = XA.times(y);
        Matrix res = y.minus(XAY);

        return res;
    }

}
