package com.cola.NIO.Channel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class TestFileChannelTransferTo {
    public static void main(String[] args) {
        try (
                FileChannel from = new FileInputStream("data.txt").getChannel();
                FileChannel to = new FileOutputStream("to.txt").getChannel();
        ) {
            // 效率高 ,  底层会利用操作系统的零拷贝进行优化，2g 数据
            long size = from.size();
            // left 变量掉膘还剩余多少字节
            for (long left = size; left > 0; ) {
                System.out.println("position: " + (size - left) + "\nleft: " + left);
                left -= from.transferTo(size - left, left, to);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
