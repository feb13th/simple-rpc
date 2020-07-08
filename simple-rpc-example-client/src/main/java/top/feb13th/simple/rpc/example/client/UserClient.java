package top.feb13th.simple.rpc.example.client;

import top.feb13th.simple.rpc.client.SimpleClient;
import top.feb13th.simple.rpc.example.api.User;
import top.feb13th.simple.rpc.example.api.UserOperation;

/**
 * 客户端管理用户, 请求服务器数据
 *
 * @author feb13th
 */
public class UserClient {

  public static void main(String[] args) {
    SimpleClient simpleClient = new SimpleClient("localhost", 8888);
    new Thread(simpleClient).start();
    simpleClient.await();
    UserOperation userOperation = simpleClient.newProxyBean(UserOperation.class);
    User user = userOperation.getUser(1);
    System.out.println(user.toString());
    userOperation.addUser(user);
  }

}
