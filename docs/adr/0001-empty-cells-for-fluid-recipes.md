# 1. Use empty cells for custom fluid recipe processing

Date: 2026-09-29

## Status

Accepted

## Context

The AdvancedAssembler allows crafting recipes that consume primary items and optionally fluids, producing a primary item result, a secondary byproduct item (secondary_result), and a waste fluid. Initially, recipes were configured to require filled fluid cells (e.g., ic2:fluid_cell with NBT data) or produce them. However, IC2 Classic's Ingredient.fromJson strictly evaluates standard item syntax and aggressively drops (fails silently) any recipe that includes invalid item properties or non-standard fluid item configurations inside simple IngredientInput fields.

## Decision

We will strictly use the base ic2:cell_empty item ID in JSON recipes for all secondary output definitions that involve fluids or capsules. The AdvancedAssemblerTileEntity will handle the logical translation of consuming empty cells and filling the internal wasteTank, or replacing the empty cell with the appropriate custom cell (item.afsu.waste_acid_cell or item.afsu.toxic_sludge_cell) programmatically if they are emitted as a hard item byproduct. 

## Consequences

- **Positive:** Recipes load flawlessly during datapack compilation without throwing JsonSyntaxException or being silently dropped by IC2 Classic's recipe manager.
- **Negative:** The JSON recipe secondary_result field does not explicitly show the filled cell ID in the raw JSON, making it slightly less intuitive for developers reading the data files. This must be documented for anyone creating datapacks.
