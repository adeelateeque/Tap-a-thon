package com.zhideel.tapathon.ui;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import com.squareup.otto.Bus;
import com.zhideel.tapathon.R;
import com.zhideel.tapathon.logic.CommunicationBus;

import java.util.ArrayList;

/**
 * Created by Adeel on 14/11/13.
 */
public class GameBoardView implements CommunicationBus.BusManager {
    private Activity mContext;
    private final Bus mBus;
    private GridView gridview;

    public GameBoardView(Context context, MultiTouchView.GameLevel level, ViewGroup viewGroup) {
        MultiTouchView.setLevel(level);
        mContext = (Activity) context;
        this.mBus = CommunicationBus.getInstance();

        View.inflate(mContext, R.layout.view_game_pad, viewGroup);
        gridview = (GridView) viewGroup.findViewById(R.id.gridview);
        gridview.setAdapter(new PadAdapter(mContext));

    }

    public void resetBoard() {
        gridview.setAdapter(new PadAdapter(mContext));
    }

    @Override
    public void startBus() {
        mBus.register(this);
    }

    @Override
    public void stopBus() {
        mBus.unregister(this);
    }
}


class PadAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<MultiTouchView> tappads;

    public PadAdapter(Context c) {
        mContext = c;
        tappads = new ArrayList<MultiTouchView>();
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
        tappad.setLayoutParams(new GridView.LayoutParams(parent.getWidth() / 3,
                parent.getHeight() / 3 - 7));
        tappad.setAlpha(0.4f);
        return tappad;
    }
}
