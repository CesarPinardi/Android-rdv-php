package com.example.rdv01;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

public class Movimento extends AppCompatActivity implements View.OnClickListener{
    private static final String FILE_NAME = "movimento";
    EditText  valor_desp, valor_km, obs;
    TextView id_func, id_desp;

    /* declaracoes para o calendario */
    Button btnDatePicker;
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
        txtDate = findViewById(R.id.in_date);

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

        if(singleButton.getText().equals("Alimentação")){
            id_desp.setText("1");
        }
        if(singleButton.getText().equals("Combustível")){
            id_desp.setText("2");
            valor_km.setVisibility(View.VISIBLE);
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

    public void OnGuardarMovimento(View view) {
        Intent intent = new Intent(this, Imagem.class);
        startActivity(intent);
    }

    public void OnEnviarMovimento(View view) {

        AlertDialog alerta;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Deseja enviar os dados?");

        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                /*Função que prepara para o envio dos dados*/
                callBackground();
            }
        });

        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                Toast.makeText(Movimento.this, "Dados não enviados" + arg1, Toast.LENGTH_SHORT).show();
            }
        });
        alerta = builder.create();
        alerta.show();
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
    }

}
