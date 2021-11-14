package com.cola.Netty.Demo.ChatRoom.message;

public class PingMessage extends Message{
    @Override
    public int getMessageType() {
        return PingMessage;
    }
}
