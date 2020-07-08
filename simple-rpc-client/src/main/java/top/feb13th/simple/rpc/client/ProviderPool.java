package top.feb13th.simple.rpc.client;

import java.io.Closeable;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import top.feb13th.simple.rpc.core.Response;

/**
 * api池
 *
 * @author feb13th
 */
public class ProviderPool implements Closeable {

  private final AtomicLong requestId = new AtomicLong();
  private final Map<Long, CompletableFuture<Response>> unprocessedRequest = new ConcurrentHashMap<>();

  public long newRequestId() {
    return requestId.incrementAndGet();
  }

  public void addUnprocessedRequest(long requestId, CompletableFuture<Response> cf) {
    unprocessedRequest.put(requestId, cf);
  }

  public void removeUnprocessedRequest(long requestId) {
    unprocessedRequest.remove(requestId);
  }

  public CompletableFuture<Response> getUnprocessedRequest(long requestId) {
    return unprocessedRequest.get(requestId);
  }

  @Override
  public void close() {

  }
}
