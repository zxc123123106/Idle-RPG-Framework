package com.idlerpg.service.save;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.idlerpg.core.registry.ItemRegistry;
import com.idlerpg.domain.item.EquipmentSlot;
import com.idlerpg.domain.item.ItemDefinition;
import com.idlerpg.domain.player.Player;
import com.idlerpg.domain.save.SaveGame;
import com.idlerpg.domain.skill.ActionType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class SaveService {
    private final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);
    private final Path savePath;

    public SaveService() {
        this(Path.of(System.getProperty("user.home"), ".idle-rpg-framework", "save.json"));
    }

    public SaveService(Path savePath) {
        this.savePath = savePath;
    }

    public Path getSavePath() {
        return savePath;
    }

    public void save(Player player) throws IOException {
        player.setLastSavedAtEpochSecond(Instant.now().getEpochSecond());
        if (savePath.getParent() != null) {
            Files.createDirectories(savePath.getParent());
        }
        objectMapper.writeValue(savePath.toFile(), fromPlayer(player));
    }

    public Optional<SaveGame> load() throws IOException {
        if (!Files.exists(savePath)) {
            return Optional.empty();
        }
        return Optional.of(objectMapper.readValue(savePath.toFile(), SaveGame.class));
    }

    public SaveGame fromPlayer(Player player) {
        Map<String, Integer> skillLevels = new LinkedHashMap<>();
        Map<String, Integer> skillExperience = new LinkedHashMap<>();
        for (ActionType actionType : ActionType.values()) {
            skillLevels.put(actionType.name(), player.getSkillLevel(actionType));
            skillExperience.put(actionType.name(), player.getSkillExperience(actionType));
        }

        Map<String, String> equipment = new LinkedHashMap<>();
        player.getEquipment().forEach((slot, itemId) -> equipment.put(slot.name(), itemId));

        return new SaveGame(
                player.getLevel(),
                player.getExperience(),
                player.getGold(),
                player.getCurrentHp(),
                player.getBaseMaxHp(),
                player.getCurrentRegionId(),
                player.getUnlockedRegionIds(),
                player.getInventory().asQuantityMap(),
                skillLevels,
                skillExperience,
                equipment,
                player.getQuestProgress(),
                player.getCompletedQuestIds(),
                player.getClaimedQuestIds(),
                "",
                player.getLastSavedAtEpochSecond()
        );
    }

    public void restore(Player player, SaveGame saveGame, ItemRegistry itemRegistry) {
        player.restoreCoreStats(
                saveGame.level(),
                saveGame.experience(),
                saveGame.gold(),
                saveGame.currentHp(),
                saveGame.baseMaxHp()
        );
        player.setCurrentRegionId(saveGame.currentRegionId());
        player.getUnlockedRegionIds().clear();
        player.getUnlockedRegionIds().addAll(saveGame.unlockedRegionIds());

        player.getInventory().clear();
        saveGame.inventory().forEach((itemId, quantity) -> {
            ItemDefinition item = itemRegistry.getRequired(itemId);
            player.getInventory().setItemQuantity(item, quantity);
        });

        player.getSkillLevels().clear();
        player.getSkillExperience().clear();
        for (ActionType actionType : ActionType.values()) {
            player.getSkillLevels().put(actionType, saveGame.skillLevels().getOrDefault(actionType.name(), 1));
            player.getSkillExperience().put(actionType, saveGame.skillExperience().getOrDefault(actionType.name(), 0));
        }

        player.getEquipment().clear();
        saveGame.equipment().forEach((slotName, itemId) -> player.getEquipment().put(EquipmentSlot.valueOf(slotName), itemId));
        player.getQuestProgress().clear();
        player.getQuestProgress().putAll(saveGame.questProgress());
        player.getCompletedQuestIds().clear();
        player.getCompletedQuestIds().addAll(saveGame.completedQuestIds());
        player.getClaimedQuestIds().clear();
        player.getClaimedQuestIds().addAll(saveGame.claimedQuestIds());
        player.setActiveSkillId("");
        player.setLastSavedAtEpochSecond(saveGame.lastSavedAtEpochSecond());
    }
}
