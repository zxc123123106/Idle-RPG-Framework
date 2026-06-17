package com.idlerpg.domain.player;

import com.idlerpg.domain.item.Inventory;
import com.idlerpg.domain.item.EquipmentSlot;
import com.idlerpg.domain.skill.ActionType;

import java.time.Instant;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class Player {
    private int level = 1;
    private int experience;
    private int gold;
    private int baseMaxHp = 100;
    private int currentHp = 100;
    private int equipmentAttackBonus;
    private int equipmentDefenseBonus;
    private int equipmentHpBonus;
    private String currentRegionId = "sunlit_meadow";
    private String activeSkillId = "";
    private long lastSavedAtEpochSecond = Instant.now().getEpochSecond();
    private final Inventory inventory = new Inventory();
    private final Set<String> unlockedRegionIds = new HashSet<>();
    private final Map<ActionType, Integer> skillLevels = new EnumMap<>(ActionType.class);
    private final Map<ActionType, Integer> skillExperience = new EnumMap<>(ActionType.class);
    private final Map<EquipmentSlot, String> equipment = new EnumMap<>(EquipmentSlot.class);
    private final Map<String, Integer> questProgress = new HashMap<>();
    private final Set<String> completedQuestIds = new HashSet<>();
    private final Set<String> claimedQuestIds = new HashSet<>();

    public Player() {
        unlockedRegionIds.add(currentRegionId);
        for (ActionType actionType : ActionType.values()) {
            skillLevels.put(actionType, 1);
            skillExperience.put(actionType, 0);
        }
    }

    public int getLevel() {
        return level;
    }

    public int getExperience() {
        return experience;
    }

    public int getGold() {
        return gold;
    }

    public int getMaxHp() {
        return baseMaxHp + equipmentHpBonus;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public int getExperienceToNextLevel() {
        return level * 100;
    }

    public int getAttackPower() {
        return 12 + level * 3 + equipmentAttackBonus;
    }

    public int getDefense() {
        return equipmentDefenseBonus;
    }

    public String getCurrentRegionId() {
        return currentRegionId;
    }

    public void setCurrentRegionId(String currentRegionId) {
        if (currentRegionId == null || currentRegionId.isBlank()) {
            throw new IllegalArgumentException("Current region id is required.");
        }
        this.currentRegionId = currentRegionId;
        unlockedRegionIds.add(currentRegionId);
    }

    public String getActiveSkillId() {
        return activeSkillId;
    }

    public void setActiveSkillId(String activeSkillId) {
        this.activeSkillId = activeSkillId == null ? "" : activeSkillId;
    }

    public long getLastSavedAtEpochSecond() {
        return lastSavedAtEpochSecond;
    }

    public void setLastSavedAtEpochSecond(long lastSavedAtEpochSecond) {
        this.lastSavedAtEpochSecond = lastSavedAtEpochSecond;
    }

    public Set<String> getUnlockedRegionIds() {
        return unlockedRegionIds;
    }

    public Map<ActionType, Integer> getSkillLevels() {
        return skillLevels;
    }

    public Map<ActionType, Integer> getSkillExperience() {
        return skillExperience;
    }

    public int getSkillLevel(ActionType actionType) {
        return skillLevels.getOrDefault(actionType, 1);
    }

    public int getSkillExperience(ActionType actionType) {
        return skillExperience.getOrDefault(actionType, 0);
    }

    public int getSkillExperienceToNextLevel(ActionType actionType) {
        return getSkillLevel(actionType) * 60;
    }

    public void addSkillExperience(ActionType actionType, int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Skill experience cannot be negative.");
        }
        int nextExperience = getSkillExperience(actionType) + amount;
        skillExperience.put(actionType, nextExperience);
        while (skillExperience.get(actionType) >= getSkillExperienceToNextLevel(actionType)) {
            skillExperience.put(actionType, skillExperience.get(actionType) - getSkillExperienceToNextLevel(actionType));
            skillLevels.put(actionType, getSkillLevel(actionType) + 1);
        }
    }

    public Map<EquipmentSlot, String> getEquipment() {
        return equipment;
    }

    public Map<String, Integer> getQuestProgress() {
        return questProgress;
    }

    public Set<String> getCompletedQuestIds() {
        return completedQuestIds;
    }

    public Set<String> getClaimedQuestIds() {
        return claimedQuestIds;
    }

    public void addExperience(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Experience cannot be negative.");
        }
        experience += amount;
    }

    public int reduceCurrentExperienceByPercent(double percent) {
        if (percent < 0.0 || percent > 1.0) {
            throw new IllegalArgumentException("Experience reduction percent must be between 0 and 1.");
        }
        int lostExperience = (int) Math.floor(experience * percent);
        experience -= lostExperience;
        return lostExperience;
    }

    public void levelUp() {
        experience -= getExperienceToNextLevel();
        level++;
        baseMaxHp += 15;
        currentHp = getMaxHp();
    }

    public void addGold(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Gold cannot be negative.");
        }
        gold += amount;
    }

    public boolean spendGold(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Gold amount cannot be negative.");
        }
        if (gold < amount) {
            return false;
        }
        gold -= amount;
        return true;
    }

    public void takeDamage(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Damage cannot be negative.");
        }
        currentHp = Math.max(0, currentHp - amount);
    }

    public void healToFull() {
        currentHp = getMaxHp();
    }

    public void restoreHpToPercent(double percent) {
        if (percent < 0.0 || percent > 1.0) {
            throw new IllegalArgumentException("HP percent must be between 0 and 1.");
        }
        currentHp = Math.max(1, (int) Math.ceil(getMaxHp() * percent));
    }

    public void heal(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Heal amount cannot be negative.");
        }
        currentHp = Math.min(getMaxHp(), currentHp + amount);
    }

    public boolean isDefeated() {
        return currentHp <= 0;
    }

    public void applyEquipmentBonuses(int attackBonus, int defenseBonus, int hpBonus) {
        equipmentAttackBonus = attackBonus;
        equipmentDefenseBonus = defenseBonus;
        equipmentHpBonus = hpBonus;
        if (currentHp > getMaxHp()) {
            currentHp = getMaxHp();
        }
    }

    public void restoreCoreStats(int level, int experience, int gold, int currentHp, int baseMaxHp) {
        this.level = Math.max(1, level);
        this.experience = Math.max(0, experience);
        this.gold = Math.max(0, gold);
        this.baseMaxHp = Math.max(1, baseMaxHp);
        this.currentHp = Math.min(Math.max(1, currentHp), getMaxHp());
    }

    public int getBaseMaxHp() {
        return baseMaxHp;
    }
}
