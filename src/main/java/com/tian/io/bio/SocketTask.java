package com.tian.io.bio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author David Tian
 * @desc
 * @since 2020-01-15 15:09
 */
public class SocketTask implements Runnable{

    private Socket socket;

    public SocketTask(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {

            inputStream = socket.getInputStream();
            byte[] buffer = new byte[1024];
            int length = 0;
            //阻塞
            while ((length = inputStream.read(buffer))>0) {
                System.out.println("intput is:"+new String(buffer,0,length));

                outputStream = socket.getOutputStream();
                //本行相当于 把输入的再写回去
                outputStream.write(buffer);
                outputStream.write("sueecss".getBytes());
                System.out.println("end");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(inputStream != null){
                try {
                    inputStream.close();
                    inputStream = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
