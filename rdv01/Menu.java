package com.controll_rdv.rdv03;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Menu extends AppCompatActivity {

    TextView user;
    int classe;
    String getGerenc, getEquip, getDir;
    ListView lv;
    ArrayList<String> holder = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        /*declarações de variaveis */

        lv = findViewById(R.id.lv);

        user = findViewById(R.id.tvnomeMenu);
        pegarDados();

        if (!pegarDados()) {
            recuperarVoltar();
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String strU = prefs.getString("usr", "");
        user.setText(strU);
        recuperarVoltar();
        getDespesa();

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getDespesa() {

        String apiurl = getString(R.string.getGastos) + "beto";
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

                        String despesa = jo.getString("id_desp");
                        String total = jo.getString("sum(valor_desp)");

                        String tipoDesp;
                        switch (Integer.parseInt(despesa)) {
                            case 1:
                                tipoDesp = "Alimentação";
                                break;
                            case 2:
                                tipoDesp = "Combustível";
                                break;
                            case 3:
                                tipoDesp = "Estacionamento";
                                break;
                            case 4:
                                tipoDesp = "Hospedagem";
                                break;
                            case 5:
                                tipoDesp = "Outros";
                                break;
                            case 6:
                                tipoDesp = "Pedágio";
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value: " + Integer.parseInt(despesa));
                        }

                        holder.add(tipoDesp + ": R$" + total);

                    }

                    ArrayAdapter<String> at = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, holder);

                    lv.setAdapter(at);

                } catch (Exception e) {
                    System.out.print(e.getMessage());
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    //finish();
                }
            }

            @Override
            protected String doInBackground(String... strings) {

                try {
                    URL url = new URL(strings[0]);
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


    private void recuperarVoltar() {
        /*recuperando o username a partir da funcao sharedpreferences*/
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Menu.this);
        SharedPreferences.Editor editor = preferences.edit();
        String saveUser = user.getText().toString();
        editor.putString("usr", saveUser);
        editor.apply();
    }

    private boolean pegarDados() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle params = intent.getExtras();
            if (params != null) {
                String login = params.getString("loginUser");
                user.setText(login);
                getGerenc = params.getString("gerencia");
                getEquip = params.getString("equipe");
                getDir = params.getString("direcao");
                System.out.println("direcao: " + getDir);
                System.out.println("equipe: " + getEquip);
                System.out.println("gerencia: " + getGerenc);
                return true;
            }
            return true;
        }
        return false;
    }

    private void enviarDados(int qualClasse) {

        if (qualClasse == 1) {

            Intent cadastro = new Intent(Menu.this, Movimento.class);
            cadastro.putExtra("loginUser", user.getText().toString());
            cadastro.putExtra("direcao", getDir);
            cadastro.putExtra("equipe", getEquip);
            cadastro.putExtra("gerencia", getGerenc);
            startActivity(cadastro);

        } else if (qualClasse == 2) {

            Intent busca = new Intent(Menu.this, BuscaDespesa.class);
            busca.putExtra("User", user.getText().toString());
            startActivity(busca);

        } else {
            Toast.makeText(this, "Erro", Toast.LENGTH_SHORT).show();
        }
    }

    public void cadastroDespesa(View view) {
        classe = 1;
        enviarDados(classe);
    }

    public void visualizarDespesas(View view) {
        classe = 2;
        enviarDados(classe);
    }
}
