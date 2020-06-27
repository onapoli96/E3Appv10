package com.example.e3appv10;

import com.example.e3appv10.giorgio.Helper.Nodo;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.HashMap;

public interface AddGrafo {
    public void addGrafo(int piano, Graph<Nodo, DefaultEdge> grafo);
    public void setAllBeaconsNodes(HashMap<String,Nodo> beaconsAllNodes);
}
