package com.example.rdv01;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.widget.Toast.LENGTH_LONG;

public class Movimento extends AppCompatActivity implements View.OnClickListener {

    EditText valor_desp, valor_km, obs, id_func;
    TextView id_desp, labelkm, dicadata;
    Spinner spnDespesa;
    Button btnDatePicker, btnInsert;
    EditText txtDate;
    private int mYear, mMonth, mDay;
    String idMovimento;
    String getGerenc, getEquip, getDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movimento);

        /*associando os campos*/
        id_func = findViewById(R.id.etFunc);
        valor_desp = findViewById(R.id.etValorDesp);

        valor_desp.setFilters(new InputFilter[]{ new InputFilterMinMax("0.00", "20.00")});

        valor_km = findViewById(R.id.etValorKm);
        obs = findViewById(R.id.etObs);
        id_desp = findViewById(R.id.tvResult);
        btnDatePicker = findViewById(R.id.btn_date);
        btnInsert = findViewById(R.id.btnInsertMov);
        txtDate = findViewById(R.id.in_date);
        labelkm = findViewById(R.id.labelKm);
        spnDespesa = findViewById(R.id.spinner2);
        dicadata = findViewById(R.id.tv_dicaData);

        selecionarDespesa();


        btnDatePicker.setOnClickListener(this);

        recuperarUsername();

        recuperarGerencia();

        if(!recuperarUsername()){
            recuperarVoltar();
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String strU = prefs.getString("usr", "");
        id_func.setText(strU);
        recuperarVoltar();

    }

    private void recuperarGerencia() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle params = intent.getExtras();
            if (params != null) {
                getGerenc = params.getString("gerencia");
                getEquip = params.getString("equipe");
                getDir = params.getString("direcao");
                System.out.println(getGerenc + "" + getEquip + "" + getDir);
            }
        }
    }

    private void selecionarDespesa() {
        /*Fazendo o dropdown com os valores*/
        List<String> despesas = new ArrayList<>();
        despesas.add("Selecione a despesa");
        despesas.add("Alimentação");
        despesas.add("Combustivel");
        despesas.add("Estacionamento");
        despesas.add("Hospedagem");
        despesas.add("Outros");
        despesas.add("Pedagio");

        ArrayAdapter<String> despesaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, despesas);

        despesaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnDespesa.setAdapter(despesaAdapter);

        spnDespesa.setOnItemSelectedListener(
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String yourDespesa = spnDespesa.getSelectedItem().toString();
                    /*relacionando os valores da lista com o valor desejado pelo banco*/
                    if (yourDespesa.equals("Alimentação")) {
                        id_desp.setText("1");
                        valor_desp.getText().clear();
                        valor_km.setClickable(false);
                        valor_km.setEnabled(false);
                        valor_km.setVisibility(View.INVISIBLE);
                        labelkm.setVisibility(View.INVISIBLE);
                        valor_desp.setFilters(new InputFilter[]{ new InputFilterMinMax("1.00", "20.00")});

                    } else {
                        if (yourDespesa.equals("Combustivel")) {
                            id_desp.setText("2");
                            valor_desp.getText().clear();
                            valor_km.setEnabled(true);
                            valor_km.setClickable(true);
                            valor_km.setVisibility(View.VISIBLE);
                            labelkm.setVisibility(View.VISIBLE);
                            valor_desp.setFilters(new InputFilter[]{ new InputFilterMinMax("1.00", "9999.00")});
                            valor_km.setFilters(new InputFilter[]{ new InputFilterMinMax("1.00", "99999.00")});
                        } else {
                            if (yourDespesa.equals("Estacionamento")) {
                                id_desp.setText("3");
                                valor_desp.getText().clear();
                                valor_km.setClickable(false);
                                valor_km.setEnabled(false);
                                valor_km.setVisibility(View.INVISIBLE);
                                labelkm.setVisibility(View.INVISIBLE);
                                valor_desp.setFilters(new InputFilter[]{ new InputFilterMinMax("1.00", "9999.00")});

                            } else {
                                if (yourDespesa.equals("Hospedagem")) {
                                    id_desp.setText("4");
                                    valor_desp.getText().clear();
                                    valor_km.setClickable(false);
                                    valor_km.setEnabled(false);
                                    valor_km.setVisibility(View.INVISIBLE);
                                    labelkm.setVisibility(View.INVISIBLE);
                                    valor_desp.setFilters(new InputFilter[]{ new InputFilterMinMax("1.00", "9999.00")});

                                } else {
                                    if (yourDespesa.equals("Outros")) {
                                        id_desp.setText("5");
                                        valor_desp.getText().clear();
                                        valor_km.setClickable(false);
                                        valor_km.setEnabled(false);
                                        valor_km.setVisibility(View.INVISIBLE);
                                        labelkm.setVisibility(View.INVISIBLE);
                                        valor_desp.setFilters(new InputFilter[]{ new InputFilterMinMax("1.00", "9999.00")});

                                    } else {
                                        if (yourDespesa.equals("Pedagio")) {
                                            id_desp.setText("6");
                                            valor_desp.getText().clear();
                                            valor_km.setClickable(false);
                                            valor_km.setEnabled(false);
                                            valor_km.setVisibility(View.INVISIBLE);
                                            labelkm.setVisibility(View.INVISIBLE);
                                            valor_desp.setFilters(new InputFilter[]{ new InputFilterMinMax("1.00", "9999.00")});

                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            }
        );
    }

    /* botão para abrir o calendario */
    @Override
    public void onClick(View v) {
        if (v == btnDatePicker) {
            dicadata.setVisibility(View.INVISIBLE);
            escondeTeclado();
            /* Data atual */
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            @SuppressLint("SetTextI18n")
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) ->
                    txtDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth),
                    mYear,
                    mMonth,
                    mDay);
            datePickerDialog.show();
        }
    }

    private void verificaUser(){
        if(id_func.getText().toString().equals("")){
            Intent i = new Intent(this, MainActivity.class);
            Toast.makeText(this, "Erro, usuario não encontrado, voltando para fazer login", LENGTH_LONG).show();

            new Handler().postDelayed(() ->
                    startActivity(i), 3000);

        }
    }

    public void OnEnviarMovimento(View view) {
        verificaUser();
        AlertDialog alerta;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        /* caso não tenha internet */
        if (!isNetworkAvailable()) {
            builder.setTitle("Dispositivo não conectado!").setMessage("Conecte-se à uma rede e reinicie a aplicação para tentar novamente...");
            alerta = builder.create();
            alerta.show();
            btnInsert.setClickable(false);
        } else {
            builder.setMessage("Deseja enviar os dados?");
            builder.setPositiveButton("Sim", (arg0, arg1) -> {
                    /* Função que prepara para o envio dos dados */
                    if(checarCampos()){
                        enviarDados();
                    }else {
                        Log.d("enviar", "erro");
                    }
                }
            );
            builder.setNegativeButton("Não", (arg0, arg1) -> Toast.makeText(Movimento.this, "Dados não enviados", Toast.LENGTH_SHORT).show());
            alerta = builder.create();
            alerta.show();
        }
    }


    /*funções simples*/

    private boolean recuperarUsername() {
        /* Pegando nome do usuario digitado na tela de login */
        Intent intent = getIntent();
        if (intent != null) {
            Bundle params = intent.getExtras();
            if (params != null) {
                String login = params.getString("loginUser");
                id_func.setText(login);
                getGerenc = params.getString("gerencia");
                getEquip = params.getString("equipe");
                getDir = params.getString("direcao");

                return true;
            }
        } else {
            return false;
        }
        return false;
    }

    private void recuperarVoltar() {
        /*recuperando o username a partir da funcao sharedpreferences*/
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Movimento.this);
        SharedPreferences.Editor editor = preferences.edit();
        String saveUser = id_func.getText().toString();
        editor.putString("usr", saveUser);
        editor.apply();


    }

    private void escondeTeclado(){
        /*faz o teclado se esconder*/
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private boolean checarCampos(){
        if (id_func.getText().toString().equals("") || id_desp.getText().toString().equals("") ||
            valor_desp.getText().toString().equals("") || obs.getText().toString().equals("")
            || txtDate.getText().toString().equals("")) {
            /* caso algum campo esteja vazio */
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
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

    private void enviarDados() {
        Intent img = new Intent(Movimento.this, Imagem.class);
        img.putExtra("User", id_func.getText().toString());
        img.putExtra("Desp", id_desp.getText().toString());
        img.putExtra("IdMov", idMovimento);
        img.putExtra("ValorDesp", valor_desp.getText().toString());
        img.putExtra("ValorComb", valor_km.getText().toString());
        img.putExtra("Obs", obs.getText().toString());
        img.putExtra("Data", txtDate.getText().toString());
        img.putExtra("gerencia", getGerenc);
        img.putExtra("equipe", getEquip);
        img.putExtra("direcao", getDir);
        startActivity(img);
    }

    public void OnVoltar(View view) {
        Intent v = new Intent(Movimento.this, MainActivity.class);
        startActivity(v);
    }


}
