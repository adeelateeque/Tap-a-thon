package com.zhideel.tapathon.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.*;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.zhideel.tapathon.ConnectionManager;
import com.zhideel.tapathon.R;

public class GameMenuActivity extends Activity implements GameChannelFragment.OnServerChosenListener {

	public static final String TAG = "Tapathon";
    public static final String TAPATHON_PREFERENCES = "TAPATHON_PREFERENCES";
    public static final String USER_NAME_KEY = "USER_NAME_KEY";

    private Button btnStart;
    private ConnectionManager cm;
    private boolean bStarted = false;
    private EditText etName;

    private SharedPreferences mSharedPreferences;

    private final BroadcastReceiver mWiFiBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
        final WifiInfo info = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
        if (info == null) {
            enableButtons(false);
        } else {
            enableButtons(true);
        }
        }

    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cm = new ConnectionManager(this);
        btnStart = (Button) findViewById(R.id.btn_start);
        etName = (EditText) findViewById(R.id.user_name_text_view);
        final SharedPreferences sharedPreferences = GameMenuActivity.this.getSharedPreferences(GameMenuActivity.TAPATHON_PREFERENCES,
                Context.MODE_PRIVATE);
        final String userName = sharedPreferences.getString(GameMenuActivity.USER_NAME_KEY, "");
        etName.append(userName.trim());
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bStarted) {
                    String name = etName.getText().toString();
                    if (!(name.length() > 0)) {
                       name = "ANONYMOUS";
                    }
                    final SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(GameMenuActivity.USER_NAME_KEY, name);
                    editor.apply();
                    setNameTextView(name);

                    cm.startChord();
                    
                    FragmentTransaction dFrag = getFragmentManager().beginTransaction();
                    Fragment prev = getFragmentManager().findFragmentByTag("dialog_channel");
                    if (prev != null) {
            	    	dFrag.remove(prev);
            	    }
            	    dFrag.addToBackStack(null);
                    GameChannelFragment mFragment = new GameChannelFragment();
                    mFragment.show(getFragmentManager(), "dialog_channel");
                    dFrag.commit();
                    
                    Toast.makeText(getBaseContext(), "Start", Toast.LENGTH_SHORT).show();
                } else {
                    cm.stopChord();
                    Toast.makeText(getBaseContext(), "Stop", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mSharedPreferences = getSharedPreferences(TAPATHON_PREFERENCES, MODE_PRIVATE);

        final String name = mSharedPreferences.getString(USER_NAME_KEY, "");
        setNameTextView(name);

        registerWifiStateReceiver();

        if (!isWifiConnected()) {
            enableButtons(false);
            Toast.makeText(this, getString(R.string.wifi_off), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (cm.isNotInit()) {
            cm.initChord();
        }
    }

    @Override
    public void onDestroy() {
        if (!cm.isNotInit()) {
            cm.destroy();
        }
        super.onDestroy();
    }

    private boolean isNewUser() {
        return !mSharedPreferences.contains(USER_NAME_KEY);
    }

    void setNameTextView(String name) {
        etName.setText(name);
    }

    @Override
    public void onServerChosen(String serverName) {
        final Intent intent = new Intent(this, GamePadActivity.class);
        intent.putExtra(GamePadActivity.CLIENT, true);
        intent.putExtra(GamePadActivity.SERVER_NAME, serverName);
        startActivity(intent);
    }

    private void registerWifiStateReceiver() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(mWiFiBroadcastReceiver, filter);
    }

    public void settingsClick(View v) {
        startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
    }

    public boolean isWifiConnected() {
        final ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return networkInfo != null && networkInfo.isConnected();
    }

    public void enableButtons(boolean enabled) {
        btnStart.setEnabled(enabled);
    }

   /* public void enableClientButton() {
        mClientButton.setEnabled(true);
    }*/



}