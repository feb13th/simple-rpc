package top.feb13th.simple.rpc.server.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import top.feb13th.simple.rpc.core.Response;
import top.feb13th.simple.rpc.core.convert.Convert;

/**
 * 响应编码器
 *
 * @author feb13th
 */
public class ResponseEncoder extends MessageToByteEncoder<Response> {

  private final Convert convert;

  public ResponseEncoder(Convert convert) {
    this.convert = convert;
  }

  @Override
  protected void encode(ChannelHandlerContext ctx, Response msg, ByteBuf out) throws Exception {
    byte[] bytes = convert.objectToBytes(msg);
    out.writeInt(bytes.length);
    out.writeBytes(bytes);
  }
}
