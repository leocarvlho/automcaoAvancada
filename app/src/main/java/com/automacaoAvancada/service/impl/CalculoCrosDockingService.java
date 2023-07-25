package com.automacaoAvancada.service.impl;

import com.automacaoAvancada.model.ResponseCrossDocking;

public interface CalculoCrosDockingService {

    public ResponseCrossDocking calcula(long distanciaCarro1, long distanciaCarro2, int velocidadeCarro1, int velocidadeCarro2);
}
