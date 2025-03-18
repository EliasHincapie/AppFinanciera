package com.example.appfinanciera;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity2 extends AppCompatActivity {
    private EditText etPhone, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    SQLite databaseHelper;
    SharedPreferences sharedPreferences;
    private ImageView ojo_password;

    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        etPhone = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_registrar);
        ojo_password = findViewById(R.id.ojo_password);


        databaseHelper = new SQLite(this);
        sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);

        checkSession();

        btnLogin.setOnClickListener(v -> loginUser());

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity2.this, RegisterActivity.class));
            finish();
        });
        ojo_password.setOnClickListener(v -> togglePasswordVisibility());

    }

    private void checkSession() {
        String savedPhone = sharedPreferences.getString("phone", "");
        if (!savedPhone.isEmpty()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private void loginUser() {
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_LONG).show();
            return;
        }

        if (databaseHelper.verificarUsuario(phone, password)) {
            String userName = databaseHelper.obtenerNombreUsuario(phone);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("phone", phone);
            editor.putString("name", userName);  // Guardamos el nombre del usuario
            editor.apply();

        Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Teléfono o contraseña incorrectos", Toast.LENGTH_LONG).show();
        }
    }
    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            ojo_password.setImageResource(R.drawable.ojoabierto);
        } else {
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            ojo_password.setImageResource(R.drawable.ojoabierto);
        }
        etPassword.setSelection(etPassword.getText().length());
        isPasswordVisible = !isPasswordVisible;
    }
}



