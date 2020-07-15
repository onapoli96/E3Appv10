package com.example.e3appv10.giorgio.customs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import com.example.e3appv10.giorgio.Helper.Nodo;

public class CustomViewEdge extends View {

    private Paint paint;
    private Paint corner;
    private Nodo n1;
    private Nodo n2;
    private Context context;
    private float density;
    public CustomViewEdge(Context context, Nodo n1, Nodo n2) {
        super(context);
        this.context = context;
        this.n1 = n1;
        this.n2 = n2;
        paint = new Paint();
        corner = new Paint();
        corner.setColor(Color.BLACK);
        corner.setStrokeWidth(15);
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(15);
        this.density = -1;
    }

    public CustomViewEdge(Context context, Nodo n1, Nodo n2, float density) {
        super(context);
        this.context = context;
        this.n1 = n1;
        this.n2 = n2;
        paint = new Paint();
        corner = new Paint();
        corner.setColor(Color.BLACK);
        corner.setStrokeWidth(15);
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(15);
        this.density = density;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        /*int x1 = (int)((n1.getX() - 8 )* density);
        int y1 = (int)((n1.getY()-150)* density/1.5);
        //int y1 = (int)((n1.getY() - 64)* density);
        int x2 = (int)((n2.getX() - 8)* density);
        //int y2 = (int)((n2.getY() - 64)* density);
        int y2 = (int)((n2.getY()-150)* density/1.5);*/
        int x1 = (int)((n1.getX() )* density);
        int y1 = (int)((n1.getY() )* density);
        int x2 = (int)((n2.getX() )* density);
        int y2 = (int)((n2.getY() )* density);
        canvas.drawLine(x1, y1, x2, y2,paint);


    }
    public int getX1() {
        return n1.getX();
    }

    public int getX2() {
        return n2.getX();
    }

    public int getY1() {
        return n1.getY();
    }

    public int getY2() {
        return n2.getY();
    }

    public void setY1(int y1) {
        n1.setY(y1);
    }

    public void setY2(int y2) {
        n2.setY(y2);
    }

    public void setX1(int x1) {
        n1.setY(x1);
    }

    public void setX2(int x2) {
        n2.setX(x2);
    }
}
