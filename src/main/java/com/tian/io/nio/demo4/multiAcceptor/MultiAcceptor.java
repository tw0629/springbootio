package com.tian.io.nio.demo4.multiAcceptor;

import com.tian.io.nio.demo4.multiThread.AsyncHandler;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author David Tian
 * @desc
 * @since 2020-01-15 19:36
 */
public class MultiAcceptor implements Runnable{

    private final ServerSocketChannel serverSocketChannel;

    //获取cpu核心数
    private final int coreNum = Runtime.getRuntime().availableProcessors();

    //创建selector给SubReactor使用
    private final Selector[] selectors = new Selector[coreNum];

    private final SubReactor[] subReactors = new SubReactor[coreNum];

    //SubReactor的处理线程
    private Thread[] threads = new Thread[coreNum];

    //轮循使用SubReactor的下标索引
    private int next = 0;

    public MultiAcceptor(ServerSocketChannel serverSocketChannel) throws IOException {
        this.serverSocketChannel = serverSocketChannel;

        //初始化
        for(int i = 0; i < coreNum; i++){
            selectors[i] = Selector.open();
            //初始化sub reactor
            subReactors[i] = new SubReactor(selectors[i]);
            //初始化sub reactor的线程
            threads[i] = new Thread(subReactors[i]);
            //
            threads[i].start();
        }
    }

    /**
     * ??? 理解下面的步骤 和 SubReactor.run()中的流程
     */
    @Override
    public void run() {

        SocketChannel socketChannel;

        try {
            //阻塞获取连接
            socketChannel = serverSocketChannel.accept();
            if(socketChannel!=null){
                socketChannel.configureBlocking(false);

                //???
                subReactors[next].registering(true);
                //让线下一次subReactor的while循环下去执行selector.selcet()；
                //但是select是我们使用的不超时阻塞的方式
                //所以下一步需要执行wakeup()

                //使一个阻塞住的selector操作立即返回
                selectors[next].wakeup();
                //当前客户端通道socketChannel向selector[next]注册一个读事件,返回key
                SelectionKey selectionKey = socketChannel.register(selectors[next], SelectionKey.OP_READ);

                selectors[next].wakeup();
                //是一个阻塞住的selector操作立即返回,这样才能对刚刚注册的selectionKey感兴趣

                //本次事件注册完之后,需要再次触发select的 ...todo
                //因此这里的reactor要设置回false(具体参考SubReactor的run方法)
                subReactors[next].registering(false);

                //绑定handler ! ! !
                selectionKey.attach(new AsyncHandler(socketChannel,selectors[next]));

                //轮循负载
                if(++next == selectors.length){
                    //越界后重新分配
                    next = 0;
                }
          }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
