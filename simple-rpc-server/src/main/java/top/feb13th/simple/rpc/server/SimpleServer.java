package top.feb13th.simple.rpc.server;

import java.io.Closeable;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Setter;
import lombok.SneakyThrows;
import top.feb13th.simple.rpc.core.convert.Convert;
import top.feb13th.simple.rpc.core.convert.KryoConvert;
import top.feb13th.simple.rpc.server.codec.RequestDecoder;
import top.feb13th.simple.rpc.server.codec.ResponseEncoder;

/**
 * rpc 服务器
 *
 * @author feb13th
 */
public class SimpleServer extends ServicePool implements Runnable, Closeable {

  // 端口
  private final int port;

  // config
  @Setter
  private int timeout = 16;
  @Setter
  private int messageMaxLength = 1024 * 1024;

  @Setter
  private Convert convert = new KryoConvert(messageMaxLength);

  // loop
  private final EventLoopGroup bossEventLoop;
  private final EventLoopGroup workerEventLoop;

  public SimpleServer(int port) {
    this(port, Runtime.getRuntime().availableProcessors() + 1);
  }

  public SimpleServer(int port, int threadCount) {
    this.port = port;
    bossEventLoop = new NioEventLoopGroup();
    workerEventLoop = new NioEventLoopGroup(threadCount);

    Runtime.getRuntime().addShutdownHook(new Thread(this::close));
  }

  @Override
  @SneakyThrows
  public void run() {
    ServerBootstrap bootstrap = new ServerBootstrap();
    bootstrap.group(bossEventLoop, workerEventLoop)
        .channel(NioServerSocketChannel.class)
        .handler(new LoggingHandler(LogLevel.ERROR))
        .childHandler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel ch) {
            ch.pipeline().addLast(new IdleStateHandler(timeout, 0, 0));
            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(messageMaxLength + 4, 0, 4, 0, 4));
            ch.pipeline().addLast(new RequestDecoder(convert));
            ch.pipeline().addLast(new ResponseEncoder(convert));
            ch.pipeline().addLast(new RequestDispatcher(SimpleServer.this));
          }
        })
        .option(ChannelOption.SO_BACKLOG, 128)
        .childOption(ChannelOption.SO_KEEPALIVE, true);

    ChannelFuture channelFuture = bootstrap.bind(port).sync();
    channelFuture.channel().closeFuture().sync();
  }

  @Override
  public void close() {
    if (bossEventLoop != null) {
      bossEventLoop.shutdownGracefully();
    }
    if (workerEventLoop != null) {
      workerEventLoop.shutdownGracefully();
    }
    // 清空父类的容器
    super.close();
  }
}
