package com.example.appfinanciera;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private TextView tvUserName, tvBalance;
    private Button btnEnviarDinero, btnCambiarDivisa;
    private RecyclerView recyclerViewTransactions;
    private TransactionAdapter adapter;
    private SQLite databaseHelper;
    private SharedPreferences sharedPreferences;
    private String userPhone;
    private long balance = 3000000;

    private int pesoToYen = 2;
    private int yenToEuro = 2;
    private String currentCurrency = "$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tvUserName = findViewById(R.id.tvUserName);
        tvBalance = findViewById(R.id.tvBalance);
        btnEnviarDinero = findViewById(R.id.btnEnviarDinero);
        btnCambiarDivisa = findViewById(R.id.btnCambiarDivisa);
        recyclerViewTransactions = findViewById(R.id.recyclerViewTransactions);

        // Inicialización de BD y SharedPreferences
        databaseHelper = new SQLite(this);
        sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.apply();
        // Obtener nombre del usuario
        String userName = databaseHelper.obtenerNombrePorTelefono(userPhone);
        if (userName != null) {
            tvUserName.setText(userName);
            getSupportActionBar().setTitle("Bienvenido, " + userName);
        }

        actualizarSaldo();
        cargarHistorial();

        btnEnviarDinero.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CashActivity.class);
            startActivity(intent);
        });

        btnCambiarDivisa.setOnClickListener(v -> cambiarDivisa());
    }

    private void actualizarSaldo() {
        long saldoConvertido = convertirSaldo(balance);
        tvBalance.setText(currentCurrency + " " + String.format("%,d", saldoConvertido));
    }

    private long convertirSaldo(long saldo) {
        switch (currentCurrency) {
            case "¥":
                return saldo * pesoToYen;
            case "€":
                return (saldo * pesoToYen) / yenToEuro;
            default:
                return saldo;
        }
    }

    private void cambiarDivisa() {
        if (currentCurrency.equals("$")) {
            currentCurrency = "¥";
        } else if (currentCurrency.equals("¥")) {
            currentCurrency = "€";
        } else {
            currentCurrency = "$";
        }
        actualizarSaldo();
    }

    private void cargarHistorial() {
        List<ListHistory> transactionList = databaseHelper.obtenerHistorial(userPhone);
        adapter = new TransactionAdapter(transactionList, userPhone);
        recyclerViewTransactions.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTransactions.setAdapter(adapter);
    }
}
