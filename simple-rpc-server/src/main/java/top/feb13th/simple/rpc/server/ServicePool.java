package top.feb13th.simple.rpc.server;

import java.io.Closeable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import top.feb13th.simple.rpc.core.Path;

/**
 * 服务管理器
 *
 * @author feb13th
 */
public class ServicePool implements Closeable {

  private final Map<String, MethodHolder> pathMethodMap = new ConcurrentHashMap<>();
  private final Map<Method, MethodHolder> methodHolderMap = new ConcurrentHashMap<>();

  private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
  private final Lock readLock = readWriteLock.readLock();
  private final Lock writeLock = readWriteLock.writeLock();

  /**
   * 注册服务
   */
  public void registerService(Object service) {
    writeLock.lock();
    try {
      if (service == null) {
        throw new NullPointerException("service is null");
      }
      Class<?> clazz = service.getClass();
      Class<?>[] interfaces = clazz.getInterfaces();
      Class<?> pathInterface = null;
      for (Class<?> inter : interfaces) {
        Path annotation = inter.getAnnotation(Path.class);
        if (annotation != null && pathInterface != null) {
          throw new UnsupportedOperationException("found multiple interface with path, object:[" + service + "]");
        }
        if (annotation != null) {
          pathInterface = inter;
        }
      }

      if (pathInterface == null) {
        throw new UnsupportedOperationException("not found any interface in object:[" + service + "]");
      }

      Path annotation = pathInterface.getAnnotation(Path.class);
      String prefix = annotation == null ? "" : annotation.value();

      for (Method method : pathInterface.getMethods()) {
        Path ann = method.getAnnotation(Path.class);
        if (ann == null) {
          continue;
        }
        String path = prefix + ann.value();
        List<String> params = Arrays.stream(method.getParameters())
            .map(Parameter::getName)
            .collect(Collectors.toList());
        MethodHolder methodHolder = MethodHolder.builder()
            .path(path)
            .object(service)
            .method(method)
            .params(params)
            .build();
        pathMethodMap.put(path, methodHolder);
        methodHolderMap.put(method, methodHolder);
      }
    } finally {
      writeLock.unlock();
    }
  }

  public MethodHolder getMethodHolder(String path) {
    readLock.lock();
    try {
      return pathMethodMap.get(path);
    } finally {
      readLock.unlock();
    }
  }

  public MethodHolder getMethodHolder(Method method) {
    readLock.lock();
    try {
      return methodHolderMap.get(method);
    } finally {
      readLock.unlock();
    }
  }

  @Override
  public void close() {
    writeLock.lock();
    try {
      pathMethodMap.clear();
      methodHolderMap.clear();
    } finally {
      writeLock.lock();
    }
  }
}
