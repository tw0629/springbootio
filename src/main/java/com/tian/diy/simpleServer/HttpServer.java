package com.tian.diy.simpleServer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author David Tian
 * @desc https://www.cnblogs.com/aspnetdream/p/4217581.html
 * 启动tomcat，对于一个web容器而言，简而言之，它是系统的一个守护进程，守护着对这台服务器某个端口发起的请求，
 * 基于这一点，它就需要一个监听程序，这个监听程序来获取来自这个端口的特定请求的数据，
 * ok，直接点讲，我们这里使用Socket来获取某个端口，通常是80端口的http请求，通过简单的Java
 * 程序的死循环（粗糙的做法，后面逐步优化）来实现不断的获取80端口http请求，来达到监听80端口http请求的目的。
 *
 * @since 2020-03-17 18:11
 */
public class HttpServer {

    public static final String WEB_ROOT =
            System.getProperty("user.dir") + File.separator  + "webroot";

    // 关闭命令
    private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";

    // 是否关闭
    private boolean shutdown = false;

    public static void main(String[] args) {
        HttpServer server = new HttpServer();
        server.await();
    }

    public void await() {
        ServerSocket serverSocket = null;
        int port = 8080;
        try {
            // 创建一个socket服务器
            serverSocket =  new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // 循环等待http请求
        while (!shutdown) {
            Socket socket = null;
            InputStream input = null;
            OutputStream output = null;
            try {
                // 阻塞等待http请求
                socket = serverSocket.accept();       // <<<==================看这里
                input = socket.getInputStream();      // <<<==================看这里
                output = socket.getOutputStream();    // <<<==================看这里

                // 创建一个Request对象用于解析http请求内容
                Request request = new Request(input);
                request.parse();


                // 创建一个Response 对象，用于发送静态文本
                Response response = new Response(output);
                //!!! ****** 关键 ******
                // sendStaticResource 方法是用来发送一个静态资源，例如一个 HTML 文件。
                // 文件是否存在。假如存在的话，通过传递 File 对象让 sendStaticResource 构造一个 java.io.FileInputStream 对象。
                // 然后，它调用 FileInputStream 的 read 方法并把字 节数组写入 OutputStream 对象。
                // 请注意，这种情况下，静态资源是作为原始数据发送给浏览器 的。
                response.setRequest(request);    // <<<==================看这里
                response.sendStaticResource();

                // 关闭流
                socket.close();

                //检查URI中是否有关闭命令
                shutdown = request.getUri().equals(SHUTDOWN_COMMAND);
            }
            catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
    }

}
