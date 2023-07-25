package com.automacaoAvancada.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class CrossDockingModel  implements Serializable {

    private long distanciaCarro1;
    private long distanciaCarro2;
    private int velocidadeCarro1;
    private int velocidadeCarro2;

    public long getDistanciaCarro1() {
        return distanciaCarro1;
    }

    public void setDistanciaCarro1(long distanciaCarro1) {
        this.distanciaCarro1 = distanciaCarro1;
    }

    public long getDistanciaCarro2() {
        return distanciaCarro2;
    }

    public void setDistanciaCarro2(long distanciaCarro2) {
        this.distanciaCarro2 = distanciaCarro2;
    }

    public int getVelocidadeCarro1() {
        return velocidadeCarro1;
    }

    public void setVelocidadeCarro1(int velocidadeCarro1) {
        this.velocidadeCarro1 = velocidadeCarro1;
    }

    public int getVelocidadeCarro2() {
        return velocidadeCarro2;
    }

    public void setVelocidadeCarro2(int velocidadeCarro2) {
        this.velocidadeCarro2 = velocidadeCarro2;
    }

    public CrossDockingModel(int distanciaCarro1, int distanciaCarro2, int velocidadeCarro1, int velocidadeCarro2) {
        this.distanciaCarro1 = distanciaCarro1;
        this.distanciaCarro2 = distanciaCarro2;
        this.velocidadeCarro1 = velocidadeCarro1;
        this.velocidadeCarro2 = velocidadeCarro2;
    }

    public CrossDockingModel() {
    }


}
