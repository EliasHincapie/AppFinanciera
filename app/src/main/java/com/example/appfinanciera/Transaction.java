package com.example.appfinanciera;

public class Transaction {
        private String origen;
        private String destino;
        private int monto;
        private String fecha;

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

        public boolean isEnviado() {
            return origen != null;
        }
    }


