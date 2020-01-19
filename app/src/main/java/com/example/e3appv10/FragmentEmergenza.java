package com.example.e3appv10;

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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
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

import com.example.e3appv10.giorgio.Helper.Nodo;
import com.example.e3appv10.giorgio.customs.CustomViewEdge;

import java.util.ArrayList;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class FragmentEmergenza extends Fragment  implements View.OnClickListener {
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_emergenza_layout, container, false);
        Button b = (Button) view.findViewById(R.id.cambiaArco);
        b.setOnClickListener(this);
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
        gradi = new ArrayList<>();
        sensorManager = (SensorManager) view.getContext().getSystemService(Context.SENSOR_SERVICE);
        giroscopio = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED);

        freccia.setScaleType(ImageView.ScaleType.MATRIX);   //required

        ambp = (BitmapDrawable) freccia.getDrawable();
        bitmap = Bitmap.createBitmap(1000,1000, Bitmap.Config.RGB_565);
        bitmap = ambp.getBitmap();
        operations = Bitmap.createBitmap( (int)(bitmap.getWidth() * 2.6), (int)(bitmap.getHeight() *2.8), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(operations);
        canvas.drawBitmap(bitmap,0,0,null);

        CustomViewEdge cve = new CustomViewEdge(view.getContext(), new Nodo(20,50), new Nodo(20,20));
        CustomViewEdge cve1 = new CustomViewEdge(view.getContext(), new Nodo(20,20), new Nodo(40,10));
        CustomViewEdge cve2 = new CustomViewEdge(view.getContext(), new Nodo(40,10), new Nodo(60,10));
        CustomViewEdge cve3 = new CustomViewEdge(view.getContext(), new Nodo(60,10), new Nodo(50,60));
        CustomViewEdge cve4 = new CustomViewEdge(view.getContext(), new Nodo(50,60), new Nodo(35,40));
        CustomViewEdge cve5 = new CustomViewEdge(view.getContext(), new Nodo(35,40), new Nodo(30,45));
        CustomViewEdge cve6 = new CustomViewEdge(view.getContext(), new Nodo(30,45), new Nodo(40,60));
        CustomViewEdge cve7 = new CustomViewEdge(view.getContext(), new Nodo( 40,60), new Nodo(20,50));

        archi.add(cve);
        archi.add(cve1);
        archi.add(cve2);
        archi.add(cve3);
        archi.add(cve4);
        archi.add(cve5);
        archi.add(cve6);
        archi.add(cve7);

        invertiCoordinate();
        System.out.println(archi); //Qui controlliamo che siano invertiti bene e lo sono
        lastX = 0;
        lastY = 0;

        gradi.add((float) 27.82034063065508);
        gradi.add((float)63.43494882292202);
        gradi.add((float)348.6900675259798);
        gradi.add((float)309.559667968994496);
        gradi.add((float)225.0);
        gradi.add((float)135.0);
        gradi.add((float)348.96375653207353);

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
    return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onClick(View v) {
        cambiaArco(v);
    }

    public void cambiaArco(View v){


            //Sistema bussola
            float angoloDiRotazione = getRotation(archi.get(contatore));
            if (contatore>1){
                angoloDiRotazione -=  getRotation(archi.get(contatore-1));
            }
            //Rotazione adattata al sistema della bussola non a quello CARTESIANO
            //matrix.setRotate(gradi.get(contatore) ,pivotX,pivotY);

            matrix.setRotate(angoloDiRotazione ,pivotX,pivotY);

            System.out.println(angoloDiRotazione);
            rotazioneAttuale = angoloDiRotazione;

            contatore++;
            contatore = contatore % archi.size();
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

            System.out.println("Il minimo è "+ minY +" Il massimo è " +maxY);

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
    }



