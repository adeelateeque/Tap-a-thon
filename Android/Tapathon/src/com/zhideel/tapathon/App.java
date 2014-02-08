package com.zhideel.tapathon;

import android.app.Application;
import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.groupplay.Sgp;
import com.samsung.android.sdk.groupplay.SgpGroupPlay;

/**
 * Created by Adeel on 24/1/14.
 */
public class App extends Application implements SgpGroupPlay.SgpConnectionStatusListener {
    private static Sgp sgp = null;
    private static SgpGroupPlay sdk = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Config.context = this.getApplicationContext();
        /*boolean groupPlayAvailable = true;
        sgp = new Sgp();
        try {
            sgp.initialize(getApplicationContext());
        } catch (SsdkUnsupportedException e) {
            // Exception Handling
            groupPlayAvailable = false;
        }
        if(groupPlayAvailable == true){
            new SgpGroupPlay(this).start();
        }*/
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
        sdk.setParticipantInfo(false);
        if (sdk.hasSession()) {
            sdk.setParticipantInfo(false);
        }
    }

    public static SgpGroupPlay getGroupPlaySdk() {
        return sdk;
    }
}


