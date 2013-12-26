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
    private static boolean isContinue = true;
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

    public static void setContinue(boolean cont) {
        isContinue = cont;
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
        textPaint.setTextSize(100);

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
                if ((!isSelected) && (isContinue)) {
                    MultiTouchView.this.setBackgroundColor(colors[randInt(0, 3)]);
                } else if (isContinue) {
                    MultiTouchView.this.setBackgroundColor(Color.WHITE);
                } else {
                    MultiTouchView.this.setBackgroundColor(Color.BLACK);
                }
                invalidate();
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
        if ((!isSelected) && (isContinue)) {
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
        // get pointer index from the event object
        int pointerIndex = event.getActionIndex();

        // get pointer ID
        int pointerId = event.getPointerId(pointerIndex);

        // get masked (not specific to a pointer) action
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
                            //Toast.makeText(getContext(), Integer.toString(result), Toast.LENGTH_SHORT).show();
                            ((GamePadActivity) super.getContext()).getStatsView().resetCurrent();
                        }
                    }

                } catch (NumberFormatException e) {
                    String cOperator = currentText;
                    if (operator == null) {
                        ((GamePadActivity) super.getContext()).getStatsView().setOperator(cOperator);
                        this.isSelected = true;
                        if (operands.size() == 2) {
                            int result = ((GamePadActivity) super.getContext()).getStatsView().doCalc();
                            //Toast.makeText(getContext(), Integer.toString(result), Toast.LENGTH_SHORT).show();
                            ((GamePadActivity) super.getContext()).getStatsView().resetCurrent();
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
        canvas.drawText(currentText, this.getWidth() / 2 - 20, this.getHeight() / 2 + 40, textPaint);
    }

}