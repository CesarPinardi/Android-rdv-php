package com.example.rdv01;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    EditText userEt, passEt;
    Button login;
    Button limpar;
    CheckBox checkbox;

    private RequestQueue requestQueue;
    /* String para o banco */
    private static final String URL =
            "http://189.1.174.107:8080/app/user_control.php";
    private StringRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);

        userEt = findViewById(R.id.etUserName);
        passEt = findViewById(R.id.etPassword);
        login = findViewById(R.id.btnLogin);
        limpar = findViewById(R.id.btnLimpar);
        checkbox = findViewById(R.id.checkBox);

        /*Para recuperar o login da ultima vez que o app foi aberto*/
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String strU = prefs.getString("usr", "");
        userEt.setText(strU);

        checkbox.setOnCheckedChangeListener(
                (compoundButton, isChecked) -> {
                    if (isChecked) {
                        // mostra password
                        passEt.setTransformationMethod(
                                HideReturnsTransformationMethod.getInstance()
                        );
                    } else {
                        // esconde password
                        passEt.setTransformationMethod(
                                PasswordTransformationMethod.getInstance()
                        );
                    }
                }
        );

        /* limpar os campos*/
        limpar.setOnClickListener(
                v -> {
                    userEt.setText("");
                    passEt.setText("");
                }
        );

        /* Intent para ir para a outra activity */
        Intent i = new Intent(MainActivity.this, Movimento.class);

        requestQueue = Volley.newRequestQueue(this);
        /* quando clickar em login, chama a função (no php) para login/register */
        login.setOnClickListener(
                v -> {
                    if (
                            userEt.getText().toString().equals("") ||
                                    passEt.getText().toString().equals("")
                    ) {
                        Toast.makeText(getApplicationContext(),"Preencha todos os campos!", Toast.LENGTH_SHORT).show();

                    } else {

                        /*para ter o que o login na proxima ver que abrir*/
                        String saveUser = userEt.getText().toString();

                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                        SharedPreferences.Editor editor = preferences.edit();

                        editor.putString("usr", saveUser);
                        editor.apply();
                        request =
                                new StringRequest(Request.Method.POST, URL, response -> {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        if (Objects.requireNonNull(jsonObject.names()).get(0).equals("success")) {
                                            Bundle params = new Bundle();
                                            /* Enviar o valor do editText para a próxima activity */
                                            params.putString("loginUser", userEt.getText().toString());
                                            i.putExtras(params);
                                            /* Assim que passar, vai para a outra activity */
                                            startActivity(i);
                                        } else {
                                            /* msg de erro por 1.5sec*/
                                            new Handler()
                                                .postDelayed(() -> Toast.makeText(getApplicationContext(),"Erro ao logar",Toast.LENGTH_SHORT).show(), 1500);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                },
                                        error -> {}
                                ) {
                                    @Override
                                    protected Map<String, String> getParams() {
                                        HashMap<String, String> hashMap = new HashMap<>();
                                        hashMap.put("username", userEt.getText().toString());
                                        hashMap.put("password", passEt.getText().toString());
                                        return hashMap;
                                    }
                                };
                        requestQueue.add(request);
                    }
                }
        );
    }
}
