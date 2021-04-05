package com.example.rdv01;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

public class BackgroundWorker extends AsyncTask<String, Void, String> {

    @SuppressLint("StaticFieldLeak")
    Context context;

    BackgroundWorker(Context ctx) {
        context = ctx;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected String doInBackground(String... params) {
        String type = params[0];

        /* String banco */
        String movimento_url = "http://192.168.0.105/rdv/app/movimento_rdv.php";

        if (type.equals("regMov")) {
            try {
                String id_movimento = params[1];
                String id_func = params[2];
                String id_desp = params[3];
                String valor_desp = params[4];
                String valor_km = params[5];
                String obs = params[6];
                String dataM = params[7];
                String status = params[8];
                URL url = new URL(movimento_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(
                        new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)
                );
                String post_data =
                                URLEncoder.encode("id_movimento", "UTF-8") +
                                "=" +
                                URLEncoder.encode(id_movimento, "UTF-8") +
                                "&" +
                                URLEncoder.encode("id_func", "UTF-8") +
                                "=" +
                                URLEncoder.encode(id_func, "UTF-8") +
                                "&" +
                                URLEncoder.encode("id_desp", "UTF-8") +
                                "=" +
                                URLEncoder.encode(id_desp, "UTF-8") +
                                "&" +
                                URLEncoder.encode("valor_desp", "UTF-8") +
                                "=" +
                                URLEncoder.encode(valor_desp, "UTF-8") +
                                "&" +
                                URLEncoder.encode("valor_km", "UTF-8") +
                                "=" +
                                URLEncoder.encode(valor_km, "UTF-8") +
                                "&" +
                                URLEncoder.encode("obs", "UTF-8") +
                                "=" +
                                URLEncoder.encode(obs, "UTF-8") +
                                "&" +
                                URLEncoder.encode("dataM", "UTF-8") +
                                "=" +
                                URLEncoder.encode(dataM, "UTF-8") +
                                "&" +
                                URLEncoder.encode("status", "UTF-8") +
                                "=" +
                                URLEncoder.encode(status, "UTF-8") +
                                "&";
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1)
                );
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line);
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    protected void onPreExecute() {}

    @Override
    protected void onPostExecute(String result) {
        makeText(context, result, LENGTH_SHORT).show();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
