package com.tian.io.nio.demo4.singleThread;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * @author David Tian
 * @desc
 * @since 2020-01-15 19:35
 */
public class Handler implements Runnable{

    private final SocketChannel socketChannel;
    private final SelectionKey selectionKey;

    // ByteBuffer !!!
    private ByteBuffer readBuffer = ByteBuffer.allocate(1024);
    private ByteBuffer sendBuffer = ByteBuffer.allocate(2048);

    private final static int READ = 0;
    private final static int SEND = 1;

    private int status = READ;

    public Handler(SocketChannel socketChannel, Selector selector) throws IOException {
        //接收客户端连接
        this.socketChannel = socketChannel;
        //设置为非阻塞模式(selector仅允许非阻塞模式)
        this.socketChannel.configureBlocking(false);

        //将客户端的连接注册到
        selectionKey = socketChannel.register(selector, SelectionKey.OP_READ);
        //附加处理对象,当前是Handler对象,run()是对象处理业务的方法
        selectionKey.attach(this);
          // 走到这里说明之前的Acceptor里的
//        selectionKey.interestOps(SelectionKey.OP_READ);
          // 让阻塞的selector立即返回
//        selector.wakeup();
    }


    @Override
    public void run(){
        try{
            switch(status){
                case READ:
                    read();
                    break;

                case SEND:
                    send();
                    break;

                default:
            }
        }catch (IOException e){
            //这里异常处理做了汇总,常出的异常是server端还有未读/写完的
            System.out.println("read或send时发生异常！异常信息:"+e.getMessage());
            selectionKey.cancel();

            try {
                socketChannel.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

    }

    private void read() throws IOException {

        if(selectionKey.isValid()){
            readBuffer.clear();
            //read方法结束,意味着 读就绪 变成 读完成
            int count = socketChannel.read(readBuffer);

            if(count>0){
                System.out.println(String.format("收到来自 %s 的消息: %s",
                        socketChannel.getRemoteAddress(),new String(readBuffer.array())));

                status = SEND;
                //注册写方法
                selectionKey.interestOps(SelectionKey.OP_WRITE);
            }else {
                //读模式下拿到-1,说明客户端已经断开连接,那么将对应的selectKsy从selector中清除
                //所以这种场景下,(服务端程序)你需要关闭socketChannel并且取消key,最好是退出当前的

                selectionKey.cancel();
                socketChannel.close();
                System.out.println("read时------- 连接关闭");
            }

        }

    }

    private void send() throws IOException {

        if (selectionKey.isValid()) {

            sendBuffer.clear();
            sendBuffer.put("response ok".getBytes());
            sendBuffer.flip();
            //write方法结束
            int count = socketChannel.write(sendBuffer);
            //意味着 本次写就绪变成写完成,标记着一次事件的结束

            if(count<0){

                //同上,write场景下,取到-1,也意味着客户端断开连接
                selectionKey.cancel();
                socketChannel.close();
                System.out.println("send时------- 连接关闭");
            }

            //没有断开连接,再次切换到读
            status = READ;
            selectionKey.interestOps(SelectionKey.OP_READ);
        }


    }

}
