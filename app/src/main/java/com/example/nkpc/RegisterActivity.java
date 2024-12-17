package com.example.nkpc;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterActivity extends AppCompatActivity {
    private EditText editTextRegisterLogin;
    private EditText editTextRegisterPassword;
    private EditText editTextConfirmPassword;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextRegisterLogin = findViewById(R.id.editTextRegisterLogin);
        editTextRegisterPassword = findViewById(R.id.editTextRegisterPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        Button buttonRegister = findViewById(R.id.buttonRegister);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String login = editTextRegisterLogin.getText().toString();
                String password = editTextRegisterPassword.getText().toString();
                String confirmPassword = editTextConfirmPassword.getText().toString();

                if (login.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Проверка совпадения паролей
                if (!password.equals(confirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Регистрация пользователя
                registerUser(login, password);
            }
        });
    }

    private void registerUser(final String login, final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                try {
                    URL url = new URL("http://10.0.2.2/register.php?login=" + login + "&password=" + password);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");

                    int responseCode = urlConnection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();

                        // Показать успех в главном потоке
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                // Показать сообщение об успехе
                                new AlertDialog.Builder(RegisterActivity.this)
                                        .setTitle("Регистрация")
                                        .setMessage("Регистрация прошла успешно!")
                                        .setPositiveButton("ОК", (dialog, which) -> {
                                            // Переход на LoginActivity
                                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                            finish(); // Завершить текущее Activity, чтобы его не было в стеке
                                        })
                                        .show();
                            }
                        });

                        Log.i("RegisterUser", "Response: " + response.toString());
                    } else {
                        Log.e("RegisterUser", "GET request failed, Response Code: " + responseCode);
                    }

                } catch (Exception e) {
                    Log.e("RegisterUser", "Exception occurred: " + e.getMessage(), e);
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            }
        }).start();
    }
}