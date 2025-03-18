package com.example.appfinanciera;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RecibirDineroActivity extends AppCompatActivity {

    private EditText etMontoRecibido;
    private Button btnConfirmarRecibo;
    private SharedPreferences sharedPreferences;
    private double saldoActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recibir_dinero);

        etMontoRecibido = findViewById(R.id.etMontoRecibido);
        btnConfirmarRecibo = findViewById(R.id.btnConfirmarRecibo);

        // Obtener el saldo actual de SharedPreferences
        sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        saldoActual = Double.parseDouble(sharedPreferences.getString("saldo", "3000000"));

        btnConfirmarRecibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recibirDinero();
            }
        });
    }

    private void recibirDinero() {
        String montoStr = etMontoRecibido.getText().toString();
        if (montoStr.isEmpty()) {
            Toast.makeText(this, "Ingrese un monto válido", Toast.LENGTH_SHORT).show();
            return;
        }

        double montoRecibido = Double.parseDouble(montoStr);
        if (montoRecibido <= 0) {
            Toast.makeText(this, "El monto debe ser mayor a 0", Toast.LENGTH_SHORT).show();
            return;
        }

        // Actualizar saldo
        saldoActual += montoRecibido;

        // Guardar el nuevo saldo en SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("saldo", String.valueOf(saldoActual));
        editor.apply();

        // Volver a HomeActivity con el nuevo saldo
        Intent intent = new Intent(RecibirDineroActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
