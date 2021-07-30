package com.example.movinggame3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class gameOver extends AppCompatActivity {
    String tag="MovingGame";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(tag,"++++ gameOver onCreate +++");
        super.onCreate(null);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView(R.layout.activity_game_over);

        Button restartButton=findViewById(R.id.restart);
        Log.d(tag,"restart Button");
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(tag,"跳转页面");
                Intent intent=new Intent(gameOver.this,gameActivity.class);
                startActivity(intent);
            }
        });
        Button kissButton=findViewById(R.id.kiss);
        Log.d(tag,"kiss Button");
        kissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(gameOver.this,"我就是最强的！（奶满）",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        Log.d(tag,"gameOver onDestroy");
        super.onDestroy();

    }

    @Override
    protected void onStop() {
        Log.d(tag,"gameOver onStop");
        super.onStop();
    }

}
