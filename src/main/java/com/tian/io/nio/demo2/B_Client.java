package com.tian.io.nio.demo2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @author David Tian
 * @desc 单向通信的客户端【方法分离】
 * @since 2020-04-14 21:29
 */
public class B_Client implements Runnable {

    private SocketChannel sc = null;
    private Selector selector = null;

    public static void main(String[] args) {
        B_Client b_client = new B_Client();
        new Thread(b_client).start();

        b_client.write();

    }

    public B_Client() {
        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 8910);
        try {
            sc = SocketChannel.open();

            sc.connect(address);
            sc.configureBlocking(false); // NotYetConnectedException

            this.selector = Selector.open();
            sc.register(this.selector, SelectionKey.OP_READ);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void write() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        try {
            while (true) {
                byte[] bytes = new byte[1024];
                System.in.read(bytes);
                byteBuffer.put(bytes);
                byteBuffer.flip();
                sc.write(byteBuffer);
                byteBuffer.clear();
            }
        } catch (IOException e) {

        }

    }

    private void read(SelectionKey key) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        try {
            // 2. 获取之前注册到选择器的socket通道
            SocketChannel sc = (SocketChannel)key.channel();
            // 3. 读取数据
            int count = sc.read(byteBuffer);
            // 4. 如果没有数据
            if(count == -1) {
                key.channel().close();
                key.cancel();
                return;
            }
            // 5. 有数据就读取，读取之前需要进行flip（把position 和limit进行固定）
            byteBuffer.flip();
            // 6. 根据缓冲区的数据长度创建相应大小的byte数组，接受缓冲区数据
            byte[] bytes = new byte[byteBuffer.remaining()];
            // 7. 接收缓冲区数据
            byteBuffer.get(bytes);
            // 8. 打印结果
            String body = new String(bytes).trim();
            System.out.println("Server: " + body);


            // 记住，一定要1. 关闭通道，2. 取消key
            key.channel().close();
            key.cancel();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }

    }

    @Override
    public void run() {
        while(true){
            try {
                this.selector.select();
                Iterator<SelectionKey> keys = this.selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    if(key.isValid()){
                        if(key.isReadable()){
                            this.read(key);
                        }
                    }
                    keys.remove();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
