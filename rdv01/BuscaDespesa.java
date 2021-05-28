package com.controll_rdv.rdv01;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.Calendar;

public class BuscaDespesa extends AppCompatActivity implements View.OnClickListener {

    private static final int DATE_DIALOG_ID = 0;
    Button btnDatePickerInicial, btnDatePickerFinal;
    TextView tvdataInicial, tvdataFinal;
    String dataBancoI, dataBancoF, user;

    private int controleData;
    private final DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String data = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
            if (controleData == 1) {
                tvdataInicial.setText(data);
            } else if (controleData == 2) {
                tvdataFinal.setText(data);
            }
            formataData();
            inverteData();
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busca_despesa);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        btnDatePickerInicial = findViewById(R.id.btnDataInicial);
        btnDatePickerFinal = findViewById(R.id.btnDataFinal);

        tvdataInicial = findViewById(R.id.tvDataInicial);
        tvdataFinal = findViewById(R.id.tvDataFinal);

        pegarDados();

        btnDatePickerInicial.setOnClickListener(this);
        btnDatePickerFinal.setOnClickListener(this);

    }

    private void pegarDados() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle params = intent.getExtras();
            if (params != null) {
                String usuario = params.getString("User");
                user = usuario;
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btnDatePickerInicial) {
            controleData = 1;
            showDialog(DATE_DIALOG_ID);
        } else if (v == btnDatePickerFinal) {
            controleData = 2;
            showDialog(DATE_DIALOG_ID);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
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
        String dataFormatada = "";
        if (controleData == 1) {
            dataFormatada = tvdataInicial.getText().toString();
        } else if (controleData == 2) {
            dataFormatada = tvdataFinal.getText().toString();
        }

        System.out.println("Data sem formatar, vindo do pickerDialog: " + dataFormatada);
        System.out.println("Verificando tamanho da string...");

        int tamString = dataFormatada.length();

        if (tamString == 10) {
            System.out.println("Tamanho string = 10 -> Data no tamanho ok");
        } else if (tamString == 8) {
            System.out.println("Tamanho string = 8 -> Mes e dia < 10");
            String novaData = aumentaTamString();
            if (controleData == 1) {
                tvdataInicial.setText(novaData);
            } else if (controleData == 2) {
                tvdataFinal.setText(novaData);
            }
        } else if (tamString == 9) {
            System.out.println("Tamanho string = 9 -> Mes ou dia < 10.\nAgora descobrir qual dos dois é: ");
            char achaDia = dataFormatada.charAt(1);
            char achaMes = dataFormatada.charAt(4);
            if (achaDia == '-') {
                String novaData = addChar(dataFormatada, '0', 0);
                System.out.println("Dia < 10: " + novaData);
                if (controleData == 1) {
                    tvdataInicial.setText(novaData);
                } else if (controleData == 2) {
                    tvdataFinal.setText(novaData);
                }
            } else if (achaMes == '-') {
                String novaData = addChar(dataFormatada, '0', 3);
                System.out.println("Mes < 10: " + novaData);
                if (controleData == 1) {
                    tvdataInicial.setText(novaData);
                } else if (controleData == 2) {
                    tvdataFinal.setText(novaData);
                }
            }
        }
    }

    public String aumentaTamString() {
        String data = "";

        if (controleData == 1) {
            data = tvdataInicial.getText().toString();
        } else if (controleData == 2) {
            data = tvdataFinal.getText().toString();
        }

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

        String dataBr;

        if (controleData == 1) {
            dataBr = tvdataInicial.getText().toString();
            String auxAno = dataBr.substring(6, 10);
            String auxMes = dataBr.substring(3, 5);
            String auxDia = dataBr.substring(0, 2);

            System.out.println("Data para inverter: \nAno:" + auxAno + "\nMes:" + auxMes + "\nDia:" + auxDia);

            dataBancoI = auxAno + "-" + auxMes + "-" + auxDia;
            System.out.println("\nData invertida para o banco: " + dataBancoI);
        } else if (controleData == 2) {
            dataBr = tvdataFinal.getText().toString();
            String auxAno = dataBr.substring(6, 10);
            String auxMes = dataBr.substring(3, 5);
            String auxDia = dataBr.substring(0, 2);

            System.out.println("Data para inverter: \nAno:" + auxAno + "\nMes:" + auxMes + "\nDia:" + auxDia);

            dataBancoF = auxAno + "-" + auxMes + "-" + auxDia;
            System.out.println("\nData invertida para o banco: " + dataBancoF);

        }

    }

    public void confirmarDatas(View view) {

        String dataF = tvdataFinal.getText().toString();
        String dataI = tvdataInicial.getText().toString();

        System.out.print("Data Inicial: " + dataI);
        System.out.print("Data Final: " + dataF);

        if (dataI.isEmpty() || dataF.isEmpty()) {

            Toast.makeText(getApplicationContext(), "Escolha duas datas para confirmar!", Toast.LENGTH_SHORT).show();

        } else if (dataI.equals(dataF)) {

            Toast.makeText(getApplicationContext(), "As datas são iguais!", Toast.LENGTH_SHORT).show();

        } else {

            Intent gridDesp = new Intent(BuscaDespesa.this, GridDespesa.class);
            gridDesp.putExtra("User", user);
            gridDesp.putExtra("DataInicial", tvdataInicial.getText().toString());
            gridDesp.putExtra("DataFinal", tvdataFinal.getText().toString());
            gridDesp.putExtra("DataBancoI", dataBancoI);
            gridDesp.putExtra("DataBancoF", dataBancoF);
            startActivity(gridDesp);

        }

    }
}