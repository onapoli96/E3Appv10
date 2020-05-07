package com.example.e3appv10;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import JavaClasses.Messaggio;


class ListaMessaggiAdapter extends BaseAdapter {

    private ArrayList<Messaggio> messaggi = new ArrayList<Messaggio>();
    private ArrayList<String> orari = new ArrayList<String>();
    private Context context;
    private LayoutInflater inflter;

    public ListaMessaggiAdapter(Context applicationContext,  ArrayList<Messaggio> messaggi) {
        this.context = context;
        this.messaggi = messaggi;

        inflter = (LayoutInflater.from(applicationContext));
    }


    @Override
    public int getCount() {
        return messaggi.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.messaggi_item_list, null);

        TextView orariomessaggio = (TextView)   view.findViewById(R.id.orariomessaggio);
        TextView messaggio = (TextView)   view.findViewById(R.id.messaggio);
        messaggio.setText(messaggi.get(i).getMessaggio());
        orariomessaggio.setText(messaggi.get(i).getOrario());
        System.out.println("sono nel metodo getView");
        return view;
    }

    public void add(Messaggio messaggio){

    }
}
