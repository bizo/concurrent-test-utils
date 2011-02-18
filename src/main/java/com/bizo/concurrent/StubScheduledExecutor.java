package com.bizo.concurrent;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class StubScheduledExecutor extends EmptyScheduledExecutorService {

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

}