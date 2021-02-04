package com.example.rdv01;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
    EditText UsernameEt, PasswordEt;
    Button login;

    private RequestQueue requestQueue;
    /*String para o banco*/
    private static final String URL = "http://192.168.0.126/rdv/app/user_control.php";
    private StringRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UsernameEt = findViewById(R.id.etUserName);
        PasswordEt = findViewById(R.id.etPassword);
        login = findViewById(R.id.btnLogin);

        /*Intent para ir para a outra activity*/
        Intent i = new Intent(MainActivity.this,Movimento.class);

        requestQueue = Volley.newRequestQueue(this);
        /*quando clickar em login, chama a função (no php) para login/register */
        login.setOnClickListener(v -> {

            request = new StringRequest(Request.Method.POST, URL, response -> {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(Objects.requireNonNull(jsonObject.names()).get(0).equals("success")){

                        Bundle params = new Bundle();
                        /*Enviar o valor do editText para a próxima activity*/
                        params.putString("loginUser", UsernameEt.getText().toString());
                        i.putExtras(params);
                        /*Assim que passar, vai para a outra activity*/
                        startActivity(i);

                    } else {
                        Toast.makeText(getApplicationContext(), "Error" + jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }, error -> {

            }) {
                @Override
                protected Map<String, String> getParams() {
                    HashMap<String,String> hashMap = new HashMap<>();
                    hashMap.put("username", UsernameEt.getText().toString());
                    hashMap.put("password", PasswordEt.getText().toString());
                    return hashMap;
                }
            };
            requestQueue.add(request);
        });

    }

}
