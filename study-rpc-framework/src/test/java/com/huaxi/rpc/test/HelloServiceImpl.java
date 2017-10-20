package com.huaxi.rpc.test;

/**
 * Created by huwei on 2017/10/20.
 */
public class HelloServiceImpl implements HelloService {
  @Override
  public String hello(String name) {
    return "Hello" + name;
  }
}
