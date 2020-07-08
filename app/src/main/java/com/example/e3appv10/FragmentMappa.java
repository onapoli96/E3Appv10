package com.example.e3appv10;

import android.support.v4.app.Fragment;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.e3appv10.giorgio.Helper.Nodo;
import com.example.e3appv10.giorgio.customs.FunzioniSelezionaNodo;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;




public class FragmentMappa extends Fragment implements FunzioniSelezionaNodo {
    private ZoomLayout zoom;
    private RelativeLayout container;
    private CaricaHashmapBeacon caricaHashmap;
    private InvioDati invio;
    private static final String ip = "151.236.56.24";
    private Graph<Nodo, DefaultEdge> grafo;
    private DisplayMetrics metrics;
    private float density;
    private int piano;

    private View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup viewGroup, @Nullable Bundle savedInstanceState) {

        metrics = getResources().getDisplayMetrics();
        density = metrics.density;
        view = inflater.inflate(R.layout.fragment_mappa, viewGroup, false);
        container = (RelativeLayout) view.findViewById(R.id.container);
        zoom = new ZoomLayout(getContext());
        zoom.setMinimumWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        zoom.setMinimumHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        //zoom.setBackgroundResource(R.drawable.pianof);
        zoom.setBackgroundResource(R.drawable.pianoa);
        piano = 1;

        caricaHashmap = (CaricaHashmapBeacon) new CaricaHashmapBeacon(view.getContext(), density).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"http://"+ip+"/interfaccia_capitano/php/caricaHashmap.php?piano=1");
        invio = (InvioDati) new InvioDati(view.getContext(), density, zoom, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"http://"+ip+"/interfaccia_capitano/php/caricaGrafo.php?piano=1");

        container.addView(zoom);

        view.findViewById(R.id.bottonePianoComando).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiaPianoComando(v);
            }
        });

        view.findViewById(R.id.bottonePianoF).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("SONO NEL ONCLICK");
                cambiaPianoF(v);
            }
        });

        return view;
    }

    @Override
    public void onClickNodo(Nodo n) {
        n.setPiano(piano);
        grafo = invio.getGrafo();
        ((Home) getActivity()).setDestinazione(n);
        ((Home) getActivity()).setHashMap(caricaHashmap.getHashMap());
        ((Home) getActivity()).setGrafo(grafo);
        ((Home) getActivity()).cambiaFragment("navigazione");
    }

    public void cambiaPianoF(View v){
        piano = 1;
        System.out.println("SONO NEL PIANO A");
        zoom.setBackgroundResource(R.drawable.pianoa);

        caricaHashmap = (CaricaHashmapBeacon) new CaricaHashmapBeacon(view.getContext(), density).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"http://"+ip+"/interfaccia_capitano/php/caricaHashmap.php?piano=1");
        invio = (InvioDati) new InvioDati(view.getContext(), density, zoom, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"http://"+ip+"/interfaccia_capitano/php/caricaGrafo.php?piano=1");
        container.removeAllViews();
        zoom.removeAllViews();
        container.addView(zoom);
    }

    public void cambiaPianoComando(View v){
        piano = 2;
        System.out.println("SONO NEL PIANO B");
        zoom.setBackgroundResource(R.drawable.pianob);

        caricaHashmap = (CaricaHashmapBeacon) new CaricaHashmapBeacon(view.getContext(), density).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"http://"+ip+"/interfaccia_capitano/php/caricaHashmap.php?piano=2");
        invio = (InvioDati) new InvioDati(view.getContext(), density, zoom, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"http://"+ip+"/interfaccia_capitano/php/caricaGrafo.php?piano=2");
        container.removeAllViews();
        zoom.removeAllViews();
        container.addView(zoom);
    }
}

