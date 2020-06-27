package com.example.e3appv10.giorgio.Helper;


import java.util.Objects;

public class Nodo {
    private int x;
    private int y;
    private int piano;
    private int scala;

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public Nodo(int x, int y){
        this.x = x;
        this.y = y;
    }

    public Nodo(int x, int y, int piano, int scala){
        this.scala = scala;
        this.x = x;
        this.y = y;
        this.piano = piano;
    }

    public Nodo(int x, int y, int piano){
        this.x = x;
        this.y = y;
        this.piano = piano;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        super.equals(obj);
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Nodo other = (Nodo) obj;
        if(this.x== other.getX() && this.y == other.getY()){
            return true;
        }
        else
        {
            return false;
        }
    }

    public int getScala() {
        return scala;
    }

    public void setScala(int scala) {
        this.scala = scala;
    }

    public int getPiano() {
        return piano;
    }

    public void setPiano(int piano) {
        this.piano = piano;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    @Override
    public String toString() {
        return "x = " + x + "; y = " + y;
    }
}
