package com.tian.io.netty.demo3_rpc.server.netty;

import com.tian.io.netty.demo3_rpc.entity.ClassInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author David Tian
 * @desc
 * @since 2020-04-21 13:29
 */
public class ServerSocketNettyHandler extends ChannelInboundHandlerAdapter {

    public String getImplClassName(ClassInfo classInfo) throws Exception {

        String iName = "com.tian.io.netty.demo3_rpc.server.service";
        int i = classInfo.getClassName().lastIndexOf(".");
        String className = classInfo.getClassName().substring(i);
        Class aClass = Class.forName(iName + className);
        Reflections reflections = new Reflections(iName);
        Set<Class<?>> classes = reflections.getSubTypesOf(aClass);
        if(classes.size() == 0 ){
            System.out.println("未找到实现类");
            return null;
        }else if(classes.size() >1 ){
            System.out.println("找到实现类,未明确使用哪个实现类");
            return null;
        }else {
            Class[] classes1 = classes.toArray(new Class[0]);
            return classes1[0].getName();
        }
    }


    //读取数据事件
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        //根据需求决定下面的是否需要开线程

        ClassInfo classInfo = (ClassInfo) msg;
        Object o = Class.forName(getImplClassName(classInfo)).newInstance();
        Method method = o.getClass().getMethod(classInfo.getMethodName(), classInfo.getClassType());
        Object invoke = method.invoke(o, classInfo.getArgs());

        ctx.writeAndFlush(invoke);
    }

}
