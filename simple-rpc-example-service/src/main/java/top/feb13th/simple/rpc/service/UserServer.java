package top.feb13th.simple.rpc.service;

import top.feb13th.simple.rpc.server.SimpleServer;

/**
 * 服务器
 *
 * @author feb13th
 */
public class UserServer {

  public static void main(String[] args) {
    SimpleServer simpleServer = new SimpleServer(8888);
    simpleServer.registerService(new UserService());
    simpleServer.run();
  }

}
