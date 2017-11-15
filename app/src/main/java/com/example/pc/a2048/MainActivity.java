package com.example.pc.a2048;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import static java.lang.Math.abs;

public class MainActivity extends AppCompatActivity {

    private TextView orientation;
    private RelativeLayout main_view;
    private float startX, endX, startY, endY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_main);
        initView();

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

                        if(endX-startX > 0 && endX - startX > endY - startY)
                            orientation.setText("Right");
                        if(endY - startY > 0 && endX - startX < endY - startY)
                            orientation.setText("Bottom");
                        if(endX-startX < 0 && abs(endX - startX) > abs(endY - startY))
                            orientation.setText("Left");
                        if(endY - startY < 0 && abs(endX - startX) < abs(endY - startY))
                            orientation.setText("Top");

                        break;
                }
                return true;
            }
        });





    }

    private void initView() {
        orientation = findViewById(R.id.orientation);
        main_view = findViewById(R.id.main_view);
    }
}
