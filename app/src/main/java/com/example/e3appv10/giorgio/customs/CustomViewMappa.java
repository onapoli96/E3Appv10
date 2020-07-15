package com.example.e3appv10.giorgio.customs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.e3appv10.giorgio.Helper.Nodo;

public class CustomViewMappa extends View implements View.OnClickListener {
    public int dimensioneView;
    public Nodo nodo;
    public FunzioniSelezionaNodo funzioniSelezionaNodo;
    private Paint interno;
    private Paint esterno;
    private Context context;
    private float density;


    public CustomViewMappa(Context c, int dimensioneView, Nodo nodo , FunzioniSelezionaNodo funzioniSelezionaNodo,float density) {
        super(c);
        context = c;
        this.dimensioneView = dimensioneView;
        Log.d("DEBUG","creating CustomWidgetView w="+dimensioneView+"   h="+dimensioneView);
        this.context = context;
        this.nodo = nodo;
        this.interno = new Paint();
        this.esterno = new Paint();
        this.density = density;
        interno.setColor(Color.RED);
        esterno.setColor(Color.BLACK);
        int x = (int)((nodo.getX() - 8 )* density/1.5);
        int y = (int)((nodo.getY()-68)* density);
        setX((float)x);
        setY((float)y);
        setMinimumWidth(dimensioneView);
        setMinimumHeight(dimensioneView);
        setOnClickListener(this);

        this.funzioniSelezionaNodo = funzioniSelezionaNodo;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        Log.d("DEBUG","onMeasure w="+width+"  h="+height);

        if (widthMode == MeasureSpec.EXACTLY) {
            Log.d("DEBUG","        width mode EXACTLY");
        } else if (widthMode == MeasureSpec.AT_MOST) {
            Log.d("DEBUG","        width mode AT MOST");
        } else {
            Log.d("DEBUG","        width mode ... A PIACERE!!");
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            Log.d("DEBUG","        height mode EXACTLY");
        } else if (heightMode == MeasureSpec.AT_MOST) {
            Log.d("DEBUG","        height mode AT MOST");
        } else {
            Log.d("DEBUG","        height mode ... A PIACERE!!");
        }

        setMeasuredDimension(getSuggestedMinimumWidth(),getSuggestedMinimumHeight());
    }


    @Override
    protected void onLayout(boolean b, int x1, int y1, int x2, int y2) {
        int x = (int)((nodo.getX() - 8 )* density/1.5);
        int y = (int)((nodo.getY()-68)* density);
        setX((float)x);
        setY((float)y);
        Log.d("DEBUG","onLayout coordinate b="+b+"  x1="+x1+" y1="+y1+"  x2="+x2+"  y2="+y2);
        int smw = getSuggestedMinimumWidth();
        int smh = getSuggestedMinimumHeight();
        Log.d("DEBUG","onLayout smw="+smw+"   smh="+smh);
        setMeasuredDimension(smw,smh);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        int x = (int)((nodo.getX())* density);
        //int y = (int)((nodo.getY()-68)* density);
        int y = (int)((nodo.getY()- 68)* density);
        setX((float)x);
        setY((float)y);
        Log.d("DEBUG","onDraw, canvas.h="+canvas.getHeight()+"  w="+canvas.getWidth() + "x= " + getX() + "y= " + getY()+ "nodoX= "+ x + "nodoY= "+ y);
        canvas.drawCircle(dimensioneView/2, dimensioneView/2, dimensioneView/2, esterno);
        canvas.drawCircle(dimensioneView/2, dimensioneView/2, (dimensioneView/2)-10, interno);
    }

    @Override
    public void onClick(View view) {

        Toast.makeText(context, "Hai cliccato ", Toast.LENGTH_SHORT);
        System.out.println("Mi HAI CLICCATO");
        if(funzioniSelezionaNodo != null && nodo != null){
            funzioniSelezionaNodo.onClickNodo(nodo);
        }
    }
}
