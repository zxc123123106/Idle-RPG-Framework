package com.idlerpg.core.event;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public final class EventBus {
    private static final EventBus INSTANCE = new EventBus();

    private final Map<Class<?>, List<Consumer<? extends GameEvent>>> subscribers = new ConcurrentHashMap<>();

    private EventBus() {
    }

    public static EventBus getInstance() {
        return INSTANCE;
    }

    public static EventBus createStandalone() {
        return new EventBus();
    }

    public <E extends GameEvent> void subscribe(Class<E> eventType, Consumer<E> subscriber) {
        subscribers.computeIfAbsent(eventType, key -> new CopyOnWriteArrayList<>()).add(subscriber);
    }

    public void publish(GameEvent event) {
        subscribers.forEach((eventType, handlers) -> {
            if (eventType.isAssignableFrom(event.getClass())) {
                for (Consumer<? extends GameEvent> handler : handlers) {
                    notify(handler, event);
                }
            }
        });
    }

    public void clearSubscribers() {
        subscribers.clear();
    }

    @SuppressWarnings("unchecked")
    private <E extends GameEvent> void notify(Consumer<? extends GameEvent> handler, GameEvent event) {
        ((Consumer<E>) handler).accept((E) event);
    }
}
