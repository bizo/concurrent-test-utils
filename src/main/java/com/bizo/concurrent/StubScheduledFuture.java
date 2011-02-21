package com.bizo.concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A future that can be deterministically manipulated by test code instead of relying on real timing/threading behavior
 * like futures returned by a ScheduledExecutorService.
 */
public class StubScheduledFuture<T> implements ScheduledFuture<T> {

  private final CountDownLatch latch = new CountDownLatch(1);
  private final AtomicBoolean canceled = new AtomicBoolean(false);
  private final AtomicBoolean done = new AtomicBoolean(false);
  private final AtomicReference<T> result = new AtomicReference<T>(null);

  // TODO support get throwing an exception via an error(Exception) method

  public void done(final T result) {
    if (done.compareAndSet(false, true)) {
      this.result.set(result);
      latch.countDown();
    }
  }

  @Override
  public long getDelay(final TimeUnit unit) {
    return 0;
  }

  @Override
  public int compareTo(final Delayed o) {
    return 0;
  }

  @Override
  public boolean cancel(final boolean mayInterruptIfRunning) {
    if (canceled.compareAndSet(false, true)) {
      latch.countDown();
      return true;
    }
    return false;
  }

  @Override
  public boolean isCancelled() {
    return canceled.get();
  }

  @Override
  public boolean isDone() {
    return done.get();
  }

  @Override
  public T get() throws InterruptedException, ExecutionException {
    latch.await();
    return result.get();
  }

  @Override
  public T get(final long timeout, final TimeUnit unit)
      throws InterruptedException,
      ExecutionException,
      TimeoutException {
    latch.await(timeout, unit);
    return get();
  }

}
