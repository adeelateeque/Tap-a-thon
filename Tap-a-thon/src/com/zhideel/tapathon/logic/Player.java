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

import android.util.Pair;
import com.zhideel.tapathon.utils.Preconditions;

import java.util.Arrays;

/**
 * Represents a player's state in the poker game.
 */
public final class Player implements Comparable<Player> {

	private final String mName;
	private final String mNodeName;
	private final Pad[] mPads;
	private int mScore;


	private Player(String playerName, String nodeName) {
		mName = Preconditions.checkNotNull(playerName);
		mNodeName = Preconditions.checkNotNull(nodeName);
		mPads = new Pad[2];
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
	 * Takes the given amount from the player's pool.
	 * 
	 * @param amount
	 *            an amount to take from the player's pool
	 */
	public void takeAmount(int amount) {
		final int newAmount = mScore - amount;
		Preconditions.checkState(newAmount >= 0);
		mScore = newAmount;
	}

	/**
	 * Adds the given amount to the player's pool.
	 * 
	 * @param amount
	 *            amount to add to the player's pool
	 */
	public void addAmount(int amount) {
		mScore += amount;
	}

	/**
	 * Returns single card.
	 * 
	 * @param index
	 *            index of the card (could be 0 or 1)
	 * @return returns the card at the given index
	 * @throws IllegalArgumentException
	 *             thrown when the given index is different than 0 or 1
	 */
	public Pad getCard(int index) {
		if (index < 0 || index > 1) {
			throw new IllegalArgumentException(Integer.toString(index));
		}
		return mPads[index];
	}

	/**
	 * Sets the player's cards.
	 * 
	 * @param cards
	 *            the pair of cards to set
	 */
	public void setCards(Pair<Pad, Pad> cards) {
		mPads[0] = cards.first;
		mPads[1] = cards.second;
	}

	/**
	 * Clears player's cards.
	 */
	public void clearCards() {
		mPads[0] = mPads[1] = null;
	}

	/**
	 * Checks if player has cards.
	 * 
	 * @return boolean that indicates if the player has cards
	 */
	public boolean hasCards() {
		return mPads[0] != null && mPads[1] != null;
	}

	public Pad[] getCards() {
		return Arrays.copyOf(mPads, mPads.length);
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
