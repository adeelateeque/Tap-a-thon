package com.zhideel.tapathon;

import android.app.DialogFragment;
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
import android.widget.Toast;

public class CreateChannelFragment extends DialogFragment{
	
	private LinearLayout view;
	private EditText etChannelName;
	private CheckBox cbAllShare;
	private Button btnCreate, btnCancel;
	
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
				Toast.makeText(getActivity(), "Created", Toast.LENGTH_SHORT).show();
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
