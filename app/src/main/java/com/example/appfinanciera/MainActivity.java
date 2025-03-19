package com.example.appfinanciera;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final int SPLASH_DURATION = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(() -> {
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);


                boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);


                // Verificar si el usuario ha iniciado sesión
                Intent intent;
                if (isLoggedIn) {
                    intent = new Intent(MainActivity.this, HomeActivity.class); // Si está logueado, ir al Home
                } else {
                    intent = new Intent(MainActivity.this, LoginActivity2.class); // Si no, ir al Login
                }

                startActivity(intent);
                finish(); // Cierra splash
            } catch (Exception e) {
                e.printStackTrace(); // Imprimir el error en Logcat
            }
        }, SPLASH_DURATION);
    }
}

