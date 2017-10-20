package com.huaxi.rpc.test;

import com.huaxi.rpc.framework.Huaxi;

/**
 * 服务提供者
 */
public class Server {

  public static void main(String[] args) throws Exception {
    HelloService helloService = new HelloServiceImpl();
    Huaxi.export(helloService, 1020);
  }
}
