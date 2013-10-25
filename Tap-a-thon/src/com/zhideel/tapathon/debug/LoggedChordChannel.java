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

import java.io.FileNotFoundException;
import java.util.List;

import android.util.Log;

import com.samsung.chord.IChordChannel;
import com.zhideel.tapathon.chord.ChordMessage;
import com.zhideel.tapathon.ui.GameMenuActivity;

/**
 * Helper class that adds logging capabilities to some of the IChordChannel methods. Can be used for debugging purposes.
 */
public class LoggedChordChannel implements IChordChannel {

	private final IChordChannel mChordChannel;
	private final boolean mIsPrivate;

	/**
	 * Creates LoggedChordChannel that can be used for debugging purposes.
	 * 
	 * @param channel
	 *            IChordChannel wrapped in LoggedChordChannel
	 * @param isPrivate
	 *            indicates if channel is public or private (used for channel description)
	 */
	public LoggedChordChannel(IChordChannel channel, boolean isPrivate) {
		mChordChannel = channel;
		mIsPrivate = isPrivate;
	}

	@Override
	public String getName() {
		return mChordChannel.getName();
	}

	@Override
	public boolean isName(String channelName) {
		return mChordChannel.isName(channelName);
	}

	@Override
	public List<String> getJoinedNodeList() {
		return mChordChannel.getJoinedNodeList();
	}

	@Override
	public boolean sendData(String toNode, String payloadType, byte[][] payload) {
		Log.d(GameMenuActivity.TAG, "sendData[" + getType() + getName() + "][" + toNode + "] "
				+ getMessageDescription(payload));
		return mChordChannel.sendData(toNode, payloadType, payload);
	}

	@Override
	public boolean sendDataToAll(String payloadType, byte[][] payload) {
		Log.d(GameMenuActivity.TAG, "sendDataToAll[" + getType() + getName() + "][ALL] " + getMessageDescription(payload));
		return mChordChannel.sendDataToAll(payloadType, payload);
	}

	@Override
	public String sendFile(String toNode, String fileType, String filePath, long timeoutMsc) {
		Log.d(GameMenuActivity.TAG, "sendFile[" + getName() + "][" + toNode + "]");
		return mChordChannel.sendFile(toNode, fileType, filePath, timeoutMsc);
	}

	@Override
	public boolean acceptFile(String exchangeId, long chunkTimeoutMsc, int chunkRetries, long chunkSize) {
		return mChordChannel.acceptFile(exchangeId, chunkTimeoutMsc, chunkRetries, chunkSize);
	}

	@Override
	public boolean rejectFile(String exchangeId) {
		return mChordChannel.rejectFile(exchangeId);
	}

	@Override
	public boolean cancelFile(String exchangeId) {
		return mChordChannel.cancelFile(exchangeId);
	}

	@Override
	public String getNodeIpAddress(String nodeName) {
		return mChordChannel.getNodeIpAddress(nodeName);
	}

	private String getMessageDescription(byte[][] payload) {
		return ChordMessage.obtainChordMessage(payload[0], null).toString();
	}

	private String getType() {
		return mIsPrivate ? "(PRIVATE)" : "(PUBLIC)";
	}

	@Override
	public void acceptMultiFiles(String arg0, int arg1, int arg2, long arg3) {
		
	}

	@Override
	public void cancelMultiFiles(String arg0) {
		
	}

	@Override
	public boolean isSecureChannel() {
		return false;
	}

	@Override
	public void rejectMultiFiles(String arg0) {
		
	}

	@Override
	public String sendMultiFiles(String arg0, String arg1, List<String> arg2, int arg3) throws FileNotFoundException {
		return null;
	}

}
