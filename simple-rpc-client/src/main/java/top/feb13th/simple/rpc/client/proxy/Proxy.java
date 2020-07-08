package top.feb13th.simple.rpc.client.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

import top.feb13th.simple.rpc.client.SimpleClient;
import top.feb13th.simple.rpc.core.Path;

/**
 * api接口代理
 *
 * @author feb13th
 */
public class Proxy implements InvocationHandler {

  private final SimpleClient client;

  public Proxy(SimpleClient client) {
    this.client = client;
  }

  @SuppressWarnings("unchecked")
  public <T> T newBean(Class<T> clazz) {
    if (!clazz.isInterface()) {
      throw new UnsupportedOperationException("Proxy not support class");
    }
    return (T) java.lang.reflect.Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    // 可以考虑使用缓存
    String prefix = "";
    for (Class<?> inter : proxy.getClass().getInterfaces()) {
      Path annotation = inter.getAnnotation(Path.class);
      if (annotation != null) {
        prefix = annotation.value();
        break;
      }
    }
    Path methodPath = method.getAnnotation(Path.class);
    if (methodPath == null) {
      throw new UnsupportedOperationException(method + " not present " + Path.class);
    }

    String path = prefix + methodPath.value();
    Map<String, Object> data = new HashMap<>();
    Parameter[] parameters = method.getParameters();
    for (int i = 0; i < parameters.length; i++) {
      Parameter parameter = parameters[i];
      String name = parameter.getName();
      data.put(name, args[i]);
    }

    return client.sendRequest(path, data).getObject();
  }
}
