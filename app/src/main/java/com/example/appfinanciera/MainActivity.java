package com.example.appfinanciera;



import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

    public class MainActivity extends AppCompatActivity {
        private TextView tvWelcome;
        private Button btnLogout;
        SharedPreferences sharedPreferences;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            tvWelcome = findViewById(R.id.tv_welcome);
            btnLogout = findViewById(R.id.btn_logout);
            sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);

            String userPhone = sharedPreferences.getString("phone", "");
            tvWelcome.setText("Bienvenido, " + userPhone);

            btnLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logoutUser();
                }
            });
        }

        private void logoutUser() {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(MainActivity.this, LoginActivity2.class);
            startActivity(intent);
            finish();
        }
    }

