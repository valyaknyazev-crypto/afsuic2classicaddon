# Spec: Electric Saber GUI Inventory Animation & Render Model State (v1.4.1)

## Problem Statement

When players activate an Advanced Saber or Quantum Saber, the weapon's blade lights up and animates properly in first-person and third-person hand view. However, when viewed inside graphical user interfaces (player inventory screen, hotbar HUD, container/chest storage slots, or held by the mouse cursor), the weapon icon fails to animate or blink. Instead, it either appears completely unpowered (showing the empty hilt without an extended blade) or remains completely frozen. 

Furthermore, earlier attempts to resolve item frames on the client thread caused critical game instability: invoking NBT tag mutation routines (`putLong`) inside item property getter loops triggered heavy garbage collection pressure, quad cache invalidation, and contributed to native out-of-memory crashes on memory-constrained systems.

## Solution

Ensure that active Electric Sabers smoothly and reliably animate across all graphical user interface (GUI) contexts, player hotbars, and inventories without degrading performance or corrupting item state. 

When an active saber is displayed within any inventory slot or GUI container where a world instance is not directly supplied by the renderer, the client safely falls back to the client world time to compute the active frame. In GUI and inventory slots, the saber immediately enters an active pulsating state between extended blade frames (switching every 2 ticks / ~100 ms) without delaying for or getting stuck in the hand-held blade unfolding sequence. When held in a player's hand, the dramatic unfolding sequence is preserved upon initial activation before transitioning into the same steady pulsation. All item property resolution functions remain strictly read-only, eliminating client-side NBT mutations and memory overhead.

---

## User Stories

1. As a combat player, I want active Advanced and Quantum Sabers to visibly glow and pulsate while sitting in my inventory, so that I can immediately tell at a glance that my weapon is consuming energy without having to equip it.
2. As a player managing my hotbar, I want the active saber icon in my hotbar HUD to blink between glowing blade frames, so that I am visually warned of ongoing battery drain while exploring or building.
3. As a player organizing chests and ME storage terminals, I want active sabers stored in external containers to show their animated blade state, so that active weapons do not look identical to inactive dormant weapons.
4. As a player moving items with my mouse cursor, I want the floating saber icon on the cursor to maintain its pulsing blade animation, so that visual consistency is preserved during item dragging and sorting.
5. As an endgame warrior, I want my saber to immediately display the fully extended, pulsing blade when opened in an inventory slot, so that I do not see a delayed or truncated 27-tick blade opening sequence in static menu screens.
6. As an immersive player, I want the saber to still play the complete 27-tick blade extension animation when drawn and activated in my character's hands, so that the weapon ignition feels impactful and cinematic.
7. As a player with multiple active sabers, I want all active sabers to pulsate in harmony with the global game time, so that the inventory UI looks coherent rather than chaotic or asynchronous.
8. As a user on a system with limited RAM, I want item property calculations during inventory rendering to execute without allocating or modifying NBT tags, so that browsing large inventories never triggers stutter, memory leaks, or native memory allocation crashes.
9. As a player holding an unpowered saber (energy depleted), I want the weapon in both inventory and hand to immediately return to the inactive hilt model, so that I clearly know when recharging is required.
10. As a player in third-person view or multiplayer, I want other players holding active sabers to display the same pulsing blade animation, so that weapon activity is clearly telegraphed to team members and opponents alike.

---

## Implementation Decisions

### Decision 1: Fallback Resolution for Level in GUI Render Contexts
- In vanilla Minecraft and Forge, GUI item rendering (`ItemRenderer.renderGuiItem`) deliberately passes a `null` world/level parameter to item property resolution functions.
- The item property resolver for the active frame must gracefully resolve the ambient `ClientLevel`. It will check the provided level first; if null, it inspects the holding entity's world; if still null, it falls back to the client singleton's active world.
- If no client world exists (e.g., during main menu asset stitching or modpack preloading), the resolver defaults to a static fully-extended active frame (10.0F), never failing or falling back to the unpowered hilt model when the item is flagged active.

### Decision 2: Context-Aware Animation Routing (Inventory vs. Hand)
- The resolver distinguishes between equipped hand-held rendering and inventory slot rendering:
  - **In-Hand Context (`entity != null`)**: When held by a living entity, the resolver checks the item's activation timestamp against the current world game time. If the elapsed duration is within the ignition threshold (< 27 ticks / 1.35 seconds), it maps the progress to the unfolding blade stages (frames 0 through 8). Once fully extended (≥ 27 ticks), it transitions into the pulsing phase.
  - **Inventory/GUI Context (`entity == null` or past threshold)**: The resolver bypasses the startup sequence entirely and immediately evaluates the global pulsation phase.

### Decision 3: Cadence and Modulation of the Active Blade Pulse
- The blade pulsation alternates smoothly between the two final fully-extended blade frames (frames 9 and 10, corresponding to model predicate thresholds 10.0F and 11.0F).
- The phase is computed using the global game time modulated by 2 ticks: `(gameTime / 2) % 2`. This produces a frame swap every 100 milliseconds (5 cycles per second), providing an energetic, electric glow without visual vibration or eye strain.

### Decision 4: Absolute Read-Only Guarantee on Render Threads
- Under no circumstances may `calculateSaberFrame` or any client `ItemPropertyFunction` call write operations (`putLong`, `putBoolean`, `putString`) or mutating getters (`getOrCreateTag`) on the item stack.
- Tag inspection must use read-only queries (`hasTag() && getTag().getBoolean("active")`). If a tag is absent, the function immediately returns `0.0F` (unpowered).
- Server-side and player-action code (`use()`, `useOn()`) remains exclusively responsible for recording the activation timestamp in NBT upon turning the weapon on.

---

## Testing Decisions

### What Makes a Good Test
- Tests must verify external behavioral contracts and property evaluation results across simulated game time ticks rather than internal implementation details.
- Tests must assert that item state and NBT tags are strictly immutable when passed through client frame resolution.

### Modules Tested
- **Client Item Model Property Provider (`AFSUClientSetup`)**:
  - Verification that inactive sabers always evaluate to frame `0.0F`.
  - Verification that active sabers in a GUI context (null level, null entity) resolve to fully-extended pulsing frames (10.0F / 11.0F) based on game time.
  - Verification that active sabers in hand correctly progress through deployment frames (1.0F through 9.0F) during the initial 27 ticks, then settle into pulsation.
  - Verification that no NBT tags are added or modified during frame evaluation.
- **Electric Saber Item Interaction (`ElectricSaberItem`)**:
  - Verification that right-clicking to activate writes the activation timestamp, while deactivation clears or updates the active flag.
  - Verification that `sidedSuccess` is returned to prevent client-server synchronization glitches.

### Prior Art
- Unit tests in `AFSUBugsTest` covering block wrenching, machine capabilities, and saber inventory ticking.

---

## Out of Scope

- Introducing audio hums or loops specifically inside inventory GUI screens (sound effects remain strictly bound to in-world player actions).
- Altering the underlying JSON model files or adding new texture files (the existing 11 model overrides from `advanced_saber_active_0` to `advanced_saber_active_10` provide full coverage).
- Modifying energy consumption mechanics during GUI rendering (energy drain remains exclusively driven by `inventoryTick`).

---

## Further Notes

- Model override predicates in Minecraft check properties in reverse order (`value >= predicate`). Frame `11.0F` triggers `active_10` (the highest glow state), and frame `10.0F` triggers `active_9` (the secondary glow state). Frame `0.0F` falls through all overrides to the base unpowered model.
