package com.idlerpg.controller;

import com.idlerpg.command.StartActionCommand;
import com.idlerpg.command.StartCombatCommand;
import com.idlerpg.command.StopActionCommand;
import com.idlerpg.command.StopCombatCommand;
import com.idlerpg.core.engine.GameEngine;
import com.idlerpg.core.event.CombatEvent;
import com.idlerpg.core.event.EventBus;
import com.idlerpg.core.event.GameEvent;
import com.idlerpg.core.event.GameTickEvent;
import com.idlerpg.core.event.ItemAddedEvent;
import com.idlerpg.core.event.LevelUpEvent;
import com.idlerpg.core.event.QuestEvent;
import com.idlerpg.core.event.RegionUnlockedEvent;
import com.idlerpg.core.event.ShopPurchaseEvent;
import com.idlerpg.core.event.SkillProgressEvent;
import com.idlerpg.core.loader.JsonDataLoader;
import com.idlerpg.core.loader.LoadedGameData;
import com.idlerpg.core.registry.EnemyRegistry;
import com.idlerpg.core.registry.ItemRegistry;
import com.idlerpg.core.registry.QuestRegistry;
import com.idlerpg.core.registry.RegionRegistry;
import com.idlerpg.core.registry.ShopRegistry;
import com.idlerpg.core.registry.SkillRegistry;
import com.idlerpg.domain.context.GameContext;
import com.idlerpg.domain.enemy.EnemyDefinition;
import com.idlerpg.domain.enemy.EnemyInstance;
import com.idlerpg.domain.item.EquipmentSlot;
import com.idlerpg.domain.item.InventoryStack;
import com.idlerpg.domain.item.ItemDefinition;
import com.idlerpg.domain.item.ItemType;
import com.idlerpg.domain.player.Player;
import com.idlerpg.domain.quest.QuestDefinition;
import com.idlerpg.domain.region.RegionDefinition;
import com.idlerpg.domain.shop.ShopEntry;
import com.idlerpg.domain.skill.ActionType;
import com.idlerpg.domain.skill.SkillDefinition;
import com.idlerpg.factory.EnemyFactory;
import com.idlerpg.factory.SkillFactory;
import com.idlerpg.service.combat.CombatService;
import com.idlerpg.service.equipment.EquipmentService;
import com.idlerpg.service.gathering.GatheringService;
import com.idlerpg.service.inventory.InventoryService;
import com.idlerpg.service.progression.ProgressionService;
import com.idlerpg.service.quest.QuestService;
import com.idlerpg.service.region.RegionService;
import com.idlerpg.service.save.SaveService;
import com.idlerpg.service.shop.ShopService;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class AppController {
    @FXML private Label levelLabel;
    @FXML private Label expLabel;
    @FXML private Label goldLabel;
    @FXML private Label hpLabel;
    @FXML private Label attackLabel;
    @FXML private Label defenseLabel;
    @FXML private Label regionTitleLabel;
    @FXML private Label saveStatusLabel;
    @FXML private Label toastLabel;
    @FXML private ProgressBar expBar;
    @FXML private ProgressBar playerHpBar;

    @FXML private VBox sideEntryBox;
    @FXML private ComboBox<InventoryFilter> inventoryFilterComboBox;
    @FXML private ListView<InventoryStack> inventoryListView;
    @FXML private Label itemDetailNameLabel;
    @FXML private Label itemDetailMetaLabel;
    @FXML private Label itemDetailDescriptionLabel;
    @FXML private Label itemDetailStatsLabel;
    @FXML private Button inventoryEquipButton;
    @FXML private Button inventoryDeleteButton;

    @FXML private ScrollPane eventView;
    @FXML private ScrollPane combatView;
    @FXML private ScrollPane equipmentView;
    @FXML private ScrollPane journalView;
    @FXML private ScrollPane mapView;
    @FXML private ScrollPane shopView;

    @FXML private Label eventTitleLabel;
    @FXML private Label eventHeroLabel;
    @FXML private Label eventSubtitleLabel;
    @FXML private Label eventDescriptionLabel;
    @FXML private Label eventStatLabel;
    @FXML private Label eventBonusLabel;
    @FXML private VBox eventSkillControls;
    @FXML private ComboBox<SkillDefinition> skillComboBox;
    @FXML private Label activeSkillLabel;
    @FXML private ProgressBar skillProgressBar;
    @FXML private Label skillTimeLabel;
    @FXML private Label skillRewardLabel;
    @FXML private Button skillToggleButton;

    @FXML private ComboBox<EnemyDefinition> enemyComboBox;
    @FXML private Label combatStatusLabel;
    @FXML private ProgressBar enemyHpBar;
    @FXML private Label combatProgressLabel;
    @FXML private Button combatToggleButton;

    @FXML private ListView<EquipmentSlot> equipmentListView;
    @FXML private Label objectiveLabel;
    @FXML private Label recentRewardLabel;
    @FXML private ListView<QuestDefinition> questListView;
    @FXML private Label regionDescriptionLabel;
    @FXML private ComboBox<RegionDefinition> regionComboBox;
    @FXML private ListView<ShopEntry> shopListView;

    @FXML private Button equipmentNavButton;
    @FXML private Button journalNavButton;
    @FXML private Button mapNavButton;
    @FXML private Button shopNavButton;

    private final ObservableList<InventoryStack> inventoryRows = FXCollections.observableArrayList();
    private final ObservableList<ShopEntry> shopRows = FXCollections.observableArrayList();
    private final ObservableList<QuestDefinition> questRows = FXCollections.observableArrayList();
    private final ObservableList<EquipmentSlot> equipmentRows = FXCollections.observableArrayList(EquipmentSlot.values());
    private final Map<String, ItemDefinition> itemLookup = new LinkedHashMap<>();

    private Player player;
    private GameContext context;
    private GameEngine engine;
    private GatheringService gatheringService;
    private CombatService combatService;
    private InventoryService inventoryService;
    private RegionService regionService;
    private QuestService questService;
    private ShopService shopService;
    private EquipmentService equipmentService;
    private SaveService saveService;
    private PauseTransition toastTimer;
    private Timeline skillProgressAnimation;
    private String animatedSkillId = "";
    private MainViewMode currentViewMode = MainViewMode.EVENT;
    private SideEntry selectedSideEntry;
    private InventoryStack selectedInventoryStack;

    @FXML
    private void initialize() {
        toastLabel.setVisible(false);
        toastLabel.setManaged(false);
        configureLists();
        configureComboBoxes();
        try {
            bootstrapGame();
        } catch (IOException exception) {
            throw new IllegalStateException("無法載入遊戲資料：" + exception.getMessage(), exception);
        }
    }

    @FXML
    private void onShowEquipment() {
        showMode(MainViewMode.EQUIPMENT);
    }

    @FXML
    private void onShowJournal() {
        showMode(MainViewMode.JOURNAL);
    }

    @FXML
    private void onShowMap() {
        showMode(MainViewMode.MAP);
    }

    @FXML
    private void onShowShop() {
        showMode(MainViewMode.SHOP);
    }

    @FXML
    private void onSwitchRegion() {
        RegionDefinition selectedRegion = regionComboBox.getSelectionModel().getSelectedItem();
        if (selectedRegion == null) {
            return;
        }
        if (regionService.switchRegion(player, selectedRegion.id())) {
            notifyPlayer("已前往 " + selectedRegion.name());
            refreshAll();
            saveGame("已自動存檔");
        }
    }

    @FXML
    private void onToggleAction() {
        if (gatheringService.getActiveSkill().isPresent()) {
            stopAction();
        } else {
            startAction();
        }
    }

    private void startAction() {
        SkillDefinition selectedSkill = skillComboBox.getSelectionModel().getSelectedItem();
        if (selectedSkill == null) {
            notifyPlayer("目前區域沒有可用的採集");
            return;
        }
        if (!hasRequiredItems(selectedSkill)) {
            notifyPlayer("材料不足：" + requiredItemsText(selectedSkill));
            return;
        }
        new StartActionCommand(gatheringService, selectedSkill, player).execute();
        startSkillProgressLoop(selectedSkill);
        notifyPlayer("開始：" + selectedSkill.name());
        refreshAll();
    }

    private void stopAction() {
        new StopActionCommand(gatheringService, player).execute();
        stopSkillProgressAnimation();
        notifyPlayer("已停止目前採集");
        refreshAll();
        saveGame("已自動存檔");
    }

    @FXML
    private void onToggleCombat() {
        if (combatService.getActiveEnemy().isPresent()) {
            stopCombat();
        } else {
            startCombat();
        }
    }

    private void startCombat() {
        EnemyDefinition selectedEnemy = enemyComboBox.getSelectionModel().getSelectedItem();
        if (selectedEnemy == null) {
            notifyPlayer("目前區域沒有可挑戰的敵人");
            return;
        }
        new StartCombatCommand(context, selectedEnemy).execute();
        notifyPlayer("進入戰鬥：" + selectedEnemy.name());
        refreshAll();
    }

    private void stopCombat() {
        new StopCombatCommand(combatService).execute();
        refreshAll();
        saveGame("已自動存檔");
    }

    @FXML
    private void onClaimQuest() {
        QuestDefinition quest = questListView.getSelectionModel().getSelectedItem();
        if (quest == null) {
            notifyPlayer("請先選擇任務");
            return;
        }
        if (questService.claimReward(context, quest)) {
            notifyPlayer("領取任務獎勵：" + quest.title());
            refreshAll();
            saveGame("已自動存檔");
        } else {
            notifyPlayer("任務尚未完成或已領取");
        }
    }

    @FXML
    private void onBuySelected() {
        ShopEntry entry = shopListView.getSelectionModel().getSelectedItem();
        if (entry == null) {
            notifyPlayer("請先選擇商品");
            return;
        }
        if (shopService.buy(context, entry)) {
            notifyPlayer("購買完成：" + itemLookup.get(entry.itemId()).name());
            refreshAll();
            saveGame("已自動存檔");
        } else {
            notifyPlayer("金幣不足");
        }
    }

    @FXML
    private void onEquipSelectedInventory() {
        useSelectedInventoryStack();
    }

    @FXML
    private void onDeleteSelectedInventory() {
        if (selectedInventoryStack == null) {
            notifyPlayer("請先在右側背包選擇物品");
            return;
        }
        ItemDefinition item = selectedInventoryStack.getItem();
        int quantity = selectedInventoryStack.getQuantity();
        if (inventoryService.removeItem(player, item.id(), quantity)) {
            notifyPlayer("已刪除：" + item.name() + " x" + quantity);
            selectedInventoryStack = null;
            inventoryListView.getSelectionModel().clearSelection();
            refreshAll();
            saveGame("已自動存檔");
        }
    }

    @FXML
    private void onUnequipSelected() {
        EquipmentSlot slot = equipmentListView.getSelectionModel().getSelectedItem();
        if (slot == null) {
            notifyPlayer("請先選擇裝備欄位");
            return;
        }
        equipmentService.unequip(player, slot, itemLookup);
        notifyPlayer("已卸下 " + slotLabel(slot));
        refreshAll();
        saveGame("已自動存檔");
    }

    @FXML
    private void onManualSave() {
        saveGame("已手動存檔");
    }

    public void shutdown() {
        saveGame("關閉前已存檔");
        if (engine != null) {
            engine.stop();
        }
    }

    private void bootstrapGame() throws IOException {
        EventBus eventBus = EventBus.getInstance();
        eventBus.clearSubscribers();
        LoadedGameData data = new JsonDataLoader().loadDefaultData();

        ItemRegistry itemRegistry = new ItemRegistry();
        SkillRegistry skillRegistry = new SkillRegistry();
        EnemyRegistry enemyRegistry = new EnemyRegistry();
        RegionRegistry regionRegistry = new RegionRegistry();
        QuestRegistry questRegistry = new QuestRegistry();
        ShopRegistry shopRegistry = new ShopRegistry();
        data.items().forEach(item -> {
            itemRegistry.register(item);
            itemLookup.put(item.id(), item);
        });
        data.skills().forEach(skillRegistry::register);
        data.enemies().forEach(enemyRegistry::register);
        data.regions().forEach(regionRegistry::register);
        data.quests().forEach(questRegistry::register);
        data.shopEntries().forEach(shopRegistry::register);

        player = new Player();
        inventoryService = new InventoryService(eventBus);
        ProgressionService progressionService = new ProgressionService(eventBus);
        gatheringService = new GatheringService(new SkillFactory());
        combatService = new CombatService(new EnemyFactory(), eventBus);
        regionService = new RegionService(regionRegistry, eventBus);
        questService = new QuestService(questRegistry, eventBus);
        shopService = new ShopService(eventBus);
        equipmentService = new EquipmentService();
        saveService = new SaveService();
        context = new GameContext(
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

        restoreSaveIfPresent(itemRegistry);
        regionService.unlockEligibleRegions(player);
        subscribeToEvents(eventBus);
        engine = new GameEngine(context);
        engine.register(gatheringService);
        engine.register(combatService);
        engine.start();
        refreshAll();
        notifyPlayer("歡迎回到冒險旅程");
    }

    private void restoreSaveIfPresent(ItemRegistry itemRegistry) {
        try {
            Optional<com.idlerpg.domain.save.SaveGame> saveGame = saveService.load();
            if (saveGame.isPresent()) {
                saveService.restore(player, saveGame.get(), itemRegistry);
                player.setActiveSkillId("");
                equipmentService.recalculateBonuses(player, itemLookup);
                saveStatusLabel.setText("已載入存檔");
            } else {
                saveStatusLabel.setText("新遊戲");
            }
        } catch (IOException | RuntimeException exception) {
            saveStatusLabel.setText("新遊戲");
            addReward("舊存檔無法讀取，已建立新旅程");
        }
    }

    private void configureLists() {
        sideEntryBox.setFocusTraversable(false);

        inventoryListView.setItems(inventoryRows);
        inventoryListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(InventoryStack stack, boolean empty) {
                super.updateItem(stack, empty);
                if (empty || stack == null) {
                    setText(null);
                    return;
                }
                ItemDefinition item = stack.getItem();
                setText(item.name() + " x" + stack.getQuantity()
                        + "\n" + rarityLabel(item) + " / " + itemTypeLabel(item));
            }
        });
        inventoryListView.getSelectionModel().selectedItemProperty().addListener((observable, oldStack, newStack) -> {
            selectedInventoryStack = newStack;
            refreshItemDetail();
        });

        questListView.setItems(questRows);
        questListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(QuestDefinition quest, boolean empty) {
                super.updateItem(quest, empty);
                if (empty || quest == null) {
                    setText(null);
                    return;
                }
                int progress = questService == null ? 0 : questService.getProgress(player, quest);
                String state = questService != null && questService.isClaimed(player, quest)
                        ? "已領取"
                        : questService != null && questService.isCompleted(player, quest) ? "可領取" : "進行中";
                setText(quest.title() + "  [" + state + "]  " + progress + "/" + quest.requiredCount()
                        + "\n" + quest.description());
            }
        });

        equipmentListView.setItems(equipmentRows);
        equipmentListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(EquipmentSlot slot, boolean empty) {
                super.updateItem(slot, empty);
                if (empty || slot == null) {
                    setText(null);
                    return;
                }
                String itemId = player == null ? null : player.getEquipment().get(slot);
                ItemDefinition item = itemId == null ? null : itemLookup.get(itemId);
                setText(slotLabel(slot) + "： " + (item == null ? "未裝備" : item.name()));
            }
        });

        shopListView.setItems(shopRows);
        shopListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(ShopEntry entry, boolean empty) {
                super.updateItem(entry, empty);
                if (empty || entry == null) {
                    setText(null);
                    return;
                }
                ItemDefinition item = itemLookup.get(entry.itemId());
                String itemName = item == null ? entry.itemId() : item.name();
                String itemMeta = item == null ? "" : rarityLabel(item) + " / " + itemTypeLabel(item);
                setText(itemName + "  " + entry.price() + " G\n" + itemMeta);
            }
        });
    }

    private void configureComboBoxes() {
        inventoryFilterComboBox.setItems(FXCollections.observableArrayList(InventoryFilter.values()));
        inventoryFilterComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(InventoryFilter filter) {
                return filter == null ? "" : filter.label();
            }

            @Override
            public InventoryFilter fromString(String string) {
                return null;
            }
        });
        inventoryFilterComboBox.getSelectionModel().select(InventoryFilter.ALL);
        inventoryFilterComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldFilter, newFilter) -> {
            if (player != null && inventoryService != null) {
                refreshInventory();
            }
        });

        regionComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(RegionDefinition region) {
                return region == null ? "" : region.icon() + " " + region.name();
            }

            @Override
            public RegionDefinition fromString(String string) {
                return null;
            }
        });
        skillComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(SkillDefinition skill) {
                return skill == null ? "" : skill.name();
            }

            @Override
            public SkillDefinition fromString(String string) {
                return null;
            }
        });
        enemyComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(EnemyDefinition enemy) {
                return enemy == null ? "" : enemy.name();
            }

            @Override
            public EnemyDefinition fromString(String string) {
                return null;
            }
        });
    }

    private void subscribeToEvents(EventBus eventBus) {
        eventBus.subscribe(GameEvent.class, event -> Platform.runLater(() -> {
            surfaceEvent(event);
            refreshAll();
            if (event instanceof GameTickEvent tickEvent && tickEvent.tick() % 10 == 0) {
                saveGame("已自動存檔");
            }
        }));
    }

    private void surfaceEvent(GameEvent event) {
        if (event instanceof GameTickEvent) {
            return;
        }
        if (event instanceof SkillProgressEvent skillProgressEvent && !skillProgressEvent.complete()) {
            return;
        }
        if (event instanceof CombatEvent combatEvent) {
            if (combatEvent.type() == CombatEvent.Type.HIT) {
                return;
            }
            if (combatEvent.type() == CombatEvent.Type.VICTORY) {
                addReward(combatEvent.message());
                saveGame("已自動存檔");
                return;
            }
        }
        if (event instanceof ItemAddedEvent itemAddedEvent) {
            addReward("獲得 " + itemAddedEvent.item().name() + " x" + itemAddedEvent.quantity());
            return;
        }
        if (event instanceof QuestEvent questEvent
                && (questEvent.type() == QuestEvent.Type.COMPLETED || questEvent.type() == QuestEvent.Type.CLAIMED)) {
            addReward(questEvent.message());
            return;
        }
        if (event instanceof RegionUnlockedEvent || event instanceof ShopPurchaseEvent || event instanceof LevelUpEvent) {
            addReward(event.message());
            return;
        }
        notifyPlayer(event.message());
    }

    private void refreshAll() {
        if (player == null || context == null) {
            return;
        }
        refreshPlayer();
        refreshRegion();
        refreshSideEntries();
        refreshSkillOptions();
        refreshCombatState();
        refreshInventory();
        refreshShop();
        refreshQuests();
        equipmentListView.refresh();
        refreshMainView();
    }

    private void refreshPlayer() {
        levelLabel.setText("Lv. " + player.getLevel());
        expLabel.setText(player.getExperience() + " / " + player.getExperienceToNextLevel());
        goldLabel.setText(player.getGold() + " G");
        hpLabel.setText(player.getCurrentHp() + " / " + player.getMaxHp());
        attackLabel.setText(Integer.toString(player.getAttackPower()));
        defenseLabel.setText(Integer.toString(player.getDefense()));
        expBar.setProgress(progressRatio(player.getExperience(), player.getExperienceToNextLevel()));
        playerHpBar.setProgress(progressRatio(player.getCurrentHp(), player.getMaxHp()));
    }

    private double progressRatio(int current, int maximum) {
        if (maximum <= 0) {
            return 0.0;
        }
        return Math.max(0.0, Math.min(1.0, (double) current / maximum));
    }

    private void refreshRegion() {
        RegionDefinition region = regionService.getCurrentRegion(player);
        regionTitleLabel.setText(region.icon() + " " + region.name());
        regionDescriptionLabel.setText(region.description());
        objectiveLabel.setText(nextObjectiveText(region));
        String selectedRegionId = regionComboBox.getSelectionModel().getSelectedItem() == null
                ? ""
                : regionComboBox.getSelectionModel().getSelectedItem().id();
        var unlockedRegions = regionService.getUnlockedRegions(player);
        regionComboBox.setItems(FXCollections.observableArrayList(unlockedRegions));
        unlockedRegions.stream()
                .filter(unlockedRegion -> unlockedRegion.id().equals(selectedRegionId))
                .findFirst()
                .ifPresentOrElse(
                        selectedRegion -> regionComboBox.getSelectionModel().select(selectedRegion),
                        () -> regionComboBox.getSelectionModel().select(region)
                );
    }

    private void refreshSideEntries() {
        String selectedId = selectedSideEntry == null ? "mining" : selectedSideEntry.id();
        List<SideEntry> entries = new ArrayList<>();
        entries.add(new SideEntry("attack", "⚔", "Attack", SideEntryKind.ATTACK, null));
        entries.add(new SideEntry("health", "❤", "Health", SideEntryKind.HEALTH, null));
        entries.add(new SideEntry("defence", "▣", "Defence", SideEntryKind.DEFENSE, null));

        Set<ActionType> availableActionTypes = currentRegionActionTypes();
        for (ActionType actionType : ActionType.values()) {
            if (availableActionTypes.contains(actionType)) {
                entries.add(new SideEntry(
                        actionType.name().toLowerCase(),
                        actionTypeIcon(actionType),
                        actionTypeTitle(actionType),
                        SideEntryKind.SKILL,
                        actionType
                ));
            }
        }

        selectedSideEntry = entries.stream()
                .filter(entry -> entry.id().equals(selectedId))
                .findFirst()
                .orElseGet(() -> entries.stream()
                        .filter(entry -> entry.kind() == SideEntryKind.SKILL)
                        .findFirst()
                        .orElse(entries.get(0)));
        renderSideEntries(entries);
    }

    private void renderSideEntries(List<SideEntry> entries) {
        if (sideEntryBox.getChildren().size() != entries.size()) {
            sideEntryBox.getChildren().clear();
            for (int index = 0; index < entries.size(); index++) {
                Label label = new Label();
                label.setMaxWidth(Double.MAX_VALUE);
                label.setFocusTraversable(false);
                label.getStyleClass().add("side-entry-card");
                sideEntryBox.getChildren().add(label);
            }
        }
        for (int index = 0; index < entries.size(); index++) {
            SideEntry entry = entries.get(index);
            Label label = (Label) sideEntryBox.getChildren().get(index);
            label.setText(sideEntryText(entry));
            label.getStyleClass().removeAll("side-entry-alt", "side-entry-selected");
            if (index % 2 == 1) {
                label.getStyleClass().add("side-entry-alt");
            }
            if (entry.equals(selectedSideEntry)) {
                label.getStyleClass().add("side-entry-selected");
            }
            label.setOnMouseClicked(event -> selectSideEntry(entry));
        }
    }

    private void selectSideEntry(SideEntry entry) {
        selectedSideEntry = entry;
        if (opensCombatView(entry)) {
            currentViewMode = MainViewMode.COMBAT;
        } else {
            currentViewMode = MainViewMode.EVENT;
            refreshSkillOptions();
        }
        refreshSideEntries();
        refreshMainView();
    }

    private Set<ActionType> currentRegionActionTypes() {
        if (context == null || regionService == null || player == null) {
            return EnumSet.noneOf(ActionType.class);
        }
        Set<ActionType> actionTypes = EnumSet.noneOf(ActionType.class);
        RegionDefinition region = regionService.getCurrentRegion(player);
        for (String skillId : region.skillIds()) {
            context.getSkillRegistry().get(skillId)
                    .map(SkillDefinition::actionType)
                    .ifPresent(actionTypes::add);
        }
        return actionTypes;
    }

    private void refreshSkillOptions() {
        if (context == null || regionService == null || skillComboBox == null) {
            return;
        }
        String selectedSkillId = skillComboBox.getSelectionModel().getSelectedItem() == null
                ? ""
                : skillComboBox.getSelectionModel().getSelectedItem().id();
        List<SkillDefinition> skills = currentSkillOptions();
        skillComboBox.setItems(FXCollections.observableArrayList(skills));
        Optional<SkillDefinition> activeSkill = gatheringService == null
                ? Optional.empty()
                : gatheringService.getActiveSkill();
        SkillDefinition target = activeSkill
                .filter(skills::contains)
                .orElseGet(() -> skills.stream()
                        .filter(skill -> skill.id().equals(selectedSkillId))
                        .findFirst()
                        .orElse(skills.isEmpty() ? null : skills.getFirst()));
        if (target != null) {
            skillComboBox.getSelectionModel().select(target);
        } else {
            skillComboBox.getSelectionModel().clearSelection();
        }
    }

    private List<SkillDefinition> currentSkillOptions() {
        RegionDefinition region = regionService.getCurrentRegion(player);
        return region.skillIds().stream()
                .map(id -> context.getSkillRegistry().getRequired(id))
                .filter(skill -> selectedSideEntry == null
                        || selectedSideEntry.kind() != SideEntryKind.SKILL
                        || selectedSideEntry.actionType() == null
                        || skill.actionType() == selectedSideEntry.actionType())
                .toList();
    }

    private void refreshCombatState() {
        RegionDefinition region = regionService.getCurrentRegion(player);
        String selectedEnemyId = enemyComboBox.getSelectionModel().getSelectedItem() == null
                ? ""
                : enemyComboBox.getSelectionModel().getSelectedItem().id();
        List<EnemyDefinition> enemies = region.enemyIds().stream()
                .map(id -> context.getEnemyRegistry().getRequired(id))
                .toList();
        enemyComboBox.setItems(FXCollections.observableArrayList(enemies));
        enemies.stream()
                .filter(enemy -> enemy.id().equals(selectedEnemyId))
                .findFirst()
                .ifPresentOrElse(
                        enemy -> enemyComboBox.getSelectionModel().select(enemy),
                        () -> {
                            if (!enemies.isEmpty()) {
                                enemyComboBox.getSelectionModel().selectFirst();
                            }
                        }
                );

        Optional<EnemyInstance> activeEnemy = combatService.getActiveEnemy();
        if (activeEnemy.isPresent()) {
            EnemyInstance enemy = activeEnemy.get();
            combatStatusLabel.setText(enemy.getDefinition().name() + " HP "
                    + enemy.getCurrentHp() + " / " + enemy.getDefinition().maxHp());
            enemyHpBar.setProgress((double) enemy.getCurrentHp() / enemy.getDefinition().maxHp());
            combatProgressLabel.setText("生命條：" + enemy.getCurrentHp() + " / " + enemy.getDefinition().maxHp());
            combatToggleButton.setText("Ⅱ");
            combatToggleButton.setDisable(false);
        } else {
            combatStatusLabel.setText("目前沒有戰鬥");
            enemyHpBar.setProgress(0);
            EnemyDefinition selectedEnemy = enemyComboBox.getSelectionModel().getSelectedItem();
            combatProgressLabel.setText(selectedEnemy == null
                    ? "生命條：目前無敵人"
                    : "生命條：0 / " + selectedEnemy.maxHp());
            combatToggleButton.setText("⚔");
            combatToggleButton.setDisable(selectedEnemy == null);
        }
    }

    private void refreshInventory() {
        String selectedItemId = selectedInventoryStack == null ? "" : selectedInventoryStack.getItem().id();
        ItemType selectedType = selectedInventoryFilterType();
        inventoryRows.setAll(inventoryService.getInventory(player).stream()
                .filter(stack -> selectedType == null || stack.getItem().type() == selectedType)
                .sorted(Comparator
                        .comparingInt((InventoryStack stack) -> stack.getItem().type().ordinal())
                        .thenComparing(stack -> stack.getItem().name()))
                .toList());
        InventoryStack stackToSelect = inventoryRows.stream()
                .filter(stack -> stack.getItem().id().equals(selectedItemId))
                .findFirst()
                .orElse(inventoryRows.isEmpty() ? null : inventoryRows.getFirst());
        if (stackToSelect == null) {
            inventoryListView.getSelectionModel().clearSelection();
            selectedInventoryStack = null;
        } else {
            inventoryListView.getSelectionModel().select(stackToSelect);
            selectedInventoryStack = stackToSelect;
        }
        refreshItemDetail();
    }

    private ItemType selectedInventoryFilterType() {
        InventoryFilter filter = inventoryFilterComboBox.getSelectionModel().getSelectedItem();
        return filter == null ? null : filter.itemType();
    }

    private void refreshShop() {
        ShopEntry selectedEntry = shopListView.getSelectionModel().getSelectedItem();
        String selectedEntryId = selectedEntry == null ? "" : selectedEntry.id();
        shopRows.setAll(shopService.getAvailableEntries(context));
        shopRows.stream()
                .filter(entry -> entry.id().equals(selectedEntryId))
                .findFirst()
                .ifPresentOrElse(
                        entry -> shopListView.getSelectionModel().select(entry),
                        () -> {
                            if (!shopRows.isEmpty()) {
                                shopListView.getSelectionModel().selectFirst();
                            }
                        }
                );
    }

    private void refreshQuests() {
        RegionDefinition region = regionService.getCurrentRegion(player);
        questRows.setAll(region.questIds().stream()
                .map(id -> context.getQuestRegistry().getRequired(id))
                .toList());
        questListView.refresh();
    }

    private void refreshMainView() {
        setVisibleManaged(eventView, currentViewMode == MainViewMode.EVENT);
        setVisibleManaged(combatView, currentViewMode == MainViewMode.COMBAT);
        setVisibleManaged(equipmentView, currentViewMode == MainViewMode.EQUIPMENT);
        setVisibleManaged(journalView, currentViewMode == MainViewMode.JOURNAL);
        setVisibleManaged(mapView, currentViewMode == MainViewMode.MAP);
        setVisibleManaged(shopView, currentViewMode == MainViewMode.SHOP);
        setNavActive(equipmentNavButton, currentViewMode == MainViewMode.EQUIPMENT);
        setNavActive(journalNavButton, currentViewMode == MainViewMode.JOURNAL);
        setNavActive(mapNavButton, currentViewMode == MainViewMode.MAP);
        setNavActive(shopNavButton, currentViewMode == MainViewMode.SHOP);
        refreshEventView();
        refreshItemDetail();
    }

    private void refreshEventView() {
        if (selectedSideEntry == null) {
            selectedSideEntry = new SideEntry("attack", "⚔", "Attack", SideEntryKind.ATTACK, null);
        }
        eventTitleLabel.setText(selectedSideEntry.title());
        eventHeroLabel.setText(selectedSideEntry.icon());
        switch (selectedSideEntry.kind()) {
            case ATTACK -> showStatEvent(
                    "攻擊力 " + player.getAttackPower(),
                    "基礎攻擊會隨玩家等級提升，裝備武器與工具可以再增加攻擊。",
                    "目前等級：Lv. " + player.getLevel(),
                    equipmentBonusText("攻擊", EquipmentSlot.WEAPON, EquipmentSlot.TOOL)
            );
            case HEALTH -> showStatEvent(
                    "體力 " + player.getCurrentHp() + " / " + player.getMaxHp(),
                    "體力歸零時戰鬥會停止。升級與特定裝備會提高最大體力。",
                    "玩家經驗：" + player.getExperience() + " / " + player.getExperienceToNextLevel(),
                    "裝備體力加成：" + equipmentHpBonus()
            );
            case DEFENSE -> showStatEvent(
                    "防禦 " + player.getDefense(),
                    "防禦主要來自護甲與飾品，可降低戰鬥中受到的壓力。",
                    "目前裝備欄：" + player.getEquipment().size() + " / " + EquipmentSlot.values().length,
                    equipmentBonusText("防禦", EquipmentSlot.ARMOR, EquipmentSlot.TRINKET)
            );
            case SKILL -> showSkillEvent();
        }
    }

    private void showStatEvent(String subtitle, String description, String stat, String bonus) {
        eventSubtitleLabel.setText(subtitle);
        eventDescriptionLabel.setText(description);
        eventStatLabel.setText(stat);
        eventBonusLabel.setText(bonus);
        setVisibleManaged(eventSkillControls, false);
    }

    private void showSkillEvent() {
        setVisibleManaged(eventSkillControls, true);
        refreshSkillPanel();
    }

    private void refreshSkillPanel() {
        SkillDefinition selectedSkill = skillComboBox.getSelectionModel().getSelectedItem();
        String title = actionTypeLabel(selectedSideEntry.actionType());
        eventTitleLabel.setText(title);
        eventSubtitleLabel.setText(selectedSkill == null ? "目前區域沒有此類事件" : selectedSkill.name());
        RegionDefinition region = regionService.getCurrentRegion(player);
        eventDescriptionLabel.setText(region.name() + "：" + region.description());

        if (selectedSideEntry.actionType() != null) {
            eventStatLabel.setText("技能 Lv." + player.getSkillLevel(selectedSideEntry.actionType())
                    + "  EXP " + player.getSkillExperience(selectedSideEntry.actionType())
                    + "/" + player.getSkillExperienceToNextLevel(selectedSideEntry.actionType()));
        } else {
            eventStatLabel.setText("選擇此區域可用的採集事件。");
        }
        eventBonusLabel.setText("目前區域：" + region.icon() + " " + region.name());

        Optional<SkillDefinition> activeSkill = gatheringService.getActiveSkill();
        activeSkillLabel.setText(activeSkill.map(skill -> "進行中：" + skill.name()).orElse("休息中"));
        if (activeSkill.isPresent()) {
            startSkillProgressLoop(activeSkill.get());
            skillTimeLabel.setText("時間條：約 " + activeSkill.get().durationTicks() + " 秒 / 循環中");
            skillToggleButton.setText("Ⅱ");
            skillToggleButton.setDisable(false);
        } else {
            stopSkillProgressAnimation();
            skillProgressBar.setProgress(0);
            skillTimeLabel.setText(selectedSkill == null
                    ? "時間條：目前無事件"
                    : "時間條：約 " + selectedSkill.durationTicks() + " 秒 / 循環");
            skillToggleButton.setText("▶");
            skillToggleButton.setDisable(selectedSkill == null || !hasRequiredItems(selectedSkill));
        }

        if (selectedSkill == null) {
            skillRewardLabel.setText("目前區域無採集獎勵");
        } else {
            ItemDefinition reward = itemLookup.get(selectedSkill.rewardItemId());
            skillRewardLabel.setText(skillRewardText(selectedSkill, reward));
        }
    }

    private String skillRewardText(SkillDefinition skill, ItemDefinition reward) {
        String rewardText = "獎勵預覽：" + reward.name()
                + " x" + skill.rewardQuantity() + " / EXP +" + skill.expReward();
        if (skill.consumeItemId().isBlank() || skill.consumeQuantity() <= 0) {
            return rewardText;
        }
        return rewardText + " / 消耗：" + requiredItemsText(skill);
    }

    private boolean hasRequiredItems(SkillDefinition skill) {
        return skill.consumeItemId().isBlank()
                || skill.consumeQuantity() <= 0
                || player.getInventory().getQuantity(skill.consumeItemId()) >= skill.consumeQuantity();
    }

    private String requiredItemsText(SkillDefinition skill) {
        ItemDefinition consumedItem = itemLookup.get(skill.consumeItemId());
        String consumedName = consumedItem == null ? skill.consumeItemId() : consumedItem.name();
        int available = player.getInventory().getQuantity(skill.consumeItemId());
        return consumedName + " x" + skill.consumeQuantity() + "（持有 " + available + "）";
    }

    private void refreshItemDetail() {
        if (selectedInventoryStack == null) {
            itemDetailNameLabel.setText("未選取物品");
            itemDetailMetaLabel.setText("");
            itemDetailDescriptionLabel.setText("點選背包中的物品查看詳細資訊。");
            itemDetailStatsLabel.setText("");
            inventoryEquipButton.setText("裝備選中物品");
            inventoryEquipButton.setDisable(true);
            inventoryDeleteButton.setDisable(true);
            return;
        }
        ItemDefinition item = selectedInventoryStack.getItem();
        itemDetailNameLabel.setText(item.name() + " x" + selectedInventoryStack.getQuantity());
        itemDetailMetaLabel.setText(rarityLabel(item) + " / " + itemTypeLabel(item)
                + " / 總值 " + selectedInventoryStack.getTotalValue() + " G");
        itemDetailDescriptionLabel.setText(item.description().isBlank() ? "沒有額外描述。" : item.description());
        if (item.isEquipment()) {
            itemDetailStatsLabel.setText(slotLabel(item.slot())
                    + "  攻擊 +" + item.attackBonus()
                    + " / 防禦 +" + item.defenseBonus()
                    + " / 體力 +" + item.hpBonus());
            inventoryEquipButton.setText("裝備選中物品");
            inventoryEquipButton.setDisable(false);
        } else if (item.isFood()) {
            itemDetailStatsLabel.setText("食用後回復 HP +" + item.healAmount());
            inventoryEquipButton.setText("食用選中食品");
            inventoryEquipButton.setDisable(player.getCurrentHp() >= player.getMaxHp());
        } else {
            itemDetailStatsLabel.setText("此物品目前不可裝備。");
            inventoryEquipButton.setText("裝備選中物品");
            inventoryEquipButton.setDisable(true);
        }
        inventoryDeleteButton.setDisable(false);
    }

    private void useSelectedInventoryStack() {
        if (selectedInventoryStack == null) {
            notifyPlayer("請先在右側背包選擇物品");
            return;
        }
        ItemDefinition item = selectedInventoryStack.getItem();
        if (item.isFood()) {
            eatSelectedFood(item);
            return;
        }
        if (equipmentService.equip(player, item, itemLookup)) {
            notifyPlayer("已裝備：" + item.name());
            selectedInventoryStack = null;
            inventoryListView.getSelectionModel().clearSelection();
            refreshAll();
            saveGame("已自動存檔");
        } else {
            notifyPlayer("這個道具無法裝備");
        }
    }

    private void eatSelectedFood(ItemDefinition item) {
        if (player.getCurrentHp() >= player.getMaxHp()) {
            notifyPlayer("目前 HP 已滿");
            return;
        }
        if (!inventoryService.removeItem(player, item.id(), 1)) {
            notifyPlayer("背包中沒有可食用的 " + item.name());
            return;
        }
        int beforeHp = player.getCurrentHp();
        player.heal(item.healAmount());
        int recovered = player.getCurrentHp() - beforeHp;
        notifyPlayer("食用：" + item.name() + "，HP +" + recovered);
        refreshAll();
        saveGame("已自動存檔");
    }

    private void showMode(MainViewMode mode) {
        currentViewMode = mode;
        refreshMainView();
    }

    private void setVisibleManaged(Node node, boolean visible) {
        node.setVisible(visible);
        node.setManaged(visible);
    }

    private void setNavActive(Button button, boolean active) {
        if (button == null) {
            return;
        }
        if (active) {
            if (!button.getStyleClass().contains("nav-button-active")) {
                button.getStyleClass().add("nav-button-active");
            }
        } else {
            button.getStyleClass().remove("nav-button-active");
        }
    }

    private void startSkillProgressLoop(SkillDefinition skill) {
        if (skill.id().equals(animatedSkillId)
                && skillProgressAnimation != null
                && skillProgressAnimation.getStatus() == Animation.Status.RUNNING) {
            return;
        }
        stopSkillProgressAnimation();
        animatedSkillId = skill.id();
        skillProgressBar.setProgress(0);
        skillProgressAnimation = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(skillProgressBar.progressProperty(), 0, Interpolator.LINEAR)),
                new KeyFrame(Duration.seconds(skill.durationTicks()), new KeyValue(skillProgressBar.progressProperty(), 1, Interpolator.LINEAR))
        );
        skillProgressAnimation.setCycleCount(Animation.INDEFINITE);
        skillProgressAnimation.playFromStart();
    }

    private void stopSkillProgressAnimation() {
        if (skillProgressAnimation != null) {
            skillProgressAnimation.stop();
            skillProgressAnimation = null;
        }
        animatedSkillId = "";
    }

    private String sideEntryText(SideEntry entry) {
        if (player == null) {
            return entry.icon() + " " + entry.title();
        }
        return switch (entry.kind()) {
            case ATTACK -> entry.icon() + " " + entry.title() + "  " + player.getAttackPower() + "\n攻擊力";
            case HEALTH -> entry.icon() + " " + entry.title() + "  " + player.getCurrentHp()
                    + "/" + player.getMaxHp() + "\n生命條";
            case DEFENSE -> entry.icon() + " " + entry.title() + "  " + player.getDefense() + "\n防禦力";
            case SKILL -> entry.icon() + " " + entry.title() + "  Lv." + player.getSkillLevel(entry.actionType())
                    + "\n" + player.getSkillExperience(entry.actionType())
                    + "/" + player.getSkillExperienceToNextLevel(entry.actionType());
        };
    }

    private boolean opensCombatView(SideEntry entry) {
        return entry.kind() == SideEntryKind.ATTACK
                || entry.kind() == SideEntryKind.HEALTH
                || entry.kind() == SideEntryKind.DEFENSE;
    }

    private String nextObjectiveText(RegionDefinition region) {
        return region.questIds().stream()
                .map(id -> context.getQuestRegistry().getRequired(id))
                .filter(quest -> !questService.isClaimed(player, quest))
                .findFirst()
                .map(quest -> {
                    int progress = questService.getProgress(player, quest);
                    String state = questService.isCompleted(player, quest) ? "可領取" : "進行中";
                    return state + "：" + quest.title() + " " + progress + "/" + quest.requiredCount();
                })
                .orElse("此區域任務已完成，前往新區域繼續冒險");
    }

    private void addReward(String message) {
        recentRewardLabel.setText(message);
        notifyPlayer(message);
    }

    private void notifyPlayer(String message) {
        toastLabel.setVisible(true);
        toastLabel.setManaged(true);
        toastLabel.setText(message);
        if (toastTimer != null) {
            toastTimer.stop();
        }
        toastTimer = new PauseTransition(Duration.seconds(4));
        toastTimer.setOnFinished(event -> {
            toastLabel.setText("");
            toastLabel.setVisible(false);
            toastLabel.setManaged(false);
        });
        toastTimer.playFromStart();
    }

    private void saveGame(String status) {
        if (saveService == null || player == null) {
            return;
        }
        try {
            saveService.save(player);
            saveStatusLabel.setText(status);
        } catch (IOException exception) {
            saveStatusLabel.setText("存檔失敗");
        }
    }

    private String equipmentBonusText(String label, EquipmentSlot firstSlot, EquipmentSlot secondSlot) {
        return label + "來源："
                + slotItemName(firstSlot) + " / "
                + slotItemName(secondSlot);
    }

    private String slotItemName(EquipmentSlot slot) {
        String itemId = player.getEquipment().get(slot);
        ItemDefinition item = itemId == null ? null : itemLookup.get(itemId);
        return slotLabel(slot) + "=" + (item == null ? "未裝備" : item.name());
    }

    private int equipmentHpBonus() {
        return player.getEquipment().values().stream()
                .map(itemLookup::get)
                .filter(item -> item != null)
                .mapToInt(ItemDefinition::hpBonus)
                .sum();
    }

    private String actionTypeLabel(ActionType actionType) {
        if (actionType == null) {
            return "採集";
        }
        return switch (actionType) {
            case MINING -> "採礦";
            case FISHING -> "釣魚";
            case COOKING -> "烹飪";
        };
    }

    private String actionTypeTitle(ActionType actionType) {
        return switch (actionType) {
            case MINING -> "Mining";
            case FISHING -> "Fishing";
            case COOKING -> "Cooking";
        };
    }

    private String actionTypeIcon(ActionType actionType) {
        return switch (actionType) {
            case MINING -> "⛏";
            case FISHING -> "≈";
            case COOKING -> "♨";
        };
    }

    private String itemTypeLabel(ItemDefinition item) {
        return switch (item.type()) {
            case RESOURCE -> "資源";
            case CONSUMABLE -> "食品";
            case EQUIPMENT -> "裝備";
            case QUEST -> "任務";
        };
    }

    private String rarityLabel(ItemDefinition item) {
        return switch (item.rarity()) {
            case COMMON -> "普通";
            case UNCOMMON -> "精良";
            case RARE -> "稀有";
            case EPIC -> "史詩";
        };
    }

    private String slotLabel(EquipmentSlot slot) {
        return switch (slot) {
            case WEAPON -> "武器";
            case ARMOR -> "護甲";
            case TOOL -> "工具";
            case TRINKET -> "飾品";
        };
    }

    private enum MainViewMode {
        EVENT,
        COMBAT,
        EQUIPMENT,
        JOURNAL,
        MAP,
        SHOP
    }

    private enum InventoryFilter {
        ALL(null, "全部"),
        RESOURCE(ItemType.RESOURCE, "資源"),
        CONSUMABLE(ItemType.CONSUMABLE, "食品"),
        EQUIPMENT(ItemType.EQUIPMENT, "裝備");

        private final ItemType itemType;
        private final String label;

        InventoryFilter(ItemType itemType, String label) {
            this.itemType = itemType;
            this.label = label;
        }

        private ItemType itemType() {
            return itemType;
        }

        private String label() {
            return label;
        }
    }

    private enum SideEntryKind {
        ATTACK,
        HEALTH,
        DEFENSE,
        SKILL
    }

    private record SideEntry(
            String id,
            String icon,
            String title,
            SideEntryKind kind,
            ActionType actionType
    ) {
    }
}
