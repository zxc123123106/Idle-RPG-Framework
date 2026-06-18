package com.idlerpg.core.loader;

import com.idlerpg.domain.skill.ActionType;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonDataLoaderTest {
    @Test
    void loadsDefaultJsonData() throws IOException {
        LoadedGameData data = new JsonDataLoader().loadDefaultData();

        assertFalse(data.items().isEmpty());
        assertFalse(data.skills().isEmpty());
        assertFalse(data.enemies().isEmpty());

        var itemIds = data.items().stream()
                .map(item -> item.id())
                .collect(Collectors.toSet());
        assertTrue(data.skills().stream().allMatch(skill -> itemIds.contains(skill.rewardItemId())));
        assertTrue(data.skills().stream()
                .filter(skill -> !skill.consumeItemId().isBlank())
                .allMatch(skill -> itemIds.contains(skill.consumeItemId())));

        var skillIds = data.skills().stream()
                .map(skill -> skill.id())
                .collect(Collectors.toSet());
        var skillsById = data.skills().stream()
                .collect(Collectors.toMap(skill -> skill.id(), skill -> skill));
        assertTrue(data.regions().stream()
                .flatMap(region -> region.skillIds().stream())
                .allMatch(skillIds::contains));
        assertTrue(data.regions().stream()
                .allMatch(region -> region.skillIds().stream()
                        .map(skillsById::get)
                        .anyMatch(skill -> skill.actionType() == ActionType.MINING)));
        assertTrue(data.regions().stream()
                .allMatch(region -> region.skillIds().stream()
                        .map(skillsById::get)
                        .anyMatch(skill -> skill.actionType() == ActionType.FISHING)));
        assertTrue(data.skills().stream()
                .anyMatch(skill -> !skill.isRegionRestricted()));
        assertTrue(data.skills().stream()
                .anyMatch(skill -> skill.actionType() == ActionType.GATHERING));
    }
}
