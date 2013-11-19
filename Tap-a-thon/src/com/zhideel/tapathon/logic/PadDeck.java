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

/**
 * Represents deck of 52 {@link Pad}s.
 */
public class PadDeck {

	private final List<Pad> mPads;

	public PadDeck() {
		mPads = new ArrayList<Pad>();

		initializeDeck();
	}


	private void initializeDeck() {
		for(int i=0; i<12; i++)
        {
            mPads.add(i, new Pad(Pad.PadColor.MAGENTA, Pad.PadSymbol.ZERO));
        }
	}

	@Override
	public String toString() {
		return mPads.toString();
	}

}
