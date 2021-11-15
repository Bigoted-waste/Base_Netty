package com.cola.Netty.Demo.ChatRoom.server.handler;

import com.cola.Netty.Demo.ChatRoom.message.RpcResponseMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@ChannelHandler.Sharable
public class RpcResponseMessageHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {

    //                        序号     用来接受结果的 RpcResponseMessageHandlerPromise 对象
    public static final Map<Integer, Promise<Object>> PROMISES=new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseMessage msg) throws Exception {

        log.debug("{}",msg);
//        Set<Map.Entry<Integer, Promise<Object>>> entries = PROMISES.entrySet();
//        Iterator<Map.Entry<Integer, Promise<Object>>> iterator = entries.iterator();
//        while (iterator.hasNext()){
//            System.out.println(iterator.next());
//        }

//        System.out.println("");
        // 拿到空的 promise
        Promise<Object> promise = PROMISES.get(msg.getSequenceId());

        if (promise!=null) {
            Object returnValue = msg.getReturnValue();
//            System.out.println(returnValue);
            Exception exceptionValue = msg.getExceptionValue();
            if(exceptionValue!=null) {
                promise.setFailure(exceptionValue);
            }else {
                promise.setSuccess(returnValue);
            }
        }

    }
}
