# База знаний проекта AFSU Mod (IC2 Classic 1.19.2 Addon)

## 1. Обзор проекта и окружение
- **Minecraft:** 1.19.2
- **Forge:** 43.4.0
- **Java:** 17
- **Зависимости:** 
  - `ic2-classic-1.19.2-19.2.14.jar` (`MODID`: `ic2`)
  - `Advanced-Solars-Classic-Edition-1.19.2-1.0.6.jar` (`MODID`: `advanced_solars`)
- **Основной Mod ID:** `afsu` (`com.afsu.mod.AFSUMod`)

---

## 2. Что уже реализовано

### А. Хранилище энергии (AFSU & UFA)
1. **AFSU (`afsu:afsu_block`):**
   - Блок накопителя энергии Tier-6.
   - Емкость: 1,500,000,000 EU.
   - Выходная мощность: 16,384 EU/t (пакеты Tier 6).
   - Классы: `AFSUBlock`, `AFSUBlockEntity`, `AFSUContainer`, `AFSUEnergyStorageComponent`.
   - Наследование от `DirectionalEnergyStorageBlock` и `DirectionalEnergyStorageTileEntity`.
2. **Синхронизация поворота (`DirectionalEnergyStorage*`):**
   - Двусторонняя синхронизация между внутренним состоянием `facing` в IC2 TileEntity и блочным свойством `BlockStateProperties.FACING`.
   - Вращение блока ориентирует выходной порт на игрока при установке (`nearestLookingDirection.getOpposite()`).
3. **UFA Батарея (`afsu:ufa_item`):**
   - Портативный аккумулятор емкостью 100,000,000 EU.
   - Скорость зарядки/разрядки: 8192 EU/t.
   - Tier 1 (позволяет заряжаться в любом базовом IC2-генераторе/хранилище).
   - Поддерживает динамический предикат модели `afsu:charge` (5 градаций текстуры заряда).

### Б. Солнечные панели (ASP Port 1.12.2 -> 1.19.2)
1. **Реализованные панели:**
   - **Квантовая (Quantum Solar Panel):** День 4,096 EU/t, Ночь 512 EU/t, Емкость 4M EU, Tier 5.
   - **Фотонная (Photon Solar Panel):** День 16,384 EU/t, Ночь 2,048 EU/t, Емкость 16M EU, Tier 6.
   - **Сингулярная (Singularity Solar Panel):** День 65,536 EU/t, Ночь 8,192 EU/t, Емкость 64M EU, Tier 7.
   - **Абсолютная (Absolute Solar Panel):** День 524,288 EU/t, Ночь 65,536 EU/t, Емкость 512M EU, Tier 8.
2. **Архитектура панелей:**
   - Базовый класс: `BaseSuperSolarTileEntity` (расширяет `BaseGeneratorTileEntity`).
   - Реализует `IEnergySource`, `ITickListener`, `ITileGui`, `IWrenchableTile`, `IEUProducer`.
   - Проверка неба и погоды: прямая видимость неба (`canSeeSkyFromBelowWater`), отключение генерации в грозу/дождь в соответствующих биомах.
   - Ночная/пасмурная выработка: ровно 1/8 от дневной мощности.
   - Контейнер: `SuperSolarPanelContainer` с 4 слотами для зарядки аккумуляторов (`FilterSlot.createChargeSlot`).
   - Кастомные компоненты GUI: `SuperSolarPanelComp` (индикатор солнца/луны), `SuperSolarEnergyStringComp` (вывод EU текста), `ChargeBarComponent` (полоса заряда).
3. **Промежуточные компоненты и крафты:**
   - `photon`, `photon_glass_pane`, `singularity_core`, `singularity`, `singularity_glass_pane`, `quantum_circuit`, `quantum_core`.
   - Программная регистрация рецептов молекулярного преобразователя в `FMLCommonSetupEvent`:
     - Sunnarium -> Photon (12M EU)
     - Photon -> Singularity (16M EU)

### В. Плазменный трансформатор (Plasma Transformer)
1. **Параметры:**
   - Блок: `afsu:plasma_transformer`.
   - Вход: 1 грань (по направлению `FACING`), принимает любой вольтаж (до Tier 10).
   - Выход: 5 остальных граней.
   - Регулируемый размер пакета: от 32,768 EU до 2,097,152 EU (Tier 10).
   - Количество пакетов за тик: от 1 до 32.
2. **Интерфейс и сетевая синхронизация:**
   - Кастомный компонент `PlasmaTransformerComponent` на базе GUI настраиваемого трансформатора IC2.
   - 7 быстрых кнопок предустановки тиров: `LuV`, `ZPM`, `UV`, `UHV`, `UEV`, `UIV`, `MAX`.
   - Кнопки точной настройки (+ / -) с модификаторами клавиш (`Ctrl` = 10x, `Shift` = 100x, `Alt` = 1000x).
   - Кнопка подтверждения (`Confirm`).
   - Синхронизация клиент -> сервер через `tile.sendToServer(...)` и интерфейс `INetworkClientEventListener`.
3. **Рецепт:**
   - 4x `ic2:transformer_adjustable`, 2x `ic2:stabilized_machine_block`, 2x `afsu:quantum_circuit`, 1x `afsu:ufa_item`.

---

## 3. Ключевые архитектурные паттерны и правила
1. **Strict Registry Verification:**
   - Никогда не угадывать идентификаторы предметов/блоков (`ic2:...`, `advanced_solars:...`). Всегда проверять через декомпилированные классы и константы модов.
2. **Targeted Decompilation:**
   - Декомпилировать только целевые классы IC2 по мере необходимости через `cfr.jar` в `.scratch/`.
3. **IC2 Component GUI Model:**
   - Все окна собираются из `GuiWidget` и `ContainerComponent<T>`. Это исключает самописный рендеринг и сохраняет нативный стиль и сетевую синхронизацию IC2.
4. **Adapter Separation:**
   - Не создавать преждевременных обобщений. Для хранилищ используется `DirectionalEnergyStorage*`, для генераторов — `BaseSuperSolarTileEntity`. Для будущих механизмов создается отдельный чистый адаптер.

---

## 4. Что нужно сделать дальше (Roadmap)
- **Механизм генерации жидкостей из EU (Fluid Generator / Spawner):**
  - Создать блок и TileEntity, принимающий энергию через `IEnergySink`.
  - Реализовать внутренний `FluidTank` (емкость) и поддержку Forge Fluid Capabilities (`CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY`).
  - Разработать интерфейс (GUI) с индикатором энергии, шкалой уровня жидкости, слотами для бакетов/капсул ввода и вывода.
  - Определить конфигурацию конверсии: соотношение EU к mB для Воды, Лавы и других жидкостей.
  - Добавить рецепт крафта механизма и локализации (`en_us.json`, `ru_ru.json`).
