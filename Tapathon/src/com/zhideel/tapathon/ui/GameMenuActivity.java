package com.zhideel.tapathon.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.*;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.newrelic.agent.android.NewRelic;
import com.zhideel.tapathon.App;
import com.zhideel.tapathon.Config;
import com.zhideel.tapathon.R;

public class GameMenuActivity extends Activity implements SelectChannelFragment.OnServerChosenListener {

    public static final String TAG = "Tapathon";
    public static final String TAPATHON_PREFERENCES = "TAPATHON_PREFERENCES";
    public static final String USER_NAME_KEY = "USER_NAME_KEY";

    private Button btnStart;
    private Button btnGroupPlay;
    private EditText etName;

    private SharedPreferences mSharedPreferences;

    private final BroadcastReceiver mWiFiBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            refreshButtons();
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NewRelic.withApplicationToken(
                "AAa8bf8ee972e6b39c667cd145f079ceb20ecc2098"
        ).start(this.getApplication());
        setContentView(R.layout.activity_main);
        mSharedPreferences = getSharedPreferences(TAPATHON_PREFERENCES, MODE_PRIVATE);
        registerWifiStateReceiver();

        btnStart = (Button) findViewById(R.id.btn_start);
        btnGroupPlay = (Button) findViewById(R.id.btn_group_play);
        btnGroupPlay.setVisibility(View.GONE);

        refreshButtons();

        if (!btnStart.isEnabled()) {

            Toast.makeText(this, getString(R.string.wifi_off), Toast.LENGTH_LONG).show();
        }

        etName = (EditText) findViewById(R.id.user_name_text_view);
        final SharedPreferences sharedPreferences = GameMenuActivity.this.getSharedPreferences(GameMenuActivity.TAPATHON_PREFERENCES,
                Context.MODE_PRIVATE);
        final String userName = sharedPreferences.getString(GameMenuActivity.USER_NAME_KEY, android.os.Build.MODEL);
        setNameTextView(userName.trim());
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(GameMenuActivity.USER_NAME_KEY, name);
                editor.apply();

                FragmentTransaction dFrag = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("dialog_channel");
                if (prev != null) {
                    dFrag.remove(prev);
                }
                dFrag.addToBackStack(null);
                SelectChannelFragment mFragment = new SelectChannelFragment();
                mFragment.show(getFragmentManager(), "dialog_channel");
                dFrag.commit();

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshButtons();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mWiFiBroadcastReceiver);
    }

    void setNameTextView(String name) {
        etName.setText("");
        etName.append(name);
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

    public void groupPlayClick(View v) {
        //Check if we have Group Play and if we have a session
        if (App.getGroupPlaySdk() != null) {
            if (App.getGroupPlaySdk().runGroupPlay()) {
                refreshButtons();
            }
        }
    }

    public void refreshButtons() {
        //Check if we have Group Play and if we dont have a session
        if (App.getGroupPlaySdk() != null && btnGroupPlay != null) {
            if (!App.getGroupPlaySdk().hasSession()) {
                btnGroupPlay.setVisibility(View.VISIBLE);
            }
            else
            {
                btnGroupPlay.setVisibility(View.GONE);
            }
        }

        if (btnStart != null) {
            if (Config.isNetworkAvailable()) {
                btnStart.setEnabled(true);
                btnStart.setText("Start");
            } else {
                btnStart.setEnabled(false);
                btnStart.setText("Please Connect...");
            }
        }
    }
}