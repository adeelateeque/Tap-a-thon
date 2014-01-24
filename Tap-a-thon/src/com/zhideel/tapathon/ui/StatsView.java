package com.zhideel.tapathon.ui;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.squareup.otto.Bus;
import com.zhideel.tapathon.R;
import com.zhideel.tapathon.logic.CommunicationBus;
import com.zhideel.tapathon.logic.GameLogicController;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Adeel on 15/11/13.
 */
public class StatsView implements CommunicationBus.BusManager {
    private Activity mContext;
    private final Bus mBus;
    private boolean isPaused = false;
    private ArrayList<Integer> operands;
    private String operator;
    private TextView tvScore, tvQuestion, tvTimer;
    private Random rand = new Random();
    private int randomQns, correctAnswerCount, totalQuestions;
    private int interval = 60;

    public StatsView(Context context, ViewGroup viewGroup) {
        mContext = (Activity) context;
        this.mBus = CommunicationBus.getInstance();

        View.inflate(mContext, R.layout.view_stats, viewGroup);

        operands = new ArrayList<Integer>();
        tvScore = (TextView) viewGroup.findViewById(R.id.tv_multipler);
        tvQuestion = (TextView) viewGroup.findViewById(R.id.tv_qns);
        tvTimer = (TextView) viewGroup.findViewById(R.id.tv_timer);
        timer();
        correctAnswerCount = 0;
        totalQuestions = 0;
        tvScore.setText("0");
        newQuestion();
    }

    public void setInterval(int interval) {
        tvTimer.setText(Integer.toString(interval));
    }

    private void timer() {
        final Timer time = new Timer();
        time.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (isPaused == false) {
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (interval != 0) {

                                interval--;
                                setInterval(interval);

                            } else {
                                ((GamePadActivity) mContext).getGameBoard().pauseBoard(true);
                                time.cancel();
                                ((GamePadActivity) mContext).showGameEndView();
                                mBus.post(GameLogicController.EndGameEvent.INSTANCE);
                            }
                        }

                    });
                }
            }
        }, 0, 1000);
    }

    public int randInt(int min, int max) {
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    public void addOperand(Integer operand) {
        if (operands.size() < 2) {
            operands.add(operand);
        }
    }

    public ArrayList<Integer> getOperands() {
        return operands;
    }

    public void setOperator(String operator) {
        if (this.operator == null) {
            this.operator = operator;
        }
    }

    public String getOperator() {
        return this.operator;
    }

    public int doCalc() {
        Integer op1 = operands.get(0);
        Integer op2 = operands.get(1);
        Integer result = 0;
        if (operator.equalsIgnoreCase("X")) {
            result = op1 * op2;
        } else if (operator.equalsIgnoreCase("/")) {
            result = op1 / op2;
        } else if (operator.equalsIgnoreCase("-")) {
            result = op1 - op2;
        } else {
            result = op1 + op2;
        }

        if (result == randomQns) {
            double multiplier = Double.parseDouble(tvScore.getText().toString());
            multiplier = Double.valueOf(multiplier + 1);
            tvScore.setText(Double.toString(multiplier));
            correctAnswerCount++;
            newQuestion();
        }
        return result;
    }

    public void newQuestion() {
        randomQns = randInt(0, 20);
        tvQuestion.setText(Integer.toString(randomQns));
        operands.clear();
        operator = null;
        totalQuestions++;
        ((GamePadActivity) mContext).getGameBoard().resetBoard();
    }

    public void setPaused(Boolean paused) {
        this.isPaused = paused;
        if (isPaused == false) {
            timer();
        }
    }


    @Override
    public void startBus() {
        mBus.register(this);
    }

    @Override
    public void stopBus() {
        mBus.unregister(this);
    }
}
