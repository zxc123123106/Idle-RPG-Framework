# Idle RPG Framework 操作與擴充說明

本文件說明如何在 IntelliJ IDEA 執行 `final_prj`、操作遊戲，以及透過 JSON 新增道具、事件、敵人、地圖、任務與商店內容。

## 1. 專案位置

主要 Maven 專案：

```text
/Users/ian/Documents/swfw/final/final_prj
```

重要檔案：

```text
final_prj/pom.xml
final_prj/src/main/java/com/idlerpg/Launcher.java
final_prj/src/main/resources/view/fxml/main.fxml
final_prj/src/main/resources/view/css/app.css
final_prj/src/main/resources/data/items.json
final_prj/src/main/resources/data/skills.json
final_prj/src/main/resources/data/enemies.json
final_prj/src/main/resources/data/regions.json
final_prj/src/main/resources/data/quests.json
final_prj/src/main/resources/data/shop.json
final_prj/docs/uml/
```

## 2. 使用 IntelliJ IDEA 執行

1. 開啟 IntelliJ IDEA。
2. 選擇 `Open`。
3. 選擇 `/Users/ian/Documents/swfw/final/final_prj`。
4. IntelliJ 偵測到 `pom.xml` 後，以 Maven project 匯入。
5. 等待 Maven dependencies 載入完成。
6. 將 Project SDK 設為 JDK 26。
7. 開啟 `src/main/java/com/idlerpg/Launcher.java`。
8. 執行 `com.idlerpg.Launcher`。

若需要手動建立 Run Configuration：

```text
Type: Application
Main class: com.idlerpg.Launcher
Working directory: /Users/ian/Documents/swfw/final/final_prj
JRE: JDK 26
VM options: --enable-native-access=ALL-UNNAMED
```

請不要直接把 `com.idlerpg.Main` 當成一般 Java 程式執行，否則可能出現：

```text
JavaFX runtime components are missing
```

## 3. 是否需要安裝 Maven

只使用 IntelliJ IDEA 時通常不需要另外安裝 `mvn`。IntelliJ 可直接讀取 `pom.xml`、下載 dependencies、執行程式與測試。

若要在 Terminal 使用以下指令，才需要安裝 Maven：

```bash
mvn test
mvn javafx:run
```

macOS 可使用 Homebrew 安裝：

```bash
brew install maven
mvn -version
```

## 4. 遊戲畫面與操作

畫面分為四個主要部分：

- 上方狀態列：等級、經驗、生命、金幣、目前區域與存檔狀態。
- 左側狀態與事件列：Attack、Health、Defence，以及目前區域可用的 Mining、Fishing、Gathering、Cooking。
- 中央主畫面：一次只顯示目前選取的事件或全域功能。
- 右側背包：可篩選並選取道具，查看名稱、稀有度、用途、價值與能力。
- 底部全域導覽：戰鬥、裝備、任務、地圖、商店。

### 4.1 採集與烹飪

1. 在左側選擇 `Mining`、`Fishing`、`Gathering` 或 `Cooking`。
2. 在中央選擇要執行的事件。
3. 查看每輪時間、獎勵與材料需求。
4. 按 `▶` 開始。
5. 執行時按鈕會變成 `Ⅱ`，再次按下即可停止。
6. 時間條完成後，道具、玩家 EXP、技能 EXP 與任務進度會更新。

`Cooking` 會先消耗指定材料。材料不足時無法開始；活動執行中若材料耗盡，系統會自動停止。

### 4.2 戰鬥

1. 點擊底部 `戰鬥`，或點左側 Attack。
2. 選擇敵人。
3. 按 `▶` 開始自動戰鬥。
4. 執行中按鈕變為 `Ⅱ`，可用同一按鈕撤退。
5. 勝利後獲得 EXP、Gold 並更新戰鬥任務。
6. 戰敗時會扣除目前經驗的一部分，並回復部分 HP。

### 4.3 背包與道具

1. 使用右側下拉選單篩選全部、資源、食品或裝備。
2. 點選背包項目查看詳細資料。
3. 食品可直接食用並回復 HP。
4. 裝備可直接裝備。
5. 不需要的物品可刪除，或到商店切換出售模式換取 Gold。

### 4.4 裝備

1. 點擊底部 `裝備`。
2. 查看武器、防具、工具與飾品欄位。
3. 裝備物品可增加攻擊、防禦、最大 HP，或加快指定事件。
4. 選取裝備欄後可卸下裝備。

### 4.5 任務、地圖與商店

- 任務：完成條件後選取任務並領取獎勵。
- 地圖：已解鎖地圖可直接點擊前往；未解鎖地圖會顯示等級與任務需求。
- 商店：購買目前區域商品，或切換出售模式批量出售背包物品。

## 5. 存檔

存檔位置：

```text
~/.idle-rpg-framework/save.json
```

遊戲會：

- 每 10 個 tick 自動存檔。
- 在重要操作後自動存檔。
- 關閉視窗時存檔。
- 支援右上角手動存檔。

若想重新開始，可先關閉遊戲，再刪除：

```text
~/.idle-rpg-framework/save.json
```

## 6. 資料驅動擴充原則

多數遊戲內容可直接改 JSON，不需要修改 Controller 或 Service：

- 使用既有種類新增 item：只改 JSON。
- 使用既有 `ActionType` 新增 event：只改 JSON，並把 id 掛到地圖。
- 新增 enemy、quest、region、shop entry：只改 JSON 並維持 id 關聯。

若要新增全新的行為種類，例如 `WOODCUTTING`：

1. 修改 `ActionType` enum。
2. 新增 `WoodcuttingStrategy`。
3. 在 `SkillFactory` 加入建立規則。
4. 在 Controller 加入名稱與圖示。
5. 再用 JSON 新增實際事件。

因此，已存在 `CookingStrategy` 且 Factory、enum、UI 都已支援 `COOKING` 時，新增新的料理事件只需修改 JSON。只有「新增全新的行為類型」才需要 Java 程式。

## 7. 新增 Item

修改：

```text
final_prj/src/main/resources/data/items.json
```

### 7.1 一般資源

```json
{
  "id": "silver_ore",
  "name": "銀礦石",
  "type": "RESOURCE",
  "value": 25,
  "description": "帶有微光的銀色礦石。",
  "icon": "◆",
  "rarity": "UNCOMMON"
}
```

### 7.2 可食用道具

```json
{
  "id": "herb_soup",
  "name": "藥草湯",
  "type": "CONSUMABLE",
  "value": 35,
  "description": "食用後回復生命。",
  "icon": "♨",
  "rarity": "UNCOMMON",
  "healAmount": 45
}
```

### 7.3 裝備

```json
{
  "id": "silver_sword",
  "name": "銀劍",
  "type": "EQUIPMENT",
  "value": 240,
  "description": "比鐵劍更鋒利的武器。",
  "icon": "⚔",
  "rarity": "RARE",
  "slot": "WEAPON",
  "attackBonus": 14,
  "defenseBonus": 0,
  "hpBonus": 0
}
```

### 7.4 加速工具

```json
{
  "id": "silver_pickaxe",
  "name": "銀礦鎬",
  "type": "EQUIPMENT",
  "value": 260,
  "description": "提高採礦速度。",
  "icon": "⛏",
  "rarity": "RARE",
  "slot": "TOOL",
  "attackBonus": 0,
  "defenseBonus": 0,
  "hpBonus": 0,
  "speedActionType": "MINING",
  "speedBonusPercent": 45
}
```

可用值：

- `type`：`RESOURCE`、`CONSUMABLE`、`EQUIPMENT`、`QUEST`
- `rarity`：`COMMON`、`UNCOMMON`、`RARE`、`EPIC`
- `slot`：`WEAPON`、`ARMOR`、`TOOL`、`TRINKET`
- `speedActionType`：`MINING`、`FISHING`、`GATHERING`、`COOKING`

## 8. 新增 Event

修改：

```text
final_prj/src/main/resources/data/skills.json
```

目前支援：

```text
MINING
FISHING
GATHERING
COOKING
```

### 8.1 不消耗材料

```json
{
  "id": "mine_silver",
  "name": "開採銀礦",
  "actionType": "MINING",
  "durationTicks": 6,
  "rewardItemId": "silver_ore",
  "rewardQuantity": 1,
  "expReward": 35
}
```

### 8.2 消耗材料

```json
{
  "id": "cook_herb_soup",
  "name": "烹煮藥草湯",
  "actionType": "COOKING",
  "durationTicks": 6,
  "rewardItemId": "herb_soup",
  "rewardQuantity": 1,
  "expReward": 30,
  "consumeItemId": "shadow_herb",
  "consumeQuantity": 2,
  "regionRestricted": false
}
```

欄位：

- `durationTicks`：基礎完成秒數。
- `rewardItemId`：必須存在於 `items.json`。
- `consumeItemId`：選填，必須存在於 `items.json`。
- `regionRestricted`：`false` 代表所有地圖都可使用；預設為 `true`。

區域限定事件還要加到 `regions.json`：

```json
"skillIds": ["mine_copper", "fish_river", "mine_silver"]
```

## 9. 新增 Enemy

修改：

```text
final_prj/src/main/resources/data/enemies.json
```

```json
{
  "id": "cave_bat",
  "name": "洞穴蝙蝠",
  "maxHp": 55,
  "attack": 6,
  "defense": 2,
  "expReward": 40,
  "goldReward": 12
}
```

再把 id 加到地圖：

```json
"enemyIds": ["training_slime", "cave_bat"]
```

## 10. 新增 Region

修改：

```text
final_prj/src/main/resources/data/regions.json
```

```json
{
  "id": "silver_cave",
  "name": "銀光洞窟",
  "description": "礦壁閃著銀色光芒的洞窟。",
  "icon": "◇",
  "requiredLevel": 3,
  "requiredQuestId": "first_ore",
  "skillIds": ["mine_silver"],
  "enemyIds": ["cave_bat"],
  "shopItemIds": ["silver_sword"],
  "questIds": ["silver_supply"]
}
```

所有 id 都必須指向其他 JSON 中已存在的資料。

## 11. 新增 Quest

修改：

```text
final_prj/src/main/resources/data/quests.json
```

```json
{
  "id": "silver_supply",
  "title": "銀礦補給",
  "description": "取得 5 個銀礦石。",
  "type": "GATHER_ITEM",
  "targetId": "silver_ore",
  "requiredCount": 5,
  "rewardExp": 90,
  "rewardGold": 70,
  "rewardItemId": "silver_sword",
  "rewardQuantity": 1,
  "unlockRegionId": "silver_cave"
}
```

任務類型：

- `GATHER_ITEM`
- `DEFEAT_ENEMY`
- `OBTAIN_ITEM`
- `REACH_LEVEL`

## 12. 新增 Shop 商品

修改：

```text
final_prj/src/main/resources/data/shop.json
```

```json
{
  "id": "buy_silver_sword",
  "itemId": "silver_sword",
  "price": 220,
  "requiredRegionId": "silver_cave"
}
```

商品的 `itemId` 必須同時存在於 `items.json`，並加入對應地圖的 `shopItemIds`。

## 13. 新增完整內容的建議順序

1. 在 `items.json` 新增所有材料、獎勵與裝備。
2. 在 `skills.json` 新增事件。
3. 在 `enemies.json` 新增敵人。
4. 在 `quests.json` 新增任務。
5. 在 `shop.json` 新增商品。
6. 在 `regions.json` 新增或修改地圖關聯。
7. 驗證 JSON 語法與所有 id。
8. 重新啟動遊戲。

常見錯誤：

- id 重複。
- `rewardItemId` 或 `consumeItemId` 不存在。
- 地圖引用不存在的 skill、enemy、quest 或 item。
- JSON 最後一筆資料後多放逗號。
- 新增了全新 `ActionType`，但沒有同步 Strategy、Factory 與 UI。

## 14. 自備素材

建議放在：

```text
final_prj/src/main/resources/assets/backgrounds/
final_prj/src/main/resources/assets/characters/
final_prj/src/main/resources/assets/enemies/
final_prj/src/main/resources/assets/items/
```

目前 `icon` 使用文字或 Unicode 符號。若要改成圖片：

- 只是替換既有圖片路徑：可先在 JSON 新增路徑欄位，再由 UI 載入。
- 要加入角色立繪、怪物圖、地圖背景或物品縮圖：需要修改 FXML、CSS 與 Controller 的圖片顯示邏輯。

素材本身可以自行準備，但第一次接入圖片系統仍需要程式修改；完成通用圖片欄位後，後續同類素材即可主要透過 JSON 替換。

## 15. 測試

IntelliJ 中可執行整個：

```text
src/test/java
```

主要測試包含：

```text
EventBusTest
RegistryTest
JsonDataLoaderTest
InventoryServiceTest
GatheringServiceTest
CombatServiceTest
SaveServiceTest
RegionServiceTest
QuestServiceTest
ShopServiceTest
EquipmentServiceTest
PlayerTest
SkillSpeedCalculatorTest
```

目前驗證結果為 23 個測試通過。

## 16. 常見問題

### JavaFX runtime components are missing

執行 `com.idlerpg.Launcher`，不要直接執行 `com.idlerpg.Main`。

### Unsupported JavaFX configuration

這是 Java 26 classpath 啟動方式的警告。若畫面正常開啟，不影響專題展示。可加入：

```text
--enable-native-access=ALL-UNNAMED
```

### JSON 修改後沒有生效

JSON 只在啟動時載入，請停止後重新執行程式。

### 舊存檔造成資料錯誤

新增或刪除重要資料 id 後，舊存檔可能仍引用舊 id。開發期間可刪除：

```text
~/.idle-rpg-framework/save.json
```

再重新啟動。
