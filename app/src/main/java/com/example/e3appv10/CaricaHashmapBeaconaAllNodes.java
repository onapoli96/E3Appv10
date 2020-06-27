package com.example.e3appv10;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.example.e3appv10.giorgio.Helper.Nodo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class CaricaHashmapBeaconaAllNodes extends AsyncTask<String,Void,String> {
    private Context context;
    private HashMap<String,Nodo> beaconNodo;
    private float density;
    private AddGrafo ad;

    public CaricaHashmapBeaconaAllNodes(Context context, float density, AddGrafo ad){
        this.ad = ad;
        this.context = context;
        this.density = density;
        beaconNodo = new HashMap<>();
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
        System.out.println("RISPOSTA: "+risposta);
        return mostroDati(risposta);


    }
    @Override
    protected void onPostExecute(String result) {

        try {

            JSONArray array = new JSONArray(result);
            for(int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String idBeacon = object.get("IDBeacon").toString();
                int x = Integer.parseInt(object.get("X").toString());
                int y = Integer.parseInt(object.get("Y").toString());
                int piano = Integer.parseInt(object.get("Piano").toString());
                int scala = Integer.parseInt(object.get("Scala").toString());
                Nodo n = new Nodo(x, y, piano, scala);
                beaconNodo.put(idBeacon, n);
                System.out.println("sono qui"+ x+" "+ y + " "+ idBeacon);
            }
            ad.setAllBeaconsNodes(beaconNodo);

        } catch (JSONException e) {
            e.printStackTrace();
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

    public HashMap<String, Nodo> getHashMap(){
        return beaconNodo;
    }

}
