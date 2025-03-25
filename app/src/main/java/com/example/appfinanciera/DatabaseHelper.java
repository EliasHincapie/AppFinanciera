package com.example.appfinanciera;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "register.db";
    private static final int DATABASE_VERSION = 5;
    private static DatabaseHelper instance;

    private static int PESO_A_YEN = 2;  // 1 Peso = 2 Yenes
    private static int YEN_A_EURO = 2;  // 1 Yen = 2 Euros



    private SQLiteDatabase db;

    private DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Patrón Singleton para evitar múltiples instancias
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE usuarios (" +
                "email TEXT PRIMARY KEY, " +
                "name TEXT, " +
                "telefono INTEGER UNIQUE, " +
                "cc INTEGER UNIQUE, " +
                "password TEXT, " +
                "saldo INTEGER DEFAULT 3000000)");

        db.execSQL("CREATE TABLE historial (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "tarjeta_origen TEXT, " +
                "tarjeta_destino TEXT, " +
                "monto_enviado INTEGER, " +
                "fecha TEXT, " +
                "user_email TEXT, " +
                "FOREIGN KEY(user_email) REFERENCES usuarios(email))");

        Log.d("DB_DEBUG", "Tablas creadas correctamente.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS usuarios");
        db.execSQL("DROP TABLE IF EXISTS historial");
        onCreate(db);
    }

    // Insertar usuario
    public boolean insertarUsuario(String email, String name, long telefono, long cedula, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("name", name);
        values.put("telefono", telefono);
        values.put("cc", cedula);
        values.put("password", password);
        values.put("saldo", 3000000 );

        long result = db.insert("usuarios", null, values);
        db.close();
        return result != -1;
    }


    // Verificar existencia de email o cédula
    public boolean verificarDatos(String email, long cedula) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT 1 FROM usuarios WHERE email = ? OR cc = ?",
                new String[]{email, String.valueOf(cedula)});

        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }


    // Verificar usuario y contraseña
    public boolean verificarUsuario(String telefono, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT 1 FROM usuarios WHERE telefono = ? AND password = ?",
                new String[]{telefono, password});

        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }


    public int obtenerSaldoPorTelefono(String telefono) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT saldo FROM usuarios WHERE telefono = ?", new String[]{telefono});
        int saldo = cursor.moveToFirst() ? cursor.getInt(0) : 0;
        cursor.close();
        return saldo;
    }


    public boolean actualizarSaldo(String phone, int monto) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT saldo FROM usuarios WHERE telefono = ?", new String[]{phone});

        if (cursor.moveToFirst()) {
            int saldoActual = cursor.getInt(0);
            int nuevoSaldo = saldoActual + monto;

            ContentValues values = new ContentValues();
            values.put("saldo", nuevoSaldo);

            int filasAfectadas = db.update("usuarios", values, "telefono = ?", new String[]{phone});
            cursor.close();
            db.close();

            return filasAfectadas > 0;
        }

        cursor.close();
        db.close();
        return false;
    }





    // Registrar transacción sin cerrar la BD
    public boolean registrarTransaccion(String origen, String destino, int monto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("tarjeta_origen", origen);
        values.put("tarjeta_destino", destino);
        values.put("monto_enviado", monto);
        values.put("user_email", obtenerEmailPorTelefono(origen));

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        values.put("fecha", sdf.format(new Date()));

        long resultado = db.insert("historial", null, values);
        db.close();
        return resultado != -1;
    }




    // Obtener el símbolo de la moneda actual
    public static String obtenerSimboloMoneda(String moneda) {
        switch (moneda.toUpperCase()) {
            case "PESO":
                return "$";
            case "YEN":
                return "¥";
            case "EURO":
                return "€";
            default:
                return "?";
        }
    }



        // Obtener email sin cerrar la BD
    public String obtenerEmailPorTelefono(String telefono) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT email FROM usuarios WHERE telefono=?", new String[]{telefono});

        String email = cursor.moveToFirst() ? cursor.getString(0) : "";
        cursor.close();
        return email;
    }



    // Obtener historial de transacciones
    public List<ListHistory> obtenerHistorial(String userPhone) {
        List<ListHistory> historial = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT id, tarjeta_origen, tarjeta_destino, monto_enviado, fecha " +
                        "FROM historial WHERE tarjeta_origen=? OR tarjeta_destino=? ORDER BY fecha DESC",
                new String[]{userPhone, userPhone});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String origen = cursor.getString(1);
                String destino = cursor.getString(2);
                int monto = cursor.getInt(3);
                String fecha = cursor.getString(4);

                historial.add(new ListHistory(id, monto, fecha, destino, origen));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return historial;
    }

    public boolean usuarioExiste(String telefono) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT 1 FROM usuarios WHERE telefono = ?", new String[]{telefono});

        boolean exists = cursor.moveToFirst(); // Más eficiente que getCount() > 0
        cursor.close();
        db.close(); // Cerramos la BD aquí porque el metodo ya terminó su ejecución

        return exists;
    }




    public String obtenerNombrePorTelefono(String telefono) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM usuarios WHERE telefono = ?", new String[]{telefono});

        String nombre = ""; // Valor por defecto en caso de no encontrar el usuario
        if (cursor.moveToFirst()) {
            nombre = cursor.getString(0);
        }

        cursor.close();
        db.close(); // Cerramos la BD aquí porque el metodo ya terminó su ejecución

        return nombre;
    }

}