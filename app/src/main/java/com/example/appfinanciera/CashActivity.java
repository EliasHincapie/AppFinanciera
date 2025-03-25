package com.example.appfinanciera;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class     CashActivity extends AppCompatActivity {
    private EditText etPhone, etAmount;
    private Button btnSend;
    private DatabaseHelper databaseHelper;
    private String userPhone;
    private int balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash);

        etPhone = findViewById(R.id.etPhone);
        etAmount = findViewById(R.id.etAmount);
        btnSend = findViewById(R.id.btnSend);
        databaseHelper = DatabaseHelper.getInstance(this);

        // Obtener el número de teléfono del usuario desde SharedPreferences
        userPhone = getSharedPreferences("UserSession", MODE_PRIVATE).getString("userPhone", "");
        balance = databaseHelper.obtenerSaldoPorTelefono(userPhone);

        configurarBotonEnviar();
    }

    private void configurarBotonEnviar() {
        btnSend.setOnClickListener(v -> {
            String phoneDestino = etPhone.getText().toString().trim();
            String amountStr = etAmount.getText().toString().trim();

            if (phoneDestino.isEmpty() || amountStr.isEmpty()) {
                Toast.makeText(this, "Ingrese todos los datos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (phoneDestino.equals(userPhone)) {
                Toast.makeText(this, "No puedes enviarte dinero a ti mismo", Toast.LENGTH_SHORT).show();
                return;
            }

            int amount;
            try {
                amount = Integer.parseInt(amountStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Ingrese un monto válido", Toast.LENGTH_SHORT).show();
                return;
            }

            int amountRounded = redondearMonto(amount);

            if (amountRounded <= 0) {
                Toast.makeText(this, "Monto inválido", Toast.LENGTH_SHORT).show();
                return;
            }

            if (amountRounded > balance) {
                Toast.makeText(this, "Saldo insuficiente", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!databaseHelper.usuarioExiste(phoneDestino)) {
                Toast.makeText(this, "El número no está registrado", Toast.LENGTH_SHORT).show();
                return;
            }

            // Actualizar los saldos en la BD
            if (databaseHelper.actualizarSaldo(userPhone, -amountRounded) && databaseHelper.actualizarSaldo(phoneDestino, amountRounded)) {
                // Registrar la transacción en el historial
                if (registrarTransaccion(userPhone, phoneDestino, amountRounded)) {
                    Toast.makeText(this, "Transacción exitosa", Toast.LENGTH_SHORT).show();

                    // Enviar resultado a HomeActivity para actualizar historial
                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(this, "Error al registrar transacción", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Error al actualizar saldo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int redondearMonto(int monto) {
        return Math.round(monto / 100.0f) * 100;
    }




    private boolean registrarTransaccion(String origen, String destino, int monto) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("tarjeta_origen", origen);
        values.put("tarjeta_destino", destino);
        values.put("monto_enviado", monto);

        // Agregar el email del usuario que realiza la transacción
        String userEmail = databaseHelper.obtenerEmailPorTelefono(origen);
        values.put("user_email", userEmail);

        // Formato de fecha legible
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy  HH:mm:ss", Locale.getDefault());
        String fecha = sdf.format(new Date());
        values.put("fecha", fecha);

        Log.d("DB_DEBUG", "Insertando transacción: " + origen + " -> " + destino + ", Monto: " + monto + ", Fecha: " + fecha);

        long resultado = db.insert("historial", null, values);


        if (resultado == -1) {
            Log.e("DB_ERROR", "Error al insertar la transacción en la base de datos.");
            db.close();  // ❗ CIERRA LA BD AQUÍ
            return false;
        }
        db.close();
        return true;
    }
}
