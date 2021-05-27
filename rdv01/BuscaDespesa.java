package com.controll_rdv.rdv01;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class BuscaDespesa extends AppCompatActivity implements View.OnClickListener {

    private static final int DATE_DIALOG_ID = 0;
    Button btnDatePickerInicial, btnDatePickerFinal;
    TextView tvdataInicial, tvdataFinal;
    String dataBancoI, dataBancoF;
    String user;
    private int controleData;
    private final DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String data = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
            if (controleData == 1) {
                tvdataInicial.setText(data);
            } else if (controleData == 2) {
                tvdataFinal.setText(data);
            }
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busca_despesa);

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
            System.out.println("Data no tamanho ok");
        } else if (tamString == 8) {
            System.out.println("Mes e dia < 10");
            String novaData = aumentaTamString();
            if (controleData == 1) {
                tvdataInicial.setText(novaData);
            } else if (controleData == 2) {
                tvdataFinal.setText(novaData);
            }
        } else if (tamString == 9) {
            System.out.println("Mes ou dia < 10.\nAgora descobrir qual dos dois é:");
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

        //checar datas iguais ou com intervalo entre elas < 1

        Intent gridDesp = new Intent(BuscaDespesa.this, GridDespesa.class);
        gridDesp.putExtra("User", user);
        gridDesp.putExtra("DataInicial", tvdataInicial.getText().toString());
        gridDesp.putExtra("DataFinal", tvdataFinal.getText().toString());
        gridDesp.putExtra("DataBancoI", dataBancoI);
        gridDesp.putExtra("DataBancoF", dataBancoF);
        startActivity(gridDesp);
    }
}
