package com.tian.io.bio.single;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author David Tian
 * @desc
 * @since 2020-01-15 15:06
 */
public class BioServerSingle {

    /**
     * 模拟bio的服务端
     *
     * 可以本地 启动一个telnet localhost 8080 来模拟客户端client
     */
    public static void main(String[] args) {
        int port = 8080;
        //服务端
        ServerSocket serverSocket = null;
        //客户端
        Socket socket = null;

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            serverSocket = new ServerSocket(port);

            //阻塞
            socket = serverSocket.accept();
            inputStream = socket.getInputStream();
            byte[] buffer = new byte[1024];
            int length = 0;

            //阻塞
            while ((length = inputStream.read(buffer)) > 0) {
                System.out.println("input is:"+new String(buffer,0,length));

                outputStream = socket.getOutputStream();
                //本行相当于 把输入的再写回去
                outputStream.write(buffer);
                outputStream.write("success".getBytes());
                System.out.println("end");
            }


        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
