package vn.com.mbbank.adminportal.common.util;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.OutputStream;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
public class ForwardBodySubscriber  implements HttpResponse.BodySubscriber<Void> {
  private final OutputStream outputStream;
  private Flow.Subscription subscription;
  private final CompletableFuture<Void> result = new CompletableFuture<>();
  private final AtomicBoolean subscribed = new AtomicBoolean();
  private final byte[] buffer = new byte[1024];

  @Override
  public CompletionStage<Void> getBody() {
    return result;
  }

  @Override
  public void onSubscribe(Flow.Subscription subscription) {
    if (!subscribed.compareAndSet(false, true)) {
      subscription.cancel();
    } else {
      this.subscription = subscription;
      subscription.request(1);
    }
  }

  @Override
  public void onNext(List<ByteBuffer> items) {
    try {
      for (ByteBuffer bb : items) {
        writeBuffer(bb);
      }
      subscription.request(1);
    } catch (IOException e) {
      subscription.cancel();
      result.completeExceptionally(e);
    }
  }

  private void writeBuffer(ByteBuffer bb) throws IOException {
    int length;
    while ((length = Math.min(buffer.length, bb.remaining())) > 0) {
      bb.get(buffer, 0, length);
      outputStream.write(buffer, 0, length);
    }
  }

  @Override
  public void onError(Throwable throwable) {
    result.completeExceptionally(throwable);
  }

  @Override
  public void onComplete() {
    try {
      outputStream.flush();
      result.complete(null);
    } catch (IOException e) {
      result.completeExceptionally(e);
    }
  }
}