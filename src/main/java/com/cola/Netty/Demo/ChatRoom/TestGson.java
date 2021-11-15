package com.cola.Netty.Demo.ChatRoom;

import com.cola.Netty.Demo.ChatRoom.protocol.Serializer;
import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * 解决
 */
public class TestGson {
    public static void main(String[] args) {
        Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new Serializer.ClassCodec()).create();
        System.out.println(gson.toJson(String.class));
    }
}
