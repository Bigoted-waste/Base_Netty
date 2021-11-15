package com.cola.Netty.Demo.ChatRoom.server.handler;

import com.cola.Netty.Demo.ChatRoom.message.RpcRequestMessage;
import com.cola.Netty.Demo.ChatRoom.message.RpcResponseMessage;
import com.cola.Netty.Demo.ChatRoom.server.service.HelloService;
import com.cola.Netty.Demo.ChatRoom.server.service.ServicesFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage message) throws Exception {
        RpcResponseMessage responseMessage = new RpcResponseMessage();
        responseMessage.setSequenceId(message.getSequenceId());
        try {
            HelloService service = (HelloService)
                    ServicesFactory.getService(Class.forName(message.getInterfaceName()));
            Method method = service.getClass().getMethod(message.getMethodName(), message.getParameterTypes());
            Object invoke = method.invoke(service, message.getParameterValue());
            responseMessage.setReturnValue(invoke);
        } catch (Exception e) {
            e.printStackTrace();
            String msg = e.getCause().getMessage();
            responseMessage.setExceptionValue(new Exception("远程调用出错:"+msg));
        }
        ctx.writeAndFlush(responseMessage);
    }


    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RpcRequestMessage message = new RpcRequestMessage(
                1,
                "com.cola.Netty.Demo.ChatRoom.server.service.HelloService",
                "sayHello",
                String.class,
                new Class[]{String.class},
                new Object[]{"张三"}
        );
        HelloService service = (HelloService)
                ServicesFactory.getService(Class.forName(message.getInterfaceName()));
        Method method = service.getClass().getMethod(message.getMethodName(), message.getParameterTypes());
        Object invoke = method.invoke(service, message.getParameterValue());
        System.out.println(invoke);
    }

}
