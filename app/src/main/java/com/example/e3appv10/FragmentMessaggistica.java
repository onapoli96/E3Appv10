package com.example.e3appv10;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.e3appv10.R;
import com.example.e3appv10.giorgio.Helper.MqttHelper;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.Inflater;

import JavaClasses.Messaggio;

public class FragmentMessaggistica extends Fragment {

    ListView listaMessaggi;
    ArrayList<Messaggio> messaggi = new ArrayList<>();
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;


    MqttHelper mqttHelper;
    private ListaMessaggiAdapter listaMessaggiAdapter;
    private View view;

    private SharedPreferences sharedPref;
    private int codice;
    private String gruppo;


    private String savedtopic;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_messaggistica_layout, container, false);
        listaMessaggi = (ListView) view.findViewById(R.id.listamessaggi);
        listaMessaggiAdapter = new ListaMessaggiAdapter(view.getContext().getApplicationContext(), messaggi);
        listaMessaggi.setAdapter(listaMessaggiAdapter);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(view.getContext());
        savedtopic = sharedPref.getString("topic", "msg/+");
        gruppo = sharedPref.getString("gruppo", "--");
        codice = sharedPref.getInt("codice", 0);

        /*  *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *  *   *   *   *   *   *   *
        * Questa funzione serve per richiedere soccorso cliccando sul pulsante in alto
        * prende il riferimento al pulsante
        * crea una un messaggio includendo se possibile le informazioni sull'ultima posizione dell'utente
        * l'ultima posizione è memorizzata in base all'ultima volta in cui l'utente ha caricato un percorso nella schermata di navigazione
        * I due parametri lastPositionX e lastPositionY sono presi nella funzione newPath del file FragmentNavigazione.java
        *   *   *   *   *   *   *  *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *    */
        Button richiediAssistenza = view.findViewById(R.id.richiedi_assistenza);
        richiediAssistenza.setOnClickListener(v -> {

            String richiesta;
            richiesta = ""+ codice;
            int lastPositionX = sharedPref.getInt("lastPositionX",0);
            int lastPositionY = sharedPref.getInt("lastPositionY",0);

            if(lastPositionX != 0 && lastPositionY != 0){
                richiesta+="X:"+lastPositionX+" Y:"+lastPositionY;
            }
            MqttMessage message = new MqttMessage(richiesta.getBytes());
            try {
                mqttHelper.publica("msg/richiestaaiuto",message);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        });


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        startMqtt();
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        System.out.println("distruggo la view");
        try {
            mqttHelper.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    /*
    * Questa funzione permette di inserire un messaggio nella lista ogni volta che il capitano ne invia uno
    *
    *
    *
    * */
    private void startMqtt() {
        mqttHelper = new MqttHelper(view.getContext().getApplicationContext(),savedtopic);
        System.out.println("il mio topic quando starto è "+ savedtopic);

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
                System.out.println("quando arriva il messaggio il topic è "+ topic);
                System.out.println("msg/"+gruppo);
                if(topic.equals("msg/*") || topic.equals("msg/"+gruppo) || topic.equals("msg/"+gruppo+"/"+codice)) {
                    Date date = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                    Messaggio messaggio = new Messaggio(mqttMessage.toString(), formatter.format(date));
                    messaggi.add(messaggio);
                    listaMessaggi = (ListView) view.findViewById(R.id.listamessaggi);
                    listaMessaggiAdapter = new ListaMessaggiAdapter(view.getContext().getApplicationContext(), messaggi);
                    listaMessaggi.setAdapter(listaMessaggiAdapter);
                    for (Messaggio m : messaggi) {
                        System.out.println(m);
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                LayoutInflater li = getLayoutInflater();
                //Getting the View object as defined in the customtoast.xml file
                View layout = li.inflate(R.layout.customtoast,(ViewGroup) view.findViewById(R.id.custom_toast_layout));

                //Creating the Toast object
                Toast toast = new Toast(view.getContext().getApplicationContext());
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.setView(layout);//setting the view of custom toast layout
                toast.show();
                System.out.println("ho inviato un messaggio");
            }
        });
    }


}
