package com.tian.io.socket;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author David Tian
 * @desc
 * @since 2020-03-13 21:51
 */
public class SocketTest {

    public static void main(String[] args) throws IOException {

        //相当于建立tcp连接
        //这是Java里定义的对象, 底层是通过JNI 调用操作系统linux c语言 _sys_connect()方法;
        //而_sys_connect()底层是调用 tcp_connect()方法
        Socket socket = new Socket();

        //相当于建立ucp连接
        DatagramSocket datagramSocket = new DatagramSocket();

        //socket
        socket.connect(new InetSocketAddress("localhost",8080));

//
//        AppletClassLoader appletClassLoader = new AppletClassLoader();
//        appletClassLoader.loadClass()
//
//        AtomicInteger
//
//                AtomicLong

    }
}
