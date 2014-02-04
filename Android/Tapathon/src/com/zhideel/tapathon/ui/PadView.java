package com.zhideel.tapathon.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.zhideel.tapathon.Config;
import com.zhideel.tapathon.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class PadView extends LinearLayout {

    public enum GameLevel {
        EASY, MEDIUM, HARD;
    }

    public static GameLevel selectedLevel;

    private static final Random rand = new Random();
    private Paint padPaint;
    private int[] colors = {getResources().getColor(R.color.tappad_cyan), Color.MAGENTA, getResources().getColor(R.color.tappad_red), getResources().getColor(R.color.tappad_yellow)};

    private boolean isSelected = false;
    private boolean isWhite = false;
    private boolean isPaused = false;

    private String currentSymbol;
    private boolean isFirstPaint = false;

    //Combo states
    private boolean isDividedByTwo;
    private boolean isMultipliedByTwo;
    private boolean isReversed;

    private TextView tvSymbol;

    public static int maxNextQuestionDelay;
    private int randomMinDelay, randomMaxDelay;

    private boolean startGame = false;

    private static final SymbolSet symbolSet = new SymbolSet();

    private StatsView statsView;

    private GestureDetector doubleTapDetector;

    public static void setLevel(GameLevel level) {
        selectedLevel = level;
    }

    public PadView(Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_pad, this, true);
        tvSymbol = (TextView) findViewById(R.id.pad_symbol);
        doubleTapDetector = new GestureDetector(context, new DoubleTapDetector());
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                return doubleTapDetector.onTouchEvent(event);
            }
        });
        isPaused = true;
        initView();
    }

    public void setPaused(boolean paused) {
        this.isPaused = paused;
        if (isPaused == false) {
            isFirstPaint = true;
            randomPaint();
        }
    }

    private void initView() {
        padPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        padPaint.setColor(Color.WHITE);
        padPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        Typeface fontFace = Typeface.createFromAsset(Config.context.getAssets(), "Crayon.ttf");
        Typeface face = Typeface.create(fontFace, Typeface.BOLD);

        tvSymbol.setTextColor(Color.WHITE);
        tvSymbol.setTypeface(face);
        tvSymbol.setShadowLayer(5.0f, 5.0f, 5.0f, Color.BLACK);
        tvSymbol.setTextSize(75);

        if (startGame == true) {
            randomMinDelay = 0;
            randomMaxDelay = 0;
            startGame = false;
        } else {
            if (selectedLevel == GameLevel.EASY) {
                maxNextQuestionDelay = 16000;
                randomMinDelay = 4000;
                randomMaxDelay = 6000;
            } else if (selectedLevel == GameLevel.MEDIUM) {
                maxNextQuestionDelay = 12000;
                randomMinDelay = 3500;
                randomMaxDelay = 5000;
            } else if (selectedLevel == GameLevel.HARD) {
                maxNextQuestionDelay = 8000;
                randomMinDelay = 3000;
                randomMaxDelay = 4000;
            }
        }
        statsView = ((GamePadActivity) super.getContext()).getStatsView();
        setCurrentSymbol("1");
    }

    private void randomPaint() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                isFirstPaint = false;
                doThePaint();
                //As long as we are not paused we can keep painting randomly
                if (isPaused == false) {
                    new RandomSymbolGeneratorTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    randomPaint();
                }
            }
        }, isFirstPaint ? 0 : getRandomDelay());
    }

    private void doThePaint() {
        animateSymbol();

        if ((!isSelected) && (!isPaused)) {
            PadView.this.isWhite = false;
            tvSymbol.setTextColor(colors[randInt(0, 3)]);
        } else if (isSelected && !PadView.this.isWhite) {
            PadView.this.isWhite = true;
            PadView.this.setBackgroundColor(Color.WHITE);
            PadView.this.setAlpha(0.5f);
            tvSymbol.setTextColor(getResources().getColor(R.color.tappad_green));
        }
    }

    private void animateSymbol() {
        AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setDuration(500);
        anim.setRepeatCount(1);
        anim.setRepeatMode(Animation.REVERSE);
        tvSymbol.startAnimation(anim);
    }

    private int getRandomDelay() {
        return randInt(1, randInt(1, 3)) * randInt(0, 1) < 0.5 ? randomMinDelay : randomMaxDelay;
    }

    public int randInt(int min, int max) {
        return rand.nextInt((max - min) + 1) + min;
    }

    private void randSymbol() {
        if ((!isSelected) && (!isPaused)) {
            symbolSet.remove(getOriginalSymbol());
            String newSymbol;
            int rand = randInt(1, 13);
            if (rand == 10) {
                newSymbol = "+";
            } else if (rand == 11) {
                newSymbol = "-";
            } else if (rand == 12) {
                newSymbol = "X";
            } else if (rand == 13) {
                newSymbol = "/";
            } else {
                newSymbol = Integer.toString(rand);
            }

            if (!newSymbol.equals(getOriginalSymbol()) && symbolSet.add(newSymbol)) {
                setCurrentSymbol(newSymbol);
            } else {
                randSymbol();
            }
        }
    }

    private boolean canBeSelected() {
        if (statsView.getOperands().size() < 2 && !isOperator(currentSymbol)) {
            return true;
        } else if (statsView.getOperator() == null && isOperator(currentSymbol)) {
            return true;
        }

        return false;
    }


    private void divideByTwo() {
        if (!isDividedByTwo && !isOperator(currentSymbol)) {
            isDividedByTwo = true;
            isMultipliedByTwo = false;
            setCurrentSymbol(formatDecimals((Float.parseFloat(currentSymbol) / 2)));
            calculate();
        }
    }

    private void multiplyByTwo() {
        if (!isMultipliedByTwo && !isOperator(currentSymbol)) {
            isMultipliedByTwo = true;
            isDividedByTwo = false;
            setCurrentSymbol(formatDecimals(Float.parseFloat(currentSymbol) * 2));
            calculate();
        }
    }

    private void reverseSymbol() {
        isReversed = !isReversed;
        setCurrentSymbol(reversedOperator(currentSymbol));
    }

    private String reversedOperator(String operator) {
        if (operator.equals("X")) {
            return "/";
        } else if (operator.equals("/")) {
            return "X";
        } else if (operator.equals("+")) {
            return "-";
        } else if (operator.equals("-")) {
            return "+";
        }

        return operator;
    }

    private String formatDecimals(float number) {
        if (number == (int) number)
            return String.format("%d", (int) number);
        else
            return String.format("%s", number);
    }

    private void calculate() {
        ArrayList<Float> operands = statsView.getOperands();
        String operator = statsView.getOperator();

        try {
            float number = Float.parseFloat(currentSymbol);
            if (operands.size() < 2) {
                statsView.addOperand(number);
                if ((operands.size() == 2) && (operator != null)) {
                    statsView.doCalc();
                    statsView.newQuestion();
                }
            }

        } catch (NumberFormatException e) {
            if (isOperator(currentSymbol)) {
                statsView.setOperator(currentSymbol);
                if (operands.size() == 2) {
                    statsView.doCalc();
                    statsView.newQuestion();
                }
            }
        }
    }

    private void resetCombos() {
        setCurrentSymbol(getOriginalSymbol());
        isDividedByTwo = false;
        isMultipliedByTwo = false;
    }

    private String getOriginalSymbol() {
        if (!isOperator(currentSymbol)) {
            if (isDividedByTwo) {
                return formatDecimals(Float.parseFloat(currentSymbol) * 2);
            } else if (isMultipliedByTwo) {
                return formatDecimals(Float.parseFloat(currentSymbol) / 2);
            }
        } else {
            if (isReversed) {
                return reversedOperator(currentSymbol);
            }
        }

        return currentSymbol;
    }

    private boolean isOperator(String symbol) {
        return symbol.equals("X") || symbol.equals("/") || symbol.equals("+") || symbol.equals("-");
    }

    private static class SymbolSet extends CopyOnWriteArrayList<String> {
        @Override
        public boolean add(String symbol) {

            boolean canAdd = false;
            if (symbol.equals("X") && !this.contains(symbol)) {
                canAdd = true;
            } else if (symbol.equals("/") && !this.contains(symbol)) {
                canAdd = true;
            } else if (symbol.equals("+") && !this.contains(symbol)) {
                canAdd = true;
            } else if (symbol.equals("-") && !this.contains(symbol)) {
                canAdd = true;
            } else {
                if (Collections.frequency(this, symbol) < 2) {
                    canAdd = true;
                }
            }

            if (canAdd) {
                return super.add(symbol);
            } else {
                return !canAdd;
            }
        }
    }


    private void setCurrentSymbol(String symbol) {
        //if nothing has changed, don't need to animate
        if (this.currentSymbol == null || !this.currentSymbol.equals(symbol)) {
            currentSymbol = symbol;
            post(new Runnable() {
                public void run() {
                    animateSymbol();
                    tvSymbol.setText(currentSymbol);
                }
            });
        }
    }


    private class DoubleTapDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            //Always play a sound
            MediaPlayer mp = MediaPlayer.create(Config.context, R.raw.tap);
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }

            });
            mp.start();

            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            if (!isSelected && canBeSelected()) {
                isSelected = true;
                calculate();
            } else if (isSelected && !isOperator(currentSymbol)) {
                if(isDividedByTwo || isMultipliedByTwo)
                {
                    resetCombos();
                }
            } else {
                if(isOperator(currentSymbol) && !isReversed){
                    reverseSymbol();
                }
            }
            doThePaint();

            return true;
        }

        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {

            if (!isSelected && canBeSelected()) {
                isSelected = true;
                doThePaint();
            }
            if (!isOperator(currentSymbol)) {
                if (!isMultipliedByTwo) {
                    PadView.this.multiplyByTwo();
                } else {
                    resetCombos();
                }
            } else {
                reverseSymbol();
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (!isSelected && canBeSelected()) {
                isSelected = true;
                doThePaint();
            }
            if (!isOperator(currentSymbol)) {
                if (!isDividedByTwo) {
                    PadView.this.divideByTwo();
                } else {
                    resetCombos();
                }
            } else {
                reverseSymbol();
            }
        }
    }


    private class RandomSymbolGeneratorTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPostExecute(Void result) {
            doThePaint();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            randSymbol();
            return null;
        }
    }


}