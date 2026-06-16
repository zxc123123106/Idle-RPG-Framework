package com.idlerpg.factory;

import com.idlerpg.domain.item.ItemDefinition;
import com.idlerpg.domain.item.ItemType;

public final class ItemFactory {
    public ItemDefinition create(String id, String name, ItemType type, int value) {
        return new ItemDefinition(id, name, type, value);
    }
}
