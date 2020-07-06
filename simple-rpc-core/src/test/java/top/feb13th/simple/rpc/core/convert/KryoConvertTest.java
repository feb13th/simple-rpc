package top.feb13th.simple.rpc.core.convert;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * kryo 转换器测试类
 *
 * @author feb13th
 */
public class KryoConvertTest {

  private Convert convert;
  private byte[] mapData;

  @Before
  public void init() throws Exception {
    convert = new KryoConvert(1024);
    testObjectToBytes();
  }

  @Test
  public void testObjectToBytes() throws Exception {
    Map<String, Object> map = new HashMap<>();
    map.put("b", 1);
    map.put("a", "a1");
    map.put("c", null);
    map.put("d", false);
    KryoObject object = new KryoObject();
    object.setName("aaa");
    map.put("e", object);
    mapData = convert.objectToBytes(map);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testBytesToObject() throws Exception {
    Map<String, Object> object = convert.bytesToObject(mapData, HashMap.class);
    if (object != null) {
      System.out.println(object);
    }
  }

  private static class KryoObject {
    private String name;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return "KryoObject{" +
          "name='" + name + '\'' +
          '}';
    }
  }
}
