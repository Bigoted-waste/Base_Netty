package com.cola.NIO.Net;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import static com.cola.NIO.Buffer.ByteBufferUtil.debugAll;
import static com.cola.NIO.Buffer.ByteBufferUtil.debugRead;

@Slf4j
public class Server {
    public static void main(String[] args) throws IOException {
        // 1. 创建 selector，管理多个channel
        Selector selector = Selector.open();

//        ByteBuffer buffer = ByteBuffer.allocate(16);
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);    //非阻塞模式

        // 2. 建立 selector 和 channel 的联系 (注册)
        // SelectionKey 就是事件发生后，通过它可以知道 事件和哪个channel发生的事件
        SelectionKey sscKey = ssc.register(selector, 0, null);
        /*
            accept - 会在有连接请求时触发
            connect - 是客户端，连接建立后触发
            read -  可读事件
            write - 可写事件
         */
        // key 只关注accept 事件
        sscKey.interestOps(SelectionKey.OP_ACCEPT);
        log.debug("register key:{}", sscKey);

        ssc.bind(new InetSocketAddress(9003));
        for (; ; ) {
            // 3. select() 方法。没有时间就阻塞，线程阻塞。有事件，线程才会恢复运行。
            // select 在事件未处理时，它不会阻塞，事件发生后要么处理，要么取消，不能置之不理
            selector.select();
            // 4. 处理事件 ,selectorKeys 内部包含了所有发生的事件
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                //处理key 时，要从 selectedKeys 集合中删除 ,否则下次处理就会有问题
                iterator.remove();
                log.debug("key:{}", key);
                // 5.区分事件类型

                if (key.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel sc = channel.accept();
                    sc.configureBlocking(false);
                    ByteBuffer buffer = ByteBuffer.allocate(8);    //attachment  附件
                    // 将一个 byteBuffer  作为附件关联到 selectionKey
                    SelectionKey scKey = sc.register(selector, 0, buffer);
                    scKey.interestOps(SelectionKey.OP_READ);
                    log.debug("{}", sc);
                    log.debug("scKey:{}", scKey);
                } else if (key.isReadable()) {
                    try {
                        SocketChannel channel = (SocketChannel) key.channel(); //拿到触发事件的Channel
                        // 获取 selectionKey 上关联的附件
                        ByteBuffer buffer =(ByteBuffer) key.attachment();
//                        key.attach()//关联一个新的附件
                        int read = channel.read(buffer);  // 如果是正常断开，read的方法返回值是 -1
                        if (read == -1) {
                            key.cancel();
                        } else {
                           split(buffer);
                           if(buffer.position() == buffer.limit()){
                               ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() * 2);
                               buffer.flip();
                               newBuffer.put(buffer);   // 0123456789abcdef
                               key.attach(newBuffer);
                           }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        key.cancel();   // 因为客户端断开了，因此需要将 key 取消(从selectedKey 的key 在集合中真正删除)
                    }
                }
//                key.cancel(); //事件取消
            }
        }
    }

    private static void split(ByteBuffer source) {
        source.flip();
//        System.out.println("==============>"+Charset.defaultCharset().decode(source).toString());
        for (int i = 0; i < source.limit(); i++) {
            // 找到一条完整消息
            if (source.get(i) == '\n') {
                int length = i + 1 - source.position();
                // 把完整消息存入新的 ByteBuffer.allocate(5)
                ByteBuffer target = ByteBuffer.allocate(length);
                for (int j = 0; j < length; j++) {
                    target.put(source.get());
                }
                debugAll(target);
            }
        }
        source.compact();   //0123456789abcdef position 16 , limit 16
    }

    //阻塞模式
    public void m1() throws IOException {
        // 使用 nio 来理解阻塞模式
        // 0.创建ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(16);

        // 1. 创建端口
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);    //非阻塞模式

        // 2. 绑定监听端口
        ssc.bind(new InetSocketAddress(9003));

        // 3.连接集合
        ArrayList<SocketChannel> channels = new ArrayList<>();
        for (; ; ) {
            // 4.accept 建立与客户端连接， SocketChannel 用来与客户端之间通信
//            log.debug("connecting ...");
            SocketChannel sc = ssc.accept();   // 非阻塞 , 线程还会继续执行 ,如果没有连接建立， 但是sc是null
            if (sc != null) {
                log.debug("connected ...{}", sc);
                sc.configureBlocking(false);  //  非阻塞模式
                channels.add(sc);
            }
            for (SocketChannel channel : channels) {
                // 5.接受客户端发送的数据
//                log.debug("before read ...{}", channel);
                int read = channel.read(buffer);//  非阻塞 , 线程仍然会继续运行，如果没有读到数据 ，read返回 0
                if (read > 0) {
                    buffer.flip();
                    debugRead(buffer);
                    buffer.clear();
                    log.debug("after read ....{}", channel);
                }
            }
        }
    }

    //非阻塞模式
    public void m2() throws IOException {
        // 使用 nio 来理解阻塞模式
        // 0.创建ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(16);

        // 1. 创建端口
        ServerSocketChannel ssc = ServerSocketChannel.open();

        // 2. 绑定监听端口
        ssc.bind(new InetSocketAddress(9003));

        // 3.连接集合
        ArrayList<SocketChannel> channels = new ArrayList<>();
        for (; ; ) {
            // 4.accept 建立与客户端连接， SocketChannel 用来与客户端之间通信
            log.debug("connecting ...");
            SocketChannel sc = ssc.accept();   // 阻塞方法， 线程停止运行
            log.debug("connected ...{}", sc);
            channels.add(sc);
            for (SocketChannel channel : channels) {
                // 5.接受客户端发送的数据
                log.debug("before read ...{}", channel);
                channel.read(buffer);//  阻塞方法， 线程停止运行
                buffer.flip();
                debugRead(buffer);
                buffer.clear();
                log.debug("after read ....{}", channel);
            }
        }
    }
}
