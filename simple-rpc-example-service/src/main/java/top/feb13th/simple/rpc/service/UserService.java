package top.feb13th.simple.rpc.service;

import top.feb13th.simple.rpc.example.api.User;
import top.feb13th.simple.rpc.example.api.UserOperation;

/**
 * 用户服务
 *
 * @author feb13th
 */
public class UserService implements UserOperation {

  @Override
  public User getUser(int id) {
    return User.builder().id(id).name("name" + id).age(18).build();
  }

  @Override
  public void addUser(User user) {
    System.out.println(user);
  }
}
