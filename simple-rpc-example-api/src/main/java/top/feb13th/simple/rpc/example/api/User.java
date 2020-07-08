package top.feb13th.simple.rpc.example.api;

import lombok.*;

/**
 * 用户信息
 *
 * @author feb13th
 */
@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class User {

  private Integer id;
  private String name;
  private Integer age;

}
