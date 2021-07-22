package com.tian.io.nio.demo2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * @author David Tian
 * @desc NIO Demo
 * @since 2020-04-14 21:29
 */
public class B_Server implements Runnable {

    private Selector selector;

    private ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
    private ByteBuffer writeBuffer = ByteBuffer.allocate(1024);

    public B_Server(int port) {

        try {
            // 1. 打开多路复用
            this.selector = Selector.open();

            // 2. 打开服务器通道
            ServerSocketChannel ssc = ServerSocketChannel.open();

            // 3. 设置服务器通道为阻塞模式
            ssc.configureBlocking(false);

            // 4. 绑定端口
            ssc.bind(new InetSocketAddress(port));

            // 5. 把服务器channel注册到选择器中，监听阻塞事件
            ssc.register(this.selector, SelectionKey.OP_ACCEPT);
            System.out.println("注册服务端channel");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        while(true) {
            try {
                System.out.println("进入run 大轮询开始");
                // 1. 让选择器开始监听
                System.out.println("打开选择器监听事件");
                this.selector.select(); // 这里是阻塞的，返回至少一个已经准备好的channel

                // 2. 返回选择器结果集
                System.out.println("select阻塞结束");
                Iterator<SelectionKey> keys = this.selector.selectedKeys().iterator();
                System.out.println("keys：" + keys);

                // 3. 遍历
                while(keys.hasNext()) {
                    // 4. 获取其中一个元素
                    SelectionKey key = keys.next();
                    // 5. 可以移除掉
                    /*
                    channel是注册在selector中的，在后面的轮询中，是先将已准备好的channel挑选出来，即selector.select()，再通
                    过selectedKeys()生成的一个SelectionKey迭代器进行轮询的，一次轮询会将这个迭代器中的每个SelectionKey都遍历
                    一遍，每次访问后都remove()相应的SelectionKey，但是移除了selectedKeys中的SelectionKey不代表移除了selector
                    中的channel信息(这点很重要)，注册过的channel信息会以SelectionKey的形式存储在selector.keys()中，也就是说
                    每次select()后的selectedKeys迭代器中是不能还有成员的，但keys()中的成员是不会被删除的(以此来记录channel信息)。
                     */
                    keys.remove();
                    // 6. 判断有效性
                    if(key.isValid()) {
                        // 7. 监听【阻塞】状态的selectorKey
                        if(key.isAcceptable()) {
                            System.out.println("监听到【阻塞(Accept)】key: " + key);
                            this.accept(key);
                        }

                        // 8. 监听【可读】状态的selectorKey
                        if(key.isReadable()) {

                            System.out.println("监听到【可读】key: " + key);
                            this.read(key);
                        }

                        // 9. 监听【可写】状态的selectorKey
                        if(key.isWritable()) {
                            System.out.println("监听到【可写】key: " + key);
                            this.write(key);
                        }
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void accept(SelectionKey key) {
        try {
            System.out.println("进入accept()");
            // 1. 获取服务器通道
            ServerSocketChannel ssc = (ServerSocketChannel)key.channel();
            // 2. 执行阻塞方法
            SocketChannel sc = ssc.accept();
            // 3. 设置阻塞模式：非阻塞
            sc.configureBlocking(false);
            // 4. 将客户端通道注册到选择器上，并设置监听【可读】标实位
            /**
             * 如果客户端向服务端发送了数据，那么执行以下步骤
             * 4-1. 服务端的服务器操作系统内核空间接收客户端发来的数据
             * 4-2. 内核空间接收完所有的数据之后，将该客户端与服务端连接的socket的文件描述符设置为可读
             * 4-3. selector这时结束阻塞（我猜测：java的selector调用系统的socket()函数，select()函数判断有事件产生，就结束阻塞，返回给java的selector）
             */
            sc.register(this.selector, SelectionKey.OP_READ);
            System.out.println("设置监听客户端channel的监听状态为可读状态");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void read(SelectionKey key) {
        System.out.println("进入read()");
        try {
            // 1. 清空缓冲期数据
            // 如果不clear()，那么会导致buffer的position位置与limit位置相同，无法往buffer里写入数据
            // 导致下面的sc.read(this.byteBuffer)方法返回0
            this.byteBuffer.clear();
            // 2. 获取之前注册到选择器的socket通道
            SocketChannel sc = (SocketChannel)key.channel();
            // 3. 读取数据（对于butybuffer，是写入数据，所以下面从bytebuffer里读取数据之前，需要flip）
            /**
             * 接上accept()注释的 4-3
             * 这里从通道读取数据，应该是从系统内核空间把数据复制到jvm进程空间，也就是服务端
             */
            int count = sc.read(this.byteBuffer); // int是读取到的字节个数, 如果没读到数据，那么就返回-1
            // 4. 如果没有数据
            if(count == -1) {
                key.channel().close();
                key.cancel();
                return;
            }
            // 5. 有数据就读取，读取之前需要进行flip（翻转bytebuffer）
            this.byteBuffer.flip();
            // 6. 根据缓冲区的数据长度创建相应大小的byte数组，接受缓冲区数据
            byte[] bytes = new byte[byteBuffer.remaining()];
            // 7. 接收缓冲区数据
            this.byteBuffer.get(bytes);
            // 8. 打印结果
            String body = new String(bytes).trim();
            System.out.println("Server: " + body);

            // 9. 可以写回给客户端数据
            sc.configureBlocking(false);
            sc.register(this.selector, SelectionKey.OP_WRITE);
            System.out.println("读取数据完毕，将当前客户端通道的监听事件修改为【可写】");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void write(SelectionKey key) {
        System.out.println("进入write()");
        SocketChannel sc = (SocketChannel)key.channel();
        try {
//            sc.register(this.selector, SelectionKey.OP_WRITE);
            byte[] bytes = "呵呵".getBytes();
            writeBuffer.put(bytes);
            writeBuffer.flip();
            sc.write(writeBuffer);
            writeBuffer.clear();
            key.channel().close();
            key.cancel();
//            sc.register(this.selector, SelectionKey.OP_READ);

        } catch (ClosedChannelException e) {
            e.printStackTrace();
        } catch (IOException e) {

        }

    }

    public static void main(String[] args) {
        new Thread(new B_Server(8910)).start();
    }

}
