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

import java.util.*;

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

        TreeMap<Player, Integer> playerScore = new TreeMap<Player, Integer>();
        for(Player player : players)
        {
            playerScore.put(player, player.getScore());
        }
        final GameResult gameResult = new GameResult(getWinnersList(playerScore));
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

        Map<Player, Integer> sortedMap = sortByValue(playerScores);
		final List<Player> winnersList = new ArrayList<Player>(sortedMap.keySet());

		return winnersList;
	}

    public static <K, V extends Comparable<? super V>> Map<K, V>
    sortByValue( Map<K, V> map )
    {
        List<Map.Entry<K, V>> list =
                new LinkedList<Map.Entry<K, V>>( map.entrySet() );
        Collections.sort( list, new Comparator<Map.Entry<K, V>>()
        {
            public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
            {
                return (o1.getValue()).compareTo( o2.getValue() );
            }
        } );

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list)
        {
            result.put( entry.getKey(), entry.getValue() );
        }
        return result;
    }

	static class GameResult {

		private List<Player> mWinners;

		/**
		 * Constructor method. GameResult holds all game information - which player has what cards.
         * @param winnersList
         */
		public GameResult(List<Player> winnersList) {
            this.mWinners = winnersList;
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