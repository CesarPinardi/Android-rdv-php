package com.controll_rdv.rdv01;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    /* String para o banco */
    private static final String URL =
            "http://189.1.174.107:8080/app/user_control.php";
    ListView lv;
    EditText userEt, passEt;
    Button login;
    Button limpar;
    CheckBox checkbox;
    ArrayList<String> holder = new ArrayList<>();
    String name, gerenc, equip, direcao, ativo;
    private RequestQueue requestQueue;
    private StringRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);

        lv = findViewById(R.id.lv);
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

        /* Intent para ir para a outra activity */

    }

    public void fetchdata() {

        String apiurl = getString(R.string.getUser) + userEt.getText().toString();
        @SuppressLint("StaticFieldLeak")
        class dbManager extends AsyncTask<String, Void, String> {
            @RequiresApi(api = Build.VERSION_CODES.O)
            protected void onPostExecute(String data) {
                try {
                    JSONArray ja = new JSONArray(data);
                    JSONObject jo;
                    holder.clear();

                    for (int i = 0; i < ja.length(); i++) {

                        jo = ja.getJSONObject(i);
                        name = jo.getString("username");
                        holder.add("Nome: " + name);
                        gerenc = jo.getString("gerencia");
                        holder.add("Gerencia: " + gerenc);
                        equip = jo.getString("equipe");
                        holder.add("Equipe: " + equip);
                        direcao = jo.getString("direcao");
                        holder.add("Direcao: " + direcao);
                        ativo = jo.getString("ativo");
                        holder.add("Ativo: " + ativo);

                    }

                    ArrayAdapter<String> at = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, holder);

                    lv.setAdapter(at);

                } catch (Exception e) {
                    holder.clear();
                    //limpar o listview? holder.clear() n funciona;
                }
            }

            @Override
            protected String doInBackground(String... strings) {

                try {
                    java.net.URL url = new URL(strings[0]);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder data = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        data.append(line).append("\n");
                    }
                    br.close();
                    return data.toString();

                } catch (IOException e) {
                    return e.getMessage();
                }

            }
        }

        dbManager obj = new dbManager();
        obj.execute(apiurl);
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

    }

    public void login(View view) {
        fetchdata();
        Intent i = new Intent(MainActivity.this, Menu.class);

        requestQueue = Volley.newRequestQueue(this);
        /* quando clickar em login, chama a função (no php) para login/register */

        if (userEt.getText().toString().equals("") || passEt.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
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
                                params.putString("gerencia", gerenc);
                                params.putString("equipe", equip);
                                params.putString("direcao", direcao);

                                i.putExtras(params);

                                /* Assim que passar, vai para a outra activity */
                                startActivity(i);
                            } else {

                                Toast.makeText(getApplicationContext(), "Usuário não encontrado ou inativo!\n Procure o departamento de T.I.!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                            error -> {
                            }
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

    public void limpar(View view) {
        userEt.getText().clear();
        passEt.getText().clear();
    }
}