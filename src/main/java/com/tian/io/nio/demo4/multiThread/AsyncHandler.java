package com.tian.io.nio.demo4.multiThread;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author David Tian
 * @desc
 * @since 2020-01-15 19:36
 */
public class AsyncHandler implements Runnable{

    private final Selector selector;

    private final SocketChannel socketChannel;

    private final SelectionKey selectionKey;

    // ByteBuffer !!!
    private ByteBuffer readBuffer = ByteBuffer.allocate(1024);
    private ByteBuffer sendBuffer = ByteBuffer.allocate(2048);

    //读取就绪
    private final static int READ = 0;
    //响应就绪
    private final static int SEND = 1;

    //所有连接完成后都是从一个读取动作开始的
    private int status = READ;

    //异步处理线程池
    private static final ExecutorService workers = Executors.newFixedThreadPool(5);


    public AsyncHandler(SocketChannel socketChannel, Selector selector) throws IOException {
        this.socketChannel = socketChannel;
        this.socketChannel.configureBlocking(false);

        selectionKey = socketChannel.register(selector, SelectionKey.OP_READ);
        selectionKey.attach(this);
        this.selector = selector;

        //nio 进阶
        this.selector.wakeup();
    }

    @Override
    public void run() {
        //如果一个任务正在异步处理,那么这个run是直接不触发任务处理的。read和write
        switch(status){
            case READ:
                read();
                break;

            case SEND:
                send();
                break;

            default:
        }

    }

    private void read() {

        if(selectionKey.isValid()){

            try {
                readBuffer.clear();
                int count = socketChannel.read(readBuffer);


                if(count > 0){
                    workers.submit(this::readWork);
                }else {
                    selectionKey.cancel();
                    socketChannel.close();
                    System.out.println("read时-------连接关闭");
                }

            } catch (IOException e) {
                System.out.println("处理read业务时发生异常！异常信息:"+e.getMessage());
                selectionKey.cancel();
                try {
                    socketChannel.close();
                } catch (IOException e1) {
                    System.out.println("处理read业务关闭通道时发生异常！异常信息:"+e.getMessage());
                }
            }

        }
    }

    private void send() {
        if(selectionKey.isValid()){
            //异步处理
            workers.execute(this::sendWork);
            //重新设置为读
            //通过key改变通道注册事件,即改为监听写事件
            selectionKey.interestOps(SelectionKey.OP_READ);
        }

    }


    private void readWork(){
        System.out.println(new String(readBuffer.array()));
        status = SEND;
        //把当前事件改为写事件
        selectionKey.interestOps(SelectionKey.OP_WRITE);
        //唤醒阻塞在selector中的线程
        this.selector.wakeup();

        //因为该interestOps写事件是放在子线程中的,select在该channel还是对read事件感兴趣时又被调用
        //因此如果不主动唤醒,select可能并不会select该读就绪事件(在该例中永远不会selcet到)
    }

    private void sendWork(){

        //todo
        try {
            sendBuffer.clear();
            sendBuffer.put(String.format("我收到来自 %s 的信息啦: %s, 200 OK",
                    socketChannel.getRemoteAddress(),
                    new String(readBuffer.array())).getBytes());
            sendBuffer.flip();

            int count = socketChannel.write(sendBuffer);

            if(count<0){
                selectionKey.cancel();
                socketChannel.close();
                System.out.println("send时------- 连接关闭");
            }else{
                //再次切换到读
                status = READ;
            }


        } catch (IOException e) {
            System.out.println("异步处理send业务时发生异常! 异常信息:"+e.getMessage());
            selectionKey.cancel();

            try {
                socketChannel.close();
            } catch (IOException e1) {
                System.out.println("异步处理send业务关闭通道时发生异常! 异常信息:"+e.getMessage());
            }
        }

    }
}

