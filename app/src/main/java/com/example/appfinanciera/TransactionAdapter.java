package com.example.appfinanciera;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private List<ListHistory> transactions;
    private String userPhone;
    private Context context;

    public TransactionAdapter(Context context, List<ListHistory> transactions, String userPhone) {
        this.context = context;
        this.transactions = transactions;
        this.userPhone = userPhone;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ListHistory transaction = transactions.get(position);
        boolean esEnviado = transaction.getOrigen().equals(userPhone);
        String otherParty = esEnviado ? transaction.getDestino() : transaction.getOrigen();

        holder.tvMonto.setText((esEnviado ? "- " : "+ ") + "$ " + String.format("%,d", transaction.getMonto()));
        holder.tvNumero.setText(otherParty);


        // Formateo de fecha
        String formattedDate = formatFecha(transaction.getFecha());
        holder.tvFecha.setText(formattedDate);

        // Ajustar el diseño tipo chat
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.gravity = esEnviado ? Gravity.END : Gravity.START;
        holder.container.setLayoutParams(params);

        holder.container.setBackgroundResource(esEnviado ? R.drawable.sent_message_bg : R.drawable.received_message_bg);
        holder.tvMonto.setTextColor(esEnviado ? Color.RED : Color.GREEN);

        // Manejo de clic para mostrar detalles de la transacción
        holder.itemView.setOnClickListener(v -> showTransactionDetails(context, transaction, position + 1, esEnviado));
    }

    @Override
    public int getItemCount() {
        return transactions != null ? transactions.size() : 0;
    }


    private void showTransactionDetails(Context context, ListHistory transaction, int transactionNumber, boolean esEnviado) {

        // Obtener el nombre de la otra persona
        String otherPartyPhone = esEnviado ? transaction.getDestino() : transaction.getOrigen();
        String otherPartyName = new SQLite(context).obtenerNombrePorTelefono(otherPartyPhone);


        // Construir los detalles de la transacción
        String details = "Transacción #" + transactionNumber + "\n\n" +
                (esEnviado ? "Envío a: " : "Recibido de: ") + otherPartyName + "\n" +
                "Número: " + otherPartyPhone + "\n" +
                "Monto: $" + String.format(Locale.getDefault(), "%,d", transaction.getMonto()) + "\n" +
                "Fecha: " + formatFecha(transaction.getFecha());

        // Mostrar los detalles en un AlertDialog
        new AlertDialog.Builder(context)
                .setTitle("Detalles de Transacción")
                .setMessage(details)
                .setPositiveButton("Cerrar", null)
                .show();
    } private String formatFecha(String fecha) {
        try {
            long timestamp = Long.parseLong(fecha);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            return sdf.format(new Date(timestamp));
        } catch (NumberFormatException e) {
            return fecha;
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumero,tvMonto, tvFecha;
        LinearLayout container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumero = itemView.findViewById(R.id.tvNumero);
            container = itemView.findViewById(R.id.container);
            tvMonto = itemView.findViewById(R.id.tvMonto);
            tvFecha = itemView.findViewById(R.id.tvFecha);
        }
    }
}
