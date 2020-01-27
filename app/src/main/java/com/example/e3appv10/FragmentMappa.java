package com.example.e3appv10;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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
        zoom.setBackgroundResource(R.drawable.pianof);

        caricaHashmap = (CaricaHashmapBeacon) new CaricaHashmapBeacon(view.getContext(), density).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"http://"+ip+"/interfaccia_capitano/php/caricaHashmap.php?piano=4");
        invio = (InvioDati) new InvioDati(view.getContext(), density, zoom, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"http://"+ip+"/interfaccia_capitano/php/caricaGrafo.php?piano=4");

        container.addView(zoom);

        return view;
    }

    @Override
    public void onClickNodo(Nodo n) {
        grafo = invio.getGrafo();
        ((Home) getActivity()).setDestinazione(n);
        ((Home) getActivity()).setHashMap(caricaHashmap.getHashMap());
        ((Home) getActivity()).setGrafo(grafo);
        ((Home) getActivity()).cambiaFragment("navigazione");
    }
}

