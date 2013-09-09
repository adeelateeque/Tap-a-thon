package com.zhideel.tapathon;

import android.app.Activity;
import android.os.Bundle;

import java.util.List;

import com.samsung.chord.ChordManager;
import com.samsung.chord.IChordChannel;
import com.samsung.chord.ChordManager.INetworkListener;
import com.samsung.chord.IChordChannelListener;
import com.samsung.chord.IChordManagerListener;

//TODO add double tap listener
//TODO add long tap listener
public class MyActivity extends Activity {

    MultitouchView tappadView;

    ChordManager mChordManager;
    private static final String CHORD_SAMPLE_MESSAGE_TYPE = "com.samsung.android.sdk.chord.example.MESSAGE_TYPE";
    private static final String CHORD_HELLO_TEST_CHANNEL = "com.samsung.android.sdk.chord.example.HELLOTESTCHANNEL";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tappadView = (MultitouchView) findViewById(R.id.gestureOverlayView);

        // Get an instance of ChordManager.
        mChordManager = ChordManager.getInstance(this);
        mChordManager.setHandleEventLooper(getMainLooper());
        List<Integer> interfaceList = mChordManager.getAvailableInterfaceTypes();
        if (interfaceList.isEmpty()) {
            // There is no connection.
            return;
        }
        int nError = mChordManager.start(interfaceList.get(0).intValue(), mManagerListener);
        /*if ( nError != ERROR_NONE ){
            // Fail to start.
            return;
        }*/
    }

    // Listener for Chord manager events.
    private IChordManagerListener mManagerListener = new IChordManagerListener() {
        @Override
        public void onStarted(String name, int reason) {
            if (STARTED_BY_USER == reason) {
                // Called when Chord is started successfully.
                joinChannel();
            }
        }

        @Override
        public void onNetworkDisconnected() {

        }

        @Override
        public void onError(int i) {

        }

        @Override
        public void onStopped(int reason) {
            if (STOPPED_BY_USER == reason) {
                // Called when Chord is stopped.
            }
        }
    };

    // Join a desired channel with a given listener.
    private void joinChannel(){
        IChordChannel channel = null;
        channel = mChordManager.joinChannel(CHORD_HELLO_TEST_CHANNEL, mChannelListener);
        if(channel == null){
            // Fail to joinChannel.
            return;
        }
    }

    // Listener for Chord channel events.
    private IChordChannelListener mChannelListener=new IChordChannelListener(){
        @Override
        public void onNodeJoined(String fromNode,String fromChannel){
            byte[][]payload = new byte[1][];
            payload[0] = "Hello chord!".getBytes();
            IChordChannel channel = mChordManager.getJoinedChannel(fromChannel);
            // Send simple data.
            channel.sendData(fromNode,CHORD_SAMPLE_MESSAGE_TYPE,payload);
        }

        @Override
        public void onNodeLeft(String s, String s2) {

        }

        @Override
        public void onDataReceived(String s, String s2, String s3, byte[][] bytes) {

        }

        @Override
        public void onFileWillReceive(String s, String s2, String s3, String s4, String s5, String s6, long l) {

        }

        @Override
        public void onFileChunkReceived(String s, String s2, String s3, String s4, String s5, String s6, long l, long l2) {

        }

        @Override
        public void onFileReceived(String s, String s2, String s3, String s4, String s5, String s6, long l, String s7) {

        }

        @Override
        public void onFileChunkSent(String s, String s2, String s3, String s4, String s5, String s6, long l, long l2, long l3) {

        }

        @Override
        public void onFileSent(String s, String s2, String s3, String s4, String s5, String s6) {

        }

        @Override
        public void onFileFailed(String s, String s2, String s3, String s4, String s5, int i) {

        }

        @Override
        public void onMultiFilesWillReceive(String s, String s2, String s3, String s4, int i, String s5, long l) {

        }

        @Override
        public void onMultiFilesChunkReceived(String s, String s2, String s3, String s4, int i, String s5, long l, long l2) {

        }

        @Override
        public void onMultiFilesReceived(String s, String s2, String s3, String s4, int i, String s5, long l, String s6) {

        }

        @Override
        public void onMultiFilesChunkSent(String s, String s2, String s3, String s4, int i, String s5, long l, long l2, long l3) {

        }

        @Override
        public void onMultiFilesSent(String s, String s2, String s3, String s4, int i, String s5) {

        }

        @Override
        public void onMultiFilesFailed(String s, String s2, String s3, String s4, int i, int i2) {

        }

        @Override
        public void onMultiFilesFinished(String s, String s2, String s3, int i) {

        }
    };
}