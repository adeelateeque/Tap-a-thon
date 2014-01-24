package com.zhideel.tapathon;

import android.app.Application;
import com.samsung.android.sdk.groupplay.Sgp;
import com.samsung.android.sdk.groupplay.SgpGroupPlay;
import com.samsung.android.sdk.SsdkUnsupportedException;

/**
 * Created by Adeel on 24/1/14.
 */
public class App extends Application implements SgpGroupPlay.SgpConnectionStatusListener{
    private static Sgp sgp = null;
    private static SgpGroupPlay sdk= null;

    @Override
    public void onCreate() {
        super.onCreate();

        sgp = new Sgp();
        try {
            sgp.initialize(getApplicationContext());
        } catch (SsdkUnsupportedException e) {
            // Exception Handling
        }
        new SgpGroupPlay(this).start();

    }

    @Override
    public void onConnected(SgpGroupPlay sdk) {
        this.sdk = sdk;
        if (sdk.hasSession()) {
            // Delivers information when users join Group Play.
            sdk.setParticipantInfo(true);
        }
    }

    @Override
    public void onDisconnected() {

    }
}
