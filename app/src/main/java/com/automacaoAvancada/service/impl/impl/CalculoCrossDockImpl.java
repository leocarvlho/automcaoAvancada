package com.automacaoAvancada.service.impl.impl;


import com.automacaoAvancada.model.ResponseCrossDocking;
import com.automacaoAvancada.service.impl.CalculoCrosDockingService;

import Jama.Matrix;


public class CalculoCrossDockImpl implements CalculoCrosDockingService {

    public ResponseCrossDocking calcula(long distanciaCarro1, long distanciaCarro2, int velocidadeCarro1, int velocidadeCarro2, int c1, int c2, long distInicial1, long distInicial2) {
        ResponseCrossDocking response = new ResponseCrossDocking();

        System.out.println("Distancia carro1: "+distanciaCarro1 + " km");
        System.out.println("Distancia carro2: "+distanciaCarro2 + " km");

        System.out.println("Velocidade inicial carro1: "+velocidadeCarro1+ " km/h");
        System.out.println("Velocidade inicial carro2: "+velocidadeCarro2+ " km/h");

        // Tempo estimado para o encontro
        double t1estimado = distInicial1/velocidadeCarro1;
        double t2estimado = distInicial2/velocidadeCarro2;
        double tempoEstimado;
        if (t1estimado >= t2estimado){
            tempoEstimado = t1estimado;
        }
        else{
            tempoEstimado = t2estimado;
        }
        System.out.println("Tempo estimado para o encontro: "+tempoEstimado +" horas");

        response.setTempoEstimadoEncontro(tempoEstimado);

        //Reconcilia os dados
        double[][] y1 = {{distInicial1}, {distInicial1-c1}, {distInicial1-c1}, {distInicial1-c1}, {distInicial1-c1}};
        double[][] a1 = {{1}, {-1}, {-1}, {-1}, {-1}};
        double[][] v1 = {{1,0,0,0,0},{0,1,0,0,0},{0,0,1,0,0},{0,0,0,1,0},{0,0,0,0,1}};
        Matrix Y1 = new Matrix(y1);
        Matrix A1 = new Matrix(a1);
        Matrix V1 = new Matrix(v1);
        Matrix dadosRec1 = reconciliation(Y1, A1, V1);

        double[][] y2 = {{distInicial2}, {distInicial2-c2}, {distInicial2-c2}, {distInicial2-c2}, {distInicial2-c2}};
        double[][] a2 = {{1}, {-1}, {-1}, {-1}, {-1}};
        double[][] v2 = {{1,0,0,0,0},{0,1,0,0,0},{0,0,1,0,0},{0,0,0,1,0},{0,0,0,0,1}};
        Matrix Y2 = new Matrix(y2);
        Matrix A2 = new Matrix(a2);
        Matrix V2 = new Matrix(v2);
        Matrix dadosRec2 = reconciliation(Y2, A2, V2);

        // Velocidade indicada para o carro 1
        double dadoRec1 = dadosRec1.get(2,1);
        double distAjustada1;
        if (distanciaCarro1>dadoRec1){
            distAjustada1 = distanciaCarro1-dadoRec1;
        }
        else{
            distAjustada1 = distanciaCarro1+dadoRec1;
        }

        double velocidadeMediaCarro1 = distAjustada1/tempoEstimado;

        // Velocidade indicada para o carro 2
        double dadoRec2 = dadosRec2.get(2,1);
        double distAjustada2;
        if (distanciaCarro2>dadoRec2){
            distAjustada2 = distanciaCarro2-dadoRec2;
        }
        else{
            distAjustada2 = distanciaCarro2+dadoRec2;
        }

        double velocidadeMediaCarro2 = distAjustada2/tempoEstimado;

        response.setVelocidadeMediaCarro1(velocidadeMediaCarro1);
        response.setVelocidadeMediaCarro2(velocidadeMediaCarro2);

        // Imprime as velocidades inidcadas
        System.out.println("Velocidade indicada para o carro 1: " + velocidadeMediaCarro1 + " km/h");
        System.out.println("Velocidade indicada para o carro 2: " + velocidadeMediaCarro2 + " km/h");
        return response;
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


    @Override
    public ResponseCrossDocking calcula(long distanciaCarro1, long distanciaCarro2, int velocidadeCarro1, int velocidadeCarro2) {
        return null;
    }
}
