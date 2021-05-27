package com.controll_rdv.rdv01;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class DisplayFoto extends AppCompatActivity implements View.OnClickListener {
    TextView idFoto, tvPath;

    ImageView imageView;
    String image_path;
    String user;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_foto);
        idFoto = findViewById(R.id.tvLabelIDFoto);
        imageView = findViewById(R.id.img);

        tvPath = findViewById(R.id.tvlabelPath);

        idFoto.setVisibility(View.INVISIBLE);

        pegarDados();

        getImagePath();

    }


    public void getImagePath() {

        String apiurl = "http://189.1.174.107:8080/app/get-image-id.php/?id=" + idFoto.getText().toString();
        @SuppressLint("StaticFieldLeak")
        class dbManager extends AsyncTask<String, Void, String> {
            @RequiresApi(api = Build.VERSION_CODES.O)
            protected void onPostExecute(String data) {
                try {
                    JSONArray ja = new JSONArray(data);
                    JSONObject jo;

                    for (int i = 0; i < ja.length(); i++) {

                        jo = ja.getJSONObject(i);
                        image_path = jo.getString("image_path");

                    }

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Erro!", Toast.LENGTH_LONG).show();
                }
                tvPath.setText(image_path);
                String path = tvPath.getText().toString();

                //url ex http://189.1.174.107:8080/RDV/_lib/file/img/php/uploads/257.020%20de%20mai%20de%201.jpg
                Picasso.with(getApplicationContext()).load("http://189.1.174.107:8080/RDV/_lib/file/img/" + path).into(imageView);

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

    }


    private void pegarDados() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle params = intent.getExtras();
            if (params != null) {
                String id = params.getString("idFoto");
                idFoto.setText(id);
                String usr = params.getString("user");
                user = usr;
            }
        }
    }


    @Override
    public void onClick(View v) {

    }

    public void excluir(View view) {

        //String url = "http://189.1.174.107:8080/rdv/_lib/file/img/php/delete.php?id="+idFoto.getText().toString();

        AlertDialog alerta;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //define a mensagem
        builder.setMessage("Deseja mesmo excluir essa despesa?");
        //define um botão como positivo
        builder.setPositiveButton("Sim", (arg0, arg1) -> {
            excluirDespesa();
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                Intent intent = new Intent(getApplicationContext(), Menu.class);
                intent.putExtra("userEx", user);
                finish();
                startActivity(intent);
            }, 3000);


        });
        //define um botão como negativo.
        builder.setNegativeButton("Não", (arg0, arg1) -> {
            Toast.makeText(getApplicationContext(), "OK!\nA despesa não fui excluída!\nVoltando para o menu!", Toast.LENGTH_SHORT).show();
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                Intent v = new Intent(DisplayFoto.this, Menu.class);
                v.putExtra("UserEx", user);
                finish();
                startActivity(v);
            }, 3000);
        });
        //cria o AlertDialog
        alerta = builder.create();
        //Exibe
        alerta.show();

    }

    private void excluirDespesa() {
        StringRequest request = new StringRequest(Request.Method.POST, "http://189.1.174.107:8080/rdv/_lib/file/img/php/delete.php?id=" + idFoto.getText().toString(),
                response -> {
                    if (response.equalsIgnoreCase("Data Deleted")) {
                        Toast.makeText(getApplicationContext(), "Despesa deletada!\nVoltando para o menu!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Despesa não deletada!\nVoltando para o menu!", Toast.LENGTH_SHORT).show();
                    }
                }, error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("id", idFoto.getText().toString());

                return super.getParams();
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);

    }


}
