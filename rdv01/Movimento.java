package com.controll_rdv.rdv01;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

//import com.controll_rdv.rdv01.databinding.ActivityMovimentoBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.widget.Toast.LENGTH_LONG;

public class Movimento extends AppCompatActivity implements View.OnClickListener {

    private static final int DATE_DIALOG_ID = 0;
    EditText valor_desp, valor_km, obs, id_func;
    TextView id_desp, labelkm, dicadata;
    Spinner spnDespesa;
    Button btnDatePicker, btnInsert;
    EditText txtDate;
    String idMovimento;
    String getGerenc, getEquip, getDir;
    String dataBanco;
    private final DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String data = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
            txtDate.setText(data);
            /*
             * A lógica por trás do formato da data:
             *   a string terá que ter tamanho = 10, sendo assim
             *   se o tamanho for = 10 -> a data está ok
             *   se o tamanho for = 8 -> o dia ou o mes é < 10
             *       encontrando a posição do "-"
             *       ->(00-00-0000) -> tamanho = 10
             *       ->(0-0-0000)   -> tamanho = 8
             *       ->(00-0-0000)  -> mês < 10
             *       ->(0-00-0000)  -> dia < 10
             *         [0123456789]
             *           se for em [1] -> o dia é < 10
             *           se for em [4] -> o mes é < 10
             *       após encontrar, inserir um caracter "0" uma posição antes (ou 0 ou 3)
             *   se o tamanho for = 7 -> o dia e o mes são < 10
             *       refaz a string acrescentando um 0 antes do dia e do mes
             *
             *   depois disso, separa a string em 3 partes (dia, mes, ano) e
             *   coloca na ordem para inserir no banco (ano, mes, dia)
             */
            formataData();
            inverteData();
        }
    };
    String finalString;
    //ActivityMovimentoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movimento);
        //binding = ActivityMovimentoBinding.inflate(getLayoutInflater());
        //setContentView(binding.getRoot());

        /*associando os campos*/
        id_func = findViewById(R.id.etFunc);
        valor_desp = findViewById(R.id.etValorDesp);
        valor_km = findViewById(R.id.etValorKm);
        obs = findViewById(R.id.etObs);
        id_desp = findViewById(R.id.tvResult);
        btnDatePicker = findViewById(R.id.btn_date);
        btnInsert = findViewById(R.id.btnInsertMov);
        txtDate = findViewById(R.id.in_date);
        labelkm = findViewById(R.id.labelKm);
        spnDespesa = findViewById(R.id.spinnerDespesa);
        dicadata = findViewById(R.id.tv_dicaData);

        selecionarDespesa();

        valor_desp.addTextChangedListener(new NumberTextWatcher(valor_desp));

        btnDatePicker.setOnClickListener(this);

        recuperarUsername();

        recuperarGerencia();

        if (!recuperarUsername()) {
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
                System.out.println("Recuperar Gerencia: \nGerencia: " + getGerenc);
                System.out.println("Equipe: " + getEquip);
                System.out.println("Direcao: " + getDir);
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
                            valor_desp.setHint("Valor limite = 20,00");


                        } else {
                            if (yourDespesa.equals("Combustivel")) {
                                id_desp.setText("2");
                                valor_desp.getText().clear();
                                valor_km.setEnabled(true);
                                valor_km.setClickable(true);
                                valor_km.setVisibility(View.VISIBLE);
                                labelkm.setVisibility(View.VISIBLE);
                                valor_desp.setHint("");

                            } else {
                                if (yourDespesa.equals("Estacionamento")) {
                                    id_desp.setText("3");
                                    valor_desp.getText().clear();
                                    valor_km.setClickable(false);
                                    valor_km.setEnabled(false);
                                    valor_km.setVisibility(View.INVISIBLE);
                                    labelkm.setVisibility(View.INVISIBLE);
                                    valor_desp.setHint("");

                                } else {
                                    if (yourDespesa.equals("Hospedagem")) {
                                        id_desp.setText("4");
                                        valor_desp.getText().clear();
                                        valor_km.setClickable(false);
                                        valor_km.setEnabled(false);
                                        valor_km.setVisibility(View.INVISIBLE);
                                        labelkm.setVisibility(View.INVISIBLE);
                                        valor_desp.setHint("");

                                    } else {
                                        if (yourDespesa.equals("Outros")) {
                                            id_desp.setText("5");
                                            valor_desp.getText().clear();
                                            valor_km.setClickable(false);
                                            valor_km.setEnabled(false);
                                            valor_km.setVisibility(View.INVISIBLE);
                                            labelkm.setVisibility(View.INVISIBLE);
                                            valor_desp.setHint("");

                                        } else {
                                            if (yourDespesa.equals("Pedagio")) {
                                                id_desp.setText("6");
                                                valor_desp.getText().clear();
                                                valor_km.setClickable(false);
                                                valor_km.setEnabled(false);
                                                valor_km.setVisibility(View.INVISIBLE);
                                                labelkm.setVisibility(View.INVISIBLE);
                                                valor_desp.setHint("");

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
    @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
    @Override
    protected Dialog onCreateDialog(int id) {
        escondeTeclado();
        Calendar calendario = Calendar.getInstance();

        int ano = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        if (id == DATE_DIALOG_ID) {
            return new DatePickerDialog(this, mDateSetListener, ano, mes, dia);
        }

        return null;
    }

    public void formataData() {
        String dataFormatada = txtDate.getText().toString();
        System.out.println("Data sem formatar, vindo do pickerDialog: " + dataFormatada);
        System.out.println("Verificando tamanho da string...");

        int tamString = dataFormatada.length();

        if (tamString == 10) {
            System.out.println("Data no tamanho ok");
        } else if (tamString == 8) {
            System.out.println("Mes e dia < 10");
            String novaData = aumentaTamString();
            txtDate.setText(novaData);
        } else if (tamString == 9) {
            System.out.println("Mes ou dia < 10.\nAgora descobrir qual dos dois é:");
            char achaDia = dataFormatada.charAt(1);
            char achaMes = dataFormatada.charAt(4);
            if (achaDia == '-') {
                String novaData = addChar(dataFormatada, '0', 0);
                System.out.println("Dia < 10: " + novaData);
                txtDate.setText(novaData);
            } else if (achaMes == '-') {
                String novaData = addChar(dataFormatada, '0', 3);
                System.out.println("Mes < 10: " + novaData);
                txtDate.setText(novaData);
            }
        }
    }

    public String aumentaTamString() {
        String data = txtDate.getText().toString();
        String auxDia = data.substring(0, 1);
        String auxMes = data.substring(2, 3);
        String auxAno = data.substring(4, 8);

        System.out.println("-----Aumento da string-----");
        System.out.println("Mes:" + auxMes + "\nDia:" + auxDia + "\nAno:" + auxAno);

        String aux = "0" + auxDia + "-" + "0" + auxMes + "-" + auxAno;
        System.out.println("Nova data aumentada" + aux);
        return aux;
    }

    public String addChar(String str, char ch, int position) {
        int len = str.length();
        char[] updatedArr = new char[len + 1];
        str.getChars(0, position, updatedArr, 0);
        updatedArr[position] = ch;
        str.getChars(position, len, updatedArr, position + 1);
        return new String(updatedArr);
    }

    public void inverteData() {
        String dataBr = txtDate.getText().toString();

        String auxAno = dataBr.substring(6, 10);
        String auxMes = dataBr.substring(3, 5);
        String auxDia = dataBr.substring(0, 2);

        System.out.println("Data para inverter: \nAno:" + auxAno + "\nMes:" + auxMes + "\nDia:" + auxDia);

        dataBanco = auxAno + "-" + auxMes + "-" + auxDia;
        System.out.println("\nData invertida para o banco: " + dataBanco);
    }

    @Override
    public void onClick(View v) {
        if (v == btnDatePicker)
            showDialog(DATE_DIALOG_ID);
    }

    private void verificaUser() {
        if (id_func.getText().toString().equals("")) {
            Intent i = new Intent(this, MainActivity.class);
            Toast.makeText(this, "Erro, usuario não encontrado, voltando para fazer login", LENGTH_LONG).show();

            new Handler().postDelayed(() ->
                    startActivity(i), 3000);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
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
                        if (checarCampos()) {
                            enviarDados();
                        } else {
                            Log.d("enviar", "erro");
                        }
                    }
            );
            builder.setNegativeButton("Não", (arg0, arg1) -> Toast.makeText(Movimento.this, "Dados não enviados", Toast.LENGTH_SHORT).show());
            alerta = builder.create();
            alerta.show();
        }
    }


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
                System.out.println("Recuperar Username:\nGerencia: " + getGerenc);
                System.out.println("Equipe: " + getEquip);
                System.out.println("Direcao: " + getDir);
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

    private void escondeTeclado() {
        /*faz o teclado se esconder*/
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private boolean checarCampos() {
        if (id_func.getText().toString().equals("") || id_desp.getText().toString().equals("") ||
                valor_desp.getText().toString().equals("") || txtDate.getText().toString().equals("")) {
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean transformarValorDesp() {

        /*recebe o valor da despesa e transforma em string*/
        String valor = valor_desp.getText().toString();

        System.out.println("Valor: " + valor);

        /*pega o tamanho para comparar*/
        int tamanhoString = valor.length();

        /*aqui tira o R$ da string*/
        String tiraRS = valor.substring(2, tamanhoString);

        System.out.println("Valor sem R$: " + tiraRS);

        /*procura pelo char ,*/
        int firstIndex = tiraRS.indexOf(',');
        System.out.println(" Primeira ocorrencia de '-'" +
                " é no índice : " + firstIndex);

        /*troca o , por .*/
        finalString = replace(tiraRS, '.', firstIndex);

        System.out.println("String final: " + finalString);

        /*pega o numero da despesa*/
        int despesa = Integer.parseInt(id_desp.getText().toString());

        System.out.println(despesa);

        /*transforma a string, agora sem R$ e . no lugar da , em tipo float*/
        float f = Float.parseFloat(finalString);

        System.out.println("Float : " + f);

        /*aqui se a despesa for igual a 1 (alimentacao) e valor maior que 20, erro*/
        if (f > 20.0 && despesa == 1) {
            System.out.println("Despesa: alimentacao" + " e valor da despesa: " + f);
            escondeTeclado();
            return false;
        } else
            return true;
    }

    public String replace(String str, char replace, int index) {
        if (str == null) {
            return null;
        } else if (index < 0 || index >= str.length()) {
            return str;
        }
        char[] chars = str.toCharArray();
        chars[index] = replace;
        return String.valueOf(chars);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void enviarDados() {

        boolean check;

        check = transformarValorDesp();

        if (check) {
            Intent img = new Intent(Movimento.this, Imagem.class);
            img.putExtra("User", id_func.getText().toString());
            img.putExtra("Desp", id_desp.getText().toString());
            img.putExtra("IdMov", idMovimento);
            img.putExtra("ValorDesp", finalString);
            img.putExtra("ValorComb", valor_km.getText().toString());
            img.putExtra("Obs", obs.getText().toString());
            img.putExtra("Data", dataBanco);
            img.putExtra("gerencia", getGerenc);
            img.putExtra("equipe", getEquip);
            img.putExtra("direcao", getDir);
            System.out.print("Dados: \n"
                    + id_func.getText().toString() +
                    "\n" + id_desp.getText().toString() +
                    "\n" + idMovimento +
                    "\n" + finalString +
                    "\n" + valor_km.getText().toString() +
                    "\n" + obs.getText().toString() +
                    "\n" + dataBanco +
                    "\n" + getGerenc +
                    "\n" + getEquip +
                    "\n" + getDir);
            startActivity(img);
            finish();
        } else {
            valor_desp.getFocusable();
            Toast.makeText(this, "Valor de alimentação maior que 20 reais!", LENGTH_LONG).show();
        }
    }

    public void OnVoltar(View view) {
        Intent v = new Intent(Movimento.this, Menu.class);
        startActivity(v);
    }

}
