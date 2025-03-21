package com.example.appfinanciera;

public class Transaction {
    private String origen;
    private String destino;
    private int monto;
    private String fecha;


    public Transaction() {
    }

    public Transaction(String origen, String destino, int monto, String fecha) {
        this.origen = origen;
        this.destino = destino;
        this.monto = monto;
        this.fecha = fecha;
    }
    public String getOrigen() {
        return origen;
    }

    public String getDestino() {
        return destino;
    }

    public int getMonto() {
        return monto;
    }

    public String getFecha() {
        return fecha;
    }

    // Setters (útiles si necesitas modificar datos después de la creación)
    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public void setMonto(int monto) {
        this.monto = monto;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    // MEtodo para verificar si la transacción fue enviada (en lugar de recibida)
    public boolean isEnviado(String userPhone) {
        return userPhone != null && userPhone.equals(origen);
    }

}


