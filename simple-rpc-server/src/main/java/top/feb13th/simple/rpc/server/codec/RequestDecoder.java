package top.feb13th.simple.rpc.server.codec;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import top.feb13th.simple.rpc.core.Request;
import top.feb13th.simple.rpc.core.convert.Convert;

/**
 * 请求解码器
 *
 * @author feb13th
 */
public class RequestDecoder extends ByteToMessageDecoder {

  private final Convert convert;

  public RequestDecoder(Convert convert) {
    this.convert = convert;
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    byte[] data = new byte[in.readableBytes()];
    in.readBytes(data);
    Request request = convert.bytesToObject(data, Request.class);
    if (request != null) {
      out.add(request);
    }
  }
}
