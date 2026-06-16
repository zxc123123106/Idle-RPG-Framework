package com.idlerpg.core.registry;

import com.idlerpg.domain.common.Identifiable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Registry<T extends Identifiable> {
    private final Map<String, T> entries = new LinkedHashMap<>();

    public void register(T value) {
        if (value == null) {
            throw new IllegalArgumentException("Registry value is required.");
        }
        if (entries.containsKey(value.id())) {
            throw new IllegalArgumentException("Duplicate registry id: " + value.id());
        }
        entries.put(value.id(), value);
    }

    public Optional<T> get(String id) {
        return Optional.ofNullable(entries.get(id));
    }

    public T getRequired(String id) {
        return get(id).orElseThrow(() -> new IllegalArgumentException("Unknown registry id: " + id));
    }

    public List<T> getAll() {
        return new ArrayList<>(entries.values());
    }

    public int size() {
        return entries.size();
    }
}
