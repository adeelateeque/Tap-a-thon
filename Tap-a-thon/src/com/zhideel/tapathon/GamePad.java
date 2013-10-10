package com.zhideel.tapathon;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

//TODO add double tap listener
//TODO add long tap listener
public class GamePad extends Activity {

    MultiTouchView tappadView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game_pad);
        tappadView = (MultiTouchView) findViewById(R.id.gestureOverlayView);
    }
}