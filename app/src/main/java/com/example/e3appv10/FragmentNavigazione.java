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
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class FragmentNavigazione extends Fragment implements FunzioniCambiaBeacon {

    private HashMap<String,Nodo> hashMap;
    private BeaconHelper beaconHelper;
    private static final String ip = "151.236.56.24";
    private Graph<Nodo, DefaultEdge> grafo;
    private Button cercaPercorsoButton;
    private Button stopReadingBeaconsButton;
    private Canvas canvas;
    private Bitmap bitmap;
    private Bitmap operations;
    private PhotoView imageView;
    private PhotoViewAttacher mAttacher;
    private BitmapDrawable ambp;
    private TextView hiddenTextView;
    private InvioDati invio;
    private Button[] bottoniPiano;
    private CaricaHashmapBeacon caricaHashmap;
    private EditText editX;
    private EditText editY;
    private DisplayMetrics metrics;
    private float density;
    private Button inserisciDestinazione;
    private SharedPreferences sharedPref;

    private String nome;
    private String cognome;
    private String gruppo;
    private String cabina;
    private int codice;

    private String topic;

    private Nodo destinazione;
    private HashMap<Integer,Graph> pianoGrafo;
    private HashMap<String,Nodo> beaconsAllNodes;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
   /* private static final int PIANO_F = R.drawable.pianoa;
    private static final int PIANO_COMANDO = R.drawable.pianob;*/
    private static final int PIANO_A = R.drawable.pianoa;
    private static final int PIANO_B = R.drawable.pianob;
    MqttHelper mqttHelper;

    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)  {

        view = inflater.inflate(R.layout.fragment_navigazione_layout, container, false);

        /*Roba Navigazione*/

        metrics = getResources().getDisplayMetrics();
        density = metrics.density;
        //editX = view.findViewById(R.id.inputX);
        //editY = view.findViewById(R.id.inputY);
        /*inserisciDestinazione = (Button) view.findViewById(R.id.inviodati);


        inserisciDestinazione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiaDestinazione(v);
            }
        });*/
        beaconHelper = new BeaconHelper(view.getContext(), this);

        /*cercaPercorsoButton = (Button) view.findViewById(R.id.cercaPercorsoButton);

        cercaPercorsoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                detectBeaconInMap(v);
            }
        });*/

        /*
        stopReadingBeaconsButton = (Button) view.findViewById(R.id.caricaIPercorsiButton);
        stopReadingBeaconsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                caricaGrafo(v);
            }
        });*/

        imageView =  (PhotoView) view.findViewById(R.id.mappa);

        // Per settare dinamicamente un immagine
        /*imageView.setImageResource(R.drawable.pianof);
        Drawable drawable = getResources().getDrawable(R.drawable.pianof);
        imageView.setImageDrawable(drawable);*/
        destinazione = ((Home) getActivity()).getDestinazione();
        if(destinazione.getPiano() == 2){
            cambiaPiano(PIANO_B);
        }else {
            cambiaPiano(PIANO_A);
        }

        pianoGrafo = ((Home) getActivity()).getPianoGrafo();
        beaconsAllNodes = ((Home) getActivity()).getBeaconsAllNodes();
        // Attach a PhotoViewAttacher, which takes care of all of the zooming functionality.
        //mAttacher = new PhotoViewAttacher(imageView);

        /*ambp = (BitmapDrawable) imageView.getDrawable();
        bitmap = Bitmap.createBitmap(1000,1000, Bitmap.Config.RGB_565);
        bitmap = ambp.getBitmap();
        */
        grafo = ((Home) getActivity()).getGrafo();

        hashMap = ((Home) getActivity()).getHashMap();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            askForLocationPermissions();
            askForBluetooth();
        }
        mqttHelper = new MqttHelper(view.getContext().getApplicationContext(),topic);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(view.getContext());
        codice = sharedPref.getInt("codice", 0);
        nome = sharedPref.getString("nome", "user");
        cognome = sharedPref.getString("cognome", "user");
        gruppo = sharedPref.getString("gruppo", "--");
        cabina = sharedPref.getString("cabina", "--");
        detectBeaconInMap();

        return view;
    }

    public void cambiaPiano(int piano){
        imageView.setImageResource(piano);
        Drawable drawable = getResources().getDrawable(piano);
        imageView.setImageDrawable(drawable);
        mAttacher = new PhotoViewAttacher(imageView);
        ambp = (BitmapDrawable) imageView.getDrawable();
        bitmap = Bitmap.createBitmap(1000,1000, Bitmap.Config.RGB_565);
        bitmap = ambp.getBitmap();
        System.out.println("CAMBIA PIANO: "+piano);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {


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

    @Override
    public void onDestroy() {
        super.onDestroy();
        beaconHelper.clear();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void drawGraph(Graph<Nodo,DefaultEdge> graph){


        operations = Bitmap.createBitmap( (int)(bitmap.getWidth() * density), (int)(bitmap.getHeight() *density), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(operations);
        canvas.drawBitmap(bitmap,0,0,null);


        for (Nodo nod: graph.vertexSet()) {
            CustomView cv = new CustomView(getView().getContext(), nod, density);
            cv.draw(canvas);
            graph.edgesOf(nod);
            for (DefaultEdge e : graph.outgoingEdgesOf(nod)){
                Nodo n2 = graph.getEdgeTarget(e);
                CustomViewEdge cve = new CustomViewEdge(getView().getContext(), nod , n2, density);
                cve.draw(canvas);
            }
        }
        imageView.setImageBitmap(operations);

    }

    public void drawMinPath(ArrayList<Nodo> toDraw){

        operations = Bitmap.createBitmap( (int)(bitmap.getWidth() * density), (int)(bitmap.getHeight() *density), Bitmap.Config.ARGB_8888);

        canvas = new Canvas(operations);
        canvas.drawBitmap(bitmap,0,0,null);

        Nodo start = toDraw.get(0);
        inviaMessaggio("pos/"+codice, start.getX()+ " "+ start.getY());
        for(Nodo nod: toDraw) {
            CustomView cv = new CustomView(getView().getContext(), nod, density);
            cv.changeColor();
            cv.draw(canvas);
            CustomViewEdge cve = new CustomViewEdge(getView().getContext(), start, nod, density );
            cve.draw(canvas);
            start = nod;
        }

        imageView.setImageDrawable( new BitmapDrawable(getResources(),operations));
    }

    public void newPath(String s) {
            s = s.substring(s.length()-5);
            if (((Home) getActivity()) == null) {
                return;
            }
            beaconsAllNodes = ((Home) getActivity()).getBeaconsAllNodes();
            if(beaconsAllNodes == null){
                return;
            }
            Nodo sorgente = beaconsAllNodes.get(s);
            if(sorgente == null){
                return;
            }
            Nodo uscitaPiano = null;
            if(sorgente.getPiano() == 2){
                cambiaPiano(PIANO_B);
                System.out.println("COMANDO");
            }else {
                cambiaPiano(PIANO_A);
            }
            pianoGrafo = ((Home) getActivity()).getPianoGrafo();
            if(pianoGrafo == null){
                return;
            }
            grafo = pianoGrafo.get(sorgente.getPiano());
            if(grafo != null){
                Iterator it = beaconsAllNodes.entrySet().iterator();
                int direzione;
                if(sorgente.getPiano() == destinazione.getPiano() ) {
                    DijkstraShortestPath<Nodo, DefaultEdge> dijkstraAlg = new DijkstraShortestPath<>(grafo);

                    ShortestPathAlgorithm.SingleSourcePaths<Nodo, DefaultEdge> iPaths = dijkstraAlg.getPaths(sorgente);
                    List<Nodo> path = iPaths.getPath(destinazione).getVertexList();
                    ArrayList<Nodo> result = new ArrayList<>(path);

                    if (result != null) {
                        drawMinPath(result);

                    } else {
                        Toast.makeText(getView().getContext(), "Cammino non trovato!!", Toast.LENGTH_SHORT).show();
                    }
                }else{

                    //in caso si deve salire e dunque il piano sorgente è minore di quello di destinazione cerco le scale che salgono
                    // altrimrnti scengo quelle che scendono
                    if(sorgente.getPiano() < destinazione.getPiano()){
                        direzione = 1;
                    }
                    else{
                        direzione = 2;
                    }
                    while (it.hasNext()) {
                        // Utilizza il nuovo elemento (coppia chiave-valore)
                        // dell'hashmap

                        Map.Entry entry = (Map.Entry)it.next();
                        Nodo n = (Nodo) entry.getValue();
                        if((n.getPiano() == sorgente.getPiano()) && ((n.getScala() == 3) || (n.getScala() == direzione))){
                            uscitaPiano = n;
                            break;
                        }
                    }
                    DijkstraShortestPath<Nodo, DefaultEdge> dijkstraAlg = new DijkstraShortestPath<>(grafo);
                    ShortestPathAlgorithm.SingleSourcePaths<Nodo, DefaultEdge> iPaths = dijkstraAlg.getPaths(sorgente);
                    List<Nodo> path = iPaths.getPath(uscitaPiano).getVertexList();
                    ArrayList<Nodo> result = new ArrayList<>(path);

                    if (result != null) {
                        drawMinPath(result);

                    } else {
                        Toast.makeText(getView().getContext(), "Cammino non trovato!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void caricaGrafo(View v){
        beaconHelper.stopDetectingBeacons();

        if(!grafo.vertexSet().isEmpty()) {
            drawGraph(grafo);
        }
        else{
            Toast.makeText(getView().getContext(), "Nessun percorso è stato caricato", Toast.LENGTH_SHORT).show();
        }
    }

    //funzione associata al bottune
   /* public void detectBeaconInMap(View v){
        System.out.println("Ci arrivo");
        askForBluetooth();
        beaconHelper.startDetectingBeacons();
        cercaPercorsoButton.setEnabled(true);
        cercaPercorsoButton.setAlpha(1);

    }*/

    public void detectBeaconInMap(){
        System.out.println("Ci arrivo");
        askForBluetooth();
        System.out.println("Nodo destinazione: "+destinazione);
        beaconHelper.startDetectingBeacons();
        //cercaPercorsoButton.setEnabled(true);
        //cercaPercorsoButton.setAlpha(1);

    }
    private void inviaMessaggio(String topic, String messaggio){
        MqttMessage message = new MqttMessage(messaggio.getBytes());
        try {
            if(mqttHelper == null){
                mqttHelper = new MqttHelper(view.getContext().getApplicationContext(),this.topic);
            }
            mqttHelper.publica(topic, message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onChangeSource(String idBeacon) {

        long tempo = Instant.now().getEpochSecond();
        inviaMessaggio("pos",nome.toUpperCase()+" "+ cognome.toUpperCase()+" "+ idBeacon+" "+ tempo );
        newPath(idBeacon);
    }

    @Override
    public void infoBeaconsResived(String infoBeacon) {
        System.out.println(infoBeacon);
    }
}
