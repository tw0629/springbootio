package com.tian.io.nio.demo4.singleAcceptor;

import com.tian.io.nio.demo4.multiThread.AsyncHandler;
import com.tian.io.nio.demo4.singleThread.Handler;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author David Tian
 * @desc
 * @since 2020-01-15 19:35
 */
public class Acceptor implements Runnable{

    private final Selector selector;

    private final ServerSocketChannel serverSocketChannel;

    public Acceptor(ServerSocketChannel serverSocketChannel, Selector selector) {
        this.selector = selector;
        this.serverSocketChannel = serverSocketChannel;
    }


    @Override
    public void run() {

        SocketChannel socketChannel;

        try {
            socketChannel = serverSocketChannel.accept();

            if(socketChannel!=null){

                /**
                 * 方式一：单个线程方式
                 */
                //注册感兴趣的读事件
                //这里把客户端的通道传给handler
                Handler handler = new Handler(socketChannel, selector);
                handler.run();

                /**
                 * 方式二：多线程方式(线程池处理的)
                 */
                AsyncHandler asyncHandler = new AsyncHandler(socketChannel, selector);
                asyncHandler.run();
                //handler负责接下来的事件处理(除了连接事件以外的时间均可)



            }

        } catch (IOException e) {


        }


    }
}
