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
import android.util.Pair;
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
import com.zhideel.tapathon.events.BusEvent;
import com.zhideel.tapathon.logic.*;
import com.zhideel.tapathon.utils.BitmapCache;

import java.util.*;

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


    @Override
    public void onBackPressed() {
        // @formatter:off
        new AlertDialog.Builder(this)
                .setMessage(R.string.exit)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
        // @formatter:on
    }

    @Override
    protected void onDestroy() {

        for (CommunicationBus.BusManager manager : mManagers) {
            manager.stopBus();
        }

        if (gameBoardView != null) {
            gameBoardView.stopBus();
        }

        mGameChord.stopChord();
        if (mManager != null) {
            mManager.stop();
        }

        if (mServiceProvider != null) {
            ServiceConnector.deleteServiceProvider(mServiceProvider);
        }

        unregisterReceiver(mWiFiBroadcastReceiver);

        super.onDestroy();
        mAllShareDialog.dismiss();
        mNoAllShareCastDialog.dismiss();
    }

    public void startGame(View v) {
        mBus.post(GameLogicController.StartGameEvent.INSTANCE);
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
        mNoAllShareCastDialog.dismiss();
        mAllShareDialog.dismiss();
        super.onPause();

	}

	@Override
	protected void onResume() {
		super.onResume();
		continueMusic = false;
		MusicManager.start(this, MusicManager.MUSIC_MENU);

        if (!mIsClient) {
            if (mManager == null) {
                mNoAllShareCastDialog.show();
            } else if (!mAllShareEnabled) {
                mAllShareDialog.show();
            }
        }
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

    public static class GameActivityEvent extends BusEvent {

        private static final long serialVersionUID = 20130326L;

        GameActivityEvent() {
            super();
        }

        public static class YourTurnEvent extends GameActivityEvent {

            private static final long serialVersionUID = 20130326L;

            private final YourTurnType mTurnType;
            private final int mMinimumBidAmount;
            private final int mAmount;

            public YourTurnEvent(YourTurnType turnType, int amount, int minimumBidAmount) {
                super();
                mTurnType = turnType;
                mMinimumBidAmount = minimumBidAmount;
                mAmount = amount;
            }

            public YourTurnType getTurnType() {
                return mTurnType;
            }

            public int getMinimumBidAmount() {
                return mMinimumBidAmount;
            }

            public int getAmount() {
                return mAmount;
            }

            public enum YourTurnType {
                //@formatter:off
                CHECK,
                CALL,
                NONE;
                //@formatter:on

                @Override
                public String toString() {
                    return name();
                };

            }

        }

        public static class TurnEndEvent extends GameActivityEvent {

            private static final long serialVersionUID = 20130326L;

            public TurnEndEvent() {
                super();
            }

        }

        public static class AmountEvent extends GameActivityEvent {

            private static final long serialVersionUID = 20130326L;

            private final int mAmount;
            private final int mBidAmount;
            private final int mMinimumBidAmount;

            public AmountEvent(int amount, int bidAmount, int minimumBidAmount) {
                super();
                mAmount = amount;
                mBidAmount = bidAmount;
                mMinimumBidAmount = minimumBidAmount;
            }

            public int getAmount() {
                return mAmount;
            }

            public int getBidAmount() {
                return mBidAmount;
            }

            public int getMinimumBidAmount() {
                return mMinimumBidAmount;
            }

        }

        public static class TokenEvent extends GameActivityEvent {

            private static final long serialVersionUID = 20130326L;

            private final TokenType mTokenType;

            public TokenEvent(TokenType tokenType) {
                super();
                mTokenType = tokenType;
            }

            public TokenType getTokenType() {
                return mTokenType;
            }

            public enum TokenType {
                //@formatter:off
                SMALL_BLIND,
                BIG_BLIND,
                DEALER_WITH_SMALL_BLIND,
                DEALER,
                NONE;
                //@formatter:on

                @Override
                public String toString() {
                    return name();
                }

            }

        }

        public static class CardsEvent extends GameActivityEvent {

            private static final long serialVersionUID = 20130326L;

            private final transient Pair<Card, Card> mCards;

            public CardsEvent(Pair<Card, Card> cards) {
                super();
                mCards = cards;
            }

            public Pair<Card, Card> getCards() {
                return mCards;
            }

        }

        public static class SitEvent extends GameActivityEvent {

            private static final long serialVersionUID = 20130326L;

            public SitEvent() {
                super();
            }

        }

        public static class StandEvent extends GameActivityEvent {

            private static final long serialVersionUID = 20130326L;

            public StandEvent() {
                super();
            }

        }

        public static class GameEndEvent extends GameActivityEvent {

            private static final long serialVersionUID = 20130326L;

            public GameEndEvent() {
                super();
            }

        }

        public static class TableFullEvent extends GameActivityEvent {

            private static final long serialVersionUID = 20130329L;

            public TableFullEvent() {
                super();
            }

        }

        public static class SittingPlayersChangedEvent extends GameActivityEvent {

            private static final long serialVersionUID = 20130410L;
            private final int mSittingPlayersCount;
            private final ServerModel.GameState mGameState;

            private static final EnumSet<ServerModel.GameState> ONGOING_GAME = EnumSet.of(ServerModel.GameState.FLOP, ServerModel.GameState.PRE_FLOP,
                    ServerModel.GameState.RIVER, ServerModel.GameState.TURN);

            public SittingPlayersChangedEvent(ServerModel.GameState gameState, int sittingPlayersCount) {
                super();
                mSittingPlayersCount = sittingPlayersCount;
                mGameState = gameState;
            }

            public int getSittingPlayersCount() {
                return mSittingPlayersCount;
            }

            public boolean isGameOngoing() {
                return ONGOING_GAME.contains(mGameState);
            }
        }

        public static class ClearCardsEvent extends GameActivityEvent {

            private static final long serialVersionUID = 20140417L;

            public ClearCardsEvent() {
                super();
            }
        }

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