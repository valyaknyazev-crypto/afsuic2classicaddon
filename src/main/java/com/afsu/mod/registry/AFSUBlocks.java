package com.afsu.mod.registry;
import com.afsu.mod.AFSUMod;
import com.afsu.mod.AFSUBlock;
import com.afsu.mod.solar.SolarPanelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AFSUBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, AFSUMod.MODID);
    
    public static final RegistryObject<Block> AFSU_BLOCK = BLOCKS.register("afsu_block", () -> new AFSUBlock());
    
    public static final RegistryObject<Block> QUANTUM_SOLAR_PANEL = BLOCKS.register("quantum_solar_panel", () -> new SolarPanelBlock("quantum_solar_panel", (pos, state) -> new com.afsu.mod.solar.BaseSuperSolarTileEntity(pos, state, 4096, 2048000, 5) { public net.minecraft.world.level.block.entity.BlockEntityType<?> createType() { return com.afsu.mod.solar.BaseSuperSolarTileEntity.getQuantumType(); } }));
    public static final RegistryObject<Block> PHOTON_SOLAR_PANEL = BLOCKS.register("photon_solar_panel", () -> new SolarPanelBlock("photon_solar_panel", (pos, state) -> new com.afsu.mod.solar.BaseSuperSolarTileEntity(pos, state, 8192, 4096000, 6) { public net.minecraft.world.level.block.entity.BlockEntityType<?> createType() { return com.afsu.mod.solar.BaseSuperSolarTileEntity.getPhotonType(); } }));
    public static final RegistryObject<Block> SINGULARITY_SOLAR_PANEL = BLOCKS.register("singularity_solar_panel", () -> new SolarPanelBlock("singularity_solar_panel", (pos, state) -> new com.afsu.mod.solar.BaseSuperSolarTileEntity(pos, state, 16384, 8192000, 7) { public net.minecraft.world.level.block.entity.BlockEntityType<?> createType() { return com.afsu.mod.solar.BaseSuperSolarTileEntity.getSingularityType(); } }));
    public static final RegistryObject<Block> ABSOLUTE_SOLAR_PANEL = BLOCKS.register("absolute_solar_panel", () -> new SolarPanelBlock("absolute_solar_panel", (pos, state) -> new com.afsu.mod.solar.BaseSuperSolarTileEntity(pos, state, 65536, 32768000, 8) { public net.minecraft.world.level.block.entity.BlockEntityType<?> createType() { return com.afsu.mod.solar.BaseSuperSolarTileEntity.getAbsoluteType(); } }));
    
    public static final RegistryObject<Block> PLASMA_TRANSFORMER = BLOCKS.register("plasma_transformer", () -> new com.afsu.mod.transformer.PlasmaTransformerBlock());
    
    public static final RegistryObject<Block> ULTRA_CONDUCTOR_CABLE = BLOCKS.register("ultra_conductor_cable", () -> new com.afsu.mod.cable.CableBlock((pos, state) -> new com.afsu.mod.cable.CableTileEntity(com.afsu.mod.cable.CableTileEntity.getUltraType(), pos, state, 0.01, 32768)));
    public static final RegistryObject<Block> ABSOLUTE_CABLE = BLOCKS.register("absolute_cable", () -> new com.afsu.mod.cable.CableBlock((pos, state) -> new com.afsu.mod.cable.CableTileEntity(com.afsu.mod.cable.CableTileEntity.getAbsoluteType(), pos, state, 0.0, 131072)));
    public static final RegistryObject<Block> SUPER_CONDUCTOR_CABLE = BLOCKS.register("super_conductor_cable", () -> new com.afsu.mod.cable.CableBlock((pos, state) -> new com.afsu.mod.cable.CableTileEntity(com.afsu.mod.cable.CableTileEntity.getSuperType(), pos, state, 0.5, 16384)));
    
    public static final RegistryObject<Block> ADVANCED_ASSEMBLER = BLOCKS.register("advanced_assembler", () -> new com.afsu.mod.assembler.AdvancedAssemblerBlock());
}
