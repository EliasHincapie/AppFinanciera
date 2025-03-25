package com.example.appfinanciera;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private TextView tvUserName, tvBalance;
    private Button btnEnviarDinero, btnRecibirDinero, btnCambiarDivisa;
    private RecyclerView recyclerViewTransactions;
    private TransactionAdapter adapter;
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private String userPhone;
    private String userName;
    private int pesoToYen = 2;
    private int yenToEuro = 2;
    private String currentCurrency = "$";
    private List<ListHistory> transactionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tvUserName = findViewById(R.id.tvUserName);
        tvBalance = findViewById(R.id.tvBalance);
        btnEnviarDinero = findViewById(R.id.btnEnviarDinero);
        btnRecibirDinero = findViewById(R.id.btnRecibirDinero);
        btnCambiarDivisa = findViewById(R.id.btnCambiarDivisa);
        recyclerViewTransactions = findViewById(R.id.recyclerViewTransactions);

        // Inicialización de BD y SharedPreferences
        databaseHelper = DatabaseHelper.getInstance(this);
        sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);

        // Obtener el teléfono del usuario desde las preferencias compartidas
        userPhone = sharedPreferences.getString("userPhone", "");

        if (userPhone.isEmpty()) {
            // Si no hay teléfono de usuario, redirigir al login
            startActivity(new Intent(HomeActivity.this, LoginActivity2.class));
            finish();
            return;
        }

        // Obtener nombre del usuario - intenta primero desde SharedPreferences
        userName = sharedPreferences.getString("name", "");

        // Si no está en las preferencias, obtenerlo de la base de datos
        if (userName.isEmpty()) {
            userName = databaseHelper.obtenerNombrePorTelefono(userPhone);
            if (userName != null && !userName.isEmpty()) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("name", userName);
                editor.apply();
            }
        }

        if (userName != null && !userName.isEmpty()) {
            tvUserName.setText(userName);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("BIENVENIDO: " + userName);
            }
        }

        // Configurar RecyclerView
        recyclerViewTransactions.setLayoutManager(new LinearLayoutManager(this));
        transactionList = databaseHelper.obtenerHistorial(userPhone);
        adapter = new TransactionAdapter(this, transactionList, userPhone);
        recyclerViewTransactions.setAdapter(adapter);

        actualizarSaldo();
        cargarHistorial();

        // Configurar botones
        btnEnviarDinero.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CashActivity.class);
            startActivityForResult(intent, 1);
        });

        btnRecibirDinero.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, RecibirDineroActivity.class);
            startActivityForResult(intent, 2);
        });

        btnCambiarDivisa.setOnClickListener(v -> cambiarDivisa());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_profile) {
            return true;
        } else if (id == R.id.action_logout) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(HomeActivity.this, LoginActivity2.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void actualizarSaldo() {
        long balance = databaseHelper.obtenerSaldoPorTelefono(userPhone);
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
        Log.d("DB_DEBUG", "🔄 Recargando historial...");
        transactionList.clear(); // Limpiar la lista antes de actualizarla
        transactionList.addAll(databaseHelper.obtenerHistorial(userPhone));

        if (transactionList.isEmpty()) {
            Log.d("DB_DEBUG", "⚠️ No hay transacciones para mostrar.");
        } else {
            Log.d("DB_DEBUG", "📜 Se cargaron " + transactionList.size() + " transacciones.");
        }

        adapter.notifyDataSetChanged();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("DEBUG", "onActivityResult llamado con requestCode: " + requestCode + ", resultCode: " + resultCode);

        if ((requestCode == 1 || requestCode == 2) && resultCode == RESULT_OK) {
            Log.d("DEBUG", "Actualizando saldo y cargando historial...");

            actualizarSaldo();
            cargarHistorial();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        actualizarSaldo();
        cargarHistorial();
    }
}