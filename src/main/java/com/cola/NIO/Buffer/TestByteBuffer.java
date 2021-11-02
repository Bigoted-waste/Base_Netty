package com.cola.NIO.Buffer;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

@Slf4j
public class TestByteBuffer {
    /*  ByteBuffer 正确使用姿势
      1、向buffer写入数据，例如调用channel.read(buffer)
      2、调用flip()切换至读模式
      3、从buffer读取数据，例如调用buffer.get()
      4、调用clear() 或 compact() 切换至写模式
      5、重复 1-4 步骤

     */
    public static void main(String[] args) {
        // FileChannel
        // 1.输入输出流  2.RandomAccessFile
        try (FileChannel channel = new FileInputStream("data.txt").getChannel()) {
            // 缓冲区
            ByteBuffer byteBuffer = ByteBuffer.allocate(10);
            while (true){
                // 从channel读取数据 ，向Buffer写入
                int len = channel.read(byteBuffer);
                log.debug("读取到的字节数 {}",len);
                if(len == -1){  //没有内容了
                    break;
                }
                // 打印buffer的内容
                byteBuffer.flip();  //切换读模式
                while (byteBuffer.hasRemaining()) { // 是否还有剩余未读的数据
                    byte b = byteBuffer.get();
                    log.debug("实际字节 {}",(char)b);
                }
                System.out.println();
                //切换为写模式
                byteBuffer.clear();
            }
        } catch (IOException e) {
        }
    }
}
