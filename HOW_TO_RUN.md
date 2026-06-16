# Idle RPG Framework 操作說明

本文件說明如何在 IntelliJ IDEA 中開啟、執行與測試 `final_prj` 專案。

## 1. 專案位置

主要 Java 專案位於：

```text
final_prj/
```

重要檔案：

```text
final_prj/pom.xml
final_prj/src/main/java/com/idlerpg/Main.java
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

建議使用 IntelliJ IDEA 執行，因為目前專案已設定為 Maven + JavaFX 專案。

步驟：

1. 開啟 IntelliJ IDEA。
2. 選擇 `Open`。
3. 選擇：

```text
/Users/ian/Documents/swfw/final/final_prj
```

4. IntelliJ 偵測到 `pom.xml` 後，選擇以 Maven project 匯入。
5. 等待 Maven dependencies 載入完成。
6. 開啟：

```text
src/main/java/com/idlerpg/Launcher.java
```

7. 按下 `Run` 執行 `com.idlerpg.Launcher`。

如果 IntelliJ 沒有自動建立 Run Configuration：

1. 點選右上角 `Add Configuration...`
2. 新增 `Application`
3. Main class 設為：

```text
com.idlerpg.Launcher
```

4. Working directory 設為：

```text
/Users/ian/Documents/swfw/final/final_prj
```

5. 使用 JDK 26。

## 3. 遊戲操作方式

程式啟動後會看到深色系 JavaFX 遊戲視窗，主要區塊如下：

- 上方狀態列：顯示等級、經驗、生命、金幣、攻擊、防禦、目前區域、存檔狀態。
- 左側冒險區：切換已解鎖區域、開始採集、開始戰鬥。
- 中央分頁：任務、背包、裝備、商店。
- 右側面板：角色技能等級與最近獎勵摘要。
- 底部浮動提示：顯示短暫玩家通知，不再顯示工程用 Event Log。

採集流程：

1. 在左側 `採集活動` 下拉選單選擇技能。
2. 點擊 `開始採集`。
3. 等待進度條跑完。
4. 完成後背包會增加道具，玩家 EXP 與技能 EXP 會上升。
5. 點擊 `停止` 可停止目前採集。

戰鬥流程：

1. 在左側 `戰鬥` 下拉選單選擇敵人。
2. 點擊 `開始戰鬥`。
3. 每秒會自動進行一次戰鬥 tick。
4. 勝利後會獲得 EXP 與 Gold，相關任務也會推進。
5. 點擊 `撤退` 可停止目前戰鬥。

任務流程：

1. 在中央 `任務` 分頁查看目前區域任務。
2. 完成採集或戰鬥目標後，任務狀態會變成可領取。
3. 選擇任務並點擊 `領取選中任務獎勵`。
4. 任務可能會給 Gold、EXP、道具，或解鎖新區域。

裝備與商店：

1. 在 `商店` 分頁購買裝備或道具。
2. 在 `背包` 分頁選擇裝備，點擊 `裝備選中道具`。
3. 在 `裝備` 分頁查看目前裝備欄位，也可卸下選中欄位。

存檔與離線收益：

- 遊戲會自動定期存檔，也可以點擊右上角 `手動存檔`。
- 存檔位置預設為：

```text
~/.idle-rpg-framework/save.json
```

- 如果關閉遊戲時仍有採集活動，重新開啟時會計算最多 8 小時的離線收益。

## 4. 修改遊戲資料

本專題採資料驅動設計，新增或調整內容主要改 JSON。

新增道具：

```text
final_prj/src/main/resources/data/items.json
```

新增技能：

```text
final_prj/src/main/resources/data/skills.json
```

新增敵人：

```text
final_prj/src/main/resources/data/enemies.json
```

新增區域：

```text
final_prj/src/main/resources/data/regions.json
```

新增任務：

```text
final_prj/src/main/resources/data/quests.json
```

新增商店商品：

```text
final_prj/src/main/resources/data/shop.json
```

注意：

- `skills.json` 的 `rewardItemId` 必須對應 `items.json` 中存在的 `id`。
- `regions.json` 的 `skillIds`、`enemyIds`、`questIds` 必須對應既有資料 id。
- `shop.json` 的 `itemId` 必須對應 `items.json`。
- `durationTicks` 代表完成一次採集需要幾秒。
- 修改 JSON 後重新執行程式即可載入新資料。

## 5. 自備素材

如果之後你想放自己的角色圖、怪物圖、道具圖示或背景圖，不需要自己改核心程式。

建議先把素材放在：

```text
final_prj/src/main/resources/assets/
```

建議分類：

```text
final_prj/src/main/resources/assets/backgrounds/
final_prj/src/main/resources/assets/characters/
final_prj/src/main/resources/assets/enemies/
final_prj/src/main/resources/assets/items/
```

建議檔名使用英文與底線，例如：

```text
assets/enemies/training_slime.png
assets/items/iron_sword.png
assets/backgrounds/sunlit_meadow.png
```

你可以把素材準備好後告訴我：

```text
哪張圖對應哪個 region / enemy / item
```

我再幫你把 JSON、FXML、CSS 或 controller 接上。若只是替換現有文字符號圖示，通常只需要調整資料檔；若要大幅改版面，例如角色立繪、地圖背景、怪物卡片，就會需要我改 UI 程式。

## 6. 測試

如果 IntelliJ 已成功匯入 Maven 專案，可以在 IntelliJ 中執行：

```text
src/test/java
```

或個別執行測試類別，例如：

```text
EventBusTest
RegistryTest
JsonDataLoaderTest
InventoryServiceTest
GatheringServiceTest
CombatServiceTest
SaveServiceTest
OfflineProgressServiceTest
RegionServiceTest
QuestServiceTest
ShopServiceTest
EquipmentServiceTest
```

## 7. 需要安裝 mvn 嗎？

不一定需要。

如果你只使用 IntelliJ IDEA：

- 通常不需要另外安裝 `mvn`。
- IntelliJ 內建 Maven 支援，可以直接讀取 `pom.xml`、下載 dependencies、執行專案與測試。
- 這是目前最推薦的方式。

如果你想在 Terminal 使用指令：

```bash
mvn test
mvn javafx:run
```

那就需要安裝 Maven，因為目前系統 shell 中沒有 `mvn` 指令。

可以用以下指令確認：

```bash
mvn -version
```

如果出現：

```text
zsh: command not found: mvn
```

代表尚未安裝 Maven。

## 8. 安裝 Maven 的時機

建議安裝 Maven 的情況：

- 你想用 Terminal 跑測試。
- 你想用 Terminal 啟動 JavaFX。
- 你要交付給老師或同學用 command line 驗證。
- 你不想完全依賴 IntelliJ。

不一定要安裝 Maven 的情況：

- 你只會用 IntelliJ 開發與展示。
- IntelliJ 已經能成功匯入 `pom.xml`。
- IntelliJ 可以正常執行 `com.idlerpg.Launcher`。

## 9. 如果要安裝 Maven

macOS 常見方式是使用 Homebrew：

```bash
brew install maven
```

安裝後確認：

```bash
mvn -version
```

之後可在 `final_prj` 目錄執行：

```bash
mvn test
mvn javafx:run
```

## 10. 常見問題

### IntelliJ 顯示 JavaFX 找不到

先確認 `pom.xml` 已被 IntelliJ 以 Maven project 載入，並等待 dependencies 下載完成。

可嘗試：

```text
右側 Maven 面板 -> Reload All Maven Projects
```

### Main class 找不到

確認 Run Configuration 的 Main class 是：

```text
com.idlerpg.Launcher
```

不是舊的 default package `Main`，也不是直接執行 `com.idlerpg.Main`。

### JavaFX runtime components are missing

如果看到：

```text
Error: JavaFX runtime components are missing, and are required to run this application
```

請把 IntelliJ Run Configuration 的 Main class 改成：

```text
com.idlerpg.Launcher
```

`Main` 是 JavaFX `Application` 類別，直接執行時容易遇到 JavaFX runtime/module path 問題；`Launcher` 是一般 Java 入口，會再呼叫 `Main` 啟動 JavaFX。

### JavaFX unnamed module / native access warning

如果啟動時看到類似：

```text
WARNING: Unsupported JavaFX configuration: classes were loaded from 'unnamed module'
WARNING: Use --enable-native-access=ALL-UNNAMED to avoid a warning
```

這是 Java 26 對 JavaFX native library 載入方式的警告，不代表程式執行失敗。只要 JavaFX 視窗正常開啟，就可以繼續展示與操作。

若想減少 native access 警告，可在 IntelliJ Run Configuration 的 `VM options` 加上：

```text
--enable-native-access=ALL-UNNAMED
```

完整設定建議：

```text
Main class: com.idlerpg.Launcher
VM options: --enable-native-access=ALL-UNNAMED
Working directory: /Users/ian/Documents/swfw/final/final_prj
```

`Unsupported JavaFX configuration` 來自目前以 classpath 方式啟動 JavaFX。這個 warning 對本專題展示不影響；若要完全消除，需要把專案改成 Java module path / `module-info.java` 的模組化 JavaFX 專案。

### JSON 修改後沒有生效

重新停止並執行程式。JSON 會在程式啟動時載入。

### FXML 載入錯誤

確認資源路徑存在：

```text
src/main/resources/view/fxml/main.fxml
src/main/resources/view/css/app.css
```

## 11. 專題展示建議

展示時可以依序說明：

1. README 的專題目標是可擴充 Idle RPG Framework。
2. `data/*.json` 展示 Data-Driven Design。
3. `EventBus` 展示事件驅動與 Observer Pattern。
4. `RegionRegistry`、`QuestRegistry`、`ShopRegistry` 展示可擴充內容管理。
5. `SaveService` 與 `OfflineProgressService` 展示完整 Idle RPG 體驗。
6. `Factory`、`Strategy`、`Command` 展示設計模式。
7. JavaFX 深色 UI 展示 MVC 分層，View 不直接操作遊戲邏輯。
8. `docs/uml` 展示 UML 文件。
