package com.zhideel.tapathon.ui;

import com.zhideel.tapathon.ConnectionManager;
import com.zhideel.tapathon.R;
import com.zhideel.tapathon.R.id;
import com.zhideel.tapathon.R.layout;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class CreateChannelFragment extends DialogFragment{
	
	private LinearLayout view;
	private EditText etChannelName;
	private CheckBox cbAllShare;
	private RadioGroup rgLvl;
	private Button btnCreate, btnCancel;
	private String selection = "";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().setTitle("Create Channel");
		view = (LinearLayout) inflater.inflate(R.layout.dialog_create_channel, null);
		etChannelName = (EditText) view.findViewById(R.id.et_name);
		cbAllShare = (CheckBox) view.findViewById(R.id.cb_allshare);
		rgLvl = (RadioGroup) view.findViewById(R.id.rg_lvl);
		btnCreate = (Button) view.findViewById(R.id.btn_create);
		btnCancel = (Button) view.findViewById(R.id.btn_cancel);
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		btnCreate.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				switch (rgLvl.getCheckedRadioButtonId())
				{
					case R.id.rb_easy:
						selection = "Easy";
						break;
					
					case R.id.rb_normal:
						selection = "Normal";
						break;
					
					case R.id.rb_hard:
						selection = "Hard";
						break;
				}
				
				if (!(selection.equals(""))){
					//make connection
					Toast.makeText(getActivity(), selection, Toast.LENGTH_SHORT).show();
					ConnectionManager cm = new ConnectionManager(CreateChannelFragment.this.getActivity());
					Intent myIntent=new Intent(CreateChannelFragment.this.getActivity(), GamePadActivity.class);
					myIntent.putExtra("level", selection);
                	startActivity(myIntent);
				}
				else {
					Toast.makeText(getActivity(), "Please select a difficulty.", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		btnCancel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
         	    getDialog().dismiss();
			}
		});
	}
}
