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
        db.execSQL("CREATE TABLE usuarios (" + "email TEXT PRIMARY KEY, " + "name TEXT, " + "telefono INTEGER UNIQUE, " +  "cc INTEGER UNIQUE, " + "password TEXT)");

        db.execSQL("CREATE TABLE tarjetas (" + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "nombre TEXT, " + "pan TEXT, " + "exp TEXT, " + "cv TEXT, " + "bank TEXT, " + "monto INTEGER, " + "user_email TEXT, " + "FOREIGN KEY(user_email) REFERENCES usuarios(email))");

        db.execSQL("CREATE TABLE historial (" + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "tarjeta_origen TEXT, " + "tarjeta_destino TEXT, " + "monto_enviado INTEGER, " + "fecha TEXT, " + "user_email TEXT, " + "FOREIGN KEY(user_email) REFERENCES usuarios(email))");

        Log.d("DEBUG", "Tablas creadas correctamente.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS usuarios");
        db.execSQL("DROP TABLE IF EXISTS tarjetas");
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
        db.close();
        return exists;
    }
    public String obtenerNombreUsuario(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM usuarios WHERE telefono = ?", new String[]{phone});
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

        Cursor cursor = db.rawQuery("SELECT nombre FROM usuarios WHERE telefono = ?", new String[]{telefono});
        if (cursor.moveToFirst()) {
            nombre = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return nombre;
    }
    public Cursor obtenerSaldoPorTelefono(String telefono) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT saldo FROM usuarios WHERE telefono = ?", new String[]{telefono});
    }

    public List<ListHistory> obtenerHistorial(String userPhone) {
        List<ListHistory> historial = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT tarjeta_origen, tarjeta_destino, monto_enviado, fecha FROM historial WHERE tarjeta_origen = ? OR tarjeta_destino = ? ORDER BY fecha DESC",
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
        db.close();
        return historial;
    }

    public boolean insertarTarjeta(String nombre, String pan, String exp, String cv, String bank, int monto, String userEmail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("pan", pan);
        values.put("exp", exp);
        values.put("cv", cv);
        values.put("bank", bank);
        values.put("monto", monto);
        values.put("user_email", userEmail);

        long result = db.insert("tarjetas", null, values);
        db.close();
        return result != -1;
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
