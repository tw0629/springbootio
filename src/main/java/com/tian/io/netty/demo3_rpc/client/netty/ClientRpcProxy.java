package com.tian.io.netty.demo3_rpc.client.netty;

import com.tian.io.netty.demo3_rpc.entity.ClassInfo;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author David Tian
 * @desc
 * @since 2020-04-21 13:06
 */
public class ClientRpcProxy {

    public static Object create(Class clazz){

        System.out.println("--------- test1 ----------");


        return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new InvocationHandler() {

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                System.out.println("--------- test2 ----------");

                ClassInfo classInfo = new ClassInfo();
                classInfo.setClassName(clazz.getName());
                classInfo.setMethodName(method.getName());
                classInfo.setArgs(args);
                classInfo.setClassType(method.getParameterTypes());

                NioEventLoopGroup eventExecutors = new NioEventLoopGroup();

                Bootstrap bootstrap = new Bootstrap();

                ClientSocketNettyHandler nettyClientHandler = new ClientSocketNettyHandler();

                try {
                    bootstrap.group(eventExecutors)
                            .channel(NioSocketChannel.class)
                            .handler(new ChannelInitializer<SocketChannel>() {

                                @Override
                                protected void initChannel(SocketChannel socketChannel) throws Exception {
                                    ChannelPipeline channelPipeline = socketChannel.pipeline();
                                    channelPipeline.addLast("encoder", new ObjectEncoder());
                                    //?
                                    channelPipeline.addLast("decode", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                                    channelPipeline.addLast(nettyClientHandler);
                                }

                            });
                    System.out.println("---------client init----------");

                    ChannelFuture future = bootstrap.connect("127.0.0.1", 9090).sync();
                    //!!!
                    future.channel().writeAndFlush(classInfo).sync();


                    System.out.println("--------- XXX test XXX----------");

                    future.channel().closeFuture().sync();

                    System.out.println("--------- test3 ----------");


                } catch (Exception e) {
                    e.printStackTrace();
                }

                System.out.println("--------- test4 ----------");
                System.out.println("--------- test5 ---------- "+nettyClientHandler.getResponse());

                return nettyClientHandler.getResponse();

            }
        });

    }

}
