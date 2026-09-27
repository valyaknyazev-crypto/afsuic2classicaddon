# Encapsulate IC2 Rotation Sync in Specific Adapter Classes

In IC2 Classic, directional machines (like energy storage blocks) have their rotation state managed internally by the `TileEntity`, which must be bidirectionally synced with the Minecraft visual `BlockState` (the `FACING` property).
If done per-block, this causes boilerplate and a leaky seam (the block pushes state to the tile on placement, and the tile pushes state to the block on wrenching). 
We decided to extract this synchronization logic into reusable base adapter classes (`DirectionalEnergyStorageBlock` and `DirectionalEnergyStorageTileEntity`).

Crucially, as flagged during code review, this base layer is intentionally hardcoded to extend `BaseEnergyStorageTileEntity`, meaning it is *only* suitable for Energy Storage blocks. It cannot be used generically for standard directional machines (like furnaces or macerators). This strict coupling is intentional to avoid "Speculative Generality" (building generic abstractions before we have non-energy directional blocks). When we need to implement standard rotating mechanisms, we will create separate, explicitly named adapters for them rather than trying to force everything into one overly-generic class.

## Additional Lessons (Blockstate Rotation)
During this implementation, we discovered a quirk of Minecraft's blockstate X-axis rotation math: `x=90` actually rotates South to UP (not down, as one might intuit). When mapping the output texture to `up` and `down`, `facing=up` must use `x=90` and `facing=down` must use `x=-90`. Inverting these will cause the visual output to face opposite to the logical IC2 energy output.
