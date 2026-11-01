# Spec: AFSU Mod Gameplay Bugfixes & Data Persistence (v1.4)

## Problem Statement

Players experience catastrophic progression loss and broken mod interactions when using AFSU addon machinery:
1. When chunks unload or the world saves, machines (Advanced Assembler, Plasma Transformer, AFSU) silently fail to persist their contents or user settings to disk, leading to total loss of fluids, upgrades, and custom voltage configurations.
2. High-tier cabling (Ultra Conductor Cable) immediately vaporizes without drops upon connecting to an AFSU due to an internal breakdown rating lower than the AFSU's base emission rate.
3. The Ultimate Fluid/Energy Battery (UFAItem) is classified as Tier 1, allowing late-game 100M EU energy buffers to be charged inside entry-level wooden BatBoxes.
4. The Advanced Assembler voids returned empty fluid cells when processing stacked cell inputs, inadvertently drains clean input reagents into waste drainage pipes, and greedily matches partial recipes when unrelated items occupy empty grid slots.
5. All Super Solar Panels produce exactly 50% of their intended daytime energy output due to an incorrect mathematical division in their base initialization.
6. Advanced and Quantum Sabers lack active blade animations, silently drain energy while sitting unselected in player backpacks, and cannot be toggled while aiming at block surfaces.

## Solution

Implement comprehensive data persistence across all machines via Forge's dirty-marking lifecycle, align electrical ratings and tiers to prevent grid fires and exploits, correct container retention and fluid routing in the Advanced Assembler, restore 100% rated solar panel daytime generation, and complete client-side animation and interaction logic for electric sabers.

---

## User Stories

1. As an industrial engineer, I want my Advanced Assembler to retain all imported fluids, upgrades, and queued crafting items across chunk unloads and server restarts, so that my automated production lines do not lose valuable chemical reagents.
2. As an industrial engineer, I want changes made to the Plasma Transformer's voltage tier and packet count in its GUI to persist when I leave the area, so that my downstream factory machinery remains protected from voltage spikes after reloading the world.
3. As an energy grid operator, I want my AFSU's stored energy and configured redstone output mode to persist across server restarts, so that my emergency energy management automation remains reliable.
4. As an energy grid operator, I want Ultra Conductor Cables to withstand the full 65,536 EU/t output of an AFSU without melting, so that I can transport extreme-voltage packets across my base safely.
5. As an endgame player, I want the UFAItem to require high-tier charging infrastructure (Tier 5), so that energy progression remains balanced and consistent with IC2 Classic tier rules.
6. As a chemical processor, I want the Advanced Assembler to return empty fluid cells to an accessible output buffer even when processing stacked containers, so that my fluid containers are not deleted.
7. As an automation engineer, I want fluid extraction conduits attached to the Advanced Assembler to pull exclusively from the waste fluid tank, so that my pure chemical inputs are not accidentally siphoned into waste disposal pipes.
8. As a factory technician, I want recipe matching in the Advanced Assembler to ignore or reject incompatible items in unmapped slots, so that unintended partial recipes do not prematurely consume partial component sets.
9. As a solar farm builder, I want Quantum, Photon, Singularity, and Absolute Solar Panels to output their full rated daytime generation (4096, 8192, 16384, and 65536 EU/t respectively), so that my crafted panel arrays produce the energy promised by their recipes.
10. As a combat operative, I want my Advanced and Quantum Sabers to display glowing animated blade textures when activated in my hand, so that I have clear visual feedback of their operational state.
11. As an adventurer, I want my electric sabers to consume energy only when held in active hands (or at a severely reduced idle rate in backpacks), so that stored power is not wasted while travelling.
12. As a player, I want to toggle my electric saber on or off regardless of whether I am pointing at the open sky or targeting a block face, so that weapon activation is seamless in tight corridors.

---

## Implementation Decisions

### Ticket 1: Chunk Unload & NBT Persistence (Data Loss Prevention)
- **Dirty Marking Contracts**: Ensure `setChanged()` is invoked whenever internal block entity state mutates:
  - Inside `AdvancedAssemblerTileEntity`: attach `setChanged()` triggers to `fluidTank` and `wasteTank` listener callbacks, container stack mutations in `insertResult()` and `consumeIngredients()`, and upgrade modifications in `applyUpgrades()`.
  - Inside `PlasmaTransformerTileEntity`: invoke `setChanged()` within `onClientDataReceived()` whenever packet counts or voltage packet sizes are adjusted by the user.
  - Inside `DirectionalEnergyStorageTileEntity` and `AFSUBlockEntity`: invoke `setChanged()` on redstone mode alterations and inventory slot manipulations.
  - Inside `BaseSuperSolarTileEntity`: ensure day/night state transitions and accumulated energy state trigger appropriate periodic dirty marks.

### Ticket 2: Cable Breakdown Threshold & EnergyNet Compatibility
- **Ultra Conductor Cable Threshold**: Update the registered conductor breakdown voltage of `ULTRA_CONDUCTOR_CABLE` from 32,768 EU to 131,072 EU in `AFSUBlocks` and `AFSUBlockEntities`, providing safe headroom above the AFSU's 65,536 EU/t output packet.
- **UFAItem Tier Elevation**: Elevate the `TIER` constant of `UFAItem` from 1 to 5, preventing interaction with low-voltage storage units while enabling compatibility with high-tier charging slots.

### Ticket 3: Advanced Assembler Fluid & Container Integrity
- **Cell Retention Logic**: Refactor container item handling when consuming fluid cells: if the input stack size exceeds 1, decrement the stack by 1 and route the empty cell container item directly into the primary output slots (or a secondary container ejection slot) rather than attempting in-place slot overwrites.
- **Dedicated Fluid Extraction Routing**: Update the exposed fluid capability handler so that calls to `drain()` inspect solely the internal `wasteTank`, preventing cross-tank bleeding from the input `fluidTank`.
- **Strict Recipe Slot Matching**: Update `AdvancedAssemblerRecipe.matches()` to verify that empty recipe slots in the definition are not occupied by extraneous non-matching items in the machine grid.

### Ticket 4: Super Solar Panel Day Generation Correction
- **Daytime Output Calculation**: Correct `BaseSuperSolarTileEntity` initialization so that `this.production = generation` rather than `generation / 2`, aligning Quantum (4096 EU/t), Photon (8192 EU/t), Singularity (16384 EU/t), and Absolute (65536 EU/t) with their stated design specifications.

### Ticket 5: Electric Saber Polish & Client Lifecycle
- **Item Property Overrides**: Implement client-side `ItemProperties.register()` bindings in `AFSUClientSetup` linking the `"afsu:active_frame"` predicate to an active frame counter loop.
- **Selective Energy Consumption**: Update `ElectricSaberItem.inventoryTick()` to inspect the `isSelected` flag, draining active operating energy only when the weapon is held in the main or off-hand.
- **Block Interaction Support**: Override `useOn()` in `ElectricSaberItem` to delegate directly to `use()`, ensuring right-click toggles occur consistently whether clicking air or targeting block faces.

---

## Testing Decisions
- **Seams & Scope**: All tests will target behavior through external API seams without modifying core production code during diagnosis.
- **EnergyNet & Cable Verification**: Validate that simulated cable entities accept 65,536 EU packets without triggering `removeConductor()`.
- **Persistence Verification**: Instantiate tile entities, apply mutations (GUI packets, fluid fills, container slot edits), serialize to `CompoundTag` via `saveAdditional()`, reload into fresh instances via `load()`, and assert property parity.
- **Recipe & Container Verification**: Exercise `matches()` against clean and noisy inventories; verify item consumption preserves empty container items when consuming multi-stack fluid cells.

---

## Out of Scope
- Changing item recipes on the standard crafting table (explicitly forbidden).
- Modifying molecular transformer energy values (already balanced in v1.3).
- Adding new machine blocks or fluid types.

---

## Further Notes
- Version target upon implementation: `1.4`.
- All changes must strictly compile against Forge 1.19.2 and pass all automated Gradle test tasks.
