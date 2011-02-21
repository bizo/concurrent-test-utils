package com.bizo.concurrent;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * A fake scheduled executor for deterministically testing executor-based code.
 * 
 * Typically, you'll have some production code that takes a {@link ScheduledExecutorService}. Instead of a real one,
 * your unit test can pass in the {@link StubScheduledExecutor}. Then when your production code does:
 * 
 * {@code future = executor.scheduleAtFixedRate(...);}
 * 
 * The arguments (command, delay, etc.) are captured in a fake {@link FixedRateScheduleFuture} and returned (as a
 * future) to the production code. The production code then might do something like call {@code get} on the future to
 * block for its result.
 * 
 * Meanwhile, your test can call {@link #getFixedRateScheduleFutures()} to get the same future instance that was
 * returned to your production code and assert against it's state to ensure the production code passed the right values.
 * 
 * The test code can also cancel or complete (with an appropriate value) the future to see what the production code does
 * after it's {@code get} call is no longer blocking.
 */
public class StubScheduledExecutor extends AbstractNoopScheduledExecutorService {

  /**
   * Each call to scheduleAtFixedRate puts its return value in here so that tests can assert against its args, run the
   * command, and cancel the future.
   */
  private final List<FixedRateScheduleFuture> fixedRates = newArrayList();
  private int count = 0;
  private long lastDelay = 0;
  private Runnable command = null;
  private int shutdown = 0;

  @Override
  public ScheduledFuture<?> schedule(final Runnable command, final long delay, final TimeUnit unit) {
    System.out.format("%s %s %s\n", command, delay, unit);
    count++;
    lastDelay = delay;
    this.command = command;
    return null;
  }

  @Override
  public ScheduledFuture<?> scheduleAtFixedRate(
      final Runnable command,
      final long initialDelay,
      final long period,
      final TimeUnit unit) {
    final FixedRateScheduleFuture f = new FixedRateScheduleFuture(command, initialDelay, period, unit);
    fixedRates.add(f);
    return f;
  }

  @Override
  public void shutdown() {
    shutdown++;
  }

  public int getCount() {
    return count;
  }

  public long getLastDelay() {
    return lastDelay;
  }

  public Runnable getCommand() {
    return command;
  }

  public int getShutdown() {
    return shutdown;
  }

  public List<FixedRateScheduleFuture> getFixedRateScheduleFutures() {
    return fixedRates;
  }

  public static class FixedRateScheduleFuture extends StubScheduledFuture<Object> {
    public final Runnable command;
    public final long initialDelay;
    public final long period;
    public final TimeUnit unit;

    public FixedRateScheduleFuture(
        final Runnable command,
        final long initialDelay,
        final long period,
        final TimeUnit unit) {
      this.command = command;
      this.initialDelay = initialDelay;
      this.period = period;
      this.unit = unit;
    }
  }

}