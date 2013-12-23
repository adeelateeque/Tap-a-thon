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
package com.zhideel.tapathon.logic;

import com.zhideel.tapathon.utils.Preconditions;

/**
 * Represents a player's state in the poker game.
 */
public final class Player implements Comparable<Player> {

	private final String mName;
	private final String mNodeName;
	private int mScore;

	public Player(String playerName, String nodeName) {
		mName = Preconditions.checkNotNull(playerName);
		mNodeName = Preconditions.checkNotNull(nodeName);
		mScore = ServerModel.INITIAL_SCORE;
	}

	/**
	 * Creates new player instance
	 * 
	 * @param name
	 *            the player's name
	 * @param nodeName
	 *            the player's Chord node name
	 * @return {@link com.zhideel.tapathon.logic.Player} instance
	 */
	public static Player createPlayer(String name, String nodeName) {
		return new Player(name, nodeName);
	}

	/**
	 * Adds the given amount to the player's pool.
	 * 
	 * @param score
	 *            amount to add to the player's pool
	 */
	public void addScore(int score) {
		mScore += score;
	}

	public int getScore() {
		return mScore;
	}

	public String getName() {
		return mName;
	}

	public String getNodeName() {
		return mNodeName;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}

		final Player other = (Player) obj;
		return mNodeName.equals(other.mNodeName);
	}

	@Override
	public int hashCode() {
		final String hash = mNodeName + mName;
		return hash.hashCode();
	}

	@Override
	public String toString() {
		return mName;
	}

	@Override
	public int compareTo(Player another) {
		if (mScore > another.mScore) {
			return 1;
		} else if (mScore < another.mScore) {
			return -1;
		} else {
			return 0;
		}
	}
}
