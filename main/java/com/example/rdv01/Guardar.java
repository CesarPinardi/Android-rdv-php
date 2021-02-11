package com.example.rdv01;

import android.content.Intent;
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

public class Guardar extends AppCompatActivity {
    private static final String FILE_NAME = "example.txt";
    EditText func, despesa, valordespesa, valorcomb, obs, data, arqNome;
    Button insert, show;
    TextView tv;


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
        String text = "\nFuncionario-> " + func.getText().toString() + "\nDespesa-> " + despesa.getText().toString() + "\nValor-> " + valordespesa.getText().toString()
                + "\nValorComb-> " + valorcomb.getText().toString() + "\nObs-> " + obs.getText().toString() + "\nData-> " + data.getText().toString() +"\n";
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(arqNome.getText().toString(), MODE_PRIVATE);
            fos.write(text.getBytes());
            func.getText().clear();
            despesa.getText().clear();
            valordespesa.getText().clear();
            valorcomb.getText().clear();
            obs.getText().clear();
            data.getText().clear();
            arqNome.getText().clear();
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
}
