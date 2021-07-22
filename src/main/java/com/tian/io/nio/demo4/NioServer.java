package com.tian.io.nio.demo4;

import com.tian.io.nio.demo4.multiAcceptor.MultiAcceptor;
import com.tian.io.nio.demo4.singleAcceptor.Acceptor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author David Tian
 * @desc
 * @since 2020-01-15 19:36
 */
public class NioServer {

    //多路复用器
    private final Selector selector;

    private final ServerSocketChannel serverSocketChannel;

    public NioServer(int port) throws IOException {
        //Reactor初始化
        //打开一个Selector
        selector = Selector.open();
        //建立一个server端的通道
        serverSocketChannel = ServerSocketChannel.open();
        //绑定服务端口号
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        //selector模式下,所有通道都必须是非阻塞的
        serverSocketChannel.configureBlocking(false);

        //最初给channel注册上的事件都是accept
        SelectionKey selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        //attach callback object , Acceptor
        /**
         * 方式一：单Acceptor
         */
        //绑定接收事件处理器
        selectionKey.attach(new Acceptor(serverSocketChannel,selector));

        /**
         * 方式二：多Acceptor
         */
        selectionKey.attach(new MultiAcceptor(serverSocketChannel));


    }

    public void run(){

        try {
            while(!Thread.interrupted()){

                System.out.println("start");
                //就绪事件到达之前,阻塞
                selector.select();
                //拿到本次selector 获取的就绪事件
                Set<SelectionKey> selectionKeys = selector.selectedKeys();

                Iterator<SelectionKey> it = selectionKeys.iterator();
                while(it.hasNext()){
                    //这里进行任务分发
                    dispatch((SelectionKey)it.next());
                }

                selectionKeys.clear();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(selector!=null){

                try {
                    selector.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }


    private void dispatch(SelectionKey selectionKey){

        //这里很关键, 拿到每次selectKey 里面附带的处理对象
        Runnable runnable = (Runnable) selectionKey.attachment();
        //调用之前注册callback对象
        if(runnable!=null){
            //只是拿到句柄执行的run方法,并没有起新的线程
            runnable.run();
        }
    }

    public static void main(String[] args) throws IOException {

        NioServer nioServer = new NioServer(8080);

        nioServer.run();
    }
}
