package com.huaxi.rpc.framework;


import com.huaxi.rpc.util.StringUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 花溪RPC框架
 *
 * @Author huaxi
 */
public class Huaxi {

  /**
   * 将指定服务暴露出来，供客服端远程调用
   * @param service 需要暴露的服务
   * @param port 暴露的端口号
   * @throws Exception
   */
  public static void export(final Object service, int port) throws Exception {
    if (service == null)
      throw new IllegalArgumentException("service not null");
    if (port < 0 || port > 65535)
      throw new IllegalArgumentException("port in [0,65535]");
    System.out.println("Export Service:" + service.getClass().getName() + " port:" + port);

    ServerSocket serverSocket = new ServerSocket(port);
    while (true) {
      final Socket socket = serverSocket.accept();
      new Thread(() -> {
        ObjectInputStream input = null;
        ObjectOutputStream output = null;
        try {
          try {
            input = new ObjectInputStream(socket.getInputStream());
            output = new ObjectOutputStream(socket.getOutputStream());
            String methodName = input.readUTF();
            Class<?>[] paramTypes = (Class<?>[]) input.readObject();
            Object[] args = (Object[]) input.readObject();

            Method method = service.getClass().getMethod(methodName, paramTypes);
            Object result = method.invoke(service, args);
            output.writeObject(result);
          } catch (IOException e) {
            e.printStackTrace();
          }
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          if (input != null)
            try {
              input.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          if (output != null)
            try {
              output.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
        }
      }).start();
    }
  }

  /**
   * 获取远程服务
   * @param interfaceClass 服务接口Class
   * @param host 远程IP地址
   * @param port 远程端口号
   * @param <T> 指定接口的实例
   * @return
   * @throws Exception
   */
  public static <T> T getRemoteService(final Class<T> interfaceClass, final String host, final int port) throws Exception {
    verifyGetRemoteService(interfaceClass, host, port);
    return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, (Object proxy, Method method, Object[] args) -> {
      Socket socket = new Socket(host, port);
      System.out.println("get remote service :" + interfaceClass.getName()+" from "+host+":"+port);

      ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
      output.writeUTF(method.getName());
      output.writeObject(method.getParameterTypes());
      output.writeObject(args);

      ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
      Object result = input.readObject();
      return result;
    });
  }

  private static <T> void verifyGetRemoteService(final Class<T> interfaceClass, final String host, final int port) {
    if (interfaceClass == null)
      throw new IllegalArgumentException("interfaceClass not null");
    if (!interfaceClass.isInterface())
      throw new IllegalArgumentException("interfaceClass not a interface");
    if (!StringUtils.isNotBlank(host))
      throw new IllegalArgumentException("host not blank");
    if (port < 0 || port > 65535)
      throw new IllegalArgumentException("port in [0,65535]");
  }
}
