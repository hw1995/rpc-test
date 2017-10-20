package com.huaxi.rpc.test;

import com.huaxi.rpc.framework.Huaxi;

/**
 * 服务消费者
 */
public class Client {

  public static void main(String[] args) throws Exception {
    HelloService helloService = Huaxi.getRemoteService(HelloService.class, "127.0.0.1", 1020);
    String str = helloService.hello("王大锤");
    System.out.println(str);
  }
}
