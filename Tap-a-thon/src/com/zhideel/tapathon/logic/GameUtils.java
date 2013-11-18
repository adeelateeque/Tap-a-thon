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

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class GameUtils {

	private static final int MAX_CARDS_NUMBER = 7;

	private GameUtils() {
		// No. op.
	}

	/**
	 * Returns game result. Contains the players and their hands.
	 * 
	 * @param players
	 *            A list of all players.
	 * @return A GameResult object.
	 */
	public static GameResult getGameResult(List<Player> players) {
        final GameResult gameResult = new GameResult();
        
		return gameResult;
	}

	/**
	 * Transfers the winners from the map to a List.
	 * 
	 * @param playerScores
	 *            Map containing the state of the game, all the players and their scores.
	 * @return A list of winners. Usually just one, but can be more if there is a tie.
	 */
	private static List<Player> getWinnersList(TreeMap<Player, Integer> playerScores) {
		final List<Player> winnersList = new ArrayList<Player>();

		return winnersList;
	}

	static class GameResult {

		private List<Player> mWinners;

		/**
		 * Constructor method. GameResult holds all game information - which player has what cards.
		 */
		public GameResult() {

		}

		/**
		 * Returns the name of the players highest hand.
		 *
		 * @param player
		 *            The player whose hand is required.
		 * @return The score of the player.
		 */
		public Integer getPlayerScore(Player player) {
			return player.getScore();
		}

		/**
		 * Returns the list of winners. Usually only one, except when there is a tie.
		 * 
		 * @return
		 */
		public List<Player> getWinners() {
			return mWinners;
		}
	}
}