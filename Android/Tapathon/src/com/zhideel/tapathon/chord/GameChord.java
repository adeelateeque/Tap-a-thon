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

import android.content.Context;
import com.zhideel.tapathon.logic.ClientModel;

/**
 * Class responsible for handling {@link ChordMessage}s related to the game.
 */
public abstract class GameChord extends AbstractChord {

    final String mUserName;
    final String mRoomName;

    GameChord(Context context, String roomName, String gameName, String userName) {
        super(context, gameName);
        mUserName = userName;
        mRoomName = roomName;
    }

    @Override
    void handlePrivateMessage(ChordMessage message) {
        switch (message.getType()) {
            case GAME_START:
                mBus.post(new ClientModel.ClientModelEvent.GameStart());
                break;
            case GAME_END:
                mBus.post(new ClientModel.ClientModelEvent.GameEnd(message.getInt(ChordMessage.SCORE)));
                break;
            case GET_SERVERS_LIST:
            case SERVER_NAME_BROADCAST:
            case USERNAME:
            case SERVER_NODE_NAME:
                throw new IllegalArgumentException(message.getType().name());
            default:
                super.handlePrivateMessage(message);
        }
    }

    public enum ServerDisconnectedEvent {
        INSTANCE;
    }

    public static class ClientDisconnectedEvent {

        private final String mNodeName;

        public ClientDisconnectedEvent(String nodeName) {
            mNodeName = nodeName;
        }

        public String getNodeName() {
            return mNodeName;
        }
    }

    public enum JoinedToServerEvent {
        INSTANCE;
    }

}
