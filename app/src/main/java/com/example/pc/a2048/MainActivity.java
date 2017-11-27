package com.example.pc.a2048;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ContentFrameLayout;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import java.util.concurrent.ThreadLocalRandom;
import static java.lang.Math.abs;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    private ColorStateList textColor;
    private TextView score, record;
    private String orientation;
    private ContentFrameLayout main_view;
    private float startX, endX, startY, endY;
    private final int allBoard = 16;
    private TextView[] boardViewHolder;
    private String[] boardValues;
    private int outOfBoard = allBoard;
    private int randPos = outOfBoard;
    private boolean gameStarted = false;
    private boolean[] emptyBoxes;
    private boolean gameOver = false;
    private String startingValue = "2";
    private int gameScore;
    private int[] changedBoxes;
    private Dialog reset;
    private final String scoreKey = "Score", recordKey = "Record", boardKey = "Board",
            startKey = "Start", endKey = "End", prefKey = "myPref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        if(getSupportActionBar() != null)
            getSupportActionBar().hide();

        switch (this.getResources().getConfiguration().orientation){
            case 1: setContentView(R.layout.activity_main);
                break;
            case 2: setContentView(R.layout.activity_main_land);
                break;
        }

        initView();

        initBoard();

        gamePlay();
    }

    private void gamePlay() {
        main_view.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch (View v, MotionEvent event){
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                startX = event.getRawX() - v.getX();
                startY = event.getRawY() - v.getY();
                break;

            case MotionEvent.ACTION_UP:
                endX = event.getRawX() - v.getX();
                endY = event.getRawY() - v.getY();

                if (gameStarted && !gameOver) {
                    getOrientation();
                    moveValues();
                    addToRandomPosition();
                }
                break;
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putStringArray(boardKey, boardValues);
        outState.putString(scoreKey, score.getText().toString());
        outState.putString(recordKey, record.getText().toString());
        outState.putBoolean(startKey, gameStarted);
        outState.putBoolean(endKey, gameOver);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        score.setText(savedInstanceState.getString(scoreKey));
        record.setText(savedInstanceState.getString(recordKey));
        boardValues = savedInstanceState.getStringArray(boardKey);
        gameStarted = savedInstanceState.getBoolean(startKey);
        gameOver = savedInstanceState.getBoolean(endKey);
        setValuesToBoard();
        super.onRestoreInstanceState(savedInstanceState);
    }

    public void saveToSharedPreferences(){
        SharedPreferences.Editor editor = getSharedPreferences(prefKey, MODE_PRIVATE).edit();
        editor.putString(recordKey, record.getText().toString());
        editor.apply();
    }

    public String getValuesFromSharedPreferences(){
        SharedPreferences prefs = getSharedPreferences(prefKey, MODE_PRIVATE);
        return prefs.getString(recordKey, Integer.toString(0));
    }

    public void getOrientation(){
        if (endX - startX > 0 && endX - startX > endY - startY)
            orientation="Right";
        if (endY - startY > 0 && endX - startX < endY - startY)
            orientation="Bottom";
        if (endX - startX < 0 && abs(endX - startX) > abs(endY - startY))
            orientation="Left";
        if (endY - startY < 0 && abs(endX - startX) < abs(endY - startY))
            orientation="Top";
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

    public int getRow(int i){
                if(i>-1 && i<4)
                    return 1;
                if(i>3 && i<8)
                    return 2;
                if(i>7 && i<12)
                    return 3;
                if(i>11 && i<16)
                    return 4;
                return 0;
    }

    public void moveValues(){
        getEmptyBoxes();
        String middle;
        switch (orientation) {
            case "Right":
                for(int i = allBoard-1; i>-1; i--)
                    if(!boardValues[i].isEmpty()){
                        int ii = leastRight(getColumn(i),i);
                        if (boardValues[i].equals(boardValues[ii]) && i!=ii){
                            int x = Integer.parseInt(boardValues[i])*2;
                            gameScore += x;
                            middle = Integer.toString(x);
                            changedBoxes[ii]=ii;
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
                            changedBoxes[ii]=ii;
                        }
                        else
                            middle = boardValues[i];
                        boardValues[i] = "";
                        boardValues[ii] = middle;
                        getEmptyBoxes();
                }
                break;
            case "Top":
                for(int i = 0; i<allBoard; i++)
                    if (!boardValues[i].isEmpty()) {
                        int ii = leastUp(getRow(i), i);
                        if (boardValues[i].equals(boardValues[ii]) && i!=ii){
                            int x = Integer.parseInt(boardValues[i])*2;
                            gameScore += x;
                            middle = Integer.toString(x);
                            changedBoxes[ii]=ii;
                        }
                        else
                            middle = boardValues[i];
                        boardValues[i] = "";
                        boardValues[ii] = middle;
                        getEmptyBoxes();
                    }

                break;
            case "Bottom":
                for(int i = allBoard-1; i>-1; i--)
                    if (!boardValues[i].isEmpty()) {
                        int ii = leastDown(getRow(i), i);
                        if (boardValues[i].equals(boardValues[ii]) && i!=ii){
                            int x = Integer.parseInt(boardValues[i])*2;
                            gameScore += x;
                            middle = Integer.toString(x);
                            changedBoxes[ii]=ii;
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

    public int leastUp(int i, int a){
        int least = a;
        switch (i){
            case 4:
                if((boardValues[a-12].equals(boardValues[a]) && emptyBoxes[a-4] && emptyBoxes[a-8]) || emptyBoxes[a-12]) {
                    least = a - 12;
                    break;
                }
                if((boardValues[a-8].equals(boardValues[a]) && emptyBoxes[a-4]) || emptyBoxes[a-8]) {
                    least = a - 8;
                    break;
                }
                if(boardValues[a-4].equals(boardValues[a]) || emptyBoxes[a-4]) {
                    least = a - 4;
                    break;
                }
            case 3:
                if((boardValues[a-8].equals(boardValues[a]) && emptyBoxes[a-4]) || emptyBoxes[a-8]) {
                    least = a - 8;
                    break;
                }
                if(boardValues[a-4].equals(boardValues[a]) || emptyBoxes[a-4]) {
                    least = a - 4;
                    break;
                }
            case 2:
                if(boardValues[a-4].equals(boardValues[a]) || emptyBoxes[a-4]) {
                    least = a - 4;
                    break;
                }
        }
        return least;
    }

    public int leastDown(int i, int a){
        int least = a;
        switch (i){
            case 1:
                if((boardValues[a+12].equals(boardValues[a]) && emptyBoxes[a+4] && emptyBoxes[a+8]) || emptyBoxes[a+12]) {
                    least = a + 12;
                    break;
                }
                if((boardValues[a+8].equals(boardValues[a]) && emptyBoxes[a+4]) || emptyBoxes[a+8]) {
                    least = a + 8;
                    break;
                }
                if(boardValues[a+4].equals(boardValues[a]) || emptyBoxes[a+4]) {
                    least = a + 4;
                    break;
                }
            case 2:
                if((boardValues[a+8].equals(boardValues[a]) && emptyBoxes[a+4]) || emptyBoxes[a+8]) {
                    least = a + 8;
                    break;
                }
                if(boardValues[a+4].equals(boardValues[a]) || emptyBoxes[a+4]) {
                    least = a + 4;
                    break;
                }
            case 3:
                if(boardValues[a+4].equals(boardValues[a]) || emptyBoxes[a+4]) {
                    least = a + 4;
                    break;
                }
        }
        return least;
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
            boardValues[randPos]=startingValue;
            getRandomPosition();
            boardValues[randPos]=startingValue;
            gameStarted = true;
        }
        setValuesToBoard();
        record.setText(getValuesFromSharedPreferences());
    }

    public void addToRandomPosition(){
        getRandomPosition();
        if(randPos!=outOfBoard)
            boardValues[randPos]=startingValue;
        setValuesToBoard();
    }

    public void getEmptyBoxes(){
        for(int i = 0; i<allBoard; i++)
                emptyBoxes[i] = boardValues[i].equals("");
    }

    public void getRandomPosition(){
        getEmptyBoxes();
        if(emptyBoxesOver() && !gameOver) {
            record.setText(score.getText().toString());
            gameOver = true;
            reset.show();
            if (Integer.parseInt(getValuesFromSharedPreferences())<Integer.parseInt(record.getText().toString()))
                saveToSharedPreferences();
        }
        else
            if (emptyElements()==1 && lastElement()!=outOfBoard)
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
        return outOfBoard;
    }

    public Boolean emptyBoxesOver(){
        int x = 0;
        for(int i = 0; i<allBoard; i++)
            if(emptyBoxes[i])
                x++;
        return x == 0;
    }

    private void setValuesToBoard(){
        if (gameScore!=0)
            score.setText(Integer.toString(gameScore));
        for(int i = 0; i<allBoard; i++) {
            boardViewHolder[i].setText(boardValues[i]);
            getEmptyBoxes();
            if(i==changedBoxes[i] && !boardValues[i].isEmpty() && !emptyBoxes[i]) {
                Animation scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_in);
                boardViewHolder[i].startAnimation(scaleAnimation);
                changedBoxes[i]=outOfBoard;
            }
            if(i==randPos){
                Animation scaleZeroAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_from_zero);
                boardViewHolder[i].startAnimation(scaleZeroAnimation);
                randPos=outOfBoard;
            }
            switch (boardValues[i]) {
                case "":
                    boardViewHolder[i].setBackground(ContextCompat.getDrawable(this, R.drawable.back_empty));
                    break;
                case "2":
                    boardViewHolder[i].setBackground(ContextCompat.getDrawable(this, R.drawable.back_two));
                    boardViewHolder[i].setTextColor(textColor);
                    break;
                case "4":
                    boardViewHolder[i].setBackground(ContextCompat.getDrawable(this, R.drawable.back_four));
                    boardViewHolder[i].setTextColor(textColor);
                    break;
                case "8":
                    boardViewHolder[i].setBackground(ContextCompat.getDrawable(this, R.drawable.back_eight));
                    boardViewHolder[i].setTextColor(Color.WHITE);
                    break;
                case "16":
                    boardViewHolder[i].setBackground(ContextCompat.getDrawable(this, R.drawable.back_sixteen));
                    boardViewHolder[i].setTextColor(Color.WHITE);
                    break;
                case "32":
                    boardViewHolder[i].setBackground(ContextCompat.getDrawable(this, R.drawable.back_32));
                    boardViewHolder[i].setTextColor(textColor);
                    break;
                case "64":
                    boardViewHolder[i].setBackground(ContextCompat.getDrawable(this, R.drawable.back_64));
                    boardViewHolder[i].setTextColor(textColor);
                    break;
                case "128":
                    boardViewHolder[i].setBackground(ContextCompat.getDrawable(this, R.drawable.back_128));
                    boardViewHolder[i].setTextColor(textColor);
                    break;
                case "256":
                    boardViewHolder[i].setBackground(ContextCompat.getDrawable(this, R.drawable.back_256));
                    boardViewHolder[i].setTextColor(textColor);
                    break;
                case "512":
                    boardViewHolder[i].setBackground(ContextCompat.getDrawable(this, R.drawable.back_512));
                    boardViewHolder[i].setTextColor(textColor);
                    break;
            }
        }
    }

    private void initView() {
        reset = new Dialog(this);
        reset.setContentView(R.layout.reset_dialog);
        Button restart = reset.findViewById(R.id.reset);
        emptyBoxes = new boolean[allBoard];
        boardViewHolder = new TextView[allBoard];
        changedBoxes = new int[allBoard];
        boardValues = getResources().getStringArray(R.array.board);
        score = findViewById(R.id.score);
        record = findViewById(R.id.record);
        textColor = score.getTextColors();
        main_view = findViewById(android.R.id.content);
        restart.setTextColor(textColor);
        int[] boardIDs = new int[]{R.id.board_1,R.id.board_2,R.id.board_3,R.id.board_4,R.id.board_5,
                R.id.board_6,R.id.board_7,R.id.board_8,R.id.board_9,R.id.board_10,R.id.board_11,
                R.id.board_12,R.id.board_13,R.id.board_14,R.id.board_15,R.id.board_16};
        for(int i = 0; i<allBoard; i++) {
            boardViewHolder[i] = findViewById(boardIDs[i]);
            changedBoxes[i] = outOfBoard;
        }
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                intent.putExtra(recordKey, record.getText().toString());
                startActivity(intent);
                reset.dismiss();
            }
        });
    }
}