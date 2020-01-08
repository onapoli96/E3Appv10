package JavaClasses;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Loginjava extends AsyncTask<String,Void,String>{
    private Context context;

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;


    public Loginjava(Context context, String codice, String nome, String cognome){
        this.context = context;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected String doInBackground(String ... url) {
        // azioni di invio
        URL paginaURL = null;
        InputStream risposta = null;
        try {
            paginaURL = new URL(url[0]);
            HttpURLConnection client = (HttpURLConnection) paginaURL.openConnection();
            risposta = new BufferedInputStream(client.getInputStream());


        } catch (Exception e) {
            e.printStackTrace();
        }
        return mostroDati(risposta);


    }
    @Override
    protected void onPostExecute(String result) {
        String ce = "";
        String nome = "";
        String cognome = "";
        String gruppo = "";
        String cabina = "";
        try {
            JSONObject jObject = new JSONObject(result);
            ce = jObject.getString("ce");
            nome = jObject.getString("nome");
            cognome = jObject.getString("cognome");
            gruppo = jObject.getString("gruppo");
            cabina = jObject.getString("cabina");


        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(ce.equals("SI")) {
            Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
            sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            editor = sharedPref.edit();

            editor.putBoolean("loggato", true);
            editor.putString("nome", nome);
            editor.putString("cognome", cognome);
            editor.putString("gruppo", gruppo);
            editor.putString("cabina", cabina);
            editor.commit();
            Intent home = new Intent(context, com.example.e3appv10.Home.class);
            context.startActivity(home);

        }
        else{
            Toast.makeText(context, "l'utente non è presente", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static String mostroDati(InputStream in) {
        StringBuilder sb = new StringBuilder();
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String nextLine = "";
            while ((nextLine = reader.readLine()) != null) {
                sb.append(nextLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
