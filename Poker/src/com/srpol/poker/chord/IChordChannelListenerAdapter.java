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
package com.srpol.poker.chord;

import com.samsung.chord.IChordChannelListener;

/**
 * Adapter for the {@link IChordChannelListener}.
 */
public class IChordChannelListenerAdapter implements IChordChannelListener {

	@Override
	public void onDataReceived(String fromNode, String fromChannel, String payloadType, byte[][] payload) {
	}

	@Override
	public void onFileChunkReceived(String fromNode, String fromChannel, String fileName, String hash, String fileType,
			String exchangeId, long fileSize, long offset) {
	}

	@Override
	public void onFileChunkSent(String toNode, String toChannel, String fileName, String hash, String fileType,
			String exchangeId, long fileSize, long offset, long chunkSize) {
	}

	@Override
	public void onFileFailed(String node, String channel, String fileName, String hash, String exchangeId, int reason) {
	}

	@Override
	public void onFileReceived(String fromNode, String fromChannel, String fileName, String hash, String fileType,
			String exchangeId, long fileSize, String tmpFilePath) {
	}

	@Override
	public void onFileSent(String toNode, String toChannel, String fileName, String hash, String fileType,
			String exchangeId) {
	}

	@Override
	public void onFileWillReceive(String fromNode, String fromChannel, String fileName, String hash, String fileType,
			String exchangeId, long fileSize) {
	}

	@Override
	public void onNodeJoined(String fromNode, String fromChannel) {
	}

	@Override
	public void onNodeLeft(String fromNode, String fromChannel) {
	}

}
