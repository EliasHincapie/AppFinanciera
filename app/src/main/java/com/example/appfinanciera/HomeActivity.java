package com.example.appfinanciera;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private TextView tvUserName, tvBalance;
    private Button btnEnviarDinero,btnRecibirDinero, btnCambiarDivisa;
    private RecyclerView recyclerViewTransactions;
    private TransactionAdapter adapter;
    private SQLite databaseHelper;
    private SharedPreferences sharedPreferences;
    private String userPhone;
    private String userName;
    private int pesoToYen = 2;
    private int yenToEuro = 2;
    private String currentCurrency = "$";


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
        databaseHelper = new SQLite(this);
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
            // Guardar el nombre en SharedPreferences para uso futuro
            if (userName != null && !userName.isEmpty()) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("name", userName);
                editor.apply();
            }
        }

        // Configurar el nombre del usuario en el TextView y en la Toolbar
        if (userName != null && !userName.isEmpty()) {
            tvUserName.setText(userName);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("BIENVENIDO:  " + userName);
            }
        }



        btnEnviarDinero.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CashActivity.class);
            startActivity(intent);
        });

        btnRecibirDinero.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this,RecibirDineroActivity.class);
            startActivity(intent);

        });

        btnCambiarDivisa.setOnClickListener(v -> cambiarDivisa());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // menú de opciones
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_profile) {

            return true;
        } else if (id == R.id.action_logout) {
            // Borrar datos de sesión
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            // Redirigir al login
            Intent intent = new Intent(HomeActivity.this, LoginActivity2.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
           // finish();
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
        List<ListHistory> transactionList = databaseHelper.obtenerHistorial(userPhone);
        adapter = new TransactionAdapter(transactionList, userPhone);
        recyclerViewTransactions.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTransactions.setAdapter(adapter);

        // Add animation
        recyclerViewTransactions.setItemAnimator(new androidx.recyclerview.widget.DefaultItemAnimator());
        recyclerViewTransactions.addItemDecoration(new androidx.recyclerview.widget.DividerItemDecoration(
                this, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL));


    }
    @Override
    protected void onResume() {
        super.onResume();
        actualizarSaldo();
        cargarHistorial();
    }
}

