package com.zhideel.tapathon.ui;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListAdapter;
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

    public GameBoardView(Context context, PadView.GameLevel level, ViewGroup viewGroup) {
        PadView.setLevel(level);
        mContext = (Activity) context;
        this.mBus = CommunicationBus.getInstance();

        View.inflate(mContext, R.layout.view_game_pad, viewGroup);
        gridview = (GridView) viewGroup.findViewById(R.id.gridview);
        gridview.setAdapter(new PadAdapter(mContext));

    }

    public void resetBoard() {
        gridview.setAdapter(new PadAdapter(mContext));
        setPaused(false);
    }

    public void setPaused(boolean paused) {
        ListAdapter adapter = gridview.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            PadView view = (PadView) adapter.getItem(i);
            view.setPaused(paused);
        }
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
    private ArrayList<PadView> pads;

    public PadAdapter(Context c) {
        mContext = c;
        pads = new ArrayList<PadView>();
        for (int i = 0; i < getCount(); i++) {
            PadView pad = new PadView(mContext);
            pad.setAlpha(0.8f);
            pads.add(pad);
        }
    }

    public int getCount() {
        return 9;
    }

    public Object getItem(int position) {
        return pads.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        PadView pad = (PadView) getItem(position);
        GridView.LayoutParams params = new GridView.LayoutParams(parent.getWidth() / 3,
                parent.getHeight() / 3);
        pad.setLayoutParams(params);
        return pad;
    }
}
