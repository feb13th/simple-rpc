package top.feb13th.simple.rpc.client;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import top.feb13th.simple.rpc.core.Request;
import top.feb13th.simple.rpc.core.Response;

/**
 * 响应分发器
 *
 * @author feb13th
 */
public class ResponseDispatcher extends ChannelInboundHandlerAdapter {

  // 心跳健康检查
  private static final String HEALTH_PATH = "/health";

  private final SimpleClient client;

  public ResponseDispatcher(SimpleClient client) {
    this.client = client;
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    Response response = (Response) msg;
    long uniqueId = response.getUniqueId();
    CompletableFuture<Response> completableFuture = client.getUnprocessedRequest(uniqueId);
    if (completableFuture != null) {
      completableFuture.complete(response);
    }
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) {
    // 重连
    new Thread(client).start();
  }

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    if (evt instanceof IdleStateEvent) {
      IdleState state = ((IdleStateEvent) evt).state();
      if (state == IdleState.WRITER_IDLE) {
        Request request = Request.builder().uniqueId(0L).path(HEALTH_PATH).data(new HashMap<>()).build();
        ctx.channel().writeAndFlush(request);
        return;
      }
    }
    super.userEventTriggered(ctx, evt);
  }
}
