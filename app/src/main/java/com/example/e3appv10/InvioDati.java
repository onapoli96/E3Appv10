package com.example.e3appv10;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.example.e3appv10.giorgio.Helper.Edge;
import com.example.e3appv10.giorgio.Helper.Nodo;
import com.example.e3appv10.giorgio.customs.CustomViewMappa;
import com.example.e3appv10.giorgio.customs.FunzioniSelezionaNodo;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
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
import java.util.ArrayList;

public class InvioDati extends AsyncTask<String,Void,String> {
    private Context context;
    //la variabile flag viene utilizzata per sapere quando viene eseguito l'onPostexecute
   // private boolean flag;

    private Graph<Nodo, DefaultEdge> grafo;
    private ArrayList<Nodo> nodi;
    private ArrayList<Edge<Nodo,String>> archi;
    private float density;
    private ZoomLayout zoomLayout;
    private FunzioniSelezionaNodo funzioniSelezionaNodo;

    public InvioDati(Context context, float density){
        this.context = context;
        this.density = density;
        this.grafo = new DefaultDirectedGraph<>(DefaultEdge.class);
        nodi = new ArrayList<>();
        archi = new ArrayList<>();
        this.zoomLayout = null;
        this.funzioniSelezionaNodo = null;
    }

    public InvioDati(Context context, float density, ZoomLayout zoomLayout, FunzioniSelezionaNodo funzioniSelezionaNodo){
        this.context = context;
        this.density = density;
        this.grafo = new DefaultDirectedGraph<>(DefaultEdge.class);
        nodi = new ArrayList<>();
        archi = new ArrayList<>();
        this.zoomLayout = zoomLayout;
        this.funzioniSelezionaNodo = funzioniSelezionaNodo;
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


        try {
            JSONArray array = new JSONArray(result);
            for(int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                int x1 = Integer.parseInt(object.get("X1").toString());
                int y1 = Integer.parseInt(object.get("Y1").toString());
                int x2 = Integer.parseInt(object.get("X2").toString());
                int y2 = Integer.parseInt(object.get("Y2").toString());
                Nodo n1 = new Nodo(x1, y1);
                Nodo n2 = new Nodo(x2, y2);
                Edge<Nodo,String> e1;
                e1 = new Edge<>(n1,n2);
                archi.add(e1);
                if(!nodi.contains(n1)) {
                    nodi.add(n1);
                }
                if(!nodi.contains(n2)) {
                    nodi.add(n2);
                }

                if(zoomLayout != null && funzioniSelezionaNodo != null){
                    CustomViewMappa cv = new CustomViewMappa(context, 50, n1, funzioniSelezionaNodo, density);
                    CustomViewMappa cv1 = new CustomViewMappa(context, 50,n2, funzioniSelezionaNodo, density);
                    zoomLayout.addView(cv);
                    zoomLayout.addView(cv1);
                }
            }
            System.out.println("SONO NELL'ONPOSTEXECUTE: "+ nodi);
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

    public Graph getGrafo(){
        for(Nodo n1: nodi){
            grafo.addVertex(n1);
        }
        for(Edge<Nodo,String> e: archi){

            if(grafo.containsVertex(e.getIn()) && grafo.containsVertex(e.getOut())) {
                grafo.addEdge(e.getIn(), e.getOut());
                grafo.addEdge(e.getOut(), e.getIn());
            }
        }


        return grafo;
    }


    public ArrayList<Nodo> getAllNodes(){
        return nodi;
    }
}
