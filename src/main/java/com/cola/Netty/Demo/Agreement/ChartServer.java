package com.cola.Netty.Demo.Agreement;

import com.cola.Netty.Demo.Agreement.message.Message;
import com.cola.Netty.Demo.Agreement.protocol.MessageCodec;
import com.cola.Netty.Demo.Agreement.protocol.MessageCodecSharable;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.ServerSocket;

@Slf4j
public class ChartServer {
    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.group(boss,worker);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    // 粘包半包解码器
                    ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(
                            1024,12,4,0,0));
                    ch.pipeline().addLast(LOGGING_HANDLER);
                    // 自定义编码器
                    ch.pipeline().addLast(MESSAGE_CODEC);
                }
            });
            ChannelFuture channelFuture = serverBootstrap.bind(8999).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.debug("server error",e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
