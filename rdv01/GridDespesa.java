package com.controll_rdv.rdv01;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GridDespesa extends AppCompatActivity {

    ListView lv;
    ArrayList<String> holder = new ArrayList<>();
    TextView tvdataInicial, tvDataFinal;
    String dataInicialBanco, dataFinalBanco;
    //Button btnVerFoto;

    String id_foto;

    String auxHolder;
    String usuario;

    //Integer id_busca_foto;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_despesa);

        tvdataInicial = findViewById(R.id.tvExibirDataInicial);
        tvDataFinal = findViewById(R.id.tvExibirDataFinal);

        lv = findViewById(R.id.lv);

        lv.setOnItemClickListener((parent, view, position, id) -> {
            // When clicked, show a toast with the TextView text

            String dados = (String) ((TextView) view).getText();
            String auxH = auxHolder;
            System.out.print(dados);

            int indexN = dados.indexOf('D',4);

            System.out.print("index of /n is: " + indexN);

            String substring = dados.substring(0,indexN);

            System.out.print("substring is: " + substring);

            String aux = "";

            Pattern p = Pattern.compile("\\d+");
            Matcher m = p.matcher(substring);
            while(m.find()) {
                aux = m.group();
            }


            Intent intent = new Intent(getApplicationContext(), DisplayFoto.class);
            intent.putExtra("idFoto", aux);
            startActivity(intent);


        });


        pegarDados();

        getDespesa();

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getDespesa() {
        /*string exemplo http://189.1.174.107:8080/app/get-despesa.php/?dataM=2021-05-07*/

        /*fazer o get com o usuario!!*/

        String apiurl = getString(R.string.getDespesa1) + dataInicialBanco + getString(R.string.getDespesa2) + dataFinalBanco + "&user=" + usuario;
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


                        String id_pic = jo.getString("id");
                        //String cd_user = jo.getString("cd_user");
                        String valor_desp = jo.getString("valor_desp");
                        String despesa = jo.getString("id_desp");
                        String strdata = jo.getString("dataM");

                        id_foto = id_pic;

                        String ano = strdata.substring(0, 4);
                        String mes = strdata.substring(5, 7);
                        String dia = strdata.substring(8, 10);
                        System.out.print("Invertendo string vinda do json");

                        String novaData = dia + "/" + mes + "/" + ano;

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
                        holder.add("\nCodigo: " + id_pic + "\nDespesa: " + tipoDesp + "\nValor: R$" + valor_desp + "\nEm: " + novaData + "\n");
                        auxHolder = ("\nid_pic: " + id_pic + "\nDespesa: " + tipoDesp + "\nValor: R$" + valor_desp + "\nEm: " + novaData + "\n");

                        System.out.print("ano: " + ano);
                        System.out.print("mes: " + mes);
                        System.out.print("dia: " + dia);
                        System.out.print("nova data: " + novaData);

                    }
                    //Handler handler = new Handler();
                    //handler.postDelayed(() -> {

                    //},1500);

                    ArrayAdapter<String> at = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, holder);

                    lv.setAdapter(at);

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Erro! Despesa não encontrada!", Toast.LENGTH_SHORT).show();

                    //limpar o listview? holder.clear();
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

    private void pegarDados() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle params = intent.getExtras();
            if (params != null) {
                String dataInicial = params.getString("DataInicial");
                tvdataInicial.setText(dataInicial);
                String dataFinal = params.getString("DataFinal");
                tvDataFinal.setText(dataFinal);
                dataInicialBanco = params.getString("DataBancoI");
                dataFinalBanco = params.getString("DataBancoF");
                usuario = params.getString("User");
            }
        }
    }

}


