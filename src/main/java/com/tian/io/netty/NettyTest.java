package com.tian.io.netty;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author David Tian
 * @desc https://blog.csdn.net/zaogua9786/article/details/102813168
 * @since 2020-01-09 11:00
 */
public class NettyTest {

    private static Charset charset = Charset.forName("utf-8");

    public static void main(String[] args) throws IOException {
        ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(4);
        //创建一个selector
        Selector selector = Selector.open();
        //创建channel 通道
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(8080));

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //绑定9200端口
        serverSocketChannel.bind(new InetSocketAddress(9200));
        //指定非阻塞的方式
        serverSocketChannel.configureBlocking(false);
        //接下来需要serverSocketChannel绑定到selector
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        //一个线程负责选择就绪的channel  这里的话就是主线程循环
        while (true) {
            //阻塞选择就绪的事件，select（）可中断的
            int readyChannelcount = selector.select();
            if(readyChannelcount ==0){
                continue;
            }
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            //整个遍历器
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
            while (keyIterator.hasNext()){
                SelectionKey key = keyIterator.next();
                //连接进来了
                if(key.isAcceptable()){
                    System.out.println("连接进来了");
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    //注册到selector，用它来监测 注意ServerSocketChannel  和  SocketChannel是两个层次
                    socketChannel.configureBlocking(false);
                    //监测可读取
                    socketChannel.register(selector,SelectionKey.OP_READ);

                }else if(key.isReadable()){
                    System.out.println("数据发过来了，可以去读取了");
                    //交到线程池中去处理吧
                    newFixedThreadPool.submit(new SocketProcess(key));
                    //值得注意的是 这里由于使用的是线程池  可能处理的不及时（线程就绪等待等等),需要及时取消掉改
                    key.cancel();
                }else if(key.isWritable()){
                    System.out.println("数据可以发送出去了");
                }else if(key.isConnectable()){
                    System.out.println("我已经联通了其他的服务器了");

                }
                //处理完了就去掉
                keyIterator.remove();
            }


        }

    }

    //处理类
    static class SocketProcess implements  Runnable{

        SelectionKey key;

        public SocketProcess(SelectionKey key){
            super();
            this.key =key;
        }

        @Override
        public void run() {

            SocketChannel channel = (SocketChannel)key.channel();

            //读数据
            //1.创建buffer
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            try {
                //读取到buffer的数量 为-1 读完
                int readBuffer = channel.read(buffer);

                while (readBuffer!=-1){
                    //转换buffer模式，从写模式转换到读模式  读取其中的值 使得buffer可向外输出
                    //具体转换position和limit值
                    buffer.flip();

                    //看buffer缓冲还有没有东西了
                    while(buffer.hasRemaining()){
                        CharsetDecoder charsetDecoder = charset.newDecoder();
                        CharBuffer decode = charsetDecoder.decode(buffer);
                        System.out.println( decode.toString());
                        /* System.out.println((char)buffer.get());*/
                    }
                    //清除缓存  还有一个buffer.compact()整理已读的 未读到的提前
                    buffer.clear();
                    readBuffer=channel.read(buffer);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Test
    public void test(){
        //ByteBuffer b =  new ByteBuffer(); 错误

        //可以申请对外内存
        ByteBuffer b = ByteBuffer.allocate(1024);

        //DirectByteBuffer dbb =  new DirectByteBuffer();
        //HeapByteBuffer hb = new HeapByteBuffer("","");


        System.out.println(b);


        //ChannelGroup

        //ServiceConfig
        //JavassistProxyFactory

    }


}
