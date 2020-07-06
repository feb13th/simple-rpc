package top.feb13th.simple.rpc.core;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据请求对象
 *
 * @author feb13th
 */
public class Response {

  /**
   * 客户端使用的唯一id
   */
  private long uniqueId;

  /**
   * 方法的hash code
   */
  private int methodHashCode;

  /**
   * 方法返回值
   */
  private Object object;

  public long getUniqueId() {
    return uniqueId;
  }

  public void setUniqueId(long uniqueId) {
    this.uniqueId = uniqueId;
  }

  public int getMethodHashCode() {
    return methodHashCode;
  }

  public void setMethodHashCode(int methodHashCode) {
    this.methodHashCode = methodHashCode;
  }

  public Object getObject() {
    return object;
  }

  public void setObject(Object object) {
    this.object = object;
  }
}
