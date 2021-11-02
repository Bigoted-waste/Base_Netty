package com.cola.NIO.Buffer;

import java.nio.ByteBuffer;

public class TestByteBufferAllocate {
    public static void main(String[] args) {
        /*
          class java.nio.HeapByteBuffer     --java 堆内存，读写效率较低，受到gc的影响
          class java.nio.DirectByteBuffer   --直接内存，读写效率高（少一次拷贝，不受gc影响，分配效率低
         */
        System.out.println(ByteBuffer.allocate(16).getClass());
        System.out.println(ByteBuffer.allocateDirect(16).getClass());
    }
}
