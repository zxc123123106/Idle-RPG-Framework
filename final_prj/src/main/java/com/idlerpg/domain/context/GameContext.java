package com.idlerpg.domain.context;

import com.idlerpg.core.event.EventBus;
import com.idlerpg.core.registry.EnemyRegistry;
import com.idlerpg.core.registry.ItemRegistry;
import com.idlerpg.core.registry.QuestRegistry;
import com.idlerpg.core.registry.RegionRegistry;
import com.idlerpg.core.registry.ShopRegistry;
import com.idlerpg.core.registry.SkillRegistry;
import com.idlerpg.domain.player.Player;
import com.idlerpg.service.combat.CombatService;
import com.idlerpg.service.equipment.EquipmentService;
import com.idlerpg.service.quest.QuestService;
import com.idlerpg.service.region.RegionService;
import com.idlerpg.service.shop.ShopService;
import com.idlerpg.service.inventory.InventoryService;
import com.idlerpg.service.progression.ProgressionService;

public final class GameContext {
    private final Player player;
    private final EventBus eventBus;
    private final ItemRegistry itemRegistry;
    private final SkillRegistry skillRegistry;
    private final EnemyRegistry enemyRegistry;
    private final RegionRegistry regionRegistry;
    private final QuestRegistry questRegistry;
    private final ShopRegistry shopRegistry;
    private final InventoryService inventoryService;
    private final ProgressionService progressionService;
    private RegionService regionService;
    private QuestService questService;
    private ShopService shopService;
    private EquipmentService equipmentService;
    private CombatService combatService;

    public GameContext(
            Player player,
            EventBus eventBus,
            ItemRegistry itemRegistry,
            SkillRegistry skillRegistry,
            EnemyRegistry enemyRegistry,
            InventoryService inventoryService,
            ProgressionService progressionService
    ) {
        this(
                player,
                eventBus,
                itemRegistry,
                skillRegistry,
                enemyRegistry,
                new RegionRegistry(),
                new QuestRegistry(),
                new ShopRegistry(),
                inventoryService,
                progressionService
        );
    }

    public GameContext(
            Player player,
            EventBus eventBus,
            ItemRegistry itemRegistry,
            SkillRegistry skillRegistry,
            EnemyRegistry enemyRegistry,
            RegionRegistry regionRegistry,
            QuestRegistry questRegistry,
            ShopRegistry shopRegistry,
            InventoryService inventoryService,
            ProgressionService progressionService
    ) {
        this.player = player;
        this.eventBus = eventBus;
        this.itemRegistry = itemRegistry;
        this.skillRegistry = skillRegistry;
        this.enemyRegistry = enemyRegistry;
        this.regionRegistry = regionRegistry;
        this.questRegistry = questRegistry;
        this.shopRegistry = shopRegistry;
        this.inventoryService = inventoryService;
        this.progressionService = progressionService;
    }

    public Player getPlayer() {
        return player;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public ItemRegistry getItemRegistry() {
        return itemRegistry;
    }

    public SkillRegistry getSkillRegistry() {
        return skillRegistry;
    }

    public EnemyRegistry getEnemyRegistry() {
        return enemyRegistry;
    }

    public RegionRegistry getRegionRegistry() {
        return regionRegistry;
    }

    public QuestRegistry getQuestRegistry() {
        return questRegistry;
    }

    public ShopRegistry getShopRegistry() {
        return shopRegistry;
    }

    public InventoryService getInventoryService() {
        return inventoryService;
    }

    public ProgressionService getProgressionService() {
        return progressionService;
    }

    public RegionService getRegionService() {
        return regionService;
    }

    public void setRegionService(RegionService regionService) {
        this.regionService = regionService;
    }

    public QuestService getQuestService() {
        return questService;
    }

    public void setQuestService(QuestService questService) {
        this.questService = questService;
    }

    public ShopService getShopService() {
        return shopService;
    }

    public void setShopService(ShopService shopService) {
        this.shopService = shopService;
    }

    public EquipmentService getEquipmentService() {
        return equipmentService;
    }

    public void setEquipmentService(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    public CombatService getCombatService() {
        return combatService;
    }

    public void setCombatService(CombatService combatService) {
        this.combatService = combatService;
    }
}
