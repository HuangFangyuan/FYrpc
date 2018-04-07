package com.hfy.FYrpc.impl;

import com.hfy.FYrpc.anno.RpcService;
import com.hfy.FYrpc.interfaces.HelloService;

@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService {

    @Override
    public String Hello(String name) {
        return "Hello! " + name;
    }
}
