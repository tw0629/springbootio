package com.tian.io.netty.demo3_rpc.client.netty;

import com.tian.io.netty.demo3_rpc.server.service.TestService;

/**
 * @author David Tian
 * @desc
 * @since 2020-04-21 13:05
 */
public class ClientSocketNetty {

    public static void main(String[] args) {

        //注意：proxy是代理类
        //经过n轮测试,每次调用proxy的方法 都会调用new InvocationHandler().invoke() [包括proxy本身]

        TestService proxy = (TestService) ClientRpcProxy.create(TestService.class);
        System.out.println("======> "+proxy);
        System.out.println(proxy.listAll());

        //TestService o2 = (TestService) ClientRpcProxy.create(TestService.class);
        System.out.println(proxy.listById(0));

        System.out.println(proxy.addList("攀"));

    }
}
/**
 * 打印结果：
 *
 * --------- test1 ----------
 * --------- test2 ----------
 * ---------client init----------
 * --------- XXX test XXX----------
 * --------- test3 ----------
 * --------- test4 ----------
 * --------- test5 ---------- com.tian.io.netty.demo3_rpc.server.service.TestServiceImpl@71ad798c
 * ======> com.tian.io.netty.demo3_rpc.server.service.TestServiceImpl@71ad798c
 * --------- test2 ----------
 * ---------client init----------
 * --------- XXX test XXX----------
 * --------- test3 ----------
 * --------- test4 ----------
 * --------- test5 ---------- [tian, wei, 帅, 帅]
 * [tian, wei, 帅, 帅]
 * --------- test2 ----------
 * ---------client init----------
 * --------- XXX test XXX----------
 * --------- test3 ----------
 * --------- test4 ----------
 * --------- test5 ---------- tian
 * tian
 * --------- test2 ----------
 * ---------client init----------
 * --------- XXX test XXX----------
 * --------- test3 ----------
 * --------- test4 ----------
 * --------- test5 ---------- [tian, wei, 帅, 帅, 攀]
 * [tian, wei, 帅, 帅, 攀]
 *
 */
