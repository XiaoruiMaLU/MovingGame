package com.example.movinggame3;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

public class eatClass {
    float Height,Width;
    float x,y;
    Paint paint;
    Bitmap imageBM;
    public RectF imageRec;
    public eatClass(Bitmap imageBM){
        this.imageBM=imageBM;
        Height=imageBM.getHeight();
        Width=imageBM.getWidth();
    }
    public void draw(Canvas canvas,float x,float y){
        this.x=x;
        this.y=y;
        canvas.drawBitmap(imageBM,x,y,paint);
        imageRec= new RectF(x,y,x+Width,y+Height);


    }
}
