package com.example.e3appv10;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {


    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private boolean loggato;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPref.edit();
        loggato = sharedPref.getBoolean("loggato", false);
        System.out.println(loggato);
        setContentView(R.layout.activity_main);
        if(loggato){
            Intent home = new Intent(MainActivity.this,Home.class);
            startActivity(home);
        }
        else{
            Intent login = new Intent(MainActivity.this,Login.class);
            startActivity(login);
            //lancia schermata login
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(loggato){
            Intent home = new Intent(MainActivity.this,Home.class);
            startActivity(home);
        }
        else{
            Intent login = new Intent(MainActivity.this,Login.class);
            startActivity(login);
        }
    }

}
