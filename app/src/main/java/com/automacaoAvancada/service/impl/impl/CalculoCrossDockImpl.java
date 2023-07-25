package com.automacaoAvancada.service.impl.impl;

import com.automacaoAvancada.model.ResponseCrossDocking;
import com.automacaoAvancada.service.impl.CalculoCrosDockingService;

public class CalculoCrossDockImpl implements CalculoCrosDockingService {

    @Override
    public ResponseCrossDocking calcula(long distanciaCarro1, long distanciaCarro2, int velocidadeCarro1, int velocidadeCarro2) {
        ResponseCrossDocking response = new ResponseCrossDocking();
//        int distanciaCarro1 = 30; // Distância em km do carro 1 ao ponto de encontro
//        int distanciaCarro2 = 40; // Distância em km do carro 2 ao ponto de encontro

        System.out.println("Distancia carro1: "+distanciaCarro1 + " km");
        System.out.println("Distancia carro2: "+distanciaCarro2 + " km");

        // Velocidades iniciais dos carros (em km/h)
//        int velocidadeCarro1 = 60; // Velocidade inicial do carro 1
//        int velocidadeCarro2 = 40; // Velocidade inicial do carro 2

        System.out.println("Velocidade carro1: "+velocidadeCarro1+ " km/h");
        System.out.println("Velocidade carro2: "+velocidadeCarro2+ " km/h");

        // Verifica se o carro 2 já chegou ao ponto de encontro antes do carro 1 sair
//        if (distanciaCarro2 >= distanciaCarro1 && velocidadeCarro2 > velocidadeCarro1) {
//            System.out.println("O carro 2 já chegou ao ponto de encontro antes do carro 1.");
//            response.setCarro2chegouPrimeiro(true);
//            return response;
//        }

        // Tempo estimado para o encontro

        double tempoEstimado = (double) (distanciaCarro1 - distanciaCarro2) / (velocidadeCarro2 - velocidadeCarro1);
        System.out.println("Tempo estimado para o encontro: "+tempoEstimado +" horas");
        if(tempoEstimado < 0){
            tempoEstimado = tempoEstimado * -1;
        }
        response.setTempoEstimadoEncontro(tempoEstimado);

        // Velocidade média para o carro 1
        double velocidadeMediaCarro1 = distanciaCarro1 / tempoEstimado;
        // Velocidade média para o carro 2
        double velocidadeMediaCarro2 = distanciaCarro2 / tempoEstimado;

        response.setVelocidadeMediaCarro1(velocidadeMediaCarro1);
        response.setVelocidadeMediaCarro2(velocidadeMediaCarro2);


        // Imprime as velocidades médias
        System.out.println("Velocidade média necessaria do carro 1: " + velocidadeMediaCarro1 + " km/h");
        System.out.println("Velocidade média necessaria do carro 2: " + velocidadeMediaCarro2 + " km/h");
        return response;
    }
}
