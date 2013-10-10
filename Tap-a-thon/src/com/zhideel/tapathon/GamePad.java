package com.zhideel.tapathon;

import android.app.Activity;
import android.os.Bundle;

//TODO add double tap listener
//TODO add long tap listener
public class GamePad extends Activity {

    MultiTouchView tappadView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_pad);
        tappadView = (MultiTouchView) findViewById(R.id.gestureOverlayView);
    }
}