package com.tian.io.netty.demo3_rpc.client.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author David Tian
 * @desc
 * @since 2020-04-21 13:05
 */
public class ClientSocketNettyHandler extends ChannelInboundHandlerAdapter {

    private Object response;

    public Object getResponse(){
        return response;
    }

    //读取数据事件
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        this.response = msg;
        ctx.close();
    }

}
