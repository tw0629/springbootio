package com.tian.io.nio.demo4.multiAcceptor;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

/**
 * @author David Tian
 * @desc
 * @since 2020-01-15 19:36
 */
public class SubReactor implements Runnable{

    private final Selector selector;

    //注册开关表示,为什么要加这个东西,可以参考Accptor设置
    private boolean register = false;

    public SubReactor(Selector selector) {
        this.selector = selector;
    }

    @Override
    public void run() {

        while (!Thread.interrupted()) {
            while(!Thread.interrupted()&&register){
                try {
                    if(selector.select()==0){
                        continue;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    dispatch((SelectionKey)iterator.next());
                    iterator.remove();
                }

            }
        }

    }

    private void dispatch(SelectionKey selectionKey){
        Runnable runnable = (Runnable) selectionKey.attachment();
        if(runnable!=null){
            //注意：调用的是run方法，不是开启线程
            runnable.run();
        }
    }

    public void registering(boolean register){
        this.register = register;
    }
}
