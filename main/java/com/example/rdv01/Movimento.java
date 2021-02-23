package com.example.rdv01;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class Movimento extends AppCompatActivity implements View.OnClickListener {
    EditText valor_desp, valor_km, obs, id_func;
    TextView id_desp, labelkm;

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
        txtDate.setEnabled(false);
        txtDate.setClickable(false);
        labelkm = findViewById(R.id.labelKm);

        btnDatePicker.setOnClickListener(this);

        /* Pegando nome do usuario digitado na tela de login */
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

            txtDate.setClickable(true);
            txtDate.setEnabled(true);
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            @SuppressLint("SetTextI18n")
            DatePickerDialog datePickerDialog =
                    new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) ->
                    txtDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth), mYear, mMonth, mDay);
            datePickerDialog.show();
        }
    }

    public void getSelectedRadioButton(View view) {

        final RadioGroup radGroup = findViewById(R.id.rg_despesas);
        int radioID = radGroup.getCheckedRadioButtonId();
        RadioButton singleButton = findViewById(radioID);

        /* Setando os valores para enviar para o banco */
        if (singleButton.getText().equals("Alimentação")) {
            id_desp.setText("1");
            /* deixando o campo de combustivel invisivel e desativado*/
            valor_km.setClickable(false);
            valor_km.setEnabled(false);
            valor_km.setVisibility(View.INVISIBLE);
            labelkm.setVisibility(View.INVISIBLE);
        }
        if (singleButton.getText().equals("Combustível")) {
            id_desp.setText("2");
            /* caso selecione combustivel, abre a editText */
            valor_km.setVisibility(View.VISIBLE);
            labelkm.setVisibility(View.VISIBLE);
            valor_km.setEnabled(true);
            valor_km.setClickable(true);
        }
        if (singleButton.getText().equals("Estacionamento")) {
            id_desp.setText("3");
            valor_km.setClickable(false);
            valor_km.setEnabled(false);
            valor_km.setVisibility(View.INVISIBLE);
            labelkm.setVisibility(View.INVISIBLE);

        }
        if (singleButton.getText().equals("Hospedagem")) {
            id_desp.setText("4");
            valor_km.setClickable(false);
            valor_km.setEnabled(false);
            valor_km.setVisibility(View.INVISIBLE);
            labelkm.setVisibility(View.INVISIBLE);

        }
        if (singleButton.getText().equals("Outros")) {
            id_desp.setText("5");
            valor_km.setClickable(false);
            valor_km.setEnabled(false);
            valor_km.setVisibility(View.INVISIBLE);
            labelkm.setVisibility(View.INVISIBLE);
        }
        if (singleButton.getText().equals("Pedágio")) {
            id_desp.setText("6");
            valor_km.setClickable(false);
            valor_km.setEnabled(false);
            valor_km.setVisibility(View.INVISIBLE);
            labelkm.setVisibility(View.INVISIBLE);
        }

    }

    public boolean isNetworkAvailable() {
        /* função para verificar a conexão */
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

    public void OnEnviarMovimento(View view) {
        /* caso não tenha internet internet */
        AlertDialog alerta;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (!isNetworkAvailable()) {

            builder.setTitle("Dispositivo não conectado!")
                    .setMessage("Conecte-se à uma rede e reinicie a aplicação para tentar novamente...");
            alerta = builder.create();
            alerta.show();
            btnInsert.setClickable(false);

        } else {

            builder.setMessage("Deseja enviar os dados?");
            builder.setPositiveButton("Sim", (arg0, arg1) -> {
                /* Função que prepara para o envio dos dados */
                if (id_func.getText().toString().equals("") || id_desp.getText().toString().equals("")
                        || valor_desp.getText().toString().equals("") || obs.getText().toString().equals("")) {
                    /* caso algum campo esteja vazio */
                    Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();

                } else {
                    /* chama a funcao que envia os dados para o servidor */
                    callBackground();
                }
            });

            builder.setNegativeButton("Não",
                    (arg0, arg1) -> Toast.makeText(Movimento.this, "Dados não enviados", Toast.LENGTH_SHORT).show());
            alerta = builder.create();
            alerta.show();
        }

    }

    private void callBackground() {
        /*enviando os dados por servidor via classe backgroundworker*/
        String strIdFunc = id_func.getText().toString();
        String strIdDesp = id_desp.getText().toString();
        String strValorDesp = valor_desp.getText().toString();
        String strValorKm = valor_km.getText().toString();
        String strObs = obs.getText().toString();
        String strData = txtDate.getText().toString();

        String type = "regMov";
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute(type, strIdFunc, strIdDesp, strValorDesp, strValorKm, strObs, strData);

        new Handler().postDelayed(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Adicionar uma imagem?");

            builder.setNegativeButton("Não", (dialog, id) -> {
                dialog.cancel();
                new Handler().postDelayed(() -> {
                    valor_desp.getText().clear();
                    obs.getText().clear();
                }, 3000);
            });

            builder.setPositiveButton("Sim", (dialog, id) -> {
                callImagem();
                dialog.cancel();
            });
            builder.create().show();
        }, 2000);

    }

    private void callImagem() {
        Intent img = new Intent(Movimento.this, Imagem.class);
        img.putExtra("User", id_func.getText().toString());
        img.putExtra("Desp", id_desp.getText().toString());
        startActivity(img);
    }

    public void OnVoltar(View view) {
        Intent v = new Intent(Movimento.this, MainActivity.class);
        startActivity(v);
    }
}
