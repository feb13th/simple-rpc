package top.feb13th.simple.rpc.client.codec;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import top.feb13th.simple.rpc.core.Response;
import top.feb13th.simple.rpc.core.convert.Convert;

/**
 * 响应解码器
 *
 * @author feb13th
 */
public class ResponseDecoder extends ByteToMessageDecoder {

  private final Convert convert;

  public ResponseDecoder(Convert convert) {
    this.convert = convert;
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    byte[] data = new byte[in.readableBytes()];
    in.readBytes(data);
    Response response = convert.bytesToObject(data, Response.class);
    if (response == null) {
      // 不支持的消息, 直接抛弃
      return;
    }
    out.add(response);
  }
}
