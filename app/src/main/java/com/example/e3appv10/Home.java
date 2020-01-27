package com.example.e3appv10;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e3appv10.giorgio.Helper.BeaconHelper;
import com.example.e3appv10.giorgio.Helper.MqttHelper;
import com.example.e3appv10.giorgio.Helper.Nodo;
import com.example.e3appv10.giorgio.customs.CustomView;
import com.example.e3appv10.giorgio.customs.CustomViewEdge;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private HashMap<String,Nodo> hashMap;
    private Nodo destinazione;
    private SharedPreferences sharedPref;
    private TextView nomeUtente;
    private TextView gruppoecabina;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private Graph<Nodo, DefaultEdge> grafo;

    MqttHelper mqttHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        grafo = null;
        destinazione = null;
        hashMap = null;
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        /*
        Pulsante che porta alla pagina di messaggistica
        */
        FloatingActionButton fab = findViewById(R.id.fab);


        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        //Seleziono il fragment che si dovrà selezionare all'avvio dell'activityy home
        FragmentMappa fragmentMappa = new FragmentMappa();
        fragmentTransaction.add(R.id.fragmentContainer, fragmentMappa);

        fragmentTransaction.commit();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentContainer);
                if(fragment!=null){
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.remove(fragment);
                    fragmentTransaction.commit();
                }
                FragmentMessaggistica fragmentMessaggistica  = new FragmentMessaggistica();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.fragmentContainer, fragmentMessaggistica);
                fragmentTransaction.commit();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String nome = sharedPref.getString("nome", "user");
        String cognome = sharedPref.getString("cognome", "user");
        String gruppo = sharedPref.getString("gruppo", "--");
        String cabina = sharedPref.getString("cabina", "--");

        View menuLaterale = navigationView.getHeaderView(0);
        nomeUtente = menuLaterale.findViewById(R.id.nomeUtente);
        gruppoecabina = menuLaterale.findViewById(R.id.gruppoecabina);

        nomeUtente.setText("" + nome.toUpperCase() + " " + cognome.toUpperCase());
        gruppoecabina.setText("Gruppo: " + gruppo +" Cabina: " +cabina);

        startMqtt();

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction;
        FragmentAnnunci fragmentAnnunci = new FragmentAnnunci();
        FragmentEventi fragmentEventi = new FragmentEventi();
        FragmentNavigazione fragmentNavigazione = new FragmentNavigazione();
        FragmentMappa fragmentMappa = new FragmentMappa();

        Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentContainer);
        if(fragment!=null){
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(fragment);
            fragmentTransaction.commit();
        }
        if (id == R.id.miaposizione) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragmentContainer, fragmentMappa);
            fragmentTransaction.commit();
        } else if (id == R.id.annunci) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragmentContainer, fragmentAnnunci);
            fragmentTransaction.commit();
        } else if (id == R.id.eventi) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragmentContainer, fragmentEventi);
            fragmentTransaction.commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void cambiaFragment(String nomeFragment) {

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction;
        FragmentAnnunci fragmentAnnunci = new FragmentAnnunci();
        FragmentEventi fragmentEventi = new FragmentEventi();
        FragmentNavigazione fragmentNavigazione = new FragmentNavigazione();
        FragmentMappa fragmentMappa = new FragmentMappa();

        Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentContainer);
        if(fragment!=null){
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(fragment);
            fragmentTransaction.commit();
        }
        if (nomeFragment.equals("navigazione")) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragmentContainer, fragmentNavigazione);
            fragmentTransaction.commit();
        } else if (nomeFragment.equals("annunci")) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragmentContainer, fragmentAnnunci);
            fragmentTransaction.commit();
        } else if (nomeFragment.equals("eventi")) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragmentContainer, fragmentEventi);
            fragmentTransaction.commit();
        }
        else if (nomeFragment.equals("mappa")) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragmentContainer, fragmentMappa);
            fragmentTransaction.commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    private void startMqtt() {
        mqttHelper = new MqttHelper(getApplicationContext());

        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                System.out.println("connessione completata");
            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {

                if(topic.equals("client/emergenza")){
                    Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentContainer);
                    FragmentEmergenza fragmentEmergenza = new FragmentEmergenza();
                    if(fragment!=null){
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.remove(fragment);
                        fragmentTransaction.commit();
                    }
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.add(R.id.fragmentContainer, fragmentEmergenza);
                    fragmentTransaction.commit();
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                System.out.println("ho inviato un messaggio");
            }
        });
    }

    public Graph getGrafo(){
        return grafo;
    }

    public void setGrafo(Graph g){
        grafo=g;
    }

    public Nodo getDestinazione(){
        return destinazione;
    }

    public void setDestinazione(Nodo d){
        destinazione = d;
    }

    public HashMap<String, Nodo> getHashMap() {
        return hashMap;
    }

    public void setHashMap(HashMap<String, Nodo> hashMap) {
        this.hashMap = hashMap;
    }
}
