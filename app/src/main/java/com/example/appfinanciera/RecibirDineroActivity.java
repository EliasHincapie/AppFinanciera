package com.example.appfinanciera;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RecibirDineroActivity extends AppCompatActivity {
    private EditText etMontoRecibido;
    private Button btnConfirmarRecibo;
    private DatabaseHelper databaseHelper;
    private String userPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recibir_dinero);

        etMontoRecibido = findViewById(R.id.etMontoRecibido);
        btnConfirmarRecibo = findViewById(R.id.btnConfirmarRecibo);
        databaseHelper = DatabaseHelper.getInstance(this);

        userPhone = getSharedPreferences("UserSession", MODE_PRIVATE).getString("userPhone", "");

        btnConfirmarRecibo.setOnClickListener(v -> confirmarRecibo());
    }

    private void confirmarRecibo() {
        String montoStr = etMontoRecibido.getText().toString().trim();

        if (montoStr.isEmpty()) {
            Toast.makeText(this, "Ingrese un monto", Toast.LENGTH_SHORT).show();
            return;
        }

        int monto;
        try {
            monto = Integer.parseInt(montoStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ingrese un monto válido", Toast.LENGTH_SHORT).show();
            return;
        }

        int montoRedondeado = redondearMonto(monto);

        if (montoRedondeado <= 0) {
            Toast.makeText(this, "Monto inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        // Actualizar el saldo del usuario
        boolean actualizado = databaseHelper.actualizarSaldo(userPhone, montoRedondeado);

        if (actualizado) {
            // Registrar en el historial como una recepción
            databaseHelper.registrarTransaccion("USUARIO", userPhone, montoRedondeado);
            Toast.makeText(this, "Recepción exitosa de $" + montoRedondeado, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al procesar la recepción", Toast.LENGTH_SHORT).show();
        }
    }

    private int redondearMonto(int monto) {
        return (monto / 100) * 100;
    }
}