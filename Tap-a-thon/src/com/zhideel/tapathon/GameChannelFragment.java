package com.zhideel.tapathon;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class GameChannelFragment extends DialogFragment {
	
	private LinearLayout view;
	private Button btnJoin, btnCreate;
    private EditText channelName;
	private String selectedChannel;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().setTitle("Game Channel");
		view = (LinearLayout) inflater.inflate(R.layout.dialog_channel, null);
		btnJoin = (Button) view.findViewById(R.id.btn_join);
        channelName = (EditText) view.findViewById(R.id.editText);
		btnCreate = (Button) view.findViewById(R.id.btn_create);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		btnJoin.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				ConnectionManager cm = new ConnectionManager(GameChannelFragment.this.getActivity());
                cm.joinChannel(channelName.getText().toString());
                Intent myIntent=new Intent(GameChannelFragment.this.getActivity(), GamePadActivity.class);
                startActivity(myIntent);
			}
		});
		
		btnCreate.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				FragmentTransaction dFrag = getFragmentManager().beginTransaction();
				Fragment prev = getFragmentManager().findFragmentByTag("dialog_create");
				if (prev != null) {
					dFrag.remove(prev);
         	    }
         	    dFrag.addToBackStack(null);
         	    CreateChannelFragment mFragment = new CreateChannelFragment();
         	    mFragment.show(getFragmentManager(), "dialog_create");
         	    dFrag.commit();
         	    getDialog().dismiss();
			}
		});
	}

}
