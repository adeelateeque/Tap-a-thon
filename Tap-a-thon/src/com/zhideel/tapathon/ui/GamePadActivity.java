package com.zhideel.tapathon.ui;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.*;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.Toast;
import com.sec.android.allshare.ServiceConnector;
import com.sec.android.allshare.ServiceProvider;
import com.sec.android.allshare.screen.ScreenCastManager;
import com.squareup.otto.Bus;
import com.zhideel.tapathon.R;
import com.zhideel.tapathon.chord.ClientGameChord;
import com.zhideel.tapathon.chord.GameChord;
import com.zhideel.tapathon.chord.ServerGameChord;
import com.zhideel.tapathon.logic.CommunicationBus;
import com.zhideel.tapathon.logic.GameLogicController;
import com.zhideel.tapathon.logic.Model;
import com.zhideel.tapathon.utils.BitmapCache;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

//TODO add double tap listener
//TODO add long tap listener
public class GamePadActivity extends Activity implements CommunicationBus.BusManager{
	
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
    private BitmapCache mMemoryCache;
    private Vibrator mVibrator;
    private boolean mAllShareEnabled;
    private Dialog mAllShareDialog;
    private Dialog mNoAllShareCastDialog;
	private boolean continueMusic;
    private GameBoardView gameBoardView;


    private final BroadcastReceiver mWiFiBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final WifiInfo info = (WifiInfo) intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
            if (info == null) {
                finish();
                Toast.makeText(GamePadActivity.this, getString(R.string.wifi_disconnected), Toast.LENGTH_LONG).show();
            }
        }

    };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_game_pad);

        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        registerWifiStateReceiver();

        final int memClass = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        final int cacheSize = 1024 * 1024 * memClass / 8;
        mMemoryCache = new BitmapCache(cacheSize, getAssets());

        final String userName = getSharedPreferences(GameMenuActivity.POKER_PREFERENCES, MODE_PRIVATE).getString(
                GameMenuActivity.USER_NAME_KEY, "");

        mBus = CommunicationBus.getInstance();
        mManagers = new LinkedList<CommunicationBus.BusManager>();
        mManagers.add(this);

        final Model model = new Model(!mIsClient);

        final Intent intent = getIntent();
        mIsClient = intent.getBooleanExtra(CLIENT, false);

        final String roomName;

        mAllShareDialog = new AlertDialog.Builder(GamePadActivity.this).setMessage(R.string.all_share_dialog_message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mManager.activateManagerUI();
                    }
                }).setNegativeButton(R.string.all_share_dialog_exit, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).create();

        mNoAllShareCastDialog = new AlertDialog.Builder(GamePadActivity.this)
                .setMessage(R.string.no_all_share_cast_dialog).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).create();

        if (mIsClient) {
            roomName = getIntent().getStringExtra(SERVER_NAME);
            mGameChord = new ClientGameChord(this, roomName, GAME_NAME, userName);
            //mStartGame.setVisibility(View.GONE);
        } else {
            roomName = getString(R.string.room).concat(UUID.randomUUID().toString().substring(0, 3));
            mGameChord = new ServerGameChord(this, roomName, GAME_NAME, userName);

            ServiceConnector.createServiceProvider(this, new ServiceConnector.IServiceConnectEventListener() {

                @Override
                public void onCreated(ServiceProvider sprovider, ServiceConnector.ServiceState state) {
                    mServiceProvider = sprovider;
                    mManager = sprovider.getScreenCastManager();
                    if (mManager != null) {
                        mNoAllShareCastDialog.dismiss();
                        mAllShareDialog.show();
                        mManager.setScreenCastEventListener(new ScreenCastManager.IScreenCastEventListener() {

                            @Override
                            public void onStopped(ScreenCastManager screencastmanager) {
                                mAllShareEnabled = false;
                                mAllShareDialog.show();
                            }

                            @Override
                            public void onStarted(ScreenCastManager screencastmanager) {
                                mAllShareEnabled = true;
                                mAllShareDialog.dismiss();
                                screencastmanager.setMode(ScreenCastManager.ScreenMode.DUAL);
                            }
                        });

                        showGameDisplay();

                    }
                }

                @Override
                public void onDeleted(ServiceProvider sprovider) {
                }
            });

            mLogicController = new GameLogicController(model, getResources());
            mManagers.add(mLogicController);
        }

        //roomNameView.setText(roomName);
        mManagers.add(model);
        mManagers.add(mGameChord);

        for (CommunicationBus.BusManager manager : mManagers) {
            manager.startBus();
        }
	}

    public GameBoardView getGameBoard()
    {
        return gameBoardView;
    }

    private void showGameDisplay() {
        MultiTouchView.GameLevel level = null;
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            level = (MultiTouchView.GameLevel) extras.getSerializable("level");
            if(level == null)
            {
                level = MultiTouchView.GameLevel.EASY;
            }
        }


        gameBoardView = new GameBoardView(this, level,(ViewGroup)findViewById(R.id.gameboard_container));
    }


	@Override
	protected void onPause() {
		super.onPause();
		if (!continueMusic) {
			MusicManager.pause();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		continueMusic = false;
		MusicManager.start(this, MusicManager.MUSIC_MENU);
	}

    private void registerWifiStateReceiver() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(mWiFiBroadcastReceiver, filter);
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

class PadAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<MultiTouchView> tappads;

	public PadAdapter(Context c) {
		mContext = c;
		tappads = new ArrayList<MultiTouchView>();
	}

	public int getCount() {
		return 9;
	}

	public Object getItem(int position) {
		return tappads.get(position);
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		MultiTouchView tappad;
		tappad = new MultiTouchView(mContext, null);
		tappad.setLayoutParams(new GridView.LayoutParams(parent.getWidth() / 3,
				parent.getHeight() / 3 - 7));
		tappad.setAlpha(0.4f);
		return tappad;
	}

}