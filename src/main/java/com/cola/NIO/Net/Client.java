package com.cola.NIO.Net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class Client {
    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("127.0.0.1",9003));

        SocketAddress address = sc.getLocalAddress();
        sc.write(Charset.defaultCharset().encode("0123456789abcdef\n"));
        sc.write(Charset.defaultCharset().encode("0123456789abcdef88888\n"));
//        System.out.println("waiting ...");
        System.in.read();
    }
}
