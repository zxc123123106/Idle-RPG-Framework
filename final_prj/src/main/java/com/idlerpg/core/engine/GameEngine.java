package com.idlerpg.core.engine;

import com.idlerpg.core.event.GameTickEvent;
import com.idlerpg.domain.context.GameContext;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public final class GameEngine implements AutoCloseable {
    private final GameContext context;
    private final List<Tickable> tickables = new CopyOnWriteArrayList<>();
    private final AtomicLong tick = new AtomicLong();
    private final AtomicBoolean running = new AtomicBoolean();
    private ScheduledExecutorService executorService;

    public GameEngine(GameContext context) {
        this.context = context;
    }

    public void register(Tickable tickable) {
        tickables.add(tickable);
    }

    public void start() {
        if (!running.compareAndSet(false, true)) {
            return;
        }
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(this::tickOnce, 0, 1, TimeUnit.SECONDS);
    }

    public void stop() {
        running.set(false);
        if (executorService != null) {
            executorService.shutdownNow();
        }
    }

    public void tickOnce() {
        long currentTick = tick.incrementAndGet();
        for (Tickable tickable : tickables) {
            tickable.tick(context);
        }
        context.getEventBus().publish(new GameTickEvent(currentTick, Instant.now()));
    }

    @Override
    public void close() {
        stop();
    }
}
