package com.idlerpg;

import com.idlerpg.core.event.EventBus;
import com.idlerpg.core.registry.EnemyRegistry;
import com.idlerpg.core.registry.ItemRegistry;
import com.idlerpg.core.registry.QuestRegistry;
import com.idlerpg.core.registry.RegionRegistry;
import com.idlerpg.core.registry.ShopRegistry;
import com.idlerpg.core.registry.SkillRegistry;
import com.idlerpg.domain.context.GameContext;
import com.idlerpg.domain.player.Player;
import com.idlerpg.factory.EnemyFactory;
import com.idlerpg.service.combat.CombatService;
import com.idlerpg.service.equipment.EquipmentService;
import com.idlerpg.service.inventory.InventoryService;
import com.idlerpg.service.progression.ProgressionService;
import com.idlerpg.service.quest.QuestService;
import com.idlerpg.service.region.RegionService;
import com.idlerpg.service.shop.ShopService;

public final class TestGameContextFactory {
    private TestGameContextFactory() {
    }

    public static TestContext create() {
        EventBus eventBus = EventBus.createStandalone();
        ItemRegistry itemRegistry = new ItemRegistry();
        SkillRegistry skillRegistry = new SkillRegistry();
        EnemyRegistry enemyRegistry = new EnemyRegistry();
        RegionRegistry regionRegistry = new RegionRegistry();
        QuestRegistry questRegistry = new QuestRegistry();
        ShopRegistry shopRegistry = new ShopRegistry();
        Player player = new Player();
        InventoryService inventoryService = new InventoryService(eventBus);
        ProgressionService progressionService = new ProgressionService(eventBus);
        CombatService combatService = new CombatService(new EnemyFactory(), eventBus);
        RegionService regionService = new RegionService(regionRegistry, eventBus);
        QuestService questService = new QuestService(questRegistry, eventBus);
        ShopService shopService = new ShopService(eventBus);
        EquipmentService equipmentService = new EquipmentService();
        GameContext context = new GameContext(
                player,
                eventBus,
                itemRegistry,
                skillRegistry,
                enemyRegistry,
                regionRegistry,
                questRegistry,
                shopRegistry,
                inventoryService,
                progressionService
        );
        context.setCombatService(combatService);
        context.setRegionService(regionService);
        context.setQuestService(questService);
        context.setShopService(shopService);
        context.setEquipmentService(equipmentService);
        return new TestContext(
                context,
                eventBus,
                itemRegistry,
                skillRegistry,
                enemyRegistry,
                regionRegistry,
                questRegistry,
                shopRegistry,
                inventoryService,
                progressionService,
                combatService,
                regionService,
                questService,
                shopService,
                equipmentService
        );
    }

    public record TestContext(
            GameContext context,
            EventBus eventBus,
            ItemRegistry itemRegistry,
            SkillRegistry skillRegistry,
            EnemyRegistry enemyRegistry,
            RegionRegistry regionRegistry,
            QuestRegistry questRegistry,
            ShopRegistry shopRegistry,
            InventoryService inventoryService,
            ProgressionService progressionService,
            CombatService combatService,
            RegionService regionService,
            QuestService questService,
            ShopService shopService,
            EquipmentService equipmentService
    ) {
    }
}
