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

import com.squareup.otto.Bus;
import com.zhideel.tapathon.logic.CommunicationBus.BusManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains the game model used on the server side.
 */
public class ServerModel implements BusManager {

	/**
	 * Specifies initial amount of tokens each player has at the beginning of the game.
	 */
	public static final int INITIAL_SCORE = 0;
	public static final int MAX_PLAYER_NUMBER = 4;
	public static final int MIN_PLAYER_NUMBER = 2;

	private final List<Player> mPlayers;
	private final Bus mBus;
	private PadDeck mDeck;
	private GameState mGameState;

	public ServerModel() {
		mPlayers = new ArrayList<Player>();
		mBus = CommunicationBus.getInstance();
		mGameState = GameState.NOT_STARTED;
	}

	void addPlayer(Player player) {
		mPlayers.add(player);
	}

	void removePlayer(Player player) {
		Player toRemove = null;

		for (Player p : mPlayers) {
			if (p.getNodeName().equalsIgnoreCase(player.getNodeName())) {
				toRemove = p;
				break;
			}
		}

		mPlayers.remove(toRemove);
	}

	/**
	 * Return player with the specified node name
	 * 
	 * @param nodeName
	 *            name of the player's node to be returned
	 * @return player with the specified name or null if such player does not exist.
	 */
	Player getPlayer(String nodeName) {
		for (Player player : mPlayers) {
			if (player.getNodeName().equalsIgnoreCase(nodeName)) {
				return player;
			}
		}
		return null;
	}

	public List<Player> getPlayers() {
		return new ArrayList<Player>(mPlayers);
	}

	PadDeck getDeck() {
		return mDeck;
	}

	GameState getGameState() {
		return mGameState;
	}

	void setGameState(GameState gameState) {
		mGameState = gameState;
	}

	/**
	 * Performs table initialization.
	 */
	void initTable() {
		mDeck = new PadDeck();
	}

	/**
	 * Clear state of the current game.
	 */
	void clearGame() {

	}

	@Override
	public void startBus() {
		mBus.register(this);
	}

	@Override
	public void stopBus() {
		mBus.unregister(this);
	}

	/**
	 * Indicates what state the game currently is at.
	 */
	public enum GameState {
		//@formatter:off
		NOT_STARTED,
        STARTED,
        RESUMED,
        PAUSED,
		GAME_FINISHED;
		//@formatter:on

		/**
		 * Returns {@link com.zhideel.tapathon.logic.ServerModel.GameState} that follows current state.
		 * 
		 * @param currentState
		 *            of the game
		 * @return next {@link com.zhideel.tapathon.logic.ServerModel.GameState}
		 */
		public static GameState getNextState(GameState currentState) {
			switch (currentState) {
			case NOT_STARTED:
				return STARTED;
			case STARTED:
				return PAUSED;
			case PAUSED:
				return RESUMED;
			case RESUMED:
				return GAME_FINISHED;
			default:
				throw new IllegalArgumentException(currentState.name());
			}
		}

	}

}
