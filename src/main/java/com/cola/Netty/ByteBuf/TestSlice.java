package com.cola.Netty.ByteBuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import static com.cola.Netty.ByteBuf.TestByteBuf.log;

public class TestSlice {
    public static void main(String[] args) {
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(10);
        buf.writeBytes(new byte[]{'a','b','c','d','e','f','g','h','i','j'});
        log(buf);

        // 在切片过程中，没有发生复制
        ByteBuf buf1 = buf.slice(0, 5);
        buf1.retain();  // 引用计数+1
        ByteBuf buf2 = buf.slice(5, 5);
        log(buf1);
        log(buf2);

        System.out.println("释放原有 byteBuf 内存");
        buf.release();  // 引用计数-1
        log(buf1);

//        buf1.writeByte('x');

//        System.out.println("====================");
//        buf1.setByte(0,'b');
//        log(buf1);
//        log(buf);
    }
}
