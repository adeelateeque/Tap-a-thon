package com.zhideel.tapathon;

import android.app.Activity;
import android.os.Bundle;

//TODO add double tap listener
//TODO add long tap listener
public class GamePad extends Activity {

    MultitouchView tappadView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_pad);
        tappadView = (MultitouchView) findViewById(R.id.gestureOverlayView);
    }
}