package com.example.appfinanciera;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SQLite extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "register.db";
    private static final int DATABASE_VERSION = 5;

    public SQLite(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE usuarios (" + "email TEXT PRIMARY KEY, " + "name TEXT, " + "telefono INTEGER UNIQUE, " + "cc INTEGER UNIQUE, " + "password TEXT," + "saldo INTEGER DEFAULT 3000000)");

        db.execSQL("CREATE TABLE historial (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "tarjeta_origen TEXT, " +
                "tarjeta_destino TEXT, " +
                "monto_enviado INTEGER, " +
                "fecha TEXT, " +
                "user_email TEXT, " +
                "FOREIGN KEY(user_email) REFERENCES usuarios(email))");
        Log.d("DEBUG", "Tablas creadas correctamente.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS usuarios");
        db.execSQL("DROP TABLE IF EXISTS historial");
        onCreate(db);
    }


    public boolean insertarUsuario(String email, String name, long telefono, long cedula, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("name", name);
        values.put("telefono", telefono);
        values.put("cc", cedula);
        values.put("password", password);
        values.put("saldo", 3000000);

        long result = db.insert("usuarios", null, values);
        db.close();
        return result != -1;
    }


    public boolean verificarEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM usuarios WHERE email = ?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }


    public boolean verificarCedula(long cedula) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM usuarios WHERE cc = ?", new String[]{String.valueOf(cedula)});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }


    public boolean verificarUsuario(String telefono, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM usuarios WHERE telefono = ? AND password = ?",
                new String[]{telefono, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }


    public String obtenerNombreUsuario(String telefono) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM usuarios WHERE telefono = ?", new String[]{telefono});
        String nombre = "";

        if (cursor.moveToFirst()) {
            nombre = cursor.getString(0);
        }

        cursor.close();
        db.close();
        return nombre;
    }



    public String obtenerNombrePorTelefono(String telefono) {
        SQLiteDatabase db = this.getReadableDatabase();
        String nombre = "Usuario"; // Valor por defecto en caso de error

        Cursor cursor = db.rawQuery("SELECT name FROM usuarios WHERE telefono = ?", new String[]{telefono});
        if (cursor.moveToFirst()) {
            nombre = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return nombre;
    }



    public int obtenerSaldoPorTelefono(String telefono) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT saldo FROM usuarios WHERE telefono = ?", new String[]{telefono});

        int saldo = 0; // Valor por defecto si hay error
        if (cursor.moveToFirst()) {
            saldo = cursor.getInt(0);
        }
        cursor.close();
        return saldo;
    }




    public boolean actualizarSaldo(String telefono, int monto) {
        SQLiteDatabase db = this.getWritableDatabase();
        int saldoActual = obtenerSaldoPorTelefono(telefono);
        if (saldoActual + monto < 0) {
            return false;
        }
        db.execSQL("UPDATE usuarios SET saldo = saldo + ? WHERE telefono=?", new Object[]{monto, telefono});
        return true;
    }



    public void registrarTransaccion(String origen, String destino,int monto ) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();

    values.put("tarjeta_origen",origen);
    values.put("tarjeta_destino",destino);
    values.put("monto_enviado", monto);
    values.put("fecha",String.valueOf(System.currentTimeMillis()));


    db.insert("historial",null,values);
    }


    private String obtenerEmailPorTelefono(String telefono){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT email FROM usuarios WHERE telefono=?", new String[]{telefono});
        String email = "";

        if (cursor.moveToFirst()){
            email = cursor.getString(0);
        }
     cursor.close();
        db.close();
        return email;
    }



    public List<ListHistory> obtenerHistorial(String userPhone) {
        List<ListHistory> historial = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM historial WHERE tarjeta_origen=? OR tarjeta_destino=? ORDER BY fecha DESC",
                new String[]{userPhone, userPhone});
        if (cursor.moveToFirst()) {
            do {
                String origen = cursor.getString(0);
                String destino = cursor.getString(1);
                int monto = cursor.getInt(2);
                String fecha = cursor.getString(3);
                historial.add(new ListHistory(monto, fecha, destino, origen));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return historial;
    }

    public boolean insertarHistorial(String tarjetaOrigen, String tarjetaDestino, int montoEnviado, String fecha, String userEmail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("tarjeta_origen", tarjetaOrigen);
        values.put("tarjeta_destino", tarjetaDestino);
        values.put("monto_enviado", montoEnviado);
        values.put("fecha", fecha);
        values.put("user_email", userEmail);

        long result = db.insert("historial", null, values);
        db.close();
        return result != -1;
    }



}
