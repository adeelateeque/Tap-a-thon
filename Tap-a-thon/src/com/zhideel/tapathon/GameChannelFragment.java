package com.zhideel.tapathon;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class GameChannelFragment extends DialogFragment {
	
	private LinearLayout view;
	private ListView lvChannels;
	private Button btnJoin, btnCreate;
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
		lvChannels = (ListView) view.findViewById(R.id.lv_channels);
		btnJoin = (Button) view.findViewById(R.id.btn_join);
		btnCreate = (Button) view.findViewById(R.id.btn_create);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		lvChannels.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> channels, View v, int position, long id) {
				selectedChannel = channels.getItemAtPosition(position).toString();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		
		btnJoin.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity(), "Join", Toast.LENGTH_SHORT).show();
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
