package com.automacaoAvancada.utils;

import android.app.AlertDialog;
import android.content.Context;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class Utils {

    public static String generateNumberRandom(){
        Random rand = new Random();
        int upperbound = 1000;
        int result =  rand.nextInt(upperbound);
        return String.valueOf(result);
    }

    public static String dataAgora(){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return now.format(formatter);
    }

    public static String dataAgora(double horasASomar) {
        LocalDateTime now;
        if(horasASomar >= 1.0){
            now = LocalDateTime.now().plusHours((long) horasASomar);
        }else {
            now = LocalDateTime.now().plusMinutes((long) horasASomar);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return now.format(formatter);
    }

    public static String getNomeRandom(){
        String[] nomes = {
                "João", "Maria", "Pedro", "Ana", "Lucas", "Mariana", "Gustavo", "Julia",
                "Rafael", "Carolina", "Fernando", "Isabela", "Thiago", "Larissa", "Gabriel",
                "Manuela", "Diego", "Camila", "Vitor", "Amanda"
        };
        Random random = new Random();
        int indiceAleatorio = random.nextInt(nomes.length);
        return nomes[indiceAleatorio];
    }

    public static void showErrorMessageDialog(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Erro");
        builder.setMessage(message);
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss(); // Fecha o diálogo
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
