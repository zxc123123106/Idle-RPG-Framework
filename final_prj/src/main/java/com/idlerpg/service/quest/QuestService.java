package com.idlerpg.service.quest;

import com.idlerpg.core.event.EventBus;
import com.idlerpg.core.event.QuestEvent;
import com.idlerpg.core.registry.QuestRegistry;
import com.idlerpg.domain.context.GameContext;
import com.idlerpg.domain.item.ItemDefinition;
import com.idlerpg.domain.player.Player;
import com.idlerpg.domain.quest.QuestDefinition;
import com.idlerpg.domain.quest.QuestType;

import java.time.Instant;
import java.util.List;

public final class QuestService {
    private final QuestRegistry questRegistry;
    private final EventBus eventBus;

    public QuestService(QuestRegistry questRegistry, EventBus eventBus) {
        this.questRegistry = questRegistry;
        this.eventBus = eventBus;
    }

    public List<QuestDefinition> getAllQuests() {
        return questRegistry.getAll();
    }

    public int getProgress(Player player, QuestDefinition quest) {
        return player.getQuestProgress().getOrDefault(quest.id(), 0);
    }

    public boolean isCompleted(Player player, QuestDefinition quest) {
        return player.getCompletedQuestIds().contains(quest.id());
    }

    public boolean isClaimed(Player player, QuestDefinition quest) {
        return player.getClaimedQuestIds().contains(quest.id());
    }

    public void recordGathered(Player player, String itemId, int quantity) {
        record(player, QuestType.GATHER_ITEM, itemId, quantity);
    }

    public void recordEnemyDefeated(Player player, String enemyId) {
        record(player, QuestType.DEFEAT_ENEMY, enemyId, 1);
    }

    public void recordLevel(Player player) {
        for (QuestDefinition quest : questRegistry.getAll()) {
            if (quest.type() == QuestType.REACH_LEVEL) {
                updateProgress(player, quest, player.getLevel(), true);
            }
        }
    }

    public boolean claimReward(GameContext context, QuestDefinition quest) {
        Player player = context.getPlayer();
        if (!isCompleted(player, quest) || isClaimed(player, quest)) {
            return false;
        }
        if (quest.rewardGold() > 0) {
            player.addGold(quest.rewardGold());
        }
        if (quest.rewardExp() > 0) {
            context.getProgressionService().addExperience(player, quest.rewardExp());
        }
        if (!quest.rewardItemId().isBlank() && quest.rewardQuantity() > 0) {
            ItemDefinition item = context.getItemRegistry().getRequired(quest.rewardItemId());
            context.getInventoryService().addItem(player, item, quest.rewardQuantity());
        }
        if (!quest.unlockRegionId().isBlank()) {
            player.getUnlockedRegionIds().add(quest.unlockRegionId());
        }
        player.getClaimedQuestIds().add(quest.id());
        eventBus.publish(new QuestEvent(QuestEvent.Type.CLAIMED, quest, getProgress(player, quest), Instant.now()));
        if (context.getRegionService() != null) {
            context.getRegionService().unlockEligibleRegions(player);
        }
        return true;
    }

    private void record(Player player, QuestType type, String targetId, int amount) {
        for (QuestDefinition quest : questRegistry.getAll()) {
            if (quest.type() == type && quest.targetId().equals(targetId)) {
                updateProgress(player, quest, amount, false);
            }
        }
    }

    private void updateProgress(Player player, QuestDefinition quest, int amount, boolean absolute) {
        if (isClaimed(player, quest)) {
            return;
        }
        int current = getProgress(player, quest);
        int next = absolute ? Math.max(current, amount) : current + amount;
        next = Math.min(next, quest.requiredCount());
        player.getQuestProgress().put(quest.id(), next);
        eventBus.publish(new QuestEvent(QuestEvent.Type.PROGRESSED, quest, next, Instant.now()));
        if (next >= quest.requiredCount() && player.getCompletedQuestIds().add(quest.id())) {
            eventBus.publish(new QuestEvent(QuestEvent.Type.COMPLETED, quest, next, Instant.now()));
        }
    }
}
