package com.example.appfinanciera;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private List<ListHistory> transactions;
    private String userPhone;

    public TransactionAdapter(List<ListHistory> transactions, String userPhone) {
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

        holder.tvMonto.setText((esEnviado ? "- " : "+ ") + "$ " + transaction.getMonto());

        String formattedDate;
        try{
            long timestamp = Long.parseLong(transaction.getFecha());
            java.text.SimpleDateFormat sdf = new  java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
            formattedDate = sdf.format(new java.util.Date(timestamp));

        }catch (NumberFormatException e ){
            formattedDate = transaction.getFecha();
        }
        holder.tvFecha.setText(formattedDate);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT


                );
        // Alinear el diseño tipo chat

        params.gravity = esEnviado ? Gravity.END : Gravity.START;
        holder.itemView.setLayoutParams(params);
        holder.container.setBackgroundResource(esEnviado ? R.drawable.sent_message_bg : R.drawable.received_message_bg);

        holder.tvMonto.setTextColor(esEnviado ? Color.RED : Color.GREEN);
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMonto, tvFecha;
        LinearLayout container;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container);
            tvMonto = itemView.findViewById(R.id.tvMonto);
            tvFecha = itemView.findViewById(R.id.tvFecha);
        }
    }
}


