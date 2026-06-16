# Idle RPG Framework
## 基於 MVC、事件驅動與資料驅動設計之可擴充 Idle RPG Framework

---

# 一、專題簡介

本專題以《Idle Iktah》類型的 Idle RPG 遊戲作為設計參考，  
並非單純複製遊戲內容，而是著重於：

> 「建立一套具備可擴充性、低耦合、高維護性的 Idle RPG 軟體框架」

本專題將透過 Java 與物件導向設計方式，  
實作一套可支援：

- 技能系統
- 採集系統
- 道具系統
- 戰鬥系統
- UI 更新機制
- 資料驅動載入
- 事件通知系統

之遊戲框架。

本專題重點並非遊戲美術或完整遊戲內容，  
而是：

# 「軟體架構與框架設計」

---

# 二、專題目標

本專題主要目標如下：

## 1. 建立可擴充的遊戲框架

系統需支援：

- 新增技能
- 新增道具
- 新增敵人
- 新增地圖
- 新增事件

且不需修改核心程式碼。

---

## 2. 建立低耦合系統

透過：

- MVC
- Event Bus
- Service Layer
- Design Patterns

降低模組間依賴性。

---

## 3. 展示軟體設計能力

包含：

- UML Modeling
- SOLID Principles
- Design Patterns
- Refactoring
- Framework Architecture

---

## 4. 建立資料驅動系統（Data-Driven Design）

遊戲資料將透過：

```json
items.json
skills.json
enemies.json
```

進行載入，而非硬編碼於程式中。

---

# 三、開發技術

| 類別 | 技術 |
|---|---|
| 程式語言 | Java |
| GUI Framework | JavaFX |
| UI Layout | FXML |
| UI Styling | JavaFX CSS |
| 資料格式 | JSON |
| UML | PlantUML / Draw.io |
| 架構模式 | MVC |
| 設計方式 | Event-Driven Architecture |

---

# 四、系統架構

本系統採用：

# MVC + Event-Driven Architecture

---

## 4.1 MVC 架構

### Model

負責：

- 玩家資料
- 技能資料
- 道具資料
- 遊戲邏輯
- 戰鬥邏輯

---

### View

負責：

- UI 顯示
- JavaFX 畫面渲染
- 使用者互動

View 不直接操作遊戲邏輯。

---

### Controller

負責：

- 接收玩家操作
- 呼叫 Service Layer
- 派發事件

---

# 五、前後端分離設計

本專題採用：

# 「前後端邏輯分離」

概念。

---

## 前端（View）

僅負責：

- 顯示資料
- UI 更新
- 使用者輸入

不包含：

- 遊戲邏輯
- 傷害計算
- 資料修改

---

## 後端（Core / Service）

負責：

- 遊戲計算
- 資料管理
- 遊戲狀態
- 事件系統
- 戰鬥邏輯

---

# 六、核心架構設計

---

## 6.1 Event Bus System

系統核心採用事件驅動設計。

例如：

```text
採礦完成
→ 發送 ItemAddedEvent
→ 更新 Inventory
→ 更新 UI
```

---

### 優點

- 降低模組耦合
- 提高擴充性
- UI 可自動更新
- 易於維護

---

## 6.2 Registry System

系統採用 Registry 管理遊戲內容。

例如：

```text
ItemRegistry
SkillRegistry
EnemyRegistry
```

新增內容時只需：

```java
registry.register(...)
```

不需修改核心程式。

---

## 6.3 Factory System

系統採用 Factory Pattern 建立遊戲物件。

例如：

```text
ItemFactory
SkillFactory
EnemyFactory
```

可避免大量硬編碼與重複 new。

---

## 6.4 Tick-Based Engine

系統將採用 Tick 更新機制。

例如：

```text
每秒更新：
- 採礦進度
- 戰鬥進度
- 玩家狀態
- Buff 狀態
```

---

# 七、使用之設計模式

---

## 7.1 Factory Pattern

用途：

- 建立道具
- 建立技能
- 建立敵人

---

## 7.2 Strategy Pattern

用途：

- 不同行為邏輯

例如：

```text
MiningStrategy
FishingStrategy
CombatStrategy
```

---

## 7.3 Observer Pattern

用途：

- UI 更新
- Event 通知

---

## 7.4 Singleton Pattern

用途：

- GameContext
- EventBus

---

## 7.5 Command Pattern

用途：

- 玩家操作封裝

例如：

```text
EquipItemCommand
MineCommand
SellItemCommand
```

---

# 八、SOLID 設計原則

---

## S — Single Responsibility Principle

每個類別僅負責單一功能。

例如：

```text
InventoryService
```

僅負責背包邏輯。

---

## O — Open Closed Principle

系統允許擴充而不修改核心。

新增技能時：

- 不需修改既有程式
- 只需新增類別與註冊

---

## L — Liskov Substitution Principle

所有子類別需可替換父類別。

例如：

```text
Skill
↳ MiningSkill
↳ FishingSkill
```

---

## I — Interface Segregation Principle

避免過大的 Interface。

---

## D — Dependency Inversion Principle

高層模組依賴抽象而非具體實作。

例如：

```text
Controller
→ ActionStrategy Interface
```

而非直接依賴特定技能。

---

# 九、資料驅動設計（Data-Driven Design）

本系統將使用 JSON 管理遊戲資料。

例如：

```json
{
  "id": "iron_ore",
  "name": "Iron Ore",
  "value": 10
}
```

---

## 優點

- 降低硬編碼
- 易於擴充
- 易於修改平衡
- 更接近實際遊戲引擎設計

---

# 十、UML 圖

* **Class Diagram**
```
classDiagram

%% ================= ABSTRACT =================

class Skill {
    <<abstract>>
    +id
    +execute()
}

class MiningSkill
class FishingSkill

Skill <|-- MiningSkill
Skill <|-- FishingSkill

%% ================= STRATEGY =================

class ActionStrategy {
    <<interface>>
    +execute()
}

class MiningStrategy
class CombatStrategy

ActionStrategy <|.. MiningStrategy
ActionStrategy <|.. CombatStrategy

MiningSkill --> MiningStrategy

%% ================= PLAYER =================

class Player {
    +level
    +exp
}

class Inventory

Player *-- Inventory

Player --> Skill

%% ================= ITEMS =================

class Item {
    +id
    +name
}

Inventory *-- Item

%% ================= SERVICES =================

class InventoryService
class CombatService

InventoryService ..> Player
CombatService ..> Player

%% ================= FACTORY =================

class ItemFactory
class SkillFactory

ItemFactory ..> Item
SkillFactory ..> Skill

%% ================= EVENT =================

class EventBus {
    +publish()
    +subscribe()
}

class Event {
    <<abstract>>
}

class ItemAddedEvent
class LevelUpEvent

Event <|-- ItemAddedEvent
Event <|-- LevelUpEvent

EventBus --> Event

%% ================= ENGINE =================

class Tickable {
    <<interface>>
    +tick()
}

class GameEngine

Tickable <|.. MiningSkill

GameEngine --> Tickable
GameEngine --> EventBus
```

* **Package Diagram**
```
flowchart TD

UI[View Layer - JavaFX UI]
CTRL[Controller Layer]
SERVICE[Service Layer]
DOMAIN[Domain Layer]
CORE[Core Framework]
DATA[Data JSON Files]

UI --> CTRL
CTRL --> SERVICE
SERVICE --> DOMAIN
SERVICE --> CORE
DOMAIN --> CORE
CORE --> DATA
```

* **Tick Engine Flow**
```
sequenceDiagram

participant Engine
participant Tickable
participant EventBus

loop every second
    Engine->>Tickable: tick()
    Tickable->>Tickable: update progress
    Tickable->>EventBus: publish(GameTickEvent)
end
```

---

# 十一、專案目錄規劃

```text
src/
│
├── core/
│   ├── engine/
│   ├── event/
│   ├── registry/
│   └── loader/
│
├── domain/
│   ├── player/
│   ├── item/
│   ├── skill/
│   └── action/
│
├── service/
│   ├── inventory/
│   ├── combat/
│   └── progression/
│
├── factory/
│
├── controller/
│
├── view/
│   ├── fxml/
│   ├── css/
│   └── components/
│
└── data/
    ├── items.json
    ├── skills.json
    └── enemies.json
```

---

# 十二、MVP 功能規劃

本專題 MVP 將優先完成：

- 技能系統
- 採集系統
- Inventory 系統
- Event Bus
- 基本 UI
- JSON 載入
- Registry System

---

# 十三、未來可擴充方向

---

## 1. Mod System

允許外部模組新增內容。

---

## 2. Plugin Architecture

允許額外功能動態載入。

---

## 3. Multiplayer Support

未來可延伸多人同步架構。

---

## 4. AI NPC System

導入 AI 行為模組。

---

# 十四、專題預期成果

本專題預期完成：

- 一套具可擴充性的 Idle RPG Framework
- 完整 MVC 架構
- Event-Driven 系統
- Data-Driven 遊戲內容管理
- UML 文件
- GUI 操作介面
- 多種設計模式實作

---

# 十五、結論

本專題核心並非單純遊戲開發，  
而是：

# 「如何設計一套具備可維護性、可擴充性與低耦合的遊戲框架」

透過：

- MVC
- Event-Driven Architecture
- Design Patterns
- SOLID Principles
- Data-Driven Design

建立一套現代化的 Idle RPG Framework。
