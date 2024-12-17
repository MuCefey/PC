package com.example.nkpc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextLogin;
    private EditText editTextPassword;
    private Button buttonLogin;

    // Адрес для входа, аналогично адаптируй под свой IP
    private static final String LOGIN_URL = "http://10.0.2.2/login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextLogin = findViewById(R.id.editTextLogin);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String login = editTextLogin.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (login.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Пожалуйста, заполните все поля.", Toast.LENGTH_SHORT).show();
                } else {
                    new LoginUser().execute(login, password);
                }
            }
        });
    }

    private class LoginUser extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String login = params[0];
            String password = params[1];

            try {
                String urlString = LOGIN_URL + "?login=" + login + "&password=" + password; // Используем GET
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET"); // Устанавливаем метод GET
                conn.setDoInput(true);

                conn.connect(); // Подключаемся

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = in.readLine()) != null) {
                    sb.append(line);
                }

                in.close();
                return sb.toString();

            } catch (Exception e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject res = new JSONObject(result);
                if (res.getBoolean("success")) {
                    Toast.makeText(LoginActivity.this, res.getString("message"), Toast.LENGTH_LONG).show();

// Проверяем пользователя на admin
                    String login = editTextLogin.getText().toString().trim();
                    String password = editTextPassword.getText().toString().trim();

                    if ("admin".equals(login) && "admin".equals(password)) {
// Переход к AdminActivity
                        Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                        startActivity(intent);
                    } else {
// Переход к CatalogActivity
                        Intent intent = new Intent(LoginActivity.this, CatalogActivity.class);
                        startActivity(intent);
                    }
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, res.getString("message"), Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(LoginActivity.this, "Ошибка обработки ответа.", Toast.LENGTH_LONG).show();
            }
        }
    }
}