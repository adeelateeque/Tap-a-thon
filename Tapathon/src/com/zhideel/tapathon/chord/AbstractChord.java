/*
 ********************************************************************************
 * Copyright (c) 2013 Samsung Electronics, Inc.
 * All rights reserved.
 *
 * This software is a confidential and proprietary information of Samsung
 * Electronics, Inc. ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with Samsung Electronics.
 ********************************************************************************
 */
package com.zhideel.tapathon.chord;

import android.content.Context;
import com.samsung.chord.ChordManager;
import com.samsung.chord.IChordChannel;
import com.samsung.chord.IChordChannelListener;
import com.samsung.chord.IChordManagerListener;
import com.squareup.otto.Bus;
import com.zhideel.tapathon.Config;
import com.zhideel.tapathon.debug.LoggedChordChannel;
import com.zhideel.tapathon.debug.LoggedChordChannelListener;
import com.zhideel.tapathon.logic.CommunicationBus;
import com.zhideel.tapathon.logic.CommunicationBus.BusManager;

/**
 * Contains general methods related to the communication over Chord. Intitializes {@link ChordManager} and handles
 * messages passed over Chord channels.
 */
public abstract class AbstractChord implements BusManager {

    private static final String PAYLOAD_SUFFIX = "_CHORD_TYPE";
    private final String mPayloadType;

    private final ChordManager mChordManager;
    private IChordChannel mPublicChannel;
    private IChordChannel mPrivateChannel;
    final Bus mBus;

    private final IChordManagerListener mChordManagerListener = new IChordManagerListener() {

        @Override
        public void onStarted(String nodeName, int reason) {
            onChordStarted(nodeName, reason);
        }

        @Override
        public void onNetworkDisconnected() {
            onChordDisconnected();
        }

        @Override
        public void onError(int error) {
            onChordError(error);
        }

        @Override
        public void onStopped(int arg0) {

        }

    };

    private final IChordChannelListener mChordPublicChannelListener = new LoggedChordChannelListener(

            new IChordChannelListenerAdapter() {

                @Override
                public void onDataReceived(String fromNode, String fromChannel, String payloadType, byte[][] payload) {
                    if (mPayloadType.equals(payloadType)) {
                        final ChordMessage receivedMessage = ChordMessage.obtainChordMessage(payload[0], fromNode);
                        handlePublicMessage(receivedMessage);
                    }
                }

                @Override
                public void onNodeJoined(String fromNode, String fromChannel) {
                    onNodeJoinedOnPublicChannel(fromNode);
                }

                ;

                @Override
                public void onNodeLeft(String fromNode, String fromChannel) {
                    onNodeLeftOnPublicChannel(fromNode);
                }

                ;

            });

    private final IChordChannelListener mChordPrivateChannelListener = new LoggedChordChannelListener(

            new IChordChannelListenerAdapter() {

                @Override
                public void onDataReceived(String fromNode, String fromChannel, String payloadType, byte[][] payload) {
                    if (mPayloadType.equals(payloadType)) {
                        final ChordMessage receivedMessage = ChordMessage.obtainChordMessage(payload[0], fromNode);
                        handlePrivateMessage(receivedMessage);
                    }
                }

                @Override
                public void onNodeLeft(String fromNode, String fromChannel) {
                    onNodeLeftOnPrivateChannel(fromNode);
                }

                ;

                @Override
                public void onNodeJoined(String fromNode, String fromChannel) {
                    onNodeJoinedOnPrivateChannel(fromNode);
                }

                ;

            });

    AbstractChord(Context context, String gameName) {
        mPayloadType = gameName + PAYLOAD_SUFFIX;
        mChordManager = ChordManager.getInstance(context);
        mBus = CommunicationBus.getInstance();

        final int result = mChordManager.start((Config.isAPConnected()) ? ChordManager.INTERFACE_TYPE_WIFIAP : ChordManager.INTERFACE_TYPE_WIFI, mChordManagerListener);

        if (result != ChordManager.ERROR_NONE) {
            onChordStartFailed(result);
        }
    }

    public void stopChord() {
        if (mPublicChannel != null) {
            mChordManager.leaveChannel(mPublicChannel.getName());
        }
        if (mPrivateChannel != null) {
            mChordManager.leaveChannel(mPrivateChannel.getName());
        }
        mChordManager.stop();
    }

    /**
     * Joins to the channel with the specified name and stores it as a private channel.
     *
     * @param channelName
     */
    void joinPrivateChannel(String channelName) {
        mPrivateChannel = new LoggedChordChannel(mChordManager.joinChannel(channelName, mChordPrivateChannelListener),
                true);
        onJoinedToPrivateChannel(mPrivateChannel);
    }

    /**
     * Joins to the public channel (ChordManager.PUBLIC_CHANNEL).
     */
    void joinPublicChannel() {
        mPublicChannel = new LoggedChordChannel(mChordManager.joinChannel(ChordManager.PUBLIC_CHANNEL,
                mChordPublicChannelListener), false);
        onJoinedToPublicChannel(mPublicChannel);
    }

    void onChordDisconnected() {
        mBus.post(ChordDisconnectedEvent.INSTANCE);
    }

    void onChordError(int error) {
        mBus.post(ChordErrorEvent.INSTANCE);
    }

    void onChordStartFailed(int reason) {
        mBus.post(ChordStartFailedEvent.INSTANCE);
    }

    void onChordStarted(String userNodeName, int reason) {
        mBus.post(ChordStartedEvent.INSTANCE);
    }

    /**
     * Invoked when a node has left the private channel.
     *
     * @param nodeName the node's name that has left the channel
     */
    void onNodeLeftOnPrivateChannel(String nodeName) {
    }

    /**
     * Invoked when a node has joined the private channel.
     *
     * @param nodeName the node's name that has joined the channel
     */
    void onNodeJoinedOnPrivateChannel(String nodeName) {
    }

    void onNodeJoinedOnPublicChannel(String nodeName) {
        mBus.post(NodeJoinedOnPublicChannelEvent.INSTANCE);
    }

    void onNodeLeftOnPublicChannel(String nodeName) {
        mBus.post(NodeLeftOnPublicChannelEvent.INSTANCE);
    }

    void onJoinedToPrivateChannel(IChordChannel privateChannel) {
        mBus.post(JoinedToPrivateChannelEvent.INSTANCE);
    }

    void onJoinedToPublicChannel(IChordChannel publicChannel) {
        mBus.post(JoinedToPublicChannelEvent.INSTANCE);
    }

    /**
     * Sends message over public channel.
     *
     * @param message to be sent
     */
    void sendPublicMessage(ChordMessage message) {
        mPublicChannel.sendDataToAll(mPayloadType, new byte[][]{message.getBytes()});
    }

    /**
     * Sends message over private channel.
     *
     * @param message to be sent
     * @param toNode  node nome of the receiver
     */
    void sendPrivateMessage(ChordMessage message, String toNode) {
        mPrivateChannel.sendData(toNode, mPayloadType, new byte[][]{message.getBytes()});
    }

    void handlePublicMessage(ChordMessage message) {
        throw new UnsupportedOperationException(message.getType().name());
    }

    void handlePrivateMessage(ChordMessage message) {
        throw new UnsupportedOperationException(message.getType().name());
    }

    String getNodeName() {
        return mChordManager.getName();
    }

    @Override
    public void startBus() {
        mBus.register(this);
    }

    @Override
    public void stopBus() {
        mBus.unregister(this);
    }

    public enum JoinedToPrivateChannelEvent {
        INSTANCE;
    }

    public enum JoinedToPublicChannelEvent {
        INSTANCE;
    }

    public enum NodeLeftOnPublicChannelEvent {
        INSTANCE;
    }

    public enum NodeJoinedOnPublicChannelEvent {
        INSTANCE;
    }

    public enum ChordStartedEvent {
        INSTANCE;
    }

    public enum ChordStartFailedEvent {
        INSTANCE;
    }

    public enum ChordErrorEvent {
        INSTANCE;
    }

    public enum ChordDisconnectedEvent {
        INSTANCE;
    }
}
