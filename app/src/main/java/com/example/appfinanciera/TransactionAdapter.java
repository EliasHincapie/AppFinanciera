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
        holder.tvMonto.setText((esEnviado ? "- " : "+ ") + "$ " + transaction.getMonto());
        holder.tvFecha.setText(transaction.getFecha());

        // Alinear el diseño tipo chat
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.itemView.getLayoutParams();
        params.gravity = esEnviado ? Gravity.END : Gravity.START;
        holder.itemView.setLayoutParams(params);

        holder.tvMonto.setTextColor(esEnviado ? Color.RED : Color.GREEN);
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMonto, tvFecha;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMonto = itemView.findViewById(R.id.tvMonto);
            tvFecha = itemView.findViewById(R.id.tvFecha);
        }
    }
}


