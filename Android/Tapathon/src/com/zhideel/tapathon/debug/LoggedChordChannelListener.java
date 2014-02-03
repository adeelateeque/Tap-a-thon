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
package com.zhideel.tapathon.debug;

import android.util.Log;
import com.samsung.chord.IChordChannelListener;
import com.zhideel.tapathon.chord.ChordMessage;
import com.zhideel.tapathon.ui.GameMenuActivity;

/**
 * Helper class that adds logging capabilities to some of the IChordChannelListener methods. Can be used for debugging
 * purposes.
 */
public class LoggedChordChannelListener implements IChordChannelListener {

    private final IChordChannelListener mChordChannelListener;

    /**
     * Creates LoggedChordChannelListener that can be used for debugging purposes.
     *
     * @param listener IChordChannelListener wrapped in LoggedChordChannelListener
     */
    public LoggedChordChannelListener(IChordChannelListener listener) {
        mChordChannelListener = listener;
    }

    @Override
    public void onDataReceived(String fromNode, String fromChannel, String payloadType, byte[][] payload) {
        Log.d(GameMenuActivity.TAG, "onDataReceived[CHANNEL:" + fromChannel + "][" + fromNode + "] "
                + getMessageDescription(payload));
        mChordChannelListener.onDataReceived(fromNode, fromChannel, payloadType, payload);
    }

    @Override
    public void onFileChunkReceived(String fromNode, String fromChannel, String fileName, String hash, String fileType,
                                    String exchangeId, long fileSize, long offset) {
        mChordChannelListener.onFileChunkReceived(fromNode, fromChannel, fileName, hash, fileType, exchangeId,
                fileSize, offset);
    }

    @Override
    public void onFileChunkSent(String toNode, String toChannel, String fileName, String hash, String fileType,
                                String exchangeId, long fileSize, long offset, long chunkSize) {
        mChordChannelListener.onFileChunkSent(toNode, toChannel, fileName, hash, fileType, exchangeId, fileSize,
                offset, chunkSize);
    }

    @Override
    public void onFileFailed(String node, String channel, String fileName, String hash, String exchangeId, int reason) {
        mChordChannelListener.onFileFailed(node, channel, fileName, hash, exchangeId, reason);
    }

    @Override
    public void onFileReceived(String fromNode, String fromChannel, String fileName, String hash, String fileType,
                               String exchangeId, long fileSize, String tmpFilePath) {
        mChordChannelListener.onFileReceived(fromNode, fromChannel, fileName, hash, fileType, exchangeId, fileSize,
                tmpFilePath);
    }

    @Override
    public void onFileSent(String toNode, String toChannel, String fileName, String hash, String fileType,
                           String exchangeId) {
        mChordChannelListener.onFileSent(toNode, toChannel, fileName, hash, fileType, exchangeId);
    }

    @Override
    public void onFileWillReceive(String fromNode, String fromChannel, String fileName, String hash, String fileType,
                                  String exchangeId, long fileSize) {
        mChordChannelListener.onFileWillReceive(fromNode, fromChannel, fileName, hash, fileType, exchangeId, fileSize);
    }

    @Override
    public void onNodeJoined(String fromNode, String fromChannel) {
        mChordChannelListener.onNodeJoined(fromNode, fromChannel);
    }

    @Override
    public void onNodeLeft(String fromNode, String fromChannel) {
        mChordChannelListener.onNodeLeft(fromNode, fromChannel);
    }

    private String getMessageDescription(byte[][] payload) {
        return ChordMessage.obtainChordMessage(payload[0], null).toString();
    }

    @Override
    public void onMultiFilesChunkReceived(String arg0, String arg1, String arg2, String arg3, int arg4, String arg5, long arg6, long arg7) {

    }

    @Override
    public void onMultiFilesChunkSent(String arg0, String arg1, String arg2, String arg3, int arg4, String arg5, long arg6, long arg7, long arg8) {

    }

    @Override
    public void onMultiFilesFailed(String arg0, String arg1, String arg2, String arg3, int arg4, int arg5) {

    }

    @Override
    public void onMultiFilesFinished(String arg0, String arg1, String arg2, int arg3) {

    }

    @Override
    public void onMultiFilesReceived(String arg0, String arg1, String arg2, String arg3, int arg4, String arg5, long arg6, String arg7) {

    }

    @Override
    public void onMultiFilesSent(String arg0, String arg1, String arg2, String arg3, int arg4, String arg5) {

    }

    @Override
    public void onMultiFilesWillReceive(String arg0, String arg1, String arg2, String arg3, int arg4, String arg5, long arg6) {

    }

}
