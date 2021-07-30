package com.example.movinggame3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    String tag="MovingGame";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(tag,"++++ MainActivity onCreate +++");
        super.onCreate(savedInstanceState);

        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView(R.layout.activity_main);

        Button startButton=findViewById(R.id.start);
        Log.d(tag,"Start Button");
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(tag,"跳转页面");
                Intent intent=new Intent(MainActivity.this,gameActivity.class);
                startActivity(intent);

            }
        });

    }

    @Override
    protected void onDestroy() {
        Log.d(tag,"MainActivity Destroyed");
        super.onDestroy();
    }
}