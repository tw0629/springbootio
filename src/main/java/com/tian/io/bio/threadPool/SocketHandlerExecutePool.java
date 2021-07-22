package com.tian.io.bio.threadPool;


import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author David Tian
 * @desc
 * @since 2020-01-15 15:10
 */
public class SocketHandlerExecutePool {

    private ExecutorService executorService;

    public SocketHandlerExecutePool(int maxPoolSize,int queueSize) {
        executorService = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), maxPoolSize,
                10L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(queueSize));

        //测试 客户端Client大于线程池的线程数量时候,新的客户端线程连接不进来
//      executorService = new ThreadPoolExecutor(2, 2,
//                10L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(queueSize));

    }

    public void execute(Runnable task) {
        executorService.submit(task);
    }
}
