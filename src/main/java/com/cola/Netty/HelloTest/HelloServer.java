package com.cola.Netty.HelloTest;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;


/*
    11.客户端连接服务器
    12.在建立连接后，客户端服务器同时调用初始化方法 ChannelInitializer
    15.把hello，转为ByteBuf
    16.由某个EventLoop处理read事件，接收到ByteBuf
    17.将ByteBud 还原为 hello
    18.执行read方法，打印hello
 */
public class HelloServer {
    public static void main(String[] args) {
        //1.服务端启动器,负责组装 netty 组件
        new ServerBootstrap()
                //2.BossEventLoop , WorkerEventLoop(Selector,thread)
                .group(new NioEventLoopGroup())
                //3.选择服务器的 ServerSocketChannel 的实现
                .channel(NioServerSocketChannel.class)
                //4. boss 负责处理连接的 ， worker(child) 负责处理读写 ,决定了 worker(child) 能执行那些操作 (handler)
                .childHandler(
                        //5. channel 代表和客户端进行数据读写的通道 Initializer 初始化 , 负责添加别的handler
                        new ChannelInitializer<NioSocketChannel>() {
                            @Override
                            protected void initChannel(NioSocketChannel ch) throws Exception {
                                //6. 添加具体 handler
                                ch.pipeline().addLast(new StringDecoder()); //将 ByteBuf 转化为字符串
                                ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {  //自定义 handler
                                    @Override   //读事件
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        // 打印上一步转换好的字符串
                                        System.out.println(msg);
                                    }
                                });
                            }
                        })
                //7. 绑定监听端口
                .bind(8999);
    }
}
