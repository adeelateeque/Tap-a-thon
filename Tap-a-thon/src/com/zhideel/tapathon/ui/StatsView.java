package com.zhideel.tapathon.ui;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.squareup.otto.Bus;
import com.zhideel.tapathon.R;
import com.zhideel.tapathon.Stopwatch;
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
    private Stopwatch stopwatch;
    private Random rand = new Random();
    private int randomQuestion, correctAnswerCount, totalQuestions;
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
        tvScore.setText("0%");
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
                                if(stopwatch.elapsed() >= MultiTouchView.maxNextQuestionDelay)
                                {
                                   newQuestion();
                                }
                            }

                            else {
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
        Integer result;
        try{
            if (operator.equalsIgnoreCase("X")) {
                result = op1 * op2;
            } else if (operator.equalsIgnoreCase("/")) {
                result = op1 / op2;
            } else if (operator.equalsIgnoreCase("-")) {
                result = op1 - op2;
            } else {
                result = op1 + op2;
            }
        }
        catch (ArithmeticException e)
        {
          result = 0;
        }

        //If answered correctly
        if (result == randomQuestion) {
            congratulate();
            int currentScore = Integer.parseInt(tvScore.getText().toString().replace("%", ""));
            int elapsedTime = stopwatch.elapsed();
            int reward = Math.round(((((float) MultiTouchView.maxNextQuestionDelay - elapsedTime)/MultiTouchView.maxNextQuestionDelay * 100) + ((float) correctAnswerCount / totalQuestions * 100)) / 200 * 100);
            currentScore = Integer.valueOf(Math.round(((float)currentScore + reward) / 200 * 100));
            tvScore.setText(Integer.toString(currentScore) + "%");
            correctAnswerCount++;
            newQuestion();
        }
        else
        {
            criticize();
        }
        return result;
    }

    private void congratulate()
    {
        MediaPlayer mp = MediaPlayer.create(mContext, R.raw.correct_answer);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }

        });
        mp.start();
        Vibrator v = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);

        // Start without a delay
        // Each element then alternates between vibrate, sleep, vibrate, sleep...
        long[] pattern = {0, 150, 150, 500};

        // The '-1' here means to vibrate once
        // '0' would make the pattern vibrate indefinitely
        v.vibrate(pattern, -1);

        ((GamePadActivity) mContext).flashCorrectAnswerView();
    }

    private void criticize()
    {
        MediaPlayer mp = MediaPlayer.create(mContext, R.raw.wrong_answer);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }

        });
        mp.start();
        Vibrator v = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(500);
        ((GamePadActivity) mContext).flashWrongAnswerView();
    }

    public void newQuestion() {
        operands.clear();
        operator = null;
        randomQuestion = randInt(0, 20);
        tvQuestion.setText(Integer.toString(randomQuestion));
        stopwatch = Stopwatch.start();
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
