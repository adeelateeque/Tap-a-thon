package com.zhideel.tapathon.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.*;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import com.sec.android.allshare.ServiceConnector;
import com.sec.android.allshare.ServiceProvider;
import com.sec.android.allshare.screen.ScreenCastManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.zhideel.tapathon.Config;
import com.zhideel.tapathon.R;
import com.zhideel.tapathon.chord.BusEvent;
import com.zhideel.tapathon.chord.GameChord;
import com.zhideel.tapathon.logic.CommunicationBus;
import com.zhideel.tapathon.logic.GameLogicController;

import java.util.LinkedList;
import java.util.List;

public class GamePadActivity extends Activity implements CommunicationBus.BusManager {

    public static final String GAME_NAME = "TAPATHON";
    public static final String CLIENT = "CLIENT";
    public static final String SERVER_NAME = "SERVER_NAME";
    private Bus mBus;
    private GameChord mGameChord;
    private GameLogicController mLogicController;
    private List<CommunicationBus.BusManager> mManagers;
    private boolean mIsClient;
    private ServiceProvider mServiceProvider;
    private ScreenCastManager mManager;
    private Vibrator mVibrator;
    private boolean mAllShareEnabled;
    private Dialog mAllShareDialog;
    private Dialog mNoAllShareCastDialog;
    private boolean continueMusic;
    private GameBoardView gameBoardView;
    private StatsView statsView;
    private ImageView gameEndView;
    private ImageView answerResultView;
    private boolean allShareShownBefore = false;
    public static GamePadActivity instance = null;
    PadView.GameLevel level = PadView.GameLevel.EASY;

    private final BroadcastReceiver mWiFiBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
        if (!Config.isNetworkAvailable()) {
            /*Toast.makeText(GamePadActivity.this, getString(R.string.wifi_disconnected), Toast.LENGTH_LONG).show();
            finish();*/
        }
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game_pad);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            level = (PadView.GameLevel) extras.getSerializable("level");
            if (level == null) {
                level = PadView.GameLevel.EASY;
            }
        }

        gameEndView = (ImageView) findViewById(R.id.game_end_view);
        gameEndView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame();
                gameEndView.setVisibility(View.GONE);
            }
        });
        answerResultView  = (ImageView) findViewById(R.id.answer_result_view);
        Config.slideToTop(answerResultView);

        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        registerWifiStateReceiver();

        final String userName = getSharedPreferences(GameMenuActivity.TAPATHON_PREFERENCES, MODE_PRIVATE).getString(
                GameMenuActivity.USER_NAME_KEY, "");

        mBus = CommunicationBus.getInstance();
        mManagers = new LinkedList<CommunicationBus.BusManager>();
        mManagers.add(this);

        //final Model model = new Model(!mIsClient);

        final Intent intent = getIntent();
        mIsClient = intent.getBooleanExtra(CLIENT, true);

        startGame();

        /*if (mIsClient) {
            final String roomName;
            btnStart.setVisibility(View.GONE);
            roomName = getIntent().getStringExtra(SERVER_NAME);
            mGameChord = new ClientGameChord(this, roomName, GAME_NAME, userName);
        } else {
            tvWaiting.setVisibility(View.GONE);
            roomName = getIntent().getStringExtra(SERVER_NAME);
            mGameChord = new ServerGameChord(this, roomName, GAME_NAME, userName);*/

            //mManager.activateManagerUI();

            //for host to setup AllShare
            ServiceConnector.createServiceProvider(this, new ServiceConnector.IServiceConnectEventListener() {

                @Override
                public void onCreated(ServiceProvider sprovider, ServiceConnector.ServiceState state) {
                    mServiceProvider = sprovider;
                    mManager = sprovider.getScreenCastManager();
                    if (mManager != null) {
                        mManager.setScreenCastEventListener(new ScreenCastManager.IScreenCastEventListener() {

                            @Override
                            public void onStopped(ScreenCastManager screencastmanager) {
                                mAllShareEnabled = false;
                            }

                            @Override
                            public void onStarted(ScreenCastManager screencastmanager) {
                                mAllShareEnabled = true;
                                screencastmanager.setMode(ScreenCastManager.ScreenMode.DUAL);
                            }
                        });
                    }
                }

                @Override
                public void onDeleted(ServiceProvider sprovider) {
                }
            });

            //mLogicController = new GameLogicController(model, getResources());
            //mManagers.add(mLogicController);
        //}

        //mManagers.add(model);
        //mManagers.add(mGameChord);

        for (CommunicationBus.BusManager manager : mManagers) {
            manager.startBus();
        }
    }


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.exit)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent eg = new Intent(GamePadActivity.this, GameMenuActivity.class);
                        startActivity(eg);
                        finish();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    protected void onDestroy() {

        for (CommunicationBus.BusManager manager : mManagers) {
            manager.stopBus();
        }

        if (gameBoardView != null) {
            gameBoardView.stopBus();
        }

        //mGameChord.stopChord();
        /*if (mManager != null) {
            mManager.stop();
        }

        */
        if (mServiceProvider != null) {
            ServiceConnector.deleteServiceProvider(mServiceProvider);
        }

        unregisterReceiver(mWiFiBroadcastReceiver);


        MusicManager.release();

        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void resumeGame() {
        gameBoardView.setPaused(false);
        statsView.setPaused(false);
    }

    public void startGame()
    {
        gameBoardView = new GameBoardView(this, level, (ViewGroup) findViewById(R.id.gameboard_container));
        statsView = new StatsView(this, (ViewGroup) findViewById(R.id.statsboard_container));
        resumeGame();
    }

    //TODO build the image and display out to the TV http://developer.samsung.com/allshare-framework/technical-docs/Sample-View-Controller
    public void receivedSceenshot()
    {
        Bitmap[] parts = new Bitmap[4];
        Bitmap result = Bitmap.createBitmap(parts[0].getWidth() * 2, parts[0].getHeight() * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        for (int i = 0; i < parts.length; i++) {
            canvas.drawBitmap(parts[i], parts[i].getWidth() * (i % 2), parts[i].getHeight() * (i / 2), paint);
        }
    }

    public GameBoardView getGameBoard() {
        return gameBoardView;
    }

    public StatsView getStatsView() {
        return statsView;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!continueMusic) {
            MusicManager.pause();
        }
        if (statsView != null) statsView.setPaused(true);
        if (gameBoardView != null) gameBoardView.setPaused(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        continueMusic = false;
        MusicManager.start(this, MusicManager.MUSIC_MENU);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.level = (PadView.GameLevel) savedInstanceState.getSerializable("STATE_LEVEL");
        statsView.setTime(savedInstanceState.getInt("STATE_TIMER") + 3);
        statsView.tvQuestion.setText(savedInstanceState.getString("STATE_QUESTION"));
        statsView.tvScore.setText(savedInstanceState.getString("STATE_SCORE"));
        resumeGame();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("STATE_SCORE", statsView.tvScore.getText().toString());
        savedInstanceState.putString("STATE_QUESTION", statsView.tvQuestion.getText().toString());
        savedInstanceState.putInt("STATE_TIMER", statsView.getTime());
        savedInstanceState.putSerializable("STATE_LEVEL", level);
    }

    private void registerWifiStateReceiver() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(mWiFiBroadcastReceiver, filter);
    }

    public void showGameEndView() {
        mVibrator.vibrate(1000);
        gameEndView.setVisibility(View.VISIBLE);
    }

    public void flashCorrectAnswerView() {
        answerResultView.setBackgroundResource(R.drawable.correct_answer);
        answerResultView.setVisibility(View.VISIBLE);
        answerResultView.clearAnimation();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Config.slideToTop(answerResultView);
            }
        }, 500);
    }

    public void flashWrongAnswerView() {
        answerResultView.setBackgroundResource(R.drawable.wrong_answer);
        answerResultView.setVisibility(View.VISIBLE);
        answerResultView.clearAnimation();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Config.slideToTop(answerResultView);
            }
        }, 500);
    }

    public void flashNextQuestionView() {
        answerResultView.setBackgroundResource(R.drawable.nextquestion);
        answerResultView.setVisibility(View.VISIBLE);
        answerResultView.clearAnimation();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Config.slideToTop(answerResultView);
            }
        }, 500);
    }

    @Override
    public void startBus() {
        mBus.register(this);
    }

    @Override
    public void stopBus() {
        mBus.unregister(this);
    }

    @Subscribe
    public void handleGameEnd(GameActivityEvent.GameEndEvent gameEndEvent) {
    }

    @Subscribe
    public void handleGameStart(GameActivityEvent.GameStartEvent gameStartEvent) {

    }

    public static class GameActivityEvent extends BusEvent {

        private static final long serialVersionUID = 20130326L;

        GameActivityEvent() {
            super();
        }

        public static class GameStartEvent extends GameActivityEvent {

            private static final long serialVersionUID = 20130327L;

            public GameStartEvent() {
                super();
            }

        }

        public static class GameEndEvent extends GameActivityEvent {

            private static final long serialVersionUID = 20130326L;

            public GameEndEvent() {
                super();
            }

        }
    }

}
