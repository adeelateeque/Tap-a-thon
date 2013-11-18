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

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.zhideel.tapathon.chord.BusEvent;
import com.zhideel.tapathon.logic.CommunicationBus.BusManager;
import com.zhideel.tapathon.ui.GamePadActivity.GameActivityEvent;

/**
 * Contains the game model used on the client side.
 */
public class ClientModel implements BusManager {

	private final Bus mBus;

	private int mScore;
	private Pair<Pad, Pad> mCards;

	ClientModel() {
		mBus = CommunicationBus.getInstance();
	}

	/**
	 * Updates model and view when game finished.
	 * 
	 * @param event
	 *            containing information about game finish
	 */
	@Subscribe
	public void gameEnd(ClientModelEvent.GameEnd event) {
		mScore += event.getScore();

		postToGameActivity(new GameActivityEvent.GameEndEvent());
		//postToGameActivity(new GameActivityEvent.AmountEvent(mAmount, mBidAmount, mMinimumBidAmount));
	}

	@Override
	public void startBus() {
		mBus.register(this);
	}

	@Override
	public void stopBus() {
		mBus.unregister(this);
	}

	private <T extends GameActivityEvent> void postToGameActivity(T event) {
		mBus.post(event);
	}

	/**
	 * Represents event posted through the {@link com.squareup.otto.Bus} from the GameChord to update {@ClientModel}.
	 */
	public static class ClientModelEvent extends BusEvent {

		private static final long serialVersionUID = 20130321L;

		private final ClientModelEventType mType;

		private ClientModelEvent(ClientModelEventType type) {
			super();
			mType = type;
		}

		public ClientModelEventType getType() {
			return mType;
		}

		public static class GameEnd extends ClientModelEvent {

			private static final long serialVersionUID = 20130403L;

			public static final String SCORE = "SCORE";

			public GameEnd(int score) {
				super(ClientModelEventType.WON);
				putInt(SCORE, score);
			}

			public int getScore() {
				return getInt(SCORE);
			}
		}

	}

	/**
	 * Represents type of the {@link com.zhideel.tapathon.logic.ClientModel.ClientModelEvent}.
	 */
	public static enum ClientModelEventType {
		//@formatter:off
		WON,
		LOSE;
		//@formatter:off
		
		@Override
		public String toString() {
			return name();
		}
	}
}