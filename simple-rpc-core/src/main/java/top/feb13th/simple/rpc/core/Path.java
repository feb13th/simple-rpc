package top.feb13th.simple.rpc.core;

import java.lang.annotation.*;

/**
 * rpc请求路径
 *
 * @author feb13th
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Path {

  /**
   * 请求路径
   */
  String value();

}
