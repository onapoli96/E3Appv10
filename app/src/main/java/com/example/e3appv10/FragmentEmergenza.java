package com.example.e3appv10;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.e3appv10.giorgio.Helper.BeaconHelper;
import com.example.e3appv10.giorgio.Helper.MqttHelper;
import com.example.e3appv10.giorgio.Helper.Nodo;
import com.example.e3appv10.giorgio.customs.CustomViewEdge;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;

import java.time.Instant;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class FragmentEmergenza extends Fragment  implements FunzioniCambiaBeacon{

    private BeaconHelper beaconHelper;
    private View view;
    private SensorManager sensorManager;
    private Sensor giroscopio;
    private Matrix matrix;
    private SensorEventListener eventiGiroscopioListener;
    private ImageView freccia;
    private SharedPreferences mPrefs;
    private Bitmap bitmap;
    private Bitmap operations;
    private BitmapDrawable ambp;
    private Canvas canvas;
    private ArrayList<CustomViewEdge> archi;
    private ArrayList<Float> gradi;
    private int contatore;
    private RelativeLayout layout;
    private float rotazioneAttuale;
    private float rangeGreen;
    private float rangeYellow;
    private float pivotX;
    private float pivotY;
    private int lastX, lastY;

    private MqttHelper mqttHelper;
    private Nodo destinazione;
    private HashMap<Integer, Graph> pianoGrafo;
    private HashMap<String,Nodo> beaconsAllNodes;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private SharedPreferences sharedPref;
    private Graph<Nodo, DefaultEdge> grafo;
    private float lastAngolo = 0;

    private String nome;
    private String cognome;
    private String gruppo;
    private String cabina;
    private int codice;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //beaconHelper = new BeaconHelper(view.getContext(), this);
        //beaconHelper.startDetectingBeacons();
        view = inflater.inflate(R.layout.fragment_emergenza_layout, container, false);
        beaconHelper = new BeaconHelper(view.getContext(), this);
        //mqttHelper = new MqttHelper(view.getContext().getApplicationContext(),topic);
        pianoGrafo = ((Home) getActivity()).getPianoGrafo();
        beaconsAllNodes = ((Home) getActivity()).getBeaconsAllNodes();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(view.getContext());
        codice = sharedPref.getInt("codice", 0);
        nome = sharedPref.getString("nome", "user");
        cognome = sharedPref.getString("cognome", "user");
        gruppo = sharedPref.getString("gruppo", "--");
        cabina = sharedPref.getString("cabina", "--");


        layout = view.findViewById(R.id.contenitore);

        rotazioneAttuale = 0;

        freccia = view.findViewById(R.id.freccia);

        freccia.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                freccia.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                pivotX =  freccia.getWidth()/2;
                pivotY =  freccia.getHeight()/2;
            }
        });

        //freccia.setImageResource(R.drawable.freccia);
        matrix = new Matrix();
        archi = new ArrayList<>();
        sensorManager = (SensorManager) view.getContext().getSystemService(Context.SENSOR_SERVICE);
        giroscopio = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED);

        freccia.setScaleType(ImageView.ScaleType.MATRIX);   //required

        ambp = (BitmapDrawable) freccia.getDrawable();
        bitmap = Bitmap.createBitmap(1000,1000, Bitmap.Config.RGB_565);
        bitmap = ambp.getBitmap();
        operations = Bitmap.createBitmap( (int)(bitmap.getWidth() * 2.6), (int)(bitmap.getHeight() *2.8), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(operations);
        canvas.drawBitmap(bitmap,0,0,null);

        lastX = 0;
        lastY = 0;

        contatore = 0 ;
        rangeGreen = 5;
        rangeYellow = 30;


        freccia.setImageBitmap(operations);

        freccia.setImageMatrix(matrix);
        if(giroscopio == null){
            Toast.makeText(view.getContext(),"Il cellulare non supporta il giroscopio",Toast.LENGTH_SHORT);
        }
        eventiGiroscopioListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {

                //Si basa sul sistema della BUSSOLA NON DEL GRAFO CARTESIANO
                float range = Math.abs(rotazioneAttuale)%360;
                //è molto vicino
                if(range > (360-rangeGreen) || range < rangeGreen ){
                    layout.setBackgroundColor(Color.GREEN);
                }
                //sta abbastanza vicino
                else if(range > (360-rangeYellow) || range < rangeYellow) {
                    layout.setBackgroundColor(Color.YELLOW);
                }
                //sta lontano
                else{
                    layout.setBackgroundColor(Color.RED);
                }

                matrix.postRotate(event.values[2]/7, pivotX,pivotY);
                //teniamo traccia della rotazione attuale solo per cambiare il colore, dato che questa non si può ricavare da matrix.
                rotazioneAttuale += event.values[2]/7;
                freccia.setImageMatrix(matrix);

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        detectBeaconInMap();
    return view;
    }

    public void newPath(String s) {
        s = s.substring(s.length()-5);
        Nodo sorgente = beaconsAllNodes.get(s);
        if(sorgente == null){
            return;
        }
        //Nodo sorgente = new Nodo(beaconsAllNodes.get(s).getX(), beaconsAllNodes.get(s).getY(), beaconsAllNodes.get(s).getPiano());

        Nodo uscitaPiano = null;

        grafo = pianoGrafo.get(sorgente.getPiano());
        Iterator it = beaconsAllNodes.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            Nodo n = (Nodo) entry.getValue();
            if((n.getPiano() == sorgente.getPiano()) && ((n.getScala() == 3) || (n.getScala() == 1) || (n.getScala() == 2))){
                //uscitaPiano = new Nodo(n.getX(), n.getY(), n.getPiano());
                uscitaPiano = n;
                break;
            }
        }
        DijkstraShortestPath<Nodo, DefaultEdge> dijkstraAlg = new DijkstraShortestPath<>(grafo);

        ShortestPathAlgorithm.SingleSourcePaths<Nodo, DefaultEdge> iPaths = dijkstraAlg.getPaths(sorgente);
        List<Nodo> path = iPaths.getPath(uscitaPiano).getVertexList();
        ArrayList<Nodo> result = new ArrayList<>(path);
        if (result != null) {
            if(result.size() >= 2){
                Nodo n1 = new Nodo( result.get(0).getX(), result.get(0).getY());
                Nodo n2 = new Nodo( result.get(1).getX(), result.get(1).getY());
                CustomViewEdge cve = new CustomViewEdge(view.getContext(), n1, n2);
                archi.add(cve);
                invertiCoordinate();
                cambiaArco();
            }else {
                Toast.makeText(getView().getContext(), "Sei arrivato !!!", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(getView().getContext(), "Cammino non trovato!!", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }


    public void cambiaArco(){


        //Sistema bussola
        float angoloDiRotazione = getRotation(archi.get(0));
        angoloDiRotazione -= lastAngolo;

        //Rotazione adattata al sistema della bussola non a quello CARTESIANO
        //matrix.setRotate(gradi.get(contatore) ,pivotX,pivotY);

        matrix.setRotate(angoloDiRotazione ,pivotX,pivotY);

        System.out.println(angoloDiRotazione);
        rotazioneAttuale = angoloDiRotazione;

        contatore++;
        contatore = contatore % archi.size();
        lastAngolo =  getRotation(archi.get(0));

    }



        @Override
        public void onResume() {
            super.onResume();
            sensorManager.registerListener(eventiGiroscopioListener,giroscopio,SensorManager.SENSOR_DELAY_FASTEST);
        }

        @Override
        public void onPause() {
            super.onPause();
            sensorManager.unregisterListener(eventiGiroscopioListener);
        }

        /****************
         * Questa funzione simula la lettura degli archi dal database
         * prende in input un array di archi per il momento e calcola il minimo Y e il massimo Y
         * partendo da questi due numeri calcola i nuovi valori di tutti i putni per adattare il sistema al sistema cartesiano
         * Questo perchè ci è utile per il calcolo del coefficiente angolare
         *****************/
        private void invertiCoordinate(){

            int minY = archi.get(0).getY1();
            int maxY = archi.get(0).getY1();

            for(int i = 0 ; i < archi.size(); i++){
                if(archi.get(i).getY2() < minY){
                    minY = archi.get(i).getY2();
                }
                if(archi.get(i).getY2() > maxY){
                    maxY = archi.get(i).getY2();
                }
            }

            for(int i = 0 ; i < archi.size(); i++){
                archi.get(i).setY1(maxY - archi.get(i).getY1() + minY );
                archi.get(i).setY2(maxY - archi.get(i).getY2() + minY );
            }

        }

        private float getRotation(CustomViewEdge arco){

            int x2 = arco.getX2() - arco.getX1();

            int y2 = arco.getY2() - arco.getY1();

            //Dato che adattiamo il piano cartesiano nel punto x1,y1 ponendo esso = a 0,0
            if(x2==0){
                if(y2>0){
                    return 0;
                }
                else{
                    return 180;
                }
            }

            double m = ((double) y2)/ ((double) x2);
            m = Math.abs(m);
            //Qui calcolo l'arcotangente di m per avere i gradi
            double angolo = Math.atan(m);
            angolo = (angolo / Math.PI)* 180;

            //Salgo a destra
            if(x2 > 0 && y2 > 0){
                angolo = 90 - angolo;
            }
            //Salgo a sinistra
            else if(x2 < 0 && y2 > 0){
                angolo = 270 + angolo;
            }
            //Scendo a sinistra
            else if(x2 < 0 && y2 < 0){
                angolo =  270 -  angolo;
            }
            //Scendo a destra
            else{
                angolo = 90 + angolo;
            }
            return (float) angolo;
        }


    @Override
    public void infoBeaconsResived(String infoBeacon) {
        System.out.println(infoBeacon);
    }

    private boolean askForBluetooth(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            showToastMessage(getString(R.string.not_support_bluetooth_msg));
            return false;
        }
        else if (!mBluetoothAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
        }
        if(mBluetoothAdapter.isEnabled()){
            return true;
        }
        return false;
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    private void askForLocationPermissions() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                PERMISSION_REQUEST_COARSE_LOCATION);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(getView().getContext());
                    builder.setTitle(R.string.funcionality_limited);
                    builder.setMessage(getString(R.string.location_not_granted));
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {

                        }
                    });
                    builder.show();
                    askForLocationPermissions();
                }
                return;
            }
        }
    }


    private void showToastMessage (String message) {
        Toast toast = Toast.makeText(getView().getContext(), message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
    public void detectBeaconInMap(){
        askForBluetooth();
        beaconHelper.startDetectingBeacons();

    }
    private void inviaMessaggio(String topic, String messaggio){
        MqttMessage message = new MqttMessage(messaggio.getBytes());
        try {
            mqttHelper.publica(topic, message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onChangeSource(String idBeacon) {

        long tempo = Instant.now().getEpochSecond();
        //inviaMessaggio("pos",nome.toUpperCase()+" "+ cognome.toUpperCase()+" "+ idBeacon+" "+ tempo );
        newPath(idBeacon);
    }
}



