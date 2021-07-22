package com.tian.io.netty.demo3_rpc.server.service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author David Tian
 * @desc
 * @since 2020-04-21 13:12
 */
public class TestServiceImpl implements TestService{

    static ArrayList<String> list = new ArrayList<>();

    static{
        list.add("tian");
        list.add("wei");
    }

    @Override
    public List<String> listAll() {
        return list;
    }

    @Override
    public String listById(int i) {
        return list.get(0);
    }

    @Override
    public List<String> addList(String s) {
        list.add(s);
        return list;
    }
}
