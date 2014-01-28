package com.zhideel.tapathon.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import com.zhideel.tapathon.Config;
import com.zhideel.tapathon.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class PadView extends View {

    public enum GameLevel {
        EASY, MEDIUM, HARD;
    }

    // Usually this can be a field rather than a method variable
    private Random rand = new Random();
    private Paint mPaint;
    private int[] colors = {getResources().getColor(R.color.tappad_cyan), Color.MAGENTA, getResources().getColor(R.color.tappad_red), getResources().getColor(R.color.tappad_yellow)};
    private boolean isSelected = false;
    private boolean isPaused = false;
    private Paint textPaint;
    private String currentSymbol;
    private boolean isDividedByTwo;
    public static GameLevel selectedLevel;
    public static int maxNextQuestionDelay;
    private int minDelay, maxDelay;
    private boolean startGame = false;
    private static final SymbolSet symbolSet = new SymbolSet();
    private GestureDetector doubleTapDetector;

    public PadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        doubleTapDetector = new GestureDetector(context, new DoubleTapDetector());
        initView();
    }

    public static void setLevel(GameLevel level) {
        selectedLevel = level;
    }

    public void setPaused(boolean paused) {
        this.isPaused = paused;
        if (isPaused == false) {
            randomPaint();
        }
    }

    private void initView() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        Typeface fontFace = Typeface.createFromAsset(Config.context.getAssets(), "Crayon.ttf");
        Typeface face = Typeface.create(fontFace, Typeface.BOLD);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTypeface(face);
        textPaint.setShadowLayer(5.0f, 5.0f, 5.0f, Color.BLACK);
        textPaint.setTextSize(Config.getDipfromPixels(75));

        if (startGame == true) {
            minDelay = 0;
            maxDelay = 0;
            startGame = false;
        } else {
            if (selectedLevel == GameLevel.EASY) {
                maxNextQuestionDelay = 15000;
                minDelay = 4000;
                maxDelay = 6000;
            } else if (selectedLevel == GameLevel.MEDIUM) {
                maxNextQuestionDelay = 10000;
                minDelay = 3500;
                maxDelay = 4500;
            } else if (selectedLevel == GameLevel.HARD) {
                maxNextQuestionDelay = 5000;
                minDelay = 2000;
                maxDelay = 3500;
            }
        }
        currentSymbol = "1";
        randText();
        doThePaint();
        randomPaint();
    }

    private void randomPaint() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                doThePaint();
                //As long as we are not paused we can keep painting randomly
                if (isPaused == false) {
                    randText();
                    randomPaint();
                }
            }
        }, getRandomDelay());
    }

    private void doThePaint() {
        if ((!isSelected) && (!isPaused)) {
            textPaint.setColor(colors[randInt(0, 3)]);
        } else if (isSelected) {
            PadView.this.setBackgroundColor(Color.WHITE);
            PadView.this.setAlpha(0.5f);
            textPaint.setColor(getResources().getColor(R.color.tappad_green));
        }
        invalidate();
    }

    private int getRandomDelay() {
        return randInt(1, randInt(1, 3)) * randInt(0, 1) < 0.5 ? minDelay : maxDelay;
    }

    public int randInt(int min, int max) {
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    private void randText() {
        symbolSet.remove(currentSymbol);

        if ((!isSelected) && (!isPaused)) {
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

            if (!newSymbol.equals(currentSymbol) && symbolSet.add(newSymbol)) {
                currentSymbol = newSymbol;
            } else {
                randText();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int maskedAction = event.getActionMasked();

        //handle double tap
        doubleTapDetector.onTouchEvent(event);

        switch (maskedAction) {

            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN: {
                if (!isSelected) {
                    MediaPlayer mp = MediaPlayer.create(Config.context, R.raw.tap);
                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.release();
                        }

                    });
                    mp.start();
                    calculate();
                } else {
                    divideByTwo();
                }
                invalidate();
                doThePaint();
                break;
            }
            case MotionEvent.ACTION_MOVE: { // a pointer was moved
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL: {
                break;
            }
        }

        return true;
    }


    private void divideByTwo() {
        if (!isDividedByTwo && !currentSymbol.equals("X") && !currentSymbol.equals("/") && !currentSymbol.equals("+") && !currentSymbol.equals("-")) {
            isDividedByTwo = true;
            currentSymbol = Float.toString((Float.parseFloat(currentSymbol) / 2));
            calculate();
        }
    }

    private void calculate() {
        ArrayList<Float> operands = ((GamePadActivity) super.getContext()).getStatsView().getOperands();
        String operator = ((GamePadActivity) super.getContext()).getStatsView().getOperator();


        if (isDividedByTwo) {
            operands.remove(Float.parseFloat(currentSymbol) * 2);
        }

        try {
            float number = Float.parseFloat(currentSymbol);
            if (operands.size() < 2) {
                ((GamePadActivity) super.getContext()).getStatsView().addOperand(number);
                this.isSelected = true;
                if ((operands.size() == 2) && (operator != null)) {
                    ((GamePadActivity) super.getContext()).getStatsView().doCalc();
                    ((GamePadActivity) super.getContext()).getStatsView().newQuestion();
                }
            }

        } catch (NumberFormatException e) {
            if (operator == null) {
                ((GamePadActivity) super.getContext()).getStatsView().setOperator(currentSymbol);
                this.isSelected = true;
                if (operands.size() == 2) {
                    ((GamePadActivity) super.getContext()).getStatsView().doCalc();
                    ((GamePadActivity) super.getContext()).getStatsView().newQuestion();
                }
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int xPos = (int) ((canvas.getWidth() / 2) - textPaint.measureText(currentSymbol) / 2);
        int yPos = (int) ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2)) + Config.getDipfromPixels(12);
        canvas.drawText(currentSymbol, xPos, yPos, textPaint);
        invalidate();
    }


    private static class SymbolSet extends ArrayList<String> {
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

    private class DoubleTapDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            PadView.this.divideByTwo();
            return true;
        }
    }

}