package top.feb13th.simple.rpc.server;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import top.feb13th.simple.rpc.core.Request;
import top.feb13th.simple.rpc.core.Response;
import top.feb13th.simple.rpc.core.Response.ResponseBuilder;
import top.feb13th.simple.rpc.core.util.Strings;

/**
 * 服务处理器
 *
 * @author feb13th
 */
public class RequestDispatcher extends ChannelInboundHandlerAdapter {

  private final ServicePool servicePool;

  public RequestDispatcher(ServicePool servicePool) {
    assert servicePool != null;
    this.servicePool = servicePool;
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

    Request request = (Request) msg;
    String path = request.getPath();
    if (Strings.isBlank(path) || "/health".equals(path)) {
      // 错误消息或心跳, 忽略
      return;
    }

    ResponseBuilder responseBuilder = Response.builder().uniqueId(request.getUniqueId()).path(path);
    MethodHolder methodHolder = servicePool.getMethodHolder(path);
    if (methodHolder == null) {
      ctx.channel().writeAndFlush(responseBuilder.build());
      return;
    }
    Map<String, Object> paramMap = request.getData();
    List<String> paramNames = methodHolder.getParams();
    Object[] params = new Object[paramNames.size()];
    for (int i = 0; i < paramNames.size(); i++) {
      String name = paramNames.get(i);
      Object obj = paramMap.get(name);
      params[i] = obj;
    }

    Method method = methodHolder.getMethod();
    Object result = method.invoke(methodHolder.getObject(), params);
    responseBuilder.object(result);
    ctx.channel().writeAndFlush(responseBuilder.build());
  }

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    // 消息读取超时, 关闭连接
    if (evt instanceof IdleStateEvent) {
      IdleState state = ((IdleStateEvent) evt).state();
      if (state == IdleState.READER_IDLE) {
        ctx.channel().close();
        return;
      }
    }
    super.userEventTriggered(ctx, evt);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    if (cause instanceof IOException) {
      if ("Connection reset by peer".equals(cause.getMessage())) {
        // 直接断开连接的无视
        return;
      }
    }
    super.exceptionCaught(ctx, cause);
  }
}
