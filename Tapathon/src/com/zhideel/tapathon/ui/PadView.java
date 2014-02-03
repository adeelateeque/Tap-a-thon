package com.zhideel.tapathon.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.AsyncTask;
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
import java.util.concurrent.CopyOnWriteArrayList;

public class PadView extends View {

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

    private Paint symbolPaint;
    private String symbol;
    private String currentSymbol;
    private boolean isFirstPaint = false;

    //Combo states
    private boolean isDividedByTwo;
    private boolean isMultipliedByTwo;

    public static int maxNextQuestionDelay;
    private int randomMinDelay, randomMaxDelay;

    private boolean startGame = false;

    private static final SymbolSet symbolSet = new SymbolSet();

    private GestureDetector doubleTapDetector;

    public static void setLevel(GameLevel level) {
        selectedLevel = level;
    }

    public PadView(Context context, AttributeSet attrs) {
        super(context, attrs);
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

        symbolPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        symbolPaint.setColor(Color.WHITE);
        symbolPaint.setTypeface(face);
        symbolPaint.setShadowLayer(5.0f, 5.0f, 5.0f, Color.BLACK);
        symbolPaint.setTextSize(Config.getDipfromPixels(75));

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
        symbol = "";
        currentSymbol = symbol;
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
        if ((!isSelected) && (!isPaused)) {
            PadView.this.isWhite = false;
            symbolPaint.setColor(colors[randInt(0, 3)]);
            invalidate();
        } else if (isSelected && !PadView.this.isWhite) {
            PadView.this.isWhite = true;
            PadView.this.setBackgroundColor(Color.WHITE);
            PadView.this.setAlpha(0.5f);
            symbolPaint.setColor(getResources().getColor(R.color.tappad_green));
            invalidate();
        }
    }

    private int getRandomDelay() {
        return randInt(1, randInt(1, 3)) * randInt(0, 1) < 0.5 ? randomMinDelay : randomMaxDelay;
    }

    public int randInt(int min, int max) {
        return rand.nextInt((max - min) + 1) + min;
    }

    private void randSymbol() {
        symbolSet.remove(symbol);

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

            if (!newSymbol.equals(symbol) && symbolSet.add(newSymbol)) {
                symbol = newSymbol;
                currentSymbol = newSymbol;
            } else {
                randSymbol();
            }
        }
    }


    private void divideByTwo() {
        if (!isDividedByTwo && !isOperator(currentSymbol)) {
            isDividedByTwo = true;
            currentSymbol = formatDecimals((Float.parseFloat(currentSymbol) / 2));
            invalidate();
            calculate();
        }
    }

    private void multiplyByTwo() {
        if (!isMultipliedByTwo && !isOperator(currentSymbol)) {
            isMultipliedByTwo = true;
            currentSymbol = formatDecimals(Float.parseFloat(currentSymbol) * 2);
            invalidate();
            calculate();
        }
    }

    private String formatDecimals(float number) {
        if (number == (int) number)
            return String.format("%d", (int) number);
        else
            return String.format("%s", number);
    }

    private void calculate() {
        ArrayList<Float> operands = ((GamePadActivity) super.getContext()).getStatsView().getOperands();
        String operator = ((GamePadActivity) super.getContext()).getStatsView().getOperator();

        if (isDividedByTwo) {
            operands.remove(Float.parseFloat(currentSymbol) * 2);
        } else if (isMultipliedByTwo) {
            operands.remove(Float.parseFloat(currentSymbol) / 2);
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
            if (operator == null && isOperator(currentSymbol)) {
                ((GamePadActivity) super.getContext()).getStatsView().setOperator(currentSymbol);
                this.isSelected = true;
                if (operands.size() == 2) {
                    ((GamePadActivity) super.getContext()).getStatsView().doCalc();
                    ((GamePadActivity) super.getContext()).getStatsView().newQuestion();
                }
            }
        }
    }

    private void resetCombos() {
        currentSymbol = symbol;
        isDividedByTwo = false;
        isMultipliedByTwo = false;
    }

    private boolean isOperator(String symbol) {
        return symbol.equals("X") || symbol.equals("/") || symbol.equals("+") || symbol.equals("-");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int xPos = (int) ((canvas.getWidth() / 2) - symbolPaint.measureText(currentSymbol) / 2);
        int yPos = (int) ((canvas.getHeight() / 2) - ((symbolPaint.descent() + symbolPaint.ascent()) / 2)) + Config.getDipfromPixels(12);
        canvas.drawText(currentSymbol, xPos, yPos, symbolPaint);
        invalidate();
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
            if (!isSelected && e.getPointerCount() == 1) {
                calculate();
            } else if (e.getPointerCount() == 2) {
                if (!isMultipliedByTwo) {
                    multiplyByTwo();
                } else {
                    resetCombos();
                }
            } else if (!isDividedByTwo) {
                divideByTwo();
            } else {
                resetCombos();
            }
            doThePaint();
            return true;
        }

        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (!isDividedByTwo) {
                PadView.this.divideByTwo();
            } else {
                resetCombos();
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (!isMultipliedByTwo) {
                PadView.this.multiplyByTwo();
            } else {
                resetCombos();
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