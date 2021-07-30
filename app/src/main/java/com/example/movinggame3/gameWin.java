package com.example.movinggame3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class gameWin extends AppCompatActivity {
    String tag="MovingGame";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(tag,"++++ gameWin onCreate +++");
        super.onCreate(null);
        setContentView(R.layout.activity_game_win);
        //recieve intent extra
        double difficulty=getIntent().getDoubleExtra("difficulty",0.35)+0.05;
        int difficultyLevel=getIntent().getIntExtra("difficultyLevel",1)+1;

        //restart Button
        Button restartButton=findViewById(R.id.restart);
        Log.d(tag,"restart Button");
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(tag,"跳转页面");
                Intent intent=new Intent(gameWin.this,gameActivity.class);
                startActivity(intent);
            }
        });
        //difficulty up Button
        Button kissButton=findViewById(R.id.difficulty);
        Log.d(tag,"difficulty adjustment Button");
        kissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(gameWin.this,gameActivity.class);
                intent.putExtra("difficulty", difficulty);
                intent.putExtra("difficultyLevel", difficultyLevel);
                Toast.makeText(gameWin.this,"难度等级： "+difficultyLevel,Toast.LENGTH_LONG).show();
                Log.d(tag,"neg increase speed： "+(difficulty+0.05));
                startActivity(intent);

            }
        });
    }

    @Override
    protected void onDestroy() {
        Log.d(tag,"gameWin onDestroy");
        super.onDestroy();

    }

    @Override
    protected void onStop() {
        Log.d(tag,"gameWin onStop");
        super.onStop();
    }

}

