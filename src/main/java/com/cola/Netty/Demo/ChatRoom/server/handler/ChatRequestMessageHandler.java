package com.cola.Netty.Demo.ChatRoom.server.handler;

import com.cola.Netty.Demo.ChatRoom.message.ChatRequestMessage;
import com.cola.Netty.Demo.ChatRoom.message.ChatResponseMessage;
import com.cola.Netty.Demo.ChatRoom.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class ChatRequestMessageHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception {
        String to = msg.getTo();
        Channel channel = SessionFactory.getSession().getChannel(to);
        if(channel!=null){
            //在线
            channel.writeAndFlush(new ChatResponseMessage(msg.getFrom(),msg.getContent()));
        }else {
            //不在线
            ctx.writeAndFlush(new ChatResponseMessage(false,"对方不在线！"));
        }
    }
}
