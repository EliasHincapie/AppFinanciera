package com.example.appfinanciera;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import androidx.annotation.Nullable;



public class SQLite extends SQLiteOpenHelper {

    private static final String Database= "register.db";
    public SQLite(@Nullable Context context) {
        super(context, Database, null, 5);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table usuarios(email TEXT primary key, name TEXT, telefono INTEGER, long cc,password TEXT)");
        db.execSQL("create table tarjetas(id INTEGER primary key autoincrement, nombre TEXT, pan TEXT, exp TEXT, cv TEXT, bank TEXT, monto INTEGER, user_email TEXT, foreign key(user_email) references usuarios(email))");
        db.execSQL("create table historial(id INTEGER primary key autoincrement, tarjeta_origen TEXT, tarjeta_destino TEXT, monto_enviado INTEGER, fecha TEXT, user_email TEXT, foreign key(user_email) references usuarios(email))");

        Log.d("DEBUG", "Tablas creadas correctamente.");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("Drop table if exists usuarios");
        db.execSQL("DROP TABLE IF EXISTS tarjetas");
        db.execSQL("DROP TABLE IF EXISTS historial");
        onCreate(db);
    }
public boolean insertarUsuario(String email, String name, long telefono,long cedula, String password) {
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

    public boolean verificarUsuario(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM usuarios WHERE email = ? AND password = ?", new String[]{email, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
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


    public boolean verificarCedula(long intCedula) {

    }
}



