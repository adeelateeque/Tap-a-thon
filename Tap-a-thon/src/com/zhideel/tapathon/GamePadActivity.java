package com.zhideel.tapathon;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

//TODO add double tap listener
//TODO add long tap listener
public class GamePadActivity extends Activity {
    private boolean continueMusic;
    MultiTouchView tappadView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game_pad);
        tappadView = (MultiTouchView) findViewById(R.id.gestureOverlayView);
    }

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
}