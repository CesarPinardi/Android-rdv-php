package com.controll_rdv.rdv01;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class Menu extends AppCompatActivity {

    TextView user;
    int classe;
    String getGerenc, getEquip, getDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        /*declarações de variaveis */

        user = findViewById(R.id.tvnomeMenu);
        pegarDados();
    }

    private void pegarDados() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle params = intent.getExtras();
            if (params != null) {
                String login = params.getString("loginUser");
                user.setText(login);
                getGerenc = params.getString("gerencia");
                getEquip = params.getString("equipe");
                getDir = params.getString("direcao");
                System.out.println("direcao: " + getDir);
                System.out.println("equipe: " + getEquip);
                System.out.println("gerencia: " + getGerenc);
            }
        }
    }

    private void enviarDados(int qualClasse) {

        if(qualClasse == 1){

            Intent cadastro = new Intent(Menu.this, Movimento.class);
            cadastro.putExtra("loginUser", user.getText().toString());
            cadastro.putExtra("direcao", getDir);
            cadastro.putExtra("equipe", getEquip);
            cadastro.putExtra("gerencia", getGerenc);
            startActivity(cadastro);

        } else if (qualClasse == 2){

            Intent busca = new Intent(Menu.this, BuscaDespesa.class);
            busca.putExtra("User", user.getText().toString());
            startActivity(busca);

        } else {
            Toast.makeText(this, "Erro", Toast.LENGTH_SHORT).show();
        }
    }

    public void cadastroDespesa(View view) {
        classe = 1;
        enviarDados(classe);
    }

    public void visualizarDespesas(View view) {
        classe = 2;
        enviarDados(classe);
    }
}