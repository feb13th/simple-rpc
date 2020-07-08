package top.feb13th.simple.rpc.client.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import top.feb13th.simple.rpc.core.Request;
import top.feb13th.simple.rpc.core.convert.Convert;

/**
 * 请求编码器
 *
 * @author feb13th
 */
public class RequestEncoder extends MessageToByteEncoder<Request> {

  private final Convert convert;

  public RequestEncoder(Convert convert) {
    this.convert = convert;
  }

  @Override
  protected void encode(ChannelHandlerContext ctx, Request msg, ByteBuf out) throws Exception {
    byte[] bytes = convert.objectToBytes(msg);
    out.writeInt(bytes.length);
    out.writeBytes(bytes);
  }
}
