package com.zhideel.tapathon.ui;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.zhideel.tapathon.R;
import com.zhideel.tapathon.chord.AbstractChord.NodeJoinedOnPublicChannelEvent;
import com.zhideel.tapathon.chord.AbstractChord.NodeLeftOnPublicChannelEvent;
import com.zhideel.tapathon.chord.ConnectionChord;
import com.zhideel.tapathon.chord.ConnectionChord.OnServerListChangedListener;
import com.zhideel.tapathon.logic.CommunicationBus;
import com.zhideel.tapathon.logic.CommunicationBus.BusManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameChannelFragment extends DialogFragment implements OnServerListChangedListener, BusManager {
	
	private ConnectionChord mConnectionChord;
	private OnServerChosenListener mOnServerChosenListener;
	private ServerAdapter mServerAdapter;
	private Bus mBus;
	
	private LinearLayout view;
	private Button btnJoin, btnCreate;
    private ListView listChannel;
	private String selectedChannel;
	
	@Subscribe
	public void onNodeLeftOnPublicChannel(NodeLeftOnPublicChannelEvent event) {
		findServers();
	}

	@Subscribe
	public void onNodeJoinedOnPublicChannel(NodeJoinedOnPublicChannelEvent event) {
		findServers();
	}
	
	private void findServers() {
		mConnectionChord.findServers();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBus = CommunicationBus.getInstance();
		mConnectionChord = new ConnectionChord(getActivity(), GamePadActivity.GAME_NAME, GameChannelFragment.this);
		mOnServerChosenListener = (OnServerChosenListener) getActivity();
		startBus();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().setTitle("Available room");
		view = (LinearLayout) inflater.inflate(R.layout.dialog_channel, null);
		btnJoin = (Button) view.findViewById(R.id.btn_join);
		listChannel = (ListView) view.findViewById(R.id.lv_channel);
		btnCreate = (Button) view.findViewById(R.id.btn_create);
		
		mServerAdapter = new ServerAdapter();
		listChannel.setAdapter(mServerAdapter);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		btnJoin.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
                Intent myIntent=new Intent(GameChannelFragment.this.getActivity(), GamePadActivity.class);
                startActivity(myIntent);
                getDialog().dismiss();
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
	
	@Override
	public void startBus() {
		mBus.register(this);
	}

	@Override
	public void stopBus() {
		mBus.unregister(this);
	}

	@Override
	public void onChanged(List<String> availableServers) {
		mServerAdapter.setServersList(availableServers);
	}
	
	/**
	 * Interface definition for a callback to be invoked when server is chosen.
	 */
	public interface OnServerChosenListener {

		/**
		 * Called when a view with server's name has been clicked.
		 * 
		 * @param serverName
		 *            name of the clicked server
		 */
		void onServerChosen(String serverName);

	}

	/**
	 * Interface definition for a callback to be invoked when a servers list has changed.
	 */
	public interface OnServerListChangedListener {

		void onChanged(List<String> availableServers);

	}
	
	private class ServerAdapter extends BaseAdapter {

		private final List<String> mServers;

		public ServerAdapter() {
			super();
			mServers = new ArrayList<String>();
		}

		public void setServersList(List<String> servers) {
			if (!(mServers.size() == servers.size() && mServers.containsAll(servers))) {
				mServers.clear();
				mServers.addAll(servers);
				Collections.sort(mServers);
				notifyDataSetChanged();
			}
		}

		@Override
		public int getCount() {
			return mServers.size();
		}

		@Override
		public String getItem(int position) {
			return mServers.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final TextView textView = (TextView) View.inflate(getActivity(), R.layout.server_name_text_view, null);
			textView.setText(mServers.get(position));
			return textView;
		}
	}
}
