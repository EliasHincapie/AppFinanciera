package com.example.appfinanciera;


import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    private EditText etName, etEmail, etPhone, etCedula, etPassword, etConfirmPassword;
    private Button registerButton;
    private TextView nextLogin;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etCedula = findViewById(R.id.etCedula);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        registerButton = findViewById(R.id.btn_Button_Register);
        nextLogin = findViewById(R.id.tv_login);

        databaseHelper = DatabaseHelper.getInstance(this);

        registerButton.setOnClickListener(v -> registerUser());

        nextLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity2.class));
            finish();
        });
    }

    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String telefono = etPhone.getText().toString().trim();
        String cedula = etCedula.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (email.isEmpty() || name.isEmpty() || telefono.isEmpty() || cedula.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_LONG).show();
            return;
        }

        if (!validarCedula(cedula)) {
            Toast.makeText(this, "Cédula inválida", Toast.LENGTH_LONG).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show();
            return;
        }

        if (!validarEmail(email)) {
            Toast.makeText(this, "Correo inválido", Toast.LENGTH_LONG).show();
            return;
        }

        if (!validarContraseña(password)) {
            Toast.makeText(this, "Contraseña no segura", Toast.LENGTH_LONG).show();
            return;
        }

        if (!validarNombre(name)) {
            Toast.makeText(this, "Nombre no válido", Toast.LENGTH_LONG).show();
            return;
        }

        if (!validarTelefono(telefono)) {
            Toast.makeText(this, "Teléfono inválido", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            long intTelefono = Long.parseLong(telefono);
            long intCedula = Long.parseLong(cedula);

            if (databaseHelper.verificarDatos(email,intCedula)) {
                Toast.makeText(this, "Este correo o cedula ya está registrado", Toast.LENGTH_LONG).show();
                return;
            }


            boolean insert = databaseHelper.insertarUsuario(email, name, intTelefono, intCedula, password);
            if (insert) {
                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, LoginActivity2.class));
                finish();
            } else {
                Toast.makeText(this, "Registro fallido", Toast.LENGTH_LONG).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Teléfono y cédula deben ser números válidos", Toast.LENGTH_LONG).show();
        }
    }

    public boolean validarContraseña(String password) {
        return password.length() >= 8 &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[a-z].*") &&
                password.matches(".*\\d.*") &&
                password.matches(".*[!@#$%^&*()_+\"{}|:;'<>,.?/].*");
    }

    public boolean validarNombre(String name) {
        return name.length() >= 8 && name.matches("[a-zA-Z\\s]+");
    }

    public boolean validarEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean validarTelefono(String tel) {
        return tel.matches("\\d{9,10}");
    }

    public boolean validarCedula(String cedula) {
        return cedula.matches("\\d{7,10}");
    }
}
