package com.cola.Netty.Demo.ChatRoom.message;

public class PongMessage extends Message{

    @Override
    public int getMessageType() {
        return PongMessage;
    }
}
