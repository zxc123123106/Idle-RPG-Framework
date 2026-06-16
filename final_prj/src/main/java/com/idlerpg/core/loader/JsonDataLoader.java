package com.idlerpg.core.loader;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idlerpg.domain.enemy.EnemyDefinition;
import com.idlerpg.domain.item.ItemDefinition;
import com.idlerpg.domain.quest.QuestDefinition;
import com.idlerpg.domain.region.RegionDefinition;
import com.idlerpg.domain.shop.ShopEntry;
import com.idlerpg.domain.skill.SkillDefinition;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public final class JsonDataLoader {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LoadedGameData loadDefaultData() throws IOException {
        return new LoadedGameData(
                loadList("/data/items.json", ItemDefinition.class),
                loadList("/data/skills.json", SkillDefinition.class),
                loadList("/data/enemies.json", EnemyDefinition.class),
                loadList("/data/regions.json", RegionDefinition.class),
                loadList("/data/quests.json", QuestDefinition.class),
                loadList("/data/shop.json", ShopEntry.class)
        );
    }

    public <T> List<T> loadList(String resourcePath, Class<T> itemType) throws IOException {
        try (InputStream inputStream = JsonDataLoader.class.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            JavaType listType = objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, itemType);
            return objectMapper.readValue(inputStream, listType);
        }
    }
}
