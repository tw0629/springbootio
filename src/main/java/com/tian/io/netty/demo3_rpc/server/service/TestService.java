package com.tian.io.netty.demo3_rpc.server.service;

import java.util.List;

/**
 * @author David Tian
 * @desc
 * @since 2020-04-21 13:10
 */
public interface TestService {

    List<String> listAll();

    String listById(int i);

    List<String> addList(String s);
}
