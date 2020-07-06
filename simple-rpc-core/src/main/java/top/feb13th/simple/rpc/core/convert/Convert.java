package top.feb13th.simple.rpc.core.convert;

/**
 * 数据转换接口
 *
 * @author feb13th
 */
public interface Convert {

  byte[] objectToBytes(Object obj) throws Exception;

  <T> T bytesToObject(byte[] data, Class<T> type) throws Exception;

}
