# simple-rpc
移除一切复杂功能，只为做到最简单的服务调用。
**本项目仅能作为理解RPC调用原理，若需在生产环境中使用，请酌量优化**

# 使用文档
## API
**API**模块提供了基础的接口信息, 客户端和服务端都需要引入该模块，服务器需实现**API**模块提供的接口, 客户端直接调用**API**提供的接口。

第一步：**API**需要引入**simple-rpc-core**项目：
```xml
<dependency>
  <groupId>top.feb13th</groupId>
  <artifactId>simple-rpc-core</artifactId>
  <version>${project.version}</version>
</dependency>
```
第二步：编写接口，接口可选是否标注**top.feb13th.simple.rpc.core.Path**注解。接口内的方法必须使用**top.feb13th.simple.rpc.core.Path**进行标注, 以下是个简单的例子：
```java
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

```

## Server
**Server**需引入**API**项目，并实现**API**提供的接口。同时需要引入**simple-rpc-server**项目：

第一步：配置项目引用
```xml
<!-- API项目, 替换为自己的 -->
<dependency>
  <groupId>top.feb13th</groupId>
  <artifactId>simple-rpc-example-api</artifactId>
  <version>${project.version}</version>
</dependency>

<!-- 服务器项目 -->
<dependency>
  <groupId>top.feb13th</groupId>
  <artifactId>simple-rpc-server</artifactId>
  <version>${project.version}</version>
</dependency>
```

第二步：编写**API**提供的接口的实现类：
```java
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
```
第三步：启动服务器并注册服务
```java
import top.feb13th.simple.rpc.server.SimpleServer;

/**
 * 服务器
 *
 * @author feb13th
 */
public class UserServer {

  public static void main(String[] args) {
    // 初始化服务器端口
    SimpleServer simpleServer = new SimpleServer(8888);
    // 注册服务, 可以在任何时候进行注册
    simpleServer.registerService(new UserService());
    // 启动服务, 可以将该方法放入异步线程进行执行
    simpleServer.run();
  }

}
```

## Client
**Client**通过调用**API**提供的接口进行业务处理

第一步：配置项目引用
```xml
<!-- API项目, 替换为自己的 -->
<dependency>
  <groupId>top.feb13th</groupId>
  <artifactId>simple-rpc-example-api</artifactId>
  <version>${project.version}</version>
</dependency>

<!-- 客户端项目 -->
<dependency>
  <groupId>top.feb13th</groupId>
  <artifactId>simple-rpc-client</artifactId>
  <version>${revision}</version>
</dependency>
```

第二步：启动客户端，调用接口测试
```java
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
    // 初始化客户端
    SimpleClient simpleClient = new SimpleClient("localhost", 8888);
    // 异步启动客户端
    new Thread(simpleClient).start();
    // 等待客户端启动完成
    simpleClient.await();
    // 生成 api 代理对象
    UserOperation userOperation = simpleClient.newProxyBean(UserOperation.class);
    
    // 执行业操作
    User user = userOperation.getUser(1);
    System.out.println(user.toString());
    userOperation.addUser(user);
  }

}
```