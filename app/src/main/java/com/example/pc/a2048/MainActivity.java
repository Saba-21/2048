package com.example.pc.a2048;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import static java.lang.Math.abs;

public class MainActivity extends AppCompatActivity {

    private String orientation;
    private RelativeLayout main_view;
    private float startX, endX, startY, endY;
    private final int allBoard = 16;
    private TextView[] boardHolder = new TextView[allBoard];
    private String[] boardValues;
    private int[] boardIDs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_main);

        initView();

        setValuesToBoard();

        getTouchOrientation();

    }

    private void getTouchOrientation(){
        main_view.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        startX = event.getRawX() - v.getX();
                        startY = event.getRawY() - v.getY();
                        break;

                    case MotionEvent.ACTION_UP:
                        endX = event.getRawX() - v.getX();
                        endY = event.getRawY() - v.getY();

                        if (endX - startX > 0 && endX - startX > endY - startY)
                            orientation="Right";
                        if (endY - startY > 0 && endX - startX < endY - startY)
                            orientation="Bottom";
                        if (endX - startX < 0 && abs(endX - startX) > abs(endY - startY))
                            orientation="Left";
                        if (endY - startY < 0 && abs(endX - startX) < abs(endY - startY))
                            orientation="Top";

                        Toast.makeText(MainActivity.this, orientation, Toast.LENGTH_SHORT).show();

                        break;
                }
                return true;
            }
        });
    }

    private void setValuesToBoard(){
        for(int i = 0; i<allBoard; i++)
            boardHolder[i].setText(boardValues[i]);
    }

    private void initView() {
        main_view = findViewById(R.id.main_view);
        boardValues = getResources().getStringArray(R.array.board);
        boardIDs = new int[]{R.id.board_1,R.id.board_2,R.id.board_3,R.id.board_4,R.id.board_5,
                R.id.board_6,R.id.board_7,R.id.board_8,R.id.board_9,R.id.board_10,R.id.board_12,
                R.id.board_11,R.id.board_13,R.id.board_14,R.id.board_15,R.id.board_16};
        for(int i = 0; i<allBoard; i++)
            boardHolder[i] = findViewById(boardIDs[i]);
    }
}