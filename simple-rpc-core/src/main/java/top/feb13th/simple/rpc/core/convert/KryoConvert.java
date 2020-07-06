package top.feb13th.simple.rpc.core.convert;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * 使用 kryo 进行序列化及反序列化
 *
 * @author feb13th
 */
public class KryoConvert implements Convert {

  private final int bufferSize;

  /**
   * 创建指定缓存的转换器
   *
   * @param bufferSize 可序列化的最大字节书
   */
  public KryoConvert(int bufferSize) {
    this.bufferSize = bufferSize;
  }

  private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
    Kryo kryo = new Kryo();
    kryo.setReferences(false);
    kryo.setRegistrationRequired(false);
    return kryo;
  });

  public byte[] objectToBytes(Object obj) {
    Output output = new Output(bufferSize);
    kryoThreadLocal.get().writeObjectOrNull(output, obj, obj.getClass());
    return output.toBytes();
  }

  public <T> T bytesToObject(byte[] data, Class<T> type) {
    Input input = new Input(data);
    return kryoThreadLocal.get().readObjectOrNull(input, type);
  }
}
