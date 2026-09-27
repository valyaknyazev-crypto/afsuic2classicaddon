package com.afsu.mod.registry;
import com.afsu.mod.AFSUMod;
import com.afsu.mod.AFSUBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AFSUBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, AFSUMod.MODID);
    
    public static final RegistryObject<BlockEntityType<AFSUBlockEntity>> AFSU_BLOCK_ENTITY = BLOCK_ENTITIES.register("afsu_block", () -> BlockEntityType.Builder.of(AFSUBlockEntity::new, AFSUBlocks.AFSU_BLOCK.get()).build(null));
    
    public static final RegistryObject<BlockEntityType<com.afsu.mod.solar.BaseSuperSolarTileEntity>> PHOTON_SOLAR_PANEL_ENTITY = BLOCK_ENTITIES.register("photon_solar_panel", () -> BlockEntityType.Builder.<com.afsu.mod.solar.BaseSuperSolarTileEntity>of((pos, state) -> new com.afsu.mod.solar.BaseSuperSolarTileEntity(pos, state, 8192, 120_000_000, 6) { public net.minecraft.world.level.block.entity.BlockEntityType<?> createType() { return com.afsu.mod.solar.BaseSuperSolarTileEntity.getPhotonType(); } }, AFSUBlocks.PHOTON_SOLAR_PANEL.get()).build(null));
    public static final RegistryObject<BlockEntityType<com.afsu.mod.solar.BaseSuperSolarTileEntity>> SINGULARITY_SOLAR_PANEL_ENTITY = BLOCK_ENTITIES.register("singularity_solar_panel", () -> BlockEntityType.Builder.<com.afsu.mod.solar.BaseSuperSolarTileEntity>of((pos, state) -> new com.afsu.mod.solar.BaseSuperSolarTileEntity(pos, state, 16384, 480_000_000, 7) { public net.minecraft.world.level.block.entity.BlockEntityType<?> createType() { return com.afsu.mod.solar.BaseSuperSolarTileEntity.getSingularityType(); } }, AFSUBlocks.SINGULARITY_SOLAR_PANEL.get()).build(null));
    public static final RegistryObject<BlockEntityType<com.afsu.mod.solar.BaseSuperSolarTileEntity>> ABSOLUTE_SOLAR_PANEL_ENTITY = BLOCK_ENTITIES.register("absolute_solar_panel", () -> BlockEntityType.Builder.<com.afsu.mod.solar.BaseSuperSolarTileEntity>of((pos, state) -> new com.afsu.mod.solar.BaseSuperSolarTileEntity(pos, state, 131072, 2_000_000_000, 8) { public net.minecraft.world.level.block.entity.BlockEntityType<?> createType() { return com.afsu.mod.solar.BaseSuperSolarTileEntity.getAbsoluteType(); } }, AFSUBlocks.ABSOLUTE_SOLAR_PANEL.get()).build(null));
    public static final RegistryObject<BlockEntityType<com.afsu.mod.solar.BaseSuperSolarTileEntity>> QUANTUM_SOLAR_PANEL_ENTITY = BLOCK_ENTITIES.register("quantum_solar_panel", () -> BlockEntityType.Builder.<com.afsu.mod.solar.BaseSuperSolarTileEntity>of((pos, state) -> new com.afsu.mod.solar.BaseSuperSolarTileEntity(pos, state, 4096, 30_000_000, 5) { public net.minecraft.world.level.block.entity.BlockEntityType<?> createType() { return com.afsu.mod.solar.BaseSuperSolarTileEntity.getQuantumType(); } }, AFSUBlocks.QUANTUM_SOLAR_PANEL.get()).build(null));
    
    public static final RegistryObject<BlockEntityType<com.afsu.mod.transformer.PlasmaTransformerTileEntity>> PLASMA_TRANSFORMER_ENTITY = BLOCK_ENTITIES.register("plasma_transformer", () -> BlockEntityType.Builder.of(com.afsu.mod.transformer.PlasmaTransformerTileEntity::new, AFSUBlocks.PLASMA_TRANSFORMER.get()).build(null));
    public static final RegistryObject<BlockEntityType<com.afsu.mod.cable.CableTileEntity>> ULTRA_CONDUCTOR_CABLE_ENTITY = BLOCK_ENTITIES.register("ultra_conductor_cable", () -> BlockEntityType.Builder.of((pos, state) -> new com.afsu.mod.cable.CableTileEntity(com.afsu.mod.cable.CableTileEntity.getUltraType(), pos, state, 0.01, 2097153), AFSUBlocks.ULTRA_CONDUCTOR_CABLE.get()).build(null));
    public static final RegistryObject<BlockEntityType<com.afsu.mod.cable.CableTileEntity>> ABSOLUTE_CABLE_ENTITY = BLOCK_ENTITIES.register("absolute_cable", () -> BlockEntityType.Builder.of((pos, state) -> new com.afsu.mod.cable.CableTileEntity(com.afsu.mod.cable.CableTileEntity.getAbsoluteType(), pos, state, 0.0, 2147483647), AFSUBlocks.ABSOLUTE_CABLE.get()).build(null));
    public static final RegistryObject<BlockEntityType<com.afsu.mod.cable.CableTileEntity>> SUPER_CONDUCTOR_CABLE_ENTITY = BLOCK_ENTITIES.register("super_conductor_cable", () -> BlockEntityType.Builder.of((pos, state) -> new com.afsu.mod.cable.CableTileEntity(com.afsu.mod.cable.CableTileEntity.getSuperType(), pos, state, 0.5, 524289), AFSUBlocks.SUPER_CONDUCTOR_CABLE.get()).build(null));
    
    public static final RegistryObject<BlockEntityType<com.afsu.mod.assembler.AdvancedAssemblerTileEntity>> ADVANCED_ASSEMBLER_ENTITY = BLOCK_ENTITIES.register("advanced_assembler", () -> BlockEntityType.Builder.of(com.afsu.mod.assembler.AdvancedAssemblerTileEntity::new, AFSUBlocks.ADVANCED_ASSEMBLER.get()).build(null));
}
