package top.feb13th.simple.rpc.server;

import java.lang.reflect.Method;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

/**
 * 方法包装
 *
 * @author feb13th
 */
@Getter
@Builder
public class MethodHolder {

  private final String path;
  private final Object object;
  private final Method method;
  private final List<String> params;

}
