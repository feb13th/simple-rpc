package top.feb13th.simple.rpc.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 数据请求对象
 *
 * @author feb13th
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Response {

  /**
   * 客户端使用的唯一id
   */
  private long uniqueId;

  /**
   * 方法的hash code
   */
  private String path;

  /**
   * 方法返回值
   */
  private Object object;
}
