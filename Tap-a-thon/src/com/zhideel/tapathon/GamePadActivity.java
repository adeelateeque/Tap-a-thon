package com.zhideel.tapathon;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

//TODO add double tap listener
//TODO add long tap listener
public class GamePadActivity extends Activity {
	
	private boolean continueMusic;
	private GridView gridview;
	private ArrayList<Integer> operands;
	private String operator;
	private TextView tvMultipler, tvQns, tvTimer;
	private Random rand = new Random();
	private int randomQns, correctAns;
	private int interval = 60;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_game_pad);

		operands = new ArrayList<Integer>();
		
		tvMultipler = (TextView) findViewById(R.id.tv_multipler);
		tvQns = (TextView) findViewById(R.id.tv_qns);
		tvTimer = (TextView) findViewById(R.id.tv_timer);
		gridview = (GridView) findViewById(R.id.gridview);
		gridview.setAdapter(new PadAdapter(this));
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		timer();
		correctAns = 0;
		tvMultipler.setText("1");
		randomQns = randInt(0, 20);
		tvQns.setText(Integer.toString(randomQns));
		
		super.onPostCreate(savedInstanceState);
	}
	
	public void setInterval(int interval){
		tvTimer.setText(Integer.toString(interval));
	}

	@Override
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
	
	private void timer(){
		final Timer time = new Timer();
		time.scheduleAtFixedRate(new TimerTask(){
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (interval != 0){
							interval--;
							setInterval(interval);
						}
						else {
							MultiTouchView.setContinue(false);
							Toast.makeText(getApplication(), Integer.toString(correctAns), Toast.LENGTH_SHORT).show();
							time.cancel();
						}
					}
				});
			}
		}, 0, 1000);
	}
	
	public int randInt(int min, int max) {
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

	public void addOperand(Integer operand) {
		if (operands.size() < 2) {
			operands.add(operand);
		}
	}
	
	public ArrayList<Integer> getOperands(){
		return operands;
	}

	public void setOperator(String operator) {
		if (this.operator == null) {
			this.operator = operator;
		}
	}
	
	public String getOperator(){
		return this.operator;
	}

	public int doCalc() {
		Integer op1 = operands.get(0);
		Integer op2 = operands.get(1);
		Integer result = 0;
		if (operator.equalsIgnoreCase("X")) {
			result = op1 * op2;
		} else if (operator.equalsIgnoreCase("/")) {
			result = op1 / op2;
		} else if (operator.equalsIgnoreCase("-")) {
			result = op1 - op2;
		} else {
			result = op1 + op2;
		}
		
		if (result == randomQns){
			DecimalFormat df = new DecimalFormat("#.0");
			double multipler = Double.parseDouble(tvMultipler.getText().toString());
			multipler = Double.valueOf(df.format(multipler + 0.1));
			tvMultipler.setText(Double.toString(multipler));
			correctAns++;
		}
		return result;
	}
	
	public void resetCurrent(){
		randomQns = randInt(0, 20);
		tvQns.setText(Integer.toString(randomQns));
		operands.clear();
		operator = null;
		gridview.setAdapter(new PadAdapter(this));
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