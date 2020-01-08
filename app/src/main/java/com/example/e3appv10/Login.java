package com.example.e3appv10;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import JavaClasses.Loginjava;

public class Login extends AppCompatActivity {


    private EditText editCodice;
    private EditText editNome;
    private EditText editCognome;
    private Button btnRicerca;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private boolean loggato;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editCodice = (EditText) findViewById(R.id.editCodice);
        editNome = (EditText) findViewById(R.id.editNome);
        editCognome = (EditText) findViewById(R.id.editCognome);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPref.edit();
        loggato = sharedPref.getBoolean("loggato", false);
        if(loggato){
            Intent home = new Intent(Login.this,Home.class);
            startActivity(home);
        }
        //btnRicerca = (Button) findViewById(R.id.ricerca);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onclickBottone(View view) throws IOException {

        String codiceBiglietto = editCodice.getText().toString();
        String nome = editNome.getText().toString();
        String cognome = editCognome.getText().toString();

        if(dataControl(codiceBiglietto, nome, cognome)) {
            new Loginjava(this, codiceBiglietto, nome, cognome).execute("http://151.236.56.24/interfaccia_capitano/php/login.php?codice=" +
                    codiceBiglietto + "&nome=" + nome + "&cognome=" + cognome);
        }
        else{
            Toast.makeText(this,"Dati non corretti", Toast.LENGTH_SHORT);
        }




    }

    public boolean dataControl(String codice, String nome, String cognome){
        if((codice != null) && (!codice.equals(""))){
            if((nome != null) && (!nome.equals(""))){
                if((cognome != null) && (!cognome.equals(""))){
                    return true;
                }
            }
        }
        return false;
    }
}
