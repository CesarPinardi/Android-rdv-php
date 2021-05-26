package com.controll_rdv.rdv01;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DisplayFoto extends AppCompatActivity implements View.OnClickListener {
    TextView idFoto, tvPath;

    ImageView imageView;
    String image_path;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_foto);
        idFoto = findViewById(R.id.tvLabelIDFoto);
        imageView = findViewById(R.id.img);
        tvPath = findViewById(R.id.tvlabelPath);

        idFoto.setVisibility(View.INVISIBLE);

        //Picasso.with(getApplicationContext()).load("http://189.1.174.107:8080/RDV/_lib/file/img/php/uploads/759.025%20de%20mai%20de%202021.jpg").into(imageView);
        pegarDados();

        getImagePath();

        //Picasso.with(getApplicationContext()).load("http://189.1.174.107:8080/RDV/_lib/file/img/php/uploads/?id=389").into(imageView);

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

                //url ex http://189.1.174.107:8080/RDV/_lib/file/img/php/uploads/257.020%20de%20mai%20de%202021.jpg
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
            }
        }
    }


    @Override
    public void onClick(View v) {

    }

    public void excluir(View view) {

        /**como fazer o delete*/

        String url = "http://189.1.174.107:8080/rdv/_lib/file/img/php/delete.php?id="+idFoto.getText().toString();
        @SuppressLint("StaticFieldLeak")
        class dbManager extends AsyncTask<String, Void, String> {
            @RequiresApi(api = Build.VERSION_CODES.O)
            protected void onPostExecute(String data) {
                try {
                    Toast.makeText(getApplicationContext(), "OK!", Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Erro!", Toast.LENGTH_LONG).show();
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
        obj.execute(url);

    }


}