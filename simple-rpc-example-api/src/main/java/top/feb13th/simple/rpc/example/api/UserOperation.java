package top.feb13th.simple.rpc.example.api;

import top.feb13th.simple.rpc.core.Path;

/**
 * 用户操作
 *
 * @author feb13th
 */
@Path("/user")
public interface UserOperation {

  /**
   * 根据id获取用户信息
   */
  @Path("/get/id")
  User getUser(int id);

  @Path("/add")
  void addUser(User user);
}
