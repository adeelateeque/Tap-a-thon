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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import static com.zhideel.tapathon.utils.Preconditions.checkNotNull;

/**
 * Represents single message passed over Chord channels.
 */
public final class ChordMessage implements Serializable {

	private static final long serialVersionUID = 20130408L;

	static final String PRIVATE_CHANNEL_NAME = "PRIVATE_CHANNEL_NAME";
	static final String SERVER_NODE_NAME = "SERVER_NODE_NAME";
	static final String USERNAME = "USERNAME";

	public static final String SCORE = "SCORE";

	private final MessageType mType;
	private final Map<String, Object> mPayload;
	private String mSenderNodeName;
	private String mReceiverNodeName;
	private boolean mIsFromLogic;

	private ChordMessage(MessageType type) {
		mType = type;
		mPayload = new HashMap<String, Object>();
	}

	/**
	 * Creates and returns {@link ChordMessage} of the specified {@link MessageType}.
	 */
	public static ChordMessage obtainMessage(MessageType type) {
		return new ChordMessage(type);
	}

	public void putInt(String key, int value) {
		mPayload.put(key, value);
	}

	public void putString(String key, String value) {
		mPayload.put(key, value);
	}

	public void putObject(String key, Object value) {
		mPayload.put(key, value);
	}

	public int getInt(String key) {
		return (Integer) mPayload.get(key);
	}

	public String getString(String key) {
		return (String) mPayload.get(key);
	}

	public Object getObject(String key) {
		return mPayload.get(key);
	}

	public MessageType getType() {
		return mType;
	}

	public String getSenderNodeName() {
		return checkNotNull(mSenderNodeName);
	}

	public void setSenderNodeName(String senderNodeName) {
		mSenderNodeName = senderNodeName;
	}

	public String getReceiverNodeName() {
		return mReceiverNodeName;
	}

	public void setReceiverNodeName(String receiverNodeName) {
		mReceiverNodeName = receiverNodeName;
	}

	public boolean isFromLogic() {
		return mIsFromLogic;
	}

	public void setFromLogic(boolean isFromLogic) {
		mIsFromLogic = isFromLogic;
	}

	/**
	 * Returns {@link ChordMessage} in the form of byte array.
	 */
	byte[] getBytes() {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final ObjectOutputStream os;

		try {
			os = new ObjectOutputStream(out);
			os.writeObject(this);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return out.toByteArray();
	}

	/**
	 * Recreates {@link ChordMessage} from the byte array and sender node name.
	 * 
	 * @param data
	 *            byte array representing {@link ChordMessage}
	 * @param senderNodeName
	 *            node name of the message's sender
	 * @return
	 */
	public static ChordMessage obtainChordMessage(byte[] data, String senderNodeName) {
		final ByteArrayInputStream in = new ByteArrayInputStream(data);
		final ObjectInputStream is;
		ChordMessage message = null;

		try {
			is = new ObjectInputStream(in);
			message = (ChordMessage) is.readObject();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		message.mSenderNodeName = senderNodeName;
		return message;
	}

	@Override
	public String toString() {
		return "ChordMessage [mType=" + mType + ", mPayload=" + mPayload + ", mSenderNodeName=" + mSenderNodeName
				+ ", mReceiverNodeName=" + mReceiverNodeName + "]";
	}

	public enum MessageType {
		// @formatter:off
		
		// Client messages.
		GET_SERVERS_LIST,
		USERNAME,
		
		// Server messages.
		SERVER_NAME_BROADCAST,
		SERVER_NODE_NAME,
		GAME_END;
		// @formatter:on

		@Override
		public String toString() {
			return name();
		}

	}

}
