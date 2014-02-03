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
    private EditText etName;

    private SharedPreferences mSharedPreferences;

    private final BroadcastReceiver mWiFiBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NewRelic.withApplicationToken(
                "AAc1494d8887b688edeaf4f3eab7441cf978a22ded"
        ).start(this.getApplication());

        mSharedPreferences = getSharedPreferences(TAPATHON_PREFERENCES, MODE_PRIVATE);
        registerWifiStateReceiver();

        btnStart = (Button) findViewById(R.id.btn_start);
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
                CreateChannelFragment mFragment = new CreateChannelFragment();
                mFragment.show(getFragmentManager(), "dialog_channel");
                dFrag.commit();

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
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
}