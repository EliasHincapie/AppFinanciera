package com.example.appfinanciera;


import java.io.Serializable;

public class ListHistory implements Serializable {
    private int monto;
    private String fecha;
    private String destino;
    private String origen;

    public ListHistory(int monto, String fecha, String destino, String origen) {
        this.monto = monto;
        this.fecha = fecha;
        this.destino = destino;
        this.origen = origen;
    }

    public int getMonto() {
        return monto;
    }

    public void setMonto(int monto) {
        this.monto = monto;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }
}
