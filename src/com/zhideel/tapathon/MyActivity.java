package com.zhideel.tapathon;

import android.app.Activity;
import android.os.Bundle;

//TODO add double tap listener
//TODO add long tap listener
public class MyActivity extends Activity {
    MultitouchView tappadView;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tappadView = (MultitouchView) findViewById(R.id.gestureOverlayView);
    }

}
