package com.cola.Netty.HelloTest;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HelloClient {
    public static void main(String[] args) throws InterruptedException, IOException {
        //1. 启动器
        new Bootstrap()
                //2. 添加 EventLoop
                .group(new NioEventLoopGroup())
                //3. 选择客户端 Channel 实现
                .channel(NioSocketChannel.class)
                //4. 添加处理器
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override   //在连接建立后被调用
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                             ch.pipeline().addLast(new StringEncoder());
                    }
                })
                // 5.连接客户端
                .connect(new InetSocketAddress("127.0.0.1",8999))
                .sync()
                .channel()
                // 6. 向服务端发送数据
                .writeAndFlush("hello,world");
        System.in.read();
    }
}
