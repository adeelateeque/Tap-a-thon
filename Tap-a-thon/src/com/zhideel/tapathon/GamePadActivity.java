package com.zhideel.tapathon;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;

//TODO add double tap listener
//TODO add long tap listener
public class GamePadActivity extends Activity {
    private boolean continueMusic;
    private GridView gridview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game_pad);
        gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new PadAdapter(this));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Toast.makeText(GamePadActivity.this, "" + position, Toast.LENGTH_SHORT).show();
            }
        });
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

class PadAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<MultiTouchView> tappads;
    public PadAdapter(Context c) {
        mContext = c;
        tappads  = new ArrayList<MultiTouchView>();
    }

    public int getCount() {
        return 9;
    }

    public Object getItem(int position) {
        return tappads.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        MultiTouchView tappad;
        tappad = new MultiTouchView(mContext, null);
        tappad.setLayoutParams(new GridView.LayoutParams(parent.getWidth()/3, parent.getHeight()/3-7));
        tappad.setAlpha(0.4f);
        return tappad;
    }

}