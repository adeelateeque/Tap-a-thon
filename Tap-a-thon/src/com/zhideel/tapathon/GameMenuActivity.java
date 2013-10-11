package com.zhideel.tapathon;

import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.samsung.chord.ChordManager;
import com.samsung.chord.IChordChannel;
import com.samsung.chord.IChordChannelListener;
import com.samsung.chord.IChordManagerListener;

public class GameMenuActivity extends Activity {

    private static final String CHORD_HELLO_TEST_CHANNEL = "com.samsung.android.sdk.chord.example.HELLOTESTCHANNEL";

    private static final String CHORD_SAMPLE_MESSAGE_TYPE = "com.samsung.android.sdk.chord.example.MESSAGE_TYPE";

    private ChordManager mChordManager = null;

    private Button btnStart;
    private Button btnLogin;

    private boolean bStarted = false;

    private int mSelectedInterface = -1;

    private TextView tvName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = (Button) findViewById(R.id.btn_start);
        btnLogin = (Button) findViewById(R.id.btn_login);
        tvName = (TextView) findViewById(R.id.tv_name);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bStarted) {
                    //mLogView.appendLog("\n[B] Start Chord!");
                    startChord();
                    
                    FragmentTransaction dFrag = getFragmentManager().beginTransaction();
                    Fragment prev = getFragmentManager().findFragmentByTag("dialog_channel");
                    if (prev != null) {
            	    	dFrag.remove(prev);
            	    }
            	    dFrag.addToBackStack(null);
                    GameChannelFragment mFragment = new GameChannelFragment();
                    mFragment.show(getFragmentManager(), "dialog_channel");
                    dFrag.commit();
                    
                    Toast.makeText(getBaseContext(), "Start", Toast.LENGTH_SHORT).show();
                } else {
                    //mLogView.appendLog("\n[C] Stop Chord!");
                    stopChord();
                    Toast.makeText(getBaseContext(), "Stop", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent=new Intent(view.getContext(), GamePadActivity.class);
                startActivity(myIntent);
                finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mChordManager == null) {
            //mLogView.appendLog("\n[A] Initialize Chord!");
            initChord();
        }
    }

    @Override
    public void onDestroy() {
        if (mChordManager != null) {
            mChordManager.close();
            mChordManager = null;
        }
        super.onDestroy();
    }

    private void initChord() {

        /****************************************************
         * 1. GetInstance
         ****************************************************/
        Context app_context = this.getApplicationContext();
        mChordManager = ChordManager.getInstance(app_context);
        //mLogView.appendLog("    getInstance");

        /****************************************************
         * 2. Set some values before start If you want to use secured channel,
         * you should enable SecureMode. Please refer
         * UseSecureChannelFragment.java mChordManager.enableSecureMode(true);
         *
         *
         * Once you will use sendFile or sendMultiFiles, you have to call setTempDirectory
         * mChordManager.setTempDirectory(Environment.getExternalStorageDirectory().getAbsolutePath()
         *       + "/Chord");
         ****************************************************/
        //mLogView.appendLog("    setLooper");
        mChordManager.setHandleEventLooper(getApplication().getMainLooper());

        /**
         * Optional. If you need listening network changed, you can set callback
         * before starting chord.
         */
        mChordManager.setNetworkListener(new ChordManager.INetworkListener() {

            @Override
            public void onDisconnected(int interfaceType) {
                if (interfaceType == mSelectedInterface) {
                    Toast.makeText(getApplication(), getInterfaceName(interfaceType) + " is disconnected", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onConnected(int interfaceType) {
                if (interfaceType == mSelectedInterface) {
                    Toast.makeText(getApplication(), getInterfaceName(interfaceType) + " is connected", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private String getInterfaceName(int interfaceType) {
        if (ChordManager.INTERFACE_TYPE_WIFI == interfaceType)
            return "Wi-Fi";
        else if (ChordManager.INTERFACE_TYPE_WIFIAP == interfaceType)
            return "Mobile AP";
        else if (ChordManager.INTERFACE_TYPE_WIFIP2P == interfaceType)
            return "Wi-Fi Direct";

        return "UNKNOWN";
    }

    private void startChord() {
        /**
         * 3. Start Chord using the first interface in the list of available
         * interfaces.
         */
        List<Integer> infList = mChordManager.getAvailableInterfaceTypes();
        if (infList.isEmpty()) {
            //mLogView.appendLog("    There is no available connection.");
            return;
        }

        int interfaceType = infList.get(0);

        int nError = mChordManager.start(interfaceType, mManagerListener);
        mSelectedInterface = interfaceType;
        btnStart.setEnabled(false);

        if (ChordManager.ERROR_INVALID_STATE == nError) {
            Toast.makeText(getBaseContext(), "Invalid state!", Toast.LENGTH_SHORT).show();
        } else if (ChordManager.ERROR_INVALID_INTERFACE == nError) {
            Toast.makeText(getBaseContext(), "Invalid connection!", Toast.LENGTH_SHORT).show();
        } else if (ChordManager.ERROR_INVALID_PARAM == nError) {
            Toast.makeText(getBaseContext(), "Invalid argument!", Toast.LENGTH_SHORT).show();
        } else if (ChordManager.ERROR_FAILED == nError) {
            Toast.makeText(getBaseContext(), "Fail to start!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * ChordManagerListener
     */
    IChordManagerListener mManagerListener = new IChordManagerListener() {

        @Override
        public void onStarted(String nodeName, int reason) {
            /**
             * 4. Chord has started successfully
             */
            bStarted = true;
            btnStart.setText(R.string.stop);
            btnStart.setEnabled(true);

            if (reason == STARTED_BY_USER) {
                // Success to start by calling start() method
                Toast.makeText(getBaseContext(), "Started by user!", Toast.LENGTH_SHORT).show();
                joinTestChannel();
            } else if (reason == STARTED_BY_RECONNECTION) {
                // Re-start by network re-connection.
                Toast.makeText(getBaseContext(), "Started by recon!", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void onStopped(int reason) {
            /**
             * 8. Chord has stopped successfully
             */
            bStarted = false;
            btnStart.setText(R.string.start);
            btnStart.setEnabled(true);

            if (STOPPED_BY_USER == reason) {
                // Success to stop by calling stop() method
                Toast.makeText(getBaseContext(), "Stopped by user!", Toast.LENGTH_SHORT).show();
            } else if (NETWORK_DISCONNECTED == reason) {
                // Stopped by network disconnected
                Toast.makeText(getBaseContext(), "Network disconnect!", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onNetworkDisconnected() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onError(int error) {
            // TODO Auto-generated method stub

        }
    };

    private void joinTestChannel() {
        IChordChannel channel = null;
        /**
         * 5. Join my channel
         */
        Toast.makeText(getBaseContext(), "Join Channel!", Toast.LENGTH_SHORT).show();
        channel = mChordManager.joinChannel(CHORD_HELLO_TEST_CHANNEL, mChannelListener);
        List<IChordChannel> channels = mChordManager.getJoinedChannelList();
        Toast.makeText(getBaseContext(), channels.get(0).getName(), Toast.LENGTH_SHORT).show();

        if (channel == null) {
            Toast.makeText(getBaseContext(), "Failed to join channel!", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopChord() {
        if (mChordManager == null)
            return;

        /**
         * If you registered NetworkListener, you should unregister it.
         */
        mChordManager.setNetworkListener(null);

        /**
         * 7. Stop Chord. You can call leaveChannel explicitly.
         * mChordManager.leaveChannel(CHORD_HELLO_TEST_CHANNEL);
         */
        Toast.makeText(getBaseContext(), "Stopped!", Toast.LENGTH_SHORT).show();
        mChordManager.stop();
        btnStart.setEnabled(false);
    }

    // ***************************************************
    // ChordChannelListener
    // ***************************************************
    private IChordChannelListener mChannelListener = new IChordChannelListener() {

        /**
         * Called when a node leave event is raised on the channel.
         */
        @Override
        public void onNodeLeft(String fromNode, String fromChannel) {
            Toast.makeText(getBaseContext(), "Left " + fromNode, Toast.LENGTH_SHORT).show();
        }

        /**
         * Called when a node join event is raised on the channel
         */
        @Override
        public void onNodeJoined(String fromNode, String fromChannel) {
            Toast.makeText(getBaseContext(), "Join " + fromNode, Toast.LENGTH_SHORT).show();

            /**
             * 6. Send data to joined node
             */
            byte[][] payload = new byte[1][];
            payload[0] = "Hello chord!".getBytes();

            IChordChannel channel = mChordManager.getJoinedChannel(fromChannel);
            channel.sendData(fromNode, CHORD_SAMPLE_MESSAGE_TYPE, payload);
            Toast.makeText(getBaseContext(), "Send Data " + fromNode + ", " + new String(payload[0]), Toast.LENGTH_SHORT).show();
            //mLogView.appendLog("    sendData(" + fromNode + ", " + new String(payload[0]) + ")");
        }

        /**
         * Called when the data message received from the node.
         */
        @Override
        public void onDataReceived(String fromNode, String fromChannel, String payloadType, byte[][] payload) {
            /**
             * 6. Received data from other node
             */
            if (payloadType.equals(CHORD_SAMPLE_MESSAGE_TYPE)) {
                //mLogView.appendLog("    >onDataReceived(" + fromNode + ", " + new String( payload[0]) + ")");
            }
        }

        /**
         * The following callBacks are not used in this Fragment. Please refer
         * to the SendFilesFragment.java
         */
        @Override
        public void onMultiFilesWillReceive(String fromNode, String fromChannel, String fileName, String taskId, int totalCount, String fileType, long fileSize) {

        }

        @Override
        public void onMultiFilesSent(String toNode, String toChannel, String fileName, String taskId, int index, String fileType) {

        }

        @Override
        public void onMultiFilesReceived(String fromNode, String fromChannel, String fileName, String taskId, int index, String fileType, long fileSize, String tmpFilePath) {

        }

        @Override
        public void onMultiFilesFinished(String node, String channel, String taskId, int reason) {

        }

        @Override
        public void onMultiFilesFailed(String node, String channel, String fileName, String taskId, int index, int reason) {

        }

        @Override
        public void onMultiFilesChunkSent(String toNode, String toChannel, String fileName, String taskId, int index, String fileType, long fileSize, long offset, long chunkSize) {

        }

        @Override
        public void onMultiFilesChunkReceived(String fromNode, String fromChannel, String fileName, String taskId, int index, String fileType, long fileSize, long offset) {

        }

        @Override
        public void onFileWillReceive(String fromNode, String fromChannel, String fileName, String hash, String fileType, String exchangeId, long fileSize) {

        }

        @Override
        public void onFileSent(String toNode, String toChannel, String fileName, String hash, String fileType, String exchangeId) {

        }

        @Override
        public void onFileReceived(String fromNode, String fromChannel, String fileName, String hash, String fileType, String exchangeId, long fileSize, String tmpFilePath) {

        }

        @Override
        public void onFileFailed(String node, String channel, String fileName, String hash, String exchangeId, int reason) {

        }

        @Override
        public void onFileChunkSent(String toNode, String toChannel, String fileName, String hash, String fileType, String exchangeId, long fileSize, long offset, long chunkSize) {

        }

        @Override
        public void onFileChunkReceived(String fromNode, String fromChannel, String fileName, String hash, String fileType, String exchangeId, long fileSize, long offset) {

        }
    };
}