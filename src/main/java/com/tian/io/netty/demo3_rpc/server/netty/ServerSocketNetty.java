package com.tian.io.netty.demo3_rpc.server.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * @author David Tian
 * @desc
 * @since 2020-04-21 13:13
 */
public class ServerSocketNetty {

    public static void main(String[] args) {

        EventLoopGroup bossGroup = new NioEventLoopGroup();

        EventLoopGroup workGroup = new NioEventLoopGroup();


        ServerBootstrap serverBootstrap = new ServerBootstrap();
        try {
            serverBootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>(){

                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {

                            ChannelPipeline channelPipeline = socketChannel.pipeline();
                            channelPipeline.addLast("encoder", new ObjectEncoder());
                            //?
                            channelPipeline.addLast("decode", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                            channelPipeline.addLast(new ServerSocketNettyHandler());

                        }
                    });

            System.out.println("---------server init----------");

            ChannelFuture future = serverBootstrap.bind(9090).sync();

            System.out.println("---------server start----------");

            //关闭通道 关闭线程组 非阻塞
            future.channel().closeFuture().sync();

            System.out.println("---------test1 ----------");

        } catch (Exception e) {
            e.getMessage();
        }finally {

            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
            System.out.println("---------test2 ----------");
        }

    }
}
