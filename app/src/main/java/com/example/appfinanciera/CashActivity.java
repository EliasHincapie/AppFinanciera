package com.example.appfinanciera;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CashActivity extends AppCompatActivity {
    private EditText etPhone, etAmount;
    private Button btnSend;
    SQLite databaseHelper;
    String userPhone;
    int balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash);

        etPhone = findViewById(R.id.etPhone);
        etAmount = findViewById(R.id.etAmount);
        btnSend = findViewById(R.id.btnSend);
        databaseHelper = new SQLite(this);

        userPhone = getSharedPreferences("UserSession", MODE_PRIVATE).getString("phone", "");

        // Obtener el saldo actual del usuario
        balance = obtenerSaldo(userPhone);

        btnSend.setOnClickListener(v -> enviarDinero());
    }

    private void enviarDinero() {
        String phoneDestino = etPhone.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();

        if (phoneDestino.isEmpty() || amountStr.isEmpty()) {
            Toast.makeText(this, "Ingrese todos los datos", Toast.LENGTH_SHORT).show();
            return;
        }

        int amount = Integer.parseInt(amountStr);
        int amountRounded = redondearMonto(amount);

        if (amountRounded <= 0) {
            Toast.makeText(this, "Monto inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amountRounded > balance) {
            Toast.makeText(this, "Saldo insuficiente", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!usuarioExiste(phoneDestino)) {
            Toast.makeText(this, "El número no está registrado", Toast.LENGTH_SHORT).show();
            return;
        }

        // Actualizar los saldos en la BD
        actualizarSaldo(userPhone, -amountRounded);
        actualizarSaldo(phoneDestino, amountRounded);

        // Registrar la transacción en el historial
        registrarTransaccion(userPhone, phoneDestino, amountRounded);

        Toast.makeText(this, "Transacción exitosa", Toast.LENGTH_SHORT).show();
        finish();
    }

    private int redondearMonto(int monto) {
        return (monto / 100) * 100;
    }

    private int obtenerSaldo(String phone) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT saldo FROM usuarios WHERE telefono=?", new String[]{phone});
        int saldo = 0;
        if (cursor.moveToFirst()) {
            saldo = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return saldo;
    }

    private boolean usuarioExiste(String phone) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM usuarios WHERE telefono=?", new String[]{phone});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    private void actualizarSaldo(String phone, int monto) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.execSQL("UPDATE usuarios SET saldo = saldo + ? WHERE telefono=?", new Object[]{monto, phone});
        db.close();
    }

    private void registrarTransaccion(String origen, String destino, int monto) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("tarjeta_origen", origen);
        values.put("tarjeta_destino", destino);
        values.put("monto_enviado", monto);
        values.put("fecha", System.currentTimeMillis());

        db.insert("historial", null, values);
        db.close();
    }
}
