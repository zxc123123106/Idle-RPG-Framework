package com.idlerpg.core.registry;

import com.idlerpg.domain.item.ItemDefinition;
import com.idlerpg.domain.item.ItemType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegistryTest {
    @Test
    void registerAndLookupById() {
        Registry<ItemDefinition> registry = new Registry<>();
        ItemDefinition item = new ItemDefinition("copper_ore", "Copper Ore", ItemType.RESOURCE, 5);

        registry.register(item);

        assertTrue(registry.get("copper_ore").isPresent());
        assertEquals(item, registry.getRequired("copper_ore"));
    }

    @Test
    void duplicateIdsAreRejected() {
        Registry<ItemDefinition> registry = new Registry<>();
        ItemDefinition item = new ItemDefinition("copper_ore", "Copper Ore", ItemType.RESOURCE, 5);

        registry.register(item);

        assertThrows(IllegalArgumentException.class, () -> registry.register(item));
    }
}
