package com.tian.io.netty.demo1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * @author David Tian
 * @desc    nio实现
 * @since 2020-01-09 17:16
 */
public class QQServerNio {

    private static byte[] bytes = new byte[1024];

    private static ByteBuffer byteBuffer = ByteBuffer.allocate(512);

    private static List<SocketChannel> list = new ArrayList<>();

    public static void main(String[] args) {

        try {

            //listener
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(8080));
            //设置非阻塞
            serverSocketChannel.configureBlocking(false);

            while(true){
                //阻塞
                SocketChannel socketChannel = serverSocketChannel.accept();
                if(socketChannel==null){

                    Thread.sleep(500);
                    System.out.println("no conn");
                    for(SocketChannel client : list){
                        int k = client.read(byteBuffer);
                        if(k>0){
                            byteBuffer.flip();
                            System.out.println("==="+byteBuffer.toString());
                        }

                    }

                }else {
                    //设置非阻塞
                    socketChannel.configureBlocking(false);

                    list.add(socketChannel);
                    for(SocketChannel client : list){

                        int i = client.read(byteBuffer);
                        if(i>0){

                            byteBuffer.flip();
                            System.out.println("==="+byteBuffer.toString());
                        }
                    }
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
