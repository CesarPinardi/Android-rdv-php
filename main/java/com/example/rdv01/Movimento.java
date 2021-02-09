package com.example.rdv01;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

@SuppressWarnings("ALL")
public class Movimento extends AppCompatActivity implements View.OnClickListener{
    EditText  valor_desp, valor_km, obs;
    TextView id_func, id_desp, labelkm;

    /* declarações para o calendario */
    Button btnDatePicker, btnInsert;
    EditText txtDate;
    private int mYear, mMonth, mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movimento);

        id_func = findViewById(R.id.etFunc);
        valor_desp = findViewById(R.id.etValorDesp);
        valor_km = findViewById(R.id.etValorKm);
        obs = findViewById(R.id.etObs);

        id_desp = findViewById(R.id.tvResult);

        btnDatePicker = findViewById(R.id.btn_date);
        btnInsert = findViewById(R.id.btnInsertMov);

        txtDate = findViewById(R.id.in_date);

        labelkm = findViewById(R.id.labelKm);

        btnDatePicker.setOnClickListener(this);

        /*Pegando nome do usuario digitado na tela de login*/
        Intent intent = getIntent();
        if (intent != null) {
            Bundle params = intent.getExtras();
            if (params != null) {
                String login = params.getString("loginUser");
                id_func.setText(login);
            }
        }
    }

    @Override
    public void onClick(View v) {

        if (v == btnDatePicker) {
            /* Data atual */
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            @SuppressLint("SetTextI18n")
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, monthOfYear, dayOfMonth)
                            -> txtDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth), mYear, mMonth, mDay);
            datePickerDialog.show();
        }
    }

    public void getSelectedRadioButton(View view) {

        final RadioGroup radGroup = findViewById(R.id.rg_despesas);
        int radioID = radGroup.getCheckedRadioButtonId();
        RadioButton singleButton = findViewById(radioID);

        /*Setando os valores para enviar para o banco*/
        if(singleButton.getText().equals("Alimentação")){
            id_desp.setText("1");
        }
        if(singleButton.getText().equals("Combustível")){
            id_desp.setText("2");
            /*caso selecione combustivel, abre a editText*/
            valor_km.setVisibility(View.VISIBLE);
            labelkm.setVisibility(View.VISIBLE);
        }
        if (singleButton.getText().equals("Estacionamento")){
            id_desp.setText("3");
        }
        if(singleButton.getText().equals("Hospedagem")){
            id_desp.setText("4");
        }
        if(singleButton.getText().equals("Outros")){
            id_desp.setText("5");
        }
        if(singleButton.getText().equals("Pedágio")){
            id_desp.setText("6");
        }

    }

    public boolean isNetworkAvailable(){
        /*função para verificar a conexão*/
        try {
            ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = null;

            if(manager != null){
                networkInfo = manager.getActiveNetworkInfo();
            }
            return networkInfo != null && networkInfo.isConnected();
        } catch (NullPointerException e){
            return false;
        }
    }

    public void OnEnviarMovimento(View view) {
        /*caso não tenha internet internet*/
        AlertDialog alerta;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if(!isNetworkAvailable()){

            builder.setTitle("Dispositivo não conectado!").
            setMessage("Conecte-se à uma rede e reinicie a aplicação para tentar novamente...\n\nOu então escolha a opção 'Guardar'.");
            alerta = builder.create();
            alerta.show();
            btnInsert.setClickable(false);

        } else {

            builder.setMessage("Deseja enviar os dados?");

            builder.setPositiveButton("Sim", (arg0, arg1) -> {
                /*Função que prepara para o envio dos dados*/
                if(id_desp.getText().toString().equals("")){
                    builder.setMessage("Erro, campos não preenchidos!");
                }else {
                    callBackground();
                }
            });

            builder.setNegativeButton("Não", (arg0, arg1) ->
                    Toast.makeText(Movimento.this, "Dados não enviados" + arg1, Toast.LENGTH_SHORT).show());
            alerta = builder.create();
            alerta.show();
        }

    }

    private void callBackground() {

        String strIdFunc = id_func.getText().toString();
        String strIdDesp = id_desp.getText().toString() ;
        String strValorDesp = valor_desp.getText().toString();
        String strValorKm = valor_km.getText().toString();
        String strObs = obs.getText().toString();
        String strData = txtDate.getText().toString();

        String type = "regMov";
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute(type,strIdFunc,strIdDesp,strValorDesp,strValorKm,strObs,strData);

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
            Toast.makeText(Movimento.this, "Ok!" + arg1, Toast.LENGTH_SHORT).show();
        });
        alerta = builder.create();
        alerta.show();
    }

    private void callImagem() {

        Intent requestLink = new Intent(Movimento.this, Imagem.class);
        requestLink.putExtra("User", id_func.getText().toString());
        requestLink.putExtra("Desp", id_desp.getText().toString());
        startActivity(requestLink);

    }

    public void OnGuardarMovimento(View view) {
        /*Aqui guarda os valores em um arquivo json*/
        JSONObject jObj = new JSONObject();

        try {
            jObj.put("id_func", id_func.getText().toString());
            jObj.put("id_desp", id_desp.getText().toString());
            jObj.put("valor_desp", valor_desp.getText().toString());
            jObj.put("valor_km", valor_km.getText().toString());
            jObj.put("obs", obs.getText().toString());
            jObj.put("dataM", txtDate.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String strJson = jObj.toString();
        FileOutputStream fos = null;

        /*Nome do arquivo é o funcionário + despesa + data*/
        final String FILE_NAME = "Func " + id_func.getText().toString() + "- Despesa: " + id_desp.getText().toString() + "- Data: " + txtDate.getText().toString();

        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write(strJson.getBytes());

            Toast.makeText(this, "Salvo em  " + getFilesDir() + "/" + FILE_NAME,
                    Toast.LENGTH_LONG).show();

            // Toast para depois dos testes:
            // Toast.makeText(this, "Despesa salva!" , Toast.LENGTH_LONG).show();

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
}

