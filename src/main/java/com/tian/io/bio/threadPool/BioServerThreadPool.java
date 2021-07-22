package com.tian.io.bio.threadPool;


import com.tian.io.bio.SocketTask;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author David Tian
 * @desc
 * @since 2020-01-15 15:07
 */
public class BioServerThreadPool {

    /**
     * 模拟bio的服务端
     *
     * 可以本地 启动多个telnet localhost 8080 来模拟客户端client
     *
     * 缺点:虽然加了线程池控制了线程数量,但是还是无法避免线程阻塞带来的问题
     *  (旧线程阻塞:当客户端连接数达到一定量的时候,新的客户端加不进来,被之前已连接的但此时不发送消息的线程占用着)
     */
    public static void main(String[] args) {

        int port = 8080;

        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(port);

            Socket socket = null;

            SocketHandlerExecutePool pool = new SocketHandlerExecutePool(50,100);
            while (true) {
                socket = serverSocket.accept();
                pool.execute(new SocketTask(socket));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(serverSocket!=null){
                try {
                    serverSocket.close();
                    serverSocket = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
