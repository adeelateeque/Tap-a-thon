package com.zhideel.tapathon;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.Toast;

//TODO add double tap listener
//TODO add long tap listener
public class GamePadActivity extends Activity {
	private boolean continueMusic;
	private GridView gridview;
	private ArrayList<Integer> operands;
	private String operator;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_game_pad);

		operands = new ArrayList<Integer>();

		gridview = (GridView) findViewById(R.id.gridview);
		gridview.setAdapter(new PadAdapter(this));
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
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

	public void addOperand(Integer operand) {
		if (operands.size() < 2) {
			operands.add(operand);
		} else {
			if (operator != null){
				doCalc();
			}
		}
	}

	public void setOperator(String operator) {
		this.operator = operator;
		if (operands.size() == 2) { doCalc(); }
	}

	private int doCalc() {
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
		Toast.makeText(this, Integer.toString(result), Toast.LENGTH_LONG).show();
		return result;
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