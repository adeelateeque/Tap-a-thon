package com.zhideel.tapathon.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

public class MultiTouchView extends View {

    public enum GameLevel {
        EASY, MEDIUM, HARD;
    }

    // Usually this can be a field rather than a method variable
    private static Random rand = new Random();
    private Paint mPaint;
    private int[] colors = {Color.BLUE, Color.MAGENTA, Color.RED, Color.YELLOW};
    private boolean isSelected;
    private boolean isPaused = false;
    private Paint textPaint;
    private String currentText;
    private static GameLevel selectedLevel;
    private int minDelay, maxDelay;
    private boolean startGame = false;

    public MultiTouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
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
        // set painter color to a color you like
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setShadowLayer(5.0f, 5.0f, 5.0f, Color.BLACK);
        textPaint.setTextSize(150);

        if (startGame == true) {
            minDelay = 0;
            maxDelay = 0;
            startGame = false;
        } else {
            if (selectedLevel == GameLevel.EASY) {
                minDelay = 4000;
                maxDelay = 6000;
            } else if (selectedLevel == GameLevel.MEDIUM) {
                minDelay = 3500;
                maxDelay = 4500;
            } else if (selectedLevel == GameLevel.HARD) {
                minDelay = 2000;
                maxDelay = 3500;
            }
        }

        randomPaint();
        randText();
    }

    private void randomPaint() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if ((!isSelected) && (!isPaused)) {
                    textPaint.setColor(colors[randInt(0, 3)]);
                } else if (isPaused) {
                    MultiTouchView.this.setBackgroundColor(Color.WHITE);
                    textPaint.setColor(Color.GREEN);
                } else {
                    MultiTouchView.this.setBackgroundColor(Color.BLACK);
                }
                invalidate();
                //As long as we are not paused we can keep painting randomly
                if (isPaused == false) {
                    randomPaint();
                    randText();
                }
            }
        }, getRandomDelay());
    }

    private int getRandomDelay() {
        return randInt(1, randInt(1, 3)) * randInt(0, 1) < 0.5 ? minDelay : maxDelay;
    }

    public int randInt(int min, int max) {
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    private void randText() {
        if ((!isSelected) && (!isPaused)) {
            int rand = randInt(0, 13);
            if (rand == 10) {
                currentText = "+";
            } else if (rand == 11) {
                currentText = "-";
            } else if (rand == 12) {
                currentText = "X";
            } else if (rand == 13) {
                currentText = "/";
            } else {
                currentText = Integer.toString(rand);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int maskedAction = event.getActionMasked();

        switch (maskedAction) {

            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN: {
                ArrayList<Integer> operands = ((GamePadActivity) super.getContext()).getStatsView().getOperands();
                String operator = ((GamePadActivity) super.getContext()).getStatsView().getOperator();

                try {
                    int number = Integer.parseInt(currentText);
                    if (operands.size() < 2) {
                        ((GamePadActivity) super.getContext()).getStatsView().addOperand(number);
                        this.isSelected = true;
                        if ((operands.size() == 2) && (operator != null)) {
                            int result = ((GamePadActivity) super.getContext()).getStatsView().doCalc();
                            ((GamePadActivity) super.getContext()).getStatsView().newQuestion();
                        }
                    }

                } catch (NumberFormatException e) {
                    if (operator == null) {
                        ((GamePadActivity) super.getContext()).getStatsView().setOperator(currentText);
                        this.isSelected = true;
                        if (operands.size() == 2) {
                            int result = ((GamePadActivity) super.getContext()).getStatsView().doCalc();
                            ((GamePadActivity) super.getContext()).getStatsView().newQuestion();
                        }
                    }
                }
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
        invalidate();

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText(currentText, this.getWidth() / 2 - 30, this.getHeight() / 2 + 50, textPaint);
    }

}