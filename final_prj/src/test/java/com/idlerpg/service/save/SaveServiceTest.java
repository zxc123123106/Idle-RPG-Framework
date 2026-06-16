package com.idlerpg.service.save;

import com.idlerpg.core.registry.ItemRegistry;
import com.idlerpg.domain.item.ItemDefinition;
import com.idlerpg.domain.item.ItemType;
import com.idlerpg.domain.player.Player;
import com.idlerpg.domain.save.SaveGame;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SaveServiceTest {
    @Test
    void saveAndRestorePreservesPlayerInventoryQuestsAndRegion() throws Exception {
        Path tempDir = Files.createTempDirectory("idle-rpg-save-test");
        ItemDefinition ore = new ItemDefinition("copper_ore", "Copper Ore", ItemType.RESOURCE, 5);
        ItemRegistry itemRegistry = new ItemRegistry();
        itemRegistry.register(ore);
        Player player = new Player();
        player.addGold(50);
        player.setCurrentRegionId("iron_ridge");
        player.getInventory().addItem(ore, 4);
        player.getQuestProgress().put("first_ore", 4);
        player.getCompletedQuestIds().add("first_ore");
        player.setActiveSkillId("mine_copper");

        SaveService saveService = new SaveService(tempDir.resolve("save.json"));
        saveService.save(player);
        SaveGame saveGame = saveService.load().orElseThrow();

        Player restored = new Player();
        saveService.restore(restored, saveGame, itemRegistry);

        assertEquals(50, restored.getGold());
        assertEquals("iron_ridge", restored.getCurrentRegionId());
        assertEquals(4, restored.getInventory().getQuantity("copper_ore"));
        assertTrue(restored.getCompletedQuestIds().contains("first_ore"));
        assertEquals("mine_copper", restored.getActiveSkillId());
    }
}
