package com.bizo.concurrent;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class StubScheduledExecutor extends EmptyScheduledExecutorService {

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