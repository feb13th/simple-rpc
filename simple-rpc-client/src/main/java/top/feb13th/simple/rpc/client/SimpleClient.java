package top.feb13th.simple.rpc.client;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Setter;
import lombok.SneakyThrows;
import top.feb13th.simple.rpc.client.codec.RequestEncoder;
import top.feb13th.simple.rpc.client.codec.ResponseDecoder;
import top.feb13th.simple.rpc.client.proxy.Proxy;
import top.feb13th.simple.rpc.core.Request;
import top.feb13th.simple.rpc.core.Response;
import top.feb13th.simple.rpc.core.convert.Convert;
import top.feb13th.simple.rpc.core.convert.KryoConvert;

/**
 * 客户端
 *
 * @author feb13th
 */
public class SimpleClient extends ProviderPool implements Runnable, Closeable {

  // 链接信息
  private final String host;
  private final int port;

  // config
  @Setter
  private int idleTimeout = 5;
  @Setter
  private int requestTimeout = 3;
  @Setter
  private int messageMaxLength = 1024 * 1024;
  @Setter
  private Convert convert = new KryoConvert(messageMaxLength);

  // loop
  private final EventLoopGroup workerEventLoop;
  private Channel channel;

  private Proxy beanProxy;
  private CountDownLatch countDownLatch = new CountDownLatch(1);

  public SimpleClient(String host, int port) {
    this(host, port, Runtime.getRuntime().availableProcessors() + 1);
  }

  public SimpleClient(String host, int port, int threadCount) {
    this.host = host;
    this.port = port;
    workerEventLoop = new NioEventLoopGroup(threadCount);
    Runtime.getRuntime().addShutdownHook(new Thread(this::close));
  }

  @Override
  @SneakyThrows
  public void run() {
    Bootstrap bootstrap = new Bootstrap();
    bootstrap.group(workerEventLoop)
        .channel(NioSocketChannel.class)
        .handler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel ch) {
            ch.pipeline().addLast(new IdleStateHandler(0, idleTimeout, 0));
            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(messageMaxLength + 4, 0, 4, 0, 4));
            ch.pipeline().addLast(new RequestEncoder(convert));
            ch.pipeline().addLast(new ResponseDecoder(convert));
            ch.pipeline().addLast(new ResponseDispatcher(SimpleClient.this));
          }
        })
        .option(ChannelOption.SO_KEEPALIVE, true);
    ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
    channel = channelFuture.channel();
    if (countDownLatch.getCount() > 0) {
      countDownLatch.countDown();
    }
    channel.closeFuture().sync();
  }

  // 等待程序启动
  @SneakyThrows
  public void await() {
    countDownLatch.await();
  }

  @SneakyThrows
  public Response sendRequest(String path, Map<String, Object> kv) {
    CompletableFuture<Response> completableFuture = new CompletableFuture<>();
    long id = super.newRequestId();
    Request request = Request.builder().uniqueId(id).path(path).data(kv == null ? new HashMap<>() : kv).build();
    try {
      if (channel != null && channel.isActive()) {
        super.addUnprocessedRequest(id, completableFuture);
        ChannelFuture channelFuture = channel.writeAndFlush(request);
        channelFuture.addListener(future -> {
          if (!future.isSuccess()) {
            completableFuture.completeExceptionally(future.cause());
          }
        });
      }
      return completableFuture.get(requestTimeout, TimeUnit.SECONDS);
    } finally {
      super.removeUnprocessedRequest(id);
    }
  }

  /**
   * 生成代理bean
   */
  public <T> T newProxyBean(Class<T> clazz) {
    return getBeanProxy().newBean(clazz);
  }

  private Proxy getBeanProxy() {
    if (beanProxy == null) {
      synchronized (this) {
        if (beanProxy == null) {
          beanProxy = new Proxy(this);
        }
      }
    }
    return beanProxy;
  }

  @Override
  public void close() {
    if (workerEventLoop != null) {
      workerEventLoop.shutdownGracefully();
    }
    super.close();
  }
}
