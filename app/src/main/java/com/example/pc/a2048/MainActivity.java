package com.example.pc.a2048;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.abs;

public class MainActivity extends AppCompatActivity {

    private TextView score, record;
    private String orientation;
    private LinearLayout main_view;
    private float startX, endX, startY, endY;
    private final int allBoard = 16;
    private TextView[] boardHolder = new TextView[allBoard];
    private String[] boardValues;
    private int[] boardIDs;
    private int randPos;
    private boolean gameStarted = false;
    private boolean[] emptyBoxes;
    private String startingValue = "2";
    private int gameScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_main);

        initView();

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

                        moveValues();
                        addToRandomPosition();
                        setValuesToBoard();
                        break;
                }
                return true;
            }
        });
    }

    public int getColumn(int i){
                if((i+4)%4==0)
                    return 1;
                if((i+3)%4==0)
                    return 2;
                if((i+2)%4==0)
                    return 3;
                if((i+1)%4==0)
                    return 4;
                return 0;
    }

    public void moveValues(){
        getEmptyBoxes();
        String middle;
        switch (orientation) {
            case "Right":
                for(int i = 0; i<allBoard; i++)
                    if(!boardValues[i].isEmpty()){
                        int ii = leastRight(getColumn(i),i);
                        if (boardValues[i].equals(boardValues[ii]) && i!=ii){
                            int x = Integer.parseInt(boardValues[i])*2;
                            gameScore += x;
                            middle = Integer.toString(x);
                        }
                        else
                            middle = boardValues[i];
                        boardValues[i] = "";
                        boardValues[ii]= middle;
                        getEmptyBoxes();
                    }
                break;
            case "Left":
                for(int i = 0; i<allBoard; i++)
                    if (!boardValues[i].isEmpty()) {
                        int ii = leastLeft(getColumn(i), i);
                        if (boardValues[i].equals(boardValues[ii]) && i!=ii){
                            int x = Integer.parseInt(boardValues[i])*2;
                            gameScore += x;
                            middle = Integer.toString(x);
                        }
                        else
                            middle = boardValues[i];
                        boardValues[i] = "";
                        boardValues[ii] = middle;
                        getEmptyBoxes();
                }
                break;
        }
    }

    public int leastLeft(int i, int a){
        int least = a;
        switch (i){
            case 4:
                if((boardValues[a-3].equals(boardValues[a]) && emptyBoxes[a-2] && emptyBoxes[a-1]) || emptyBoxes[a-3]) {
                    least = a - 3;
                    break;
                }
                if((boardValues[a-2].equals(boardValues[a]) && emptyBoxes[a-1]) || emptyBoxes[a-2]) {
                    least = a - 2;
                    break;
                }
                if(boardValues[a-1].equals(boardValues[a]) || emptyBoxes[a-1]) {
                    least = a - 1;
                    break;
                }
            case 3:
                if((boardValues[a-2].equals(boardValues[a]) && emptyBoxes[a-1]) || emptyBoxes[a-2]) {
                    least = a - 2;
                    break;
                }
                if(boardValues[a-1].equals(boardValues[a]) || emptyBoxes[a-1]) {
                    least = a - 1;
                    break;
                }
            case 2:
                if(boardValues[a-1].equals(boardValues[a]) || emptyBoxes[a-1]) {
                    least = a - 1;
                    break;
                }
        }
        return least;
    }

    public int leastRight(int i, int a){
        int least = a;
        switch (i){
            case 1:
                if((boardValues[a+3].equals(boardValues[a]) && emptyBoxes[a+2] && emptyBoxes[a+1]) || emptyBoxes[a+3]) {
                    least = a + 3;
                    break;
                }
                if((boardValues[a+2].equals(boardValues[a]) && emptyBoxes[a+1]) || emptyBoxes[a+2]) {
                    least = a + 2;
                    break;
                }
                if(boardValues[a+1].equals(boardValues[a]) || emptyBoxes[a+1]) {
                    least = a + 1;
                    break;
                }
            case 2:
                if((boardValues[a+2].equals(boardValues[a]) && emptyBoxes[a+1]) || emptyBoxes[a+2]) {
                    least = a + 2;
                    break;
                }
                if(boardValues[a+1].equals(boardValues[a]) || emptyBoxes[a+1]) {
                    least = a + 1;
                    break;
                }
            case 3:
                if(boardValues[a+1].equals(boardValues[a]) || emptyBoxes[a+1]) {
                    least = a + 1;
                    break;
                }
        }
        return least;
    }

    public void initBoard(){
        if(!gameStarted){
            getRandomPosition();
            randPos = 0;
            boardValues[randPos]=startingValue;
            getRandomPosition();
            randPos = 3;
            boardValues[randPos]=startingValue;
            randPos = 2;
            boardValues[randPos]=startingValue;
            randPos = 1;
            boardValues[randPos]=startingValue;
            gameStarted = true;
        }
        setValuesToBoard();
    }

    public void addToRandomPosition(){
        getRandomPosition();
        boardValues[randPos]=startingValue;
    }

    public void getEmptyBoxes(){
        emptyBoxes = new boolean [allBoard];
        for(int i = 0; i<allBoard; i++)
            if(boardValues[i].isEmpty())
                emptyBoxes[i] = true;
    }

    public void getRandomPosition(){
        getEmptyBoxes();
        if(gameOver())
            Toast.makeText(this, "Game Over!", Toast.LENGTH_SHORT).show();
        else if (emptyElements()==1 && lastElement()!=99)
            randPos = lastElement();
        else {
            int rand = ThreadLocalRandom.current().nextInt(0, 15);
            if (emptyBoxes[rand])
                randPos = rand;
            else
                getRandomPosition();
        }
    }

    public int emptyElements(){
        int x = 0;
        for(int i = 0; i<allBoard; i++)
            if(emptyBoxes[i])
                x++;
        return x;
    }

    public int lastElement(){
        for(int i = 0; i<allBoard; i++)
            if(emptyBoxes[i])
                return i;
        return 99;
    }

    public Boolean gameOver(){
        int x = 0;
        for(int i = 0; i<allBoard; i++)
            if(emptyBoxes[i])
                x++;
        if(x==0)
            return true;
        return false;
    }

    private void setValuesToBoard(){
        score.setText(Integer.toString(gameScore));
        for(int i = 0; i<allBoard; i++) {
            boardHolder[i].setText(boardValues[i]);
            switch (boardValues[i]) {
                case "8":
                    boardHolder[i].setBackground(ContextCompat.getDrawable(this, R.drawable.back_eight));
                    boardHolder[i].setTextColor(Color.WHITE);
                    break;
                case "16":
                    boardHolder[i].setBackground(ContextCompat.getDrawable(this, R.drawable.back_sixteen));
                    boardHolder[i].setTextColor(Color.WHITE);
                    break;
                case "":
                    boardHolder[i].setBackground(ContextCompat.getDrawable(this, R.drawable.back_empty));
                    break;
                case "2":
                    boardHolder[i].setBackground(ContextCompat.getDrawable(this, R.drawable.back_two));
                    boardHolder[i].setTextColor(score.getTextColors());
                    break;
                case "4":
                    boardHolder[i].setBackground(ContextCompat.getDrawable(this, R.drawable.back_four));
                    boardHolder[i].setTextColor(score.getTextColors());
                    break;
            }
        }
    }

    private void initView() {
        score = findViewById(R.id.score);
        record = findViewById(R.id.record);
        main_view = findViewById(R.id.main_view);
        boardValues = getResources().getStringArray(R.array.board);
        boardIDs = new int[]{R.id.board_1,R.id.board_2,R.id.board_3,R.id.board_4,R.id.board_5,
                R.id.board_6,R.id.board_7,R.id.board_8,R.id.board_9,R.id.board_10,R.id.board_11,
                R.id.board_12,R.id.board_13,R.id.board_14,R.id.board_15,R.id.board_16};
        for(int i = 0; i<allBoard; i++)
            boardHolder[i] = findViewById(boardIDs[i]);
        initBoard();
    }
}