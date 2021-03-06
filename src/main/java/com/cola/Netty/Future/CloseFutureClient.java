package com.cola.Netty.Future;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Scanner;

@Slf4j
public class CloseFutureClient {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        ChannelFuture channelFuture = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override   //在连接建立后被调用
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                // 1.连接客户端
                // 异步非阻塞, main 发起了调用 ，真正执行 connect 是 nio线程
                .connect(new InetSocketAddress("127.0.0.1", 8999)); //1s 后
        Channel channel = channelFuture.sync().channel();
        log.debug("{}",channel);
        new Thread(()->{
            Scanner scanner = new Scanner(System.in);
            while (true){
                String line = scanner.nextLine();
                if("q".equals(line)){
                    channel.close();    //close 异步操作 1s之后
//                    log.debug("处理关闭之后的操作！");
                    break;
                }
                channel.writeAndFlush(line);
            }
        },"input").start();

        // 获取 ClosedFuture 对象 1). 同步处理关闭， 2)异步处理关闭
        ChannelFuture closeFuture = channel.closeFuture();
//        System.out.println("waiting close.....");
//        closeFuture.sync();
//        log.debug("处理关闭之后的操作！");
        closeFuture.addListener((ChannelFutureListener) future -> {
            log.debug("处理关闭之后的操作！");
            group.shutdownGracefully();
        });

    }
}
