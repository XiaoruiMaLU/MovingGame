package com.example.movinggame3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import javax.security.auth.callback.Callback;

import static android.graphics.Bitmap.Config.ALPHA_8;

public class gameActivity extends Activity {
    gameView mgameView;
    public static String tag="MovingGame";
    //LinearLayout container=();
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(tag,"gameActivity onCreate");
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //全屏
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //gameView对象
        mgameView=new gameView(this);
        //set game view as view
        setContentView(mgameView);
    }

    @Override
    public void onDestroy()
    {
        Log.d(tag,"game activity onDestroy");
        super.onDestroy();
    }

    public class gameView extends SurfaceView  implements SurfaceHolder.Callback, Runnable,//实现接口
            SensorEventListener  {
        //游戏里需要的全部fields
        //画布&笔&图

        Canvas canvas;

        Paint paint;
        Paint recPaint;
        Paint valuePaint;
        TextPaint textPaint;
        TextPaint titlePaint;
        //游戏图案
        Bitmap bokutoBM;
        Bitmap akashiiBM;
        Bitmap volleyballBM;
        Bitmap heartBM;
        Bitmap blockBM,paperBM;
        //Button Bitmap
        Bitmap restartBM_origin;//原图
        Bitmap restartBM;//resize 之后的
        Bitmap difficultyUpBM_origin;
        Bitmap difficultyUpBM;
        //点击位置坐标
        float clickX,clickY;

        float bokutoHeight;
        float bokutoWidth;
        float akashiiHeight;
        float akashiiWidth;
        RectF akashiiRec;
        SurfaceHolder surfaceHolder;
        //能撞了吃掉的东西
        eatClass eatVolleyball,eatHeart;
        //两个按钮，能获得长方形
        eatClass eatRestartBtn, eatdifficultyUpBtn;
        //两个有伤害的
        eatClass eatBlock,eatPaper;


        float respectValue=0;
        float sukiValue=0;
        float negativeValue=0;
        public double negativeValueIncreaseSpeed;//越大越难
        public double akashiiGain=0.1;
        public double blockMovingSpeed=3;
        public int difficultyLevel;

        //刷新间隔 30ms
        int time=50;
        //画布宽高
        float canvasHeight;
        float canvasWidth;

        //bokuto每次画的位置
        float posX;
        float posY;
        //akashii的位置
        float stillX;
        float stillY;
        //变化的位置
        float GX;
        float GY;
        //血条纵坐标
        float y0=50;
        float y1=100;
        float y2=150;
        float x1;
        float x2;
        //block的位置
        float blockX,blockY;
        //paper的位置，轨迹圆圈，随机生成一个圆心
        Point circleCenter;
        float radius=200;
        double angle=10;


        //SensorManager,获取重力坐标
        SensorManager sensorManager;
        Sensor sensor;
        //是否在动,是否吃了
        Boolean isRunning;
        Boolean ballIsEaten,heartIsEaten;

        //游戏结束
        Boolean gameOver;

        //台词
        String[] bokutoDialogue;
        String[] akashiiDialogue;
        String bokutoShowText;
        String akashiiShowText;
        //计数
        int count=0;






        public gameView(Context context) {
            super(context);
            //init()的那些东西可以写在构造器里，这样gemeView一实例化就带着那些能用的东西
            Log.d(tag,"构造器，初始化一堆");
            canvas=new Canvas();
            //白色背景
            canvas.drawColor(Color.WHITE);
            //渲染位图
            paint=new Paint();
            //写条前的字
            titlePaint=new TextPaint();
            titlePaint.setColor(Color.BLACK);
            titlePaint.setTextSize(30);
            //画条的框
            recPaint=new Paint();
            recPaint.setColor(Color.BLACK);
            recPaint.setStrokeCap(Paint.Cap.BUTT);
            recPaint.setStyle(Paint.Style.STROKE);
            recPaint.setStrokeWidth(5);
            //画条里的条
            valuePaint=new Paint();
            valuePaint.setColor(Color.BLACK);
            valuePaint.setStrokeCap(Paint.Cap.BUTT);
            valuePaint.setStrokeWidth(30);

            //写字
            textPaint=new TextPaint();
            textPaint.setColor(Color.BLACK);
            textPaint.setTextSize(50);
            //圆心
            circleCenter=new Point();



            //获取木兔，创建为Bitmap
            Drawable image=getResources().getDrawable(R.drawable.bokuto);
            BitmapDrawable drawable=(BitmapDrawable) image;
            bokutoBM=drawable.getBitmap();
            //获取赤苇，创建为Bitmap
            Drawable image2=getResources().getDrawable(R.drawable.akashii);
            BitmapDrawable drawable2=(BitmapDrawable) image2;
            akashiiBM=drawable2.getBitmap();
            //获取排球，创建为Bitmap
            Drawable image3=getResources().getDrawable(R.drawable.volleyball);
            BitmapDrawable drawable3=(BitmapDrawable) image3;
            volleyballBM=drawable3.getBitmap();
            eatVolleyball=new eatClass(volleyballBM);
            //获取心，创建为Bitmap
            Drawable image4=getResources().getDrawable(R.drawable.heart);
            BitmapDrawable drawable4=(BitmapDrawable) image4;
            heartBM=drawable4.getBitmap();
            eatHeart=new eatClass(heartBM);
            //获取restart按钮，创建为BitMap
            Drawable image5=getResources().getDrawable(R.drawable.restart);
            BitmapDrawable drawable5=(BitmapDrawable) image5;
            restartBM_origin=drawable5.getBitmap();

            //获取difficulty up按钮，创建为Bitmap
            Drawable image6=getResources().getDrawable(R.drawable.difficulty_up);
            BitmapDrawable drawable6=(BitmapDrawable) image6;
            difficultyUpBM_origin=drawable6.getBitmap();
            //获取block图标，创建为Bitmap
            Drawable image7=getResources().getDrawable(R.drawable.block);
            BitmapDrawable drawable7=(BitmapDrawable) image7;
            blockBM=drawable7.getBitmap();
            eatBlock=new eatClass(blockBM);
            //获取paper图标，创建为Bitmap
            Drawable image8=getResources().getDrawable(R.drawable.paper);
            BitmapDrawable drawable8=(BitmapDrawable) image8;
            paperBM=drawable8.getBitmap();
            eatPaper=new eatClass(paperBM);


            //获取图片宽高
            akashiiHeight=akashiiBM.getHeight();
            akashiiWidth=akashiiBM.getWidth();

            Log.d(tag,"akashii 宽高"+akashiiWidth+"x"+akashiiHeight);
            bokutoHeight=bokutoBM.getHeight();
            bokutoWidth=bokutoBM.getWidth();
            //sensorManager, 注册监听器
            sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
            sensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener((SensorEventListener) this, sensor,SensorManager.SENSOR_DELAY_GAME);
            //在动
            isRunning=true;
            ballIsEaten=false;//还没被吃
            heartIsEaten=false;
            //surfaceHolder,用来拿到画布进行操作
            surfaceHolder=this.getHolder();
            surfaceHolder.addCallback((SurfaceHolder.Callback) this);

            //台词设置
            bokutoDialogue= new String[]{"Hey！Hey！Hey！！", "AKASHIIIIII！！","再来一球！！","为什么要跑？","ww!!"};
            bokutoShowText=bokutoDialogue[count];
            akashiiDialogue=new String[]{"?","bokuto先輩。","好的。","...","///"};
            akashiiShowText=akashiiDialogue[count];

            Log.d(tag,"构造器，初始化结束");
            //难度设置，接收intent
            negativeValueIncreaseSpeed=getIntent().getDoubleExtra("difficulty",0.35);
            difficultyLevel=getIntent().getIntExtra("difficultyLevel",1);






        }
        public void init(){

            //Log.d(tag,"设置无法接收touch event");

            //起始位置
            Log.d(tag,"获取画布宽高");
            DisplayMetrics dm = new DisplayMetrics();//describing general information about a display
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            canvasWidth = dm.widthPixels;//宽
            canvasHeight= dm.heightPixels; //高
            Log.d(tag,"屏幕的宽: "+canvasWidth+"屏幕的高: "+canvasHeight);
            //让重开按钮和difficulty up的size应屏幕
            int button_length=(int)canvasWidth-100;
            restartBM=Bitmap.createScaledBitmap(restartBM_origin,button_length,button_length,false);
            eatRestartBtn=new eatClass(restartBM);
            difficultyUpBM=Bitmap.createScaledBitmap(difficultyUpBM_origin,button_length,button_length,false);
            Log.d(tag,"按钮的Bitmap"+restartBM+","+difficultyUpBM);
            eatdifficultyUpBtn=new eatClass(difficultyUpBM);
            //统一摆血条的位置
            posX=canvasWidth/2;
            posY=canvasHeight/2;
            x1=canvasWidth/10;
            x2=canvasWidth/2;
            //画-->随机位置
            drawStill();
            //画-->随机位置，获取随机坐标，生成相应的矩形范围
            drawEat(eatVolleyball);
            drawEat(eatHeart);
            drawStraightTrack();
            drawCircleTrack();

        }
        //画移动的那个的东西，一直在刷新位置，同时每次都要画不动的那个，否则会被覆盖
        public void drawMove(){
            //Log.d(tag,"画更新");
            canvas.drawColor(Color.WHITE);//没有这个之前的图就会一直在
            canvas.drawBitmap(akashiiBM,stillX,stillY,paint);
            //能量条：
            //Akashii
            canvas.drawText("赤苇京治",x1,y0,titlePaint);
            // respect
            canvas.drawText("瑞斯拜",x1,y1,titlePaint);
            drawValue(x1+100,y1,respectValue);
            //suki
            canvas.drawText("suki",x1,y2,titlePaint);
            drawValue(x1+100,y2,sukiValue);
            //bokuto
            canvas.drawText("木兔光太郎",x2,y0,titlePaint);
            //negative Mode
            canvas.drawText("NEGATIVE",x2,y1,titlePaint);
            drawValue(x2+200,y1,negativeValue);
            //画吃掉的
            if (!ballIsEaten){
                //Log.d(tag,"画球");
                canvas.drawBitmap(volleyballBM,eatVolleyball.x,eatVolleyball.y,paint);//画-->更新图案,这个不能用drawEat，否则会一直换位置
            }
            if (!heartIsEaten){
                //Log.d(tag,"画心");
                canvas.drawBitmap(heartBM,eatHeart.x,eatHeart.y,paint);
            }
            //画碰到会增加nega的，每次更新赤苇也不需要更新位置，游戏结束之前一直都在那个轨迹移动即可
            eatBlock.draw(canvas,eatBlock.x,eatBlock.y);
            eatPaper.draw(canvas,eatPaper.x,eatPaper.y);
            //写对话
            canvas.drawText(bokutoShowText,posX+bokutoWidth+50,posY+bokutoHeight/2,textPaint);
            canvas.drawText(akashiiShowText,stillX+akashiiWidth+50,stillY+akashiiHeight/2,textPaint);
            //碰到右边
            if (posX>canvasWidth-bokutoWidth){
                posX=canvasWidth-bokutoWidth;
                canvas.drawBitmap(bokutoBM, posX, posY, paint);
            }
            //碰到下边
            else if (posY>canvasHeight){
                posY=canvasHeight-bokutoHeight;//意想不到的蹦蹦跳跳
                canvas.drawBitmap(bokutoBM, posX, posY, paint);
            }
            //碰到上边（这次获取的是canvas从Y=0开始）
            else if (posY<250){
                posY=250;
                canvas.drawBitmap(bokutoBM, posX, posY, paint);
            }
            //碰到左边
            else if (posX<0){
                posX=0;
                canvas.drawBitmap(bokutoBM, posX, posY, paint);
            }
            //中间部分
            else {
                canvas.drawBitmap(bokutoBM, posX, posY, paint);
            }
        }
        //画不动的那个
        public void drawStill(){
            Log.d(tag,"画赤苇");
            //isRunning=true;
            //x=[0,canvasWidth-bitmap.getWidth]
            stillX=(float) (Math.random()*(canvasWidth-akashiiWidth+1));
            //y=[250,canvasHeight-bitmap.getHeight]
            stillY=(float) (Math.random()*(canvasHeight-akashiiHeight+1-250)+250);
            Log.d(tag,"still X: "+stillX+" still Y: "+stillY);
            canvas.drawBitmap(akashiiBM,stillX,stillY,paint);
            akashiiRec= new RectF(stillX,stillY,stillX+akashiiWidth,stillY+akashiiHeight);
            ballIsEaten=false;
            heartIsEaten=false;
            drawEat(eatVolleyball);
            drawEat(eatHeart);


        }
        public void drawEat(eatClass eatItem){
            Log.d(tag,"画球和心");
            //
            eatItem.x=(float) (Math.random()*(canvasWidth-eatItem.Width+1));
            eatItem.y=(float) (Math.random()*(canvasHeight-eatItem.Height+1-250)+250);
            eatItem.draw(canvas,eatItem.x,eatItem.y);

        }
        //画血条
        public void drawValue(float x,float y,float value){
           // Log.d(tag,"画血条");
            canvas.drawRect(x,y-16-10,x+210,y+16-10,recPaint);
            if (value>0) {
                canvas.drawLine(x, y-10, x + value, y-10, valuePaint);
            }

        }
        //直线轨迹，起点：随机生成一个Y，X不变，生成后x变
        public void drawStraightTrack(){
            Log.d(tag,"画拦网");
            //
            eatBlock.x=0;
            eatBlock.y=(float) (Math.random()*(canvasHeight-eatBlock.Height+1-250)+250);
            eatBlock.draw(canvas,eatBlock.x,eatBlock.y);

        }
        //圆圈轨迹，圆心：随机生成，角度一直在变
        public void drawCircleTrack(){
            Log.d(tag,"转圈的");
            //r < x < canvas.W-eatPaper.W-r
            circleCenter.x= (int) (Math.random()*(canvasWidth-eatPaper.Width-2*radius+1)+radius);
            //250+r < y<canvasH-eatpaper.h-r
            circleCenter.y= (int) (Math.random()*(canvasHeight-eatPaper.Height-2*radius-250+1)+250+radius);
            eatPaper.x= (float) (circleCenter.x+radius*Math.cos(angle));
            eatPaper.y=(float) (circleCenter.y+radius*Math.sin(angle));
            eatPaper.draw(canvas,eatPaper.x,eatPaper.y);

        }

        //停止画的标准,一碰到赤苇，赤苇就换一个随机生成的地方
        public void stop(){
            //Log.d(tag, "stop:");

            if ( akashiiRec.contains(posX,posY)||akashiiRec.contains(posX,posY+bokutoHeight)||akashiiRec.contains(posX+bokutoWidth,posY)||akashiiRec.contains(posX+bokutoWidth,posY+bokutoHeight)){
                Log.d(tag,"撞上赤苇了");
                isRunning=false;
                count+=1;
                //设置台词
                if (count>=5){
                    count=0;
                }
                bokutoShowText=bokutoDialogue[count];
                akashiiShowText=akashiiDialogue[count];
                if (negativeValue<0){
                    negativeValue=0;
                }
                else{
                    negativeValue-=(20+akashiiGain);
                }

                drawStill();
                isRunning=true;

            }
            else if ( eatVolleyball.imageRec.contains(posX,posY)||eatVolleyball.imageRec.contains(posX,posY+bokutoHeight)||eatVolleyball.imageRec.contains(posX+bokutoWidth,posY)||eatVolleyball.imageRec.contains(posX+bokutoWidth,posY+bokutoHeight)){
                Log.d(tag,"撞上球了");
                isRunning=false;
                //暂时先这样控制，不让高于210
                if (respectValue<210){
                    respectValue+=70;
                }
                else{
                    respectValue=210;
                }
                //减少negativeValue,到0了不加
                if (negativeValue<0){
                    negativeValue=0;
                }
                else{
                    negativeValue-=10;
                }

                //把矩形坐标搞没，否则吃掉后那一部分还保留着原数据，经过一直加分
                eatVolleyball.imageRec.set(0,0,0,0);

                Log.d(tag,"respect +30");
                ballIsEaten=true;
                isRunning=true;
            }
            else if ( eatHeart.imageRec.contains(posX,posY)||eatHeart.imageRec.contains(posX,posY+bokutoHeight)||eatHeart.imageRec.contains(posX+bokutoWidth,posY)||eatHeart.imageRec.contains(posX+bokutoWidth,posY+bokutoHeight)){
                Log.d(tag,"撞上心了");
                isRunning=false;
                //增加sukiValue,到头了不加
                if (sukiValue<210){
                    sukiValue+=70;
                }
                else{
                    sukiValue=210;
                }
                //减少negativeValue,到0了不加
                if (negativeValue<0){
                    negativeValue=0;
                }
                else{
                    negativeValue-=10;
                }

                eatHeart.imageRec.set(0,0,0,0);//图消失了，坐标也要消失
                Log.d(tag,"suki +30");
                heartIsEaten=true;
                isRunning=true;

            }
            else if ( eatBlock.imageRec.contains(posX,posY)||eatBlock.imageRec.contains(posX,posY+bokutoHeight)||eatBlock.imageRec.contains(posX+bokutoWidth,posY)||eatBlock.imageRec.contains(posX+bokutoWidth,posY+bokutoHeight)){
                Log.d(tag,"撞到拦网，neg+5");
                isRunning=false;
                negativeValue+=5;
                eatBlock.imageRec.set(0,0,0,0);//图移动了，相当于原位置消失，坐标要消失
                isRunning=true;
            }
            else if ( eatPaper.imageRec.contains(posX,posY)||eatPaper.imageRec.contains(posX,posY+bokutoHeight)||eatPaper.imageRec.contains(posX+bokutoWidth,posY)||eatPaper.imageRec.contains(posX+bokutoWidth,posY+bokutoHeight)){
                Log.d(tag,"撞到试卷，neg+5");
                isRunning=false;
                negativeValue+=5;
                eatPaper.imageRec.set(0,0,0,0);//图移动了，相当于原位置消失，坐标要消失
                isRunning=true;
            }



        }


        //画
        @Override
        public void surfaceCreated(SurfaceHolder holder){
            init();
            new Thread(this).start();

        }

        @Override
        public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
            Log.v(tag,"surfaceDestroyed");
        }

        //每次Sensor变的时候，获取陀螺仪xyz变化，计算坐标
        @Override
        public void onSensorChanged(SensorEvent event){
            if (isRunning) {  //一旦坐标到了就刷新
                stop();
                //陀螺仪偏移量

                if (negativeValue<210){
                    negativeValue+=negativeValueIncreaseSpeed;
                    akashiiGain+=0.01;
                    //paper坐标变化，转圈移动，角度变
                    angle+=0.05;
                    eatPaper.x= (float) (circleCenter.x+radius*Math.cos(angle));
                    eatPaper.y=(float) (circleCenter.y+radius*Math.sin(angle));
                    //Block坐标变化，横向移动，x变
                    if (eatBlock.x>=0 && eatBlock.x<=canvasWidth-eatBlock.Width){
                        //Log.d(tag,"block右移");
                        eatBlock.x+=blockMovingSpeed;
                    }
                    else if (eatBlock.x>canvasWidth-eatBlock.Width){
                        //Log.d(tag,"block左移");
                        eatBlock.x=0;
                    }

                    if (sukiValue==210 && respectValue==210){
                        Log.d(tag,"win");
                        sensorManager.unregisterListener(this);
                       // Toast.makeText(gameActivity.this,"赤苇攻略度120%",Toast.LENGTH_LONG).show();
                        gameOver=true;
                        isRunning=false;//大重要事件：即使finish当前Activity,那个线程依然存在，存在的时候它跳转会闪一下然后调回来重开这个Activity
                        Log.d(tag,"跳转win开始");
                        Intent intent3=new Intent(gameActivity.this,gameWin.class);
                        intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent3.putExtra("difficulty", negativeValueIncreaseSpeed);
                        intent3.putExtra("difficultyLevel", difficultyLevel);
                        startActivity(intent3);


                    }
                }
                else {
                    Log.d(tag,"lost");
                    sensorManager.unregisterListener(this);
                    //Toast.makeText(gameActivity.this,"Warning：你的ACE已进入消极模式！",Toast.LENGTH_LONG).show();
                    gameOver=true;
                    isRunning=false;
                   /* mgameView=new gameView(this.getContext());
                    Log.d(tag,"新建gameView object");*/
                    Log.d(tag,"跳转lose开始");
                    Intent intent2=new Intent(gameActivity.this,gameOver.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent2);
                    //finish(); // Call once you redirect to another activity


                    //finish();//Finish this activity as well as all activities immediately below it



                }
                GX = event.values[0];
                GY = event.values[1];
                //移动坐标
                posX -= 2 * GX;
                posY += 2 * GY;
                //
            }



        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }


        @Override
        public void run() {
            while (isRunning){
                long startTime=System.currentTimeMillis();
                //线程安全锁，只有一个在画
                synchronized (surfaceHolder){
                    canvas=surfaceHolder.lockCanvas();
                    drawMove();
                    //解锁并显示在屏幕上
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
                long endTime=System.currentTimeMillis();
                int diff=(int) (endTime-startTime);

                while (diff <= time) {//直到现在到开始画过去30ms
                    diff = (int) (System.currentTimeMillis() - startTime);
                    /** 线程等待 **/
                    Thread.yield();

                }

            }

        }
    }



}
