package com.idlerpg.core.loader;

import com.idlerpg.domain.enemy.EnemyDefinition;
import com.idlerpg.domain.item.ItemDefinition;
import com.idlerpg.domain.quest.QuestDefinition;
import com.idlerpg.domain.region.RegionDefinition;
import com.idlerpg.domain.shop.ShopEntry;
import com.idlerpg.domain.skill.SkillDefinition;

import java.util.List;

public record LoadedGameData(
        List<ItemDefinition> items,
        List<SkillDefinition> skills,
        List<EnemyDefinition> enemies,
        List<RegionDefinition> regions,
        List<QuestDefinition> quests,
        List<ShopEntry> shopEntries
) {
    public LoadedGameData(List<ItemDefinition> items, List<SkillDefinition> skills, List<EnemyDefinition> enemies) {
        this(items, skills, enemies, List.of(), List.of(), List.of());
    }
}
