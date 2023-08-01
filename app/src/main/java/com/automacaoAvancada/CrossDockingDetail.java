package com.automacaoAvancada;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.automacaoAvancada.model.CrossDockingModel;
import com.automacaoAvancada.model.ResponseCrossDocking;
import com.automacaoAvancada.service.impl.impl.CalculoCross;
import com.automacaoAvancada.service.impl.impl.CalculoCrossDockImpl;
import com.automacaoAvancada.utils.DialogMessage;
import com.automacaoAvancada.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class CrossDockingDetail extends AppCompatActivity {
    private CrossDockingModel crossDockingModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) { //inicializar a tela e interações com o usuário

        super.onCreate(savedInstanceState);

        setContentView(R.layout.cross_docking_detail);

        Bundle dados = getIntent().getExtras();
        if (dados != null) {
            crossDockingModel = (CrossDockingModel) dados.getSerializable ("crossDockModel");
        }

        TextView idNumeroIdentificacao = findViewById(R.id.idNumeroIdentificacao);
        TextView idInicioViagem = findViewById(R.id.idInicioViagem);
        TextView idFimViagem = findViewById(R.id.idFimViagem);
        TextView labelDistanciaVec1 = findViewById(R.id.labelDistanciaVec1);
        TextView labelDistanciaVec2 = findViewById(R.id.labelDistanciaVec2);
        TextView idNomeMotorista1 = findViewById(R.id.idNomeMotorista1);
        TextView idNomeMotorista2 = findViewById(R.id.idNomeMotorista2);
        TextView idListaPassageiro = findViewById(R.id.idListaPassageiro);
        TextView textVelocidade2 =findViewById(R.id.textVelocidade2);
        TextView textVelocidade1 =findViewById(R.id.textVelocidade1);
        TextView tempoestimadoId =findViewById(R.id.tempoestimadoId);
        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);

        idInicioViagem.setText(Utils.dataAgora());
        idNumeroIdentificacao.setText(Utils.generateNumberRandom());

        labelDistanciaVec1.setText(crossDockingModel.getDistanciaCarro1() +" km");
        labelDistanciaVec2.setText(crossDockingModel.getDistanciaCarro2() +" km");
        idNomeMotorista1.setText(Utils.getNomeRandom());
        idNomeMotorista2.setText(Utils.getNomeRandom());
        List<String> passageiros = new ArrayList<>();
        passageiros.add(Utils.getNomeRandom());
        passageiros.add(Utils.getNomeRandom());

        idListaPassageiro.setText(passageiros.get(0)+", "+passageiros.get(1));

        double distInicial1 = crossDockingModel.getDistanciaCarro1();
        double distInicial2 = crossDockingModel.getDistanciaCarro2();
        int medidorC1 = 0;
        int medidorC2 = 0;

        CalculoCrossDockImpl crosImpl =new CalculoCrossDockImpl();
        ResponseCrossDocking response = crosImpl.calcula(crossDockingModel.getDistanciaCarro1(),
                crossDockingModel.getDistanciaCarro2(),
                crossDockingModel.getVelocidadeCarro1(),
                crossDockingModel.getVelocidadeCarro2(),
                (long) distInicial1,
                (long) distInicial2
        );

        double tEstimado = response.getTempoEstimadoEncontro();

        textVelocidade1.setText("Velocidade média inicial necessária do carro 1: "+ (int) response.getVelocidadeMediaCarro1() +" km/h");
        textVelocidade2.setText("Velocidade média inicial necessária do carro 2: "+ (int) response.getVelocidadeMediaCarro2() +" km/h");
        tempoestimadoId.setText("Tempo estimado para o encontro "+response.getTempoEstimadoEncontro() +" horas");
        idFimViagem.setText(Utils.dataAgora(response.getTempoEstimadoEncontro()));

        CalculoCross carro1 = new CalculoCross(crossDockingModel.getDistanciaCarro1(), crossDockingModel.getVelocidadeCarro1(), medidorC1, distInicial1, tEstimado);
        CalculoCross carro2 = new CalculoCross(crossDockingModel.getDistanciaCarro2(), crossDockingModel.getVelocidadeCarro2(), medidorC2, distInicial2, tEstimado);

        floatingActionButton.setOnClickListener(v->{

            String txt = "Por favor "+idNomeMotorista2.getText()+" preciso que você siga a uma velocidade média de "+(int) response.getVelocidadeMediaCarro2()+
                    "km/h para nos encontrarmos no local combinado em "+response.getTempoEstimadoEncontro()+" horas";

            DialogMessage dialogMessage = new DialogMessage(this);
            dialogMessage.show(txt);
        });

        while (crossDockingModel.getDistanciaCarro1()>0 && crossDockingModel.getDistanciaCarro2()>0)
        {
            carro1.run();
            carro2.run();
        }
    }
}
