package com.example.rdv01;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;

public class Guardar extends AppCompatActivity implements View.OnClickListener {
    EditText func, despesa, valordespesa, valorcomb, obs, data, arqNome;
    Button insert, show, enviar, pickD;
    TextView tv;
    private int mYear, mMonth, mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guardar);
        func = findViewById(R.id.etFunc);
        despesa = findViewById(R.id.etDespesa);
        valordespesa = findViewById(R.id.etValorDespesa);
        valorcomb = findViewById(R.id.etValorComb);
        obs = findViewById(R.id.etObs);
        data = findViewById(R.id.etData);
        arqNome = findViewById(R.id.etNomeArq);
        insert = findViewById(R.id.btnEnviar);
        show = findViewById(R.id.btnMostrar);
        tv = findViewById(R.id.textview);
        enviar = findViewById(R.id.btnEnviarDados);
        pickD = findViewById(R.id.labelData);

        pickD.setOnClickListener(this);


        /*Pegando form que veio de Movimento.class e colocando nos EditTexts*/

        Intent intent = getIntent();
        if (intent != null) {
            Bundle params = intent.getExtras();
            if (params != null) {
                String strfunc = params.getString("Func");
                func.setText(strfunc);
                String strdespesa = params.getString("Desp");
                despesa.setText(strdespesa);
                String strvalorDesp = params.getString("ValorDesp");
                valordespesa.setText(strvalorDesp);
                String strvalorComb = params.getString("ValorComb");
                valorcomb.setText(strvalorComb);
                String strobs = params.getString("Obs");
                obs.setText(strobs);
                String strdata = params.getString("Data");
                data.setText(strdata);
            }
        }


    }



    public void save(View v) {
        /*Aqui coloca os valores dentro da string de uma forma mais ou menos formatada para exibiçao*/
        String text = func.getText().toString() + "\n" + despesa.getText().toString() + "\n" + valordespesa.getText().toString()
                + "\n" + valorcomb.getText().toString() + "\n" + obs.getText().toString() + "\n" + data.getText().toString() + "\n";
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(arqNome.getText().toString(), MODE_PRIVATE);
            fos.write(text.getBytes());
            /*escreve o arquivo e limpa os campos*/
            Toast.makeText(this, "Saved to " + getFilesDir() + "/" + arqNome.getText().toString(),
                    Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void load(View v) {
        /*aqui faz load do arquivo e mostra em um textview*/
        FileInputStream fis = null;

        try {

            fis = openFileInput(arqNome.getText().toString());
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;
            while ((text = br.readLine()) != null) {
                sb.append(text).append("\n");
            }

            tv.setText(sb.toString());


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean isNetworkAvailable() {
        /*função para verificar a conexão*/
        try {
            ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = null;

            if (manager != null) {
                networkInfo = manager.getActiveNetworkInfo();
            }
            return networkInfo != null && networkInfo.isConnected();
        } catch (NullPointerException e) {
            return false;
        }
    }

    public void enviaDados(View view) {

        AlertDialog alerta;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (!isNetworkAvailable()) {

            builder.setTitle("Dispositivo não conectado!").
                    setMessage("Conecte-se à uma rede e reinicie a aplicação para tentar novamente...\n\nOu então escolha a opção 'Guardar'.");
            alerta = builder.create();
            alerta.show();
            enviar.setClickable(false);

        } else {

            /*Função que prepara para o envio dos dados*/
            if (func.getText().toString().equals("") || despesa.getText().toString().equals("") ||
                    valordespesa.getText().toString().equals("") || obs.getText().toString().equals("")) {
                /*caso algum campo esteja vazio*/
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();

            } else {
                callBackground();
            }

        }
    }

    private void callBackground() {
        String strIdFunc = func.getText().toString();
        String strIdDesp = despesa.getText().toString();
        String strValorDesp = valordespesa.getText().toString();
        String strValorKm = valorcomb.getText().toString();
        String strObs = obs.getText().toString();
        String strData = data.getText().toString();


        String type = "regMov";
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute(type, strIdFunc, strIdDesp, strValorDesp, strValorKm, strObs, strData);

        AlertDialog alerta;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Dados enviados com sucesso!");
        builder.setMessage("Deseja enviar uma imagem?");

        builder.setPositiveButton("Sim", (arg0, arg1) -> {
            /*Função que prepara para o envio dos dados*/
            callImagem();
        });

        builder.setNegativeButton("Não", (arg0, arg1) -> {
            /*aqui colocar um sleep antes de voltar para o menu*/
            Toast.makeText(Guardar.this, "Ok! Despesa enviada sem imagem!", Toast.LENGTH_SHORT).show();
        });
        alerta = builder.create();
        alerta.show();
    }

    private void callImagem() {
        Intent img = new Intent(Guardar.this, Imagem.class);
        img.putExtra("User", func.getText().toString());
        img.putExtra("Desp", despesa.getText().toString());
        startActivity(img);
    }

    @Override
    public void onClick(View v) {

        if (v == pickD) {
            /* Data atual */
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            @SuppressLint("SetTextI18n")
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, monthOfYear, dayOfMonth)
                            -> data.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth), mYear, mMonth, mDay);
            datePickerDialog.show();
        }
    }
}


