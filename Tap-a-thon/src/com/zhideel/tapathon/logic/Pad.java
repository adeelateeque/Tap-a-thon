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

import java.io.Serializable;

/**
 * Represents individual card used in the Tapathon game.
 */
public class Pad implements Serializable {

	private static final long serialVersionUID = 20130327L;

	private final PadColor mColor;
	private final PadSymbol symbol;

	public Pad(PadColor color, PadSymbol symbol) {
		this.mColor = color;
		this.symbol = symbol;
	}

	public Pad(Pad pad) {
		mColor = pad.mColor;
		symbol = pad.symbol;
	}

	public PadColor getColor() {
		return mColor;
	}

	public PadSymbol getSymbol() {
		return symbol;
	}

	/**
	 * Represents color of the {@link Pad}.
	 */
	public enum PadColor {

		BLUE, MAGENTA, RED, YELLOW;

		@Override
		public String toString() {
			return name();
		}

	}

	/**
	 * Represents symbol of the {@link Pad}.
	 */
	public enum PadSymbol {

		ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, DIVIDE, MULTIPLY, PLUS, MINUS;

		@Override
		public String toString() {
			return name();
		}
	}

	@Override
	public String toString() {
		return symbol.name() + " of " + mColor.name();
	}

}
