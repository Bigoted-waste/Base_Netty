package com.cola.Netty.Demo.ChatRoom.server.service;

public class HelloServiceImpl implements HelloService{
    @Override
    public String sayHello(String name) {
//        int a=1/0;
        return "你好: "+name;
    }
}
