package com.tian.io.bio.thread;

import com.tian.io.bio.SocketTask;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author David Tian
 * @desc
 * @since 2020-01-15 15:07
 */
public class BioServerThread {

    /**
     * 模拟bio的服务端
     *
     * 可以本地 启动多个telnet localhost 8080 来模拟客户端client
     *
     * 缺点:启动的线程数太多,消耗资源
     */
    public static void main(String[] args) {

        int port = 8080;
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(port);
            Socket socket = null;

            while (true){
                socket = serverSocket.accept();
                new Thread(new SocketTask(socket)).start();
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
