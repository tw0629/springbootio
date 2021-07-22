package com.tian.io.netty.demo1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author David Tian
 * @desc    bio实现
 * @since 2020-01-09 17:15
 *
 */
public class QQServer {

    private static byte[] bytes = new byte[1024];

    public static void main(String[] args){

        try {
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(8080));

            while(true){
                System.out.println("wait conn");
                //阻塞    获取连接过来的客户端
                Socket socket = serverSocket.accept();
                //阻塞
                int read = socket.getInputStream().read(bytes);
                String content = new String(bytes);
                System.out.println("==="+content);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
