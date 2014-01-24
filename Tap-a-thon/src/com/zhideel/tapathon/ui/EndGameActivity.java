package com.zhideel.tapathon.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.zhideel.tapathon.R;
import com.zhideel.tapathon.logic.GameLogicController;
import com.zhideel.tapathon.logic.Player;

import java.util.List;

/**
 * Created by Adeel on 20/11/13.
 */
public class EndGameActivity extends Activity {

    private ListView lvScore;
    private List<Player> players;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_end_game);
        lvScore = (ListView) findViewById(R.id.lv_score);
        Toast.makeText(getApplicationContext(), Integer.toString(GameLogicController.mModel.getPlayers().size()), Toast.LENGTH_SHORT).show();
        ScoreAdapter adapter = new ScoreAdapter(GameLogicController.mModel.getPlayers(), this);
        lvScore.setAdapter(adapter);
    }

    private class ScoreAdapter extends BaseAdapter {
        private Context mContext;
        private List<Player> players;

        public ScoreAdapter(List<Player> players, android.content.Context c) {
            mContext = c;
            this.players = players;
        }

        public int getCount() {
            return players.size();
        }

        public Object getItem(int position) {
            return players.get(position);
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View scoreView = View.inflate(mContext, R.layout.score_adapter, null);
            Player player = players.get(position);
            TextView tvPlayer = (TextView) scoreView.findViewById(R.id.tv_player);
            TextView tvScore = (TextView) scoreView.findViewById(R.id.tv_score);
            Toast.makeText(getApplicationContext(), player.getName().toString(), Toast.LENGTH_SHORT).show();
//            tvPlayer.setText(player.getName());
//            tvScore.setText(player.getScore());
            return scoreView;
        }
    }
}