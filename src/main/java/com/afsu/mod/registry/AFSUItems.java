package com.afsu.mod.registry;
import com.afsu.mod.AFSUMod;
import com.afsu.mod.UFAItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AFSUItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AFSUMod.MODID);
    
    public static final RegistryObject<Item> AFSU_BLOCK_ITEM = ITEMS.register("afsu_block", () -> new BlockItem(AFSUBlocks.AFSU_BLOCK.get(), new Item.Properties().tab(AFSUMod.AFSU_TAB)));
    public static final RegistryObject<Item> UFA_ITEM = ITEMS.register("ufa_item", UFAItem::new);
    public static final RegistryObject<Item> BUG_TRACKER = ITEMS.register("bug_tracker", () -> new com.afsu.mod.tools.BugTrackerItem(new Item.Properties().tab(AFSUMod.AFSU_TAB).stacksTo(1)));
    
    // Components
    public static final RegistryObject<Item> PHOTON_ITEM = ITEMS.register("photon", () -> new Item(new Item.Properties().tab(AFSUMod.AFSU_TAB)));
    public static final RegistryObject<Item> PHOTON_GLASS_PANE = ITEMS.register("photon_glass_pane", () -> new Item(new Item.Properties().tab(AFSUMod.AFSU_TAB)));
    public static final RegistryObject<Item> SINGULARITY_CORE = ITEMS.register("singularity_core", () -> new Item(new Item.Properties().tab(AFSUMod.AFSU_TAB)));
    public static final RegistryObject<Item> SINGULARITY_ITEM = ITEMS.register("singularity", () -> new Item(new Item.Properties().tab(AFSUMod.AFSU_TAB)));
    public static final RegistryObject<Item> CHUNK_OF_ABSOLUTE_SINGULARITY = ITEMS.register("chunk_of_absolute_singularity", () -> new Item(new Item.Properties().tab(AFSUMod.AFSU_TAB)));
    public static final RegistryObject<Item> OMNI_SINGULARITY = ITEMS.register("omni_singularity", () -> new Item(new Item.Properties().tab(AFSUMod.AFSU_TAB)));
    public static final RegistryObject<Item> ABSOLUTE_GLASS_PANE = ITEMS.register("absolute_glass_pane", () -> new Item(new Item.Properties().tab(AFSUMod.AFSU_TAB)));
    public static final RegistryObject<Item> ABSOLUTE_CORE = ITEMS.register("absolute_core", () -> new Item(new Item.Properties().tab(AFSUMod.AFSU_TAB)));
    public static final RegistryObject<Item> ABSOLUTE_SONNARIUM_ALLOY = ITEMS.register("absolute_sonnarium_alloy", () -> new Item(new Item.Properties().tab(AFSUMod.AFSU_TAB)));
    public static final RegistryObject<Item> HYPER_DENSE_CARBON = ITEMS.register("hyper_dense_carbon", () -> new Item(new Item.Properties().tab(AFSUMod.AFSU_TAB)));
    public static final RegistryObject<Item> ADVANCED_SABER = ITEMS.register("advanced_saber", () -> new com.afsu.mod.tools.ElectricSaberItem(new Item.Properties().tab(AFSUMod.AFSU_TAB).stacksTo(1), 360000, 8192, 1, 10, 400, 30.0, 4.0));
    public static final RegistryObject<Item> QUANTUM_SABER = ITEMS.register("quantum_saber", () -> new com.afsu.mod.tools.ElectricSaberItem(new Item.Properties().tab(AFSUMod.AFSU_TAB).stacksTo(1), 1000000, 8192, 1, 25, 800, 67.0, 7.0));
    public static final RegistryObject<Item> DUST_SULFUR = ITEMS.register("dust_sulfur", () -> new Item(new Item.Properties().tab(AFSUMod.AFSU_TAB)));
    public static final RegistryObject<Item> SINGULARITY_GLASS_PANE = ITEMS.register("singularity_glass_pane", () -> new Item(new Item.Properties().tab(AFSUMod.AFSU_TAB)));
    public static final RegistryObject<Item> QUANTUM_CIRCUIT = ITEMS.register("quantum_circuit", () -> new Item(new Item.Properties().tab(AFSUMod.AFSU_TAB)));
    public static final RegistryObject<Item> QUANTUM_CORE = ITEMS.register("quantum_core", () -> new Item(new Item.Properties().tab(AFSUMod.AFSU_TAB)));
    public static final RegistryObject<Item> QUANTUM_PLATING = ITEMS.register("quantum_plating", () -> new Item(new Item.Properties().tab(AFSUMod.AFSU_TAB)));
    public static final RegistryObject<Item> DENSE_LAPIS_PLATE = ITEMS.register("dense_lapis_plate", () -> new Item(new Item.Properties().tab(AFSUMod.AFSU_TAB)));
    public static final RegistryObject<Item> CARBON_SHEATHING = ITEMS.register("carbon_sheathing", () -> new Item(new Item.Properties().tab(AFSUMod.AFSU_TAB)));
    public static final RegistryObject<Item> NANO_SHEATHING = ITEMS.register("nano_sheathing", () -> new Item(new Item.Properties().tab(AFSUMod.AFSU_TAB)));
    public static final RegistryObject<Item> NANO_CIRCUIT = ITEMS.register("nano_circuit", () -> new Item(new Item.Properties().tab(AFSUMod.AFSU_TAB)));
    public static final RegistryObject<Item> HYBRID_CIRCUIT = ITEMS.register("hybrid_circuit", () -> new Item(new Item.Properties().tab(AFSUMod.AFSU_TAB)));
    public static final RegistryObject<Item> DUST_LAPIS = ITEMS.register("dust_lapis", () -> new Item(new Item.Properties().tab(AFSUMod.AFSU_TAB)));
    public static final RegistryObject<Item> PLATE_LAPIS = ITEMS.register("plate_lapis", () -> new Item(new Item.Properties().tab(AFSUMod.AFSU_TAB)));
    public static final RegistryObject<Item> DUST_ENERGIUM = ITEMS.register("dust_energium", () -> new Item(new Item.Properties().tab(AFSUMod.AFSU_TAB)));

    // Block Items
    public static final RegistryObject<Item> QUANTUM_SOLAR_PANEL_ITEM = ITEMS.register("quantum_solar_panel", () -> new BlockItem(AFSUBlocks.QUANTUM_SOLAR_PANEL.get(), new Item.Properties().tab(AFSUMod.AFSU_TAB)));
    public static final RegistryObject<Item> PHOTON_SOLAR_PANEL_ITEM = ITEMS.register("photon_solar_panel", () -> new BlockItem(AFSUBlocks.PHOTON_SOLAR_PANEL.get(), new Item.Properties().tab(AFSUMod.AFSU_TAB)));
    public static final RegistryObject<Item> SINGULARITY_SOLAR_PANEL_ITEM = ITEMS.register("singularity_solar_panel", () -> new BlockItem(AFSUBlocks.SINGULARITY_SOLAR_PANEL.get(), new Item.Properties().tab(AFSUMod.AFSU_TAB)));
    public static final RegistryObject<Item> ABSOLUTE_SOLAR_PANEL_ITEM = ITEMS.register("absolute_solar_panel", () -> new BlockItem(AFSUBlocks.ABSOLUTE_SOLAR_PANEL.get(), new Item.Properties().tab(AFSUMod.AFSU_TAB)));

    public static final RegistryObject<Item> PLASMA_TRANSFORMER_ITEM = ITEMS.register("plasma_transformer", () -> new BlockItem(AFSUBlocks.PLASMA_TRANSFORMER.get(), new Item.Properties().tab(AFSUMod.AFSU_TAB)));
    public static final RegistryObject<Item> ULTRA_CONDUCTOR_CABLE_ITEM = ITEMS.register("ultra_conductor_cable", () -> new BlockItem(AFSUBlocks.ULTRA_CONDUCTOR_CABLE.get(), new Item.Properties().tab(AFSUMod.AFSU_TAB)));
    public static final RegistryObject<Item> ABSOLUTE_CABLE_ITEM = ITEMS.register("absolute_cable", () -> new BlockItem(AFSUBlocks.ABSOLUTE_CABLE.get(), new Item.Properties().tab(AFSUMod.AFSU_TAB)));
    public static final RegistryObject<Item> SUPER_CONDUCTOR_CABLE_ITEM = ITEMS.register("super_conductor_cable", () -> new BlockItem(AFSUBlocks.SUPER_CONDUCTOR_CABLE.get(), new Item.Properties().tab(AFSUMod.AFSU_TAB)));
    public static final RegistryObject<Item> ADVANCED_ASSEMBLER_ITEM = ITEMS.register("advanced_assembler", () -> new BlockItem(AFSUBlocks.ADVANCED_ASSEMBLER.get(), new Item.Properties().tab(AFSUMod.AFSU_TAB)));
}
