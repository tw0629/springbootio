package com.tian.io.netty.demo1;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 * @author David Tian
 * @desc
 * @since 2020-01-09 17:16
 */
public class QQClient {

    public static void main(String[] args) {
        try {

            Socket socket = new Socket("127.0.0.1", 8080);
            Scanner scanner = new Scanner(System.in);

            while (true) {
                String txt = scanner.next();
                socket.getOutputStream().write(txt.getBytes());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
