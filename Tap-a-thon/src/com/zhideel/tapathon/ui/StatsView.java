package com.zhideel.tapathon.ui;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.otto.Bus;
import com.zhideel.tapathon.R;
import com.zhideel.tapathon.logic.CommunicationBus;
import com.zhideel.tapathon.logic.GameLogicController;

import java.text.DecimalFormat;
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
    private ArrayList<Integer> operands;
    private String operator;
    private TextView tvMultipler, tvQns, tvTimer;
    private Random rand = new Random();
    private int randomQns, correctAns;
    private int interval = 60;
    public StatsView(Context context, ViewGroup viewGroup) {
        mContext = (Activity) context;
        this.mBus = CommunicationBus.getInstance();

        View.inflate(mContext, R.layout.view_stats, viewGroup);

        operands = new ArrayList<Integer>();
        tvMultipler = (TextView) viewGroup.findViewById(R.id.tv_multipler);
        tvQns = (TextView) viewGroup.findViewById(R.id.tv_qns);
        tvTimer = (TextView) viewGroup.findViewById(R.id.tv_timer);
        timer();
        correctAns = 0;
        tvMultipler.setText("1");
        randomQns = randInt(0, 20);
        tvQns.setText(Integer.toString(randomQns));
    }

    public void setInterval(int interval){
        tvTimer.setText(Integer.toString(interval));
    }

    private void timer(){
        final Timer time = new Timer();
        time.scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run() {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (interval != 0) {
                            interval--;
                            setInterval(interval);
                        } else {
                            MultiTouchView.setContinue(false);
                            Toast.makeText(mContext, Integer.toString(correctAns), Toast.LENGTH_SHORT).show();
                            time.cancel();
                            mBus.post(GameLogicController.EndGameEvent.INSTANCE);
                        }
                    }
                });
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

    public ArrayList<Integer> getOperands(){
        return operands;
    }

    public void setOperator(String operator) {
        if (this.operator == null) {
            this.operator = operator;
        }
    }

    public String getOperator(){
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

        if (result == randomQns){
            DecimalFormat df = new DecimalFormat("#.0");
            double multipler = Double.parseDouble(tvMultipler.getText().toString());
            multipler = Double.valueOf(df.format(multipler + 0.1));
            tvMultipler.setText(Double.toString(multipler));
            correctAns++;
        }
        return result;
    }

    public void resetCurrent(){
        randomQns = randInt(0, 20);
        tvQns.setText(Integer.toString(randomQns));
        operands.clear();
        operator = null;
        ((GamePadActivity) mContext).getGameBoard().resetBoard();
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
