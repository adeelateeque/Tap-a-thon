package com.zhideel.tapathon;

import android.content.Context;
import android.widget.Toast;
import com.samsung.chord.ChordManager;
import com.samsung.chord.IChordChannel;
import com.samsung.chord.IChordChannelListener;
import com.samsung.chord.IChordManagerListener;

import java.util.List;

/**
 * Created by Adeel on 23/10/13.
 */
public class ConnectionManager implements IChordChannelListener {

    private static final String CHORD_HELLO_TEST_CHANNEL = "com.samsung.android.sdk.chord.example.HELLOTESTCHANNEL";
    private static final String CHORD_SAMPLE_MESSAGE_TYPE = "com.samsung.android.sdk.chord.example.MESSAGE_TYPE";
    private final Context context;
    private static ChordManager mChordManager;
    private int mSelectedInterface = -1;

    public ConnectionManager(Context context) {
        this.context = context;
    }

    public void initChord() {

        /****************************************************
         * 1. GetInstance
         ****************************************************/
        Context app_context = context.getApplicationContext();
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
        mChordManager.setHandleEventLooper(context.getApplicationContext().getMainLooper());

        /**
         * Optional. If you need listening network changed, you can set callback
         * before starting chord.
         */
        mChordManager.setNetworkListener(new ChordManager.INetworkListener() {

            @Override
            public void onDisconnected(int interfaceType) {
                if (interfaceType == mSelectedInterface) {
                    Toast.makeText(context.getApplicationContext(), getInterfaceName(interfaceType) + " is disconnected", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onConnected(int interfaceType) {
                if (interfaceType == mSelectedInterface) {
                    Toast.makeText(context.getApplicationContext(), getInterfaceName(interfaceType) + " is connected", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public boolean isNotInit()
    {
       return mChordManager == null;
    }

    public void destroy()
    {
        mChordManager.close();
        mChordManager = null;
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

    public void startChord() {
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

        if (ChordManager.ERROR_INVALID_STATE == nError) {
            Toast.makeText(context, "Invalid state!", Toast.LENGTH_SHORT).show();
        } else if (ChordManager.ERROR_INVALID_INTERFACE == nError) {
            Toast.makeText(context, "Invalid connection!", Toast.LENGTH_SHORT).show();
        } else if (ChordManager.ERROR_INVALID_PARAM == nError) {
            Toast.makeText(context, "Invalid argument!", Toast.LENGTH_SHORT).show();
        } else if (ChordManager.ERROR_FAILED == nError) {
            Toast.makeText(context, "Fail to start!", Toast.LENGTH_SHORT).show();
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
            if (reason == STARTED_BY_USER) {
                // Success to start by calling start() method
                Toast.makeText(context, "Started by user!", Toast.LENGTH_SHORT).show();
            } else if (reason == STARTED_BY_RECONNECTION) {
                // Re-start by network re-connection.
                Toast.makeText(context, "Started by reconnection!", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void onStopped(int reason) {
            /**
             * 8. Chord has stopped successfully
             */
            if (STOPPED_BY_USER == reason) {
                // Success to stop by calling stop() method
                Toast.makeText(context, "Stopped by user!", Toast.LENGTH_SHORT).show();
            } else if (NETWORK_DISCONNECTED == reason) {
                // Stopped by network disconnected
                Toast.makeText(context, "Network disconnect!", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onNetworkDisconnected() {


        }

        @Override
        public void onError(int error) {


        }
    };

    public void joinChannel(String channelName) {
        IChordChannel channel = null;
        /**
         * 5. Join my channel
         */
        channel = mChordManager.joinChannel(channelName, this);

        if (channel == null) {
            Toast.makeText(context, "Failed to join channel " + channelName, Toast.LENGTH_SHORT).show();
        }
    }

    public void sendData(String node, String channelName, String messageType, byte[][] data)
    {
        /**
         * 6. Send data to all nodes
         */

        IChordChannel channel = mChordManager.getJoinedChannel(channelName);
        channel.sendData(node, messageType, data);
    }


    public void stopChord() {
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
        Toast.makeText(context, "Stopped!", Toast.LENGTH_SHORT).show();
        mChordManager.stop();
    }
    /**
     * Called when a node leave event is raised on the channel.
     */
    @Override
    public void onNodeLeft(String fromNode, String fromChannel) {
        Toast.makeText(context, "Left " + fromNode, Toast.LENGTH_SHORT).show();
    }

    /**
     * Called when a node join event is raised on the channel
     */
    @Override
    public void onNodeJoined(String fromNode, String fromChannel) {
        Toast.makeText(context, "Join " + fromNode, Toast.LENGTH_SHORT).show();
        byte[][] payload = new byte[1][];
        payload[0] =  "Welcome".getBytes();
        sendData(fromNode, fromChannel, CHORD_SAMPLE_MESSAGE_TYPE, payload);
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
            Toast.makeText(context, payload[0].toString(), Toast.LENGTH_LONG).show();
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
}
