package top.feb13th.simple.rpc.core.scan;

import java.util.Set;
import java.util.function.Predicate;

/**
 * 类扫描器
 *
 * @author feb13th
 */
public interface ClassScanner {

  /**
   * 类文件后缀
   */
  String CLASS_SUFFIX = ".class";

  /**
   * 扫描指定包下的类
   *
   * @param packageName 包名
   * @param predicate   条件过滤器
   * @return 包下面符合条件的所有类
   */
  Set<Class<?>> search(String packageName, Predicate<Class<?>> predicate);

  /**
   * 扫描包下面所有的类
   *
   * @param packageName 报名
   * @return 报名下所有的类
   */
  default Set<Class<?>> search(String packageName) {
    return search(packageName, clazz -> true);
  }
}
