package com.cola.Netty.Optimization;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestByteBuf {
    public static void main(String[] args) {
        /* 在vm加参数来更改ByteBuf的状态
            1、是否支持池化 (默认是开启池化)
                -Dio.netty.allocator.type=unpooled
            2、使用堆内存还是使用直接内存 (默认使用直接内存)
                -Dio.netty.noPreferDirect=true
         */

        new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LoggingHandler());
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                //UnpooledByteBufAllocator$InstrumentedUnpooledUnsafeHeapByteBuf    非池化的堆内存
//                                ByteBuf buf = ctx.alloc().buffer();
//                                log.debug("alloc buf {}",buf);

                                // 从网络上读写数据的时候,直接内存比堆内存效率高,所以Netty对这个IO操作强制使用 DirectByteBuf
                                //UnpooledByteBufAllocator$InstrumentedUnpooledUnsafeNoCleanerDirectByteBuf  非池化的直接内存
                                log.debug("receive buf {}",msg);
                                System.out.println("");
                            }
                        });
                    }
                }).bind(8999);
    }
}
