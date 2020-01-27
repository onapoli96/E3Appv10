package com.example.e3appv10.giorgio.customs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import com.example.e3appv10.giorgio.Helper.Nodo;

public class CustomView extends View {

    private Paint interno;
    private Paint esterno;
    private Nodo nodo;
    private float density;

    public CustomView(Context context, Nodo nodo, float density) {
        super(context);
        this.nodo = nodo;
        this.interno = new Paint();
        this.esterno = new Paint();
        interno.setColor(Color.RED);
        esterno.setColor(Color.BLACK);
        this.density = density;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int x = (int)((nodo.getX() - 8 )* density);
        int y = (int)((nodo.getY() - 64)* density);
        canvas.drawCircle(x, y, 20, esterno);
        canvas.drawCircle(x, y, 15, interno);
        Paint paint = new Paint();
        paint.setTextSize(30);
        canvas.drawText("X: "+ nodo.getX() +"Y: "+ nodo.getY(), x,y-30, paint);
    }
    public void changeColor(){
        interno.setColor(Color.GREEN);
    }
}