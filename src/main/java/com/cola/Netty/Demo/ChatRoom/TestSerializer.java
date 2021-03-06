package com.cola.Netty.Demo.ChatRoom;

import com.cola.Netty.Demo.ChatRoom.config.Config;
import com.cola.Netty.Demo.ChatRoom.message.LoginRequestMessage;
import com.cola.Netty.Demo.ChatRoom.message.Message;
import com.cola.Netty.Demo.ChatRoom.protocol.MessageCodecSharable;
import com.cola.Netty.Demo.ChatRoom.protocol.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.logging.LoggingHandler;

/**
 * 测试实现序列化、反序列化的方法
 */
public class TestSerializer {
    public static void main(String[] args) {
        MessageCodecSharable CODEC = new MessageCodecSharable();
        LoggingHandler LOGGING = new LoggingHandler();
        EmbeddedChannel channel = new EmbeddedChannel(LOGGING, CODEC, LOGGING);

        LoginRequestMessage message = new LoginRequestMessage("zhangsan", "123");
//        channel.writeOutbound(message);

        ByteBuf buf = messageToBuf(message);
        channel.writeInbound(buf);
    }

    public static ByteBuf messageToBuf(Message msg){
        int algorithm = Config.getSerializerAlgorithm().ordinal();
        ByteBuf out = ByteBufAllocator.DEFAULT.buffer();
        out.writeBytes(new byte[]{1,2,3,4});
        out.writeByte(1);
        out.writeByte(algorithm);
        out.writeByte(msg.getMessageType());
        out.writeInt(msg.getSequenceId());
        out.writeByte(0xff);
        byte[] bytes = Serializer.Algorithm.values()[algorithm].serialize(msg);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
        return out;
    }
}
