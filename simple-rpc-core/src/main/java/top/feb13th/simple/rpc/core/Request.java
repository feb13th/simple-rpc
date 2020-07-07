package top.feb13th.simple.rpc.core;

import java.util.HashMap;
import java.util.Map;

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
public class Request {

  /**
   * 客户端使用的唯一id
   */
  private long uniqueId;

  /**
   * 请求的路径
   */
  private String path;

  /**
   * 方法参数对应的值
   */
  private Map<String, Object> data = new HashMap<>();
}
