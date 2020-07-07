package top.feb13th.simple.rpc.core.util;

/**
 * 字符串工具类
 *
 * @author feb13th
 */
public final class Strings {

  // 禁止构建对象
  private Strings() {
  }

  public static boolean isNull(String str) {
    return str == null;
  }

  public static boolean isNotNull(String str) {
    return !isNull(str);
  }

  public static boolean isBlank(String str) {
    return isNull(str) || "".equals(str.trim());
  }

  public static boolean isNotBlank(String str) {
    return !isBlank(str);
  }
}
