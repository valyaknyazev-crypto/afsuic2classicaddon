package com.afsu.mod;

import com.afsu.mod.registry.AFSUBlocks;
import com.afsu.mod.registry.AFSUItems;
import com.afsu.mod.registry.AFSUBlockEntities;
import com.afsu.mod.registry.AFSURecipes;
import com.afsu.mod.registry.AFSUMenus;
import com.afsu.mod.registry.AFSUFluids;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(AFSUMod.MODID)
public class AFSUMod {
    public static final String MODID = "afsu";

    public static final CreativeModeTab AFSU_TAB = new CreativeModeTab(MODID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(AFSUBlocks.AFSU_BLOCK.get());
        }
    };

    public static Block getBlock(String name) {
        return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(MODID, name));
    }

    public AFSUMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        AFSUBlocks.BLOCKS.register(modEventBus);
        AFSUItems.ITEMS.register(modEventBus);
        AFSUFluids.register(modEventBus);
        AFSUBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        AFSURecipes.RECIPE_SERIALIZERS.register(modEventBus);
        AFSURecipes.RECIPE_TYPES.register(modEventBus);
        AFSUMenus.MENU_TYPES.register(modEventBus);

        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::setup);
    }

    private void setup(final net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            try {
                // Register fluid fillings for IC2 cells
                ic2.core.item.misc.CellItem.registerFluidFilling(
                    com.afsu.mod.registry.AFSUFluids.HYDROGEN_PEROXIDE.get(),
                    com.afsu.mod.registry.AFSUFluids.HYDROGEN_PEROXIDE_CELL.get(),
                    true
                );
                ic2.core.item.misc.CellItem.registerFluidFilling(
                    com.afsu.mod.registry.AFSUFluids.SULFURIC_ACID.get(),
                    com.afsu.mod.registry.AFSUFluids.SULFURIC_ACID_CELL.get(),
                    true
                );

                ic2.core.item.misc.CellItem.registerFluidFilling(
                    com.afsu.mod.registry.AFSUFluids.WASTE_ACID.get(),
                    com.afsu.mod.registry.AFSUFluids.WASTE_ACID_CELL.get(),
                    true
                );
                ic2.core.item.misc.CellItem.registerFluidFilling(
                    com.afsu.mod.registry.AFSUFluids.TOXIC_SLUDGE.get(),
                    com.afsu.mod.registry.AFSUFluids.TOXIC_SLUDGE_CELL.get(),
                    true
                );



                // Register natively without reflection!
                ItemStack sunnarium = new ItemStack(
                        ForgeRegistries.ITEMS.getValue(
                                new ResourceLocation("advanced_solars", "sunnarium")
                        )
                );
                
                trinsdar.advancedsolars.util.AdvancedSolarsRecipes.MOLECULAR_TRANSFORMER.registerListener(list -> {
                    list.addDualRecipe(new ResourceLocation(MODID, "photon_recipe"), sunnarium, new ItemStack(AFSUItems.PHOTON_ITEM.get()), 30000000);
                    list.addDualRecipe(new ResourceLocation(MODID, "singularity_recipe"), new ItemStack(AFSUItems.PHOTON_ITEM.get()), new ItemStack(AFSUItems.SINGULARITY_ITEM.get()), 75000000);

                    ItemStack coalChunk = new ItemStack(
                            ForgeRegistries.ITEMS.getValue(new ResourceLocation("ic2", "coal_chunk")));
                    list.addDualRecipe(new ResourceLocation(MODID, "hyper_dense_carbon_recipe"), coalChunk, new ItemStack(AFSUItems.HYPER_DENSE_CARBON.get()), 10000000);

                    list.addDualRecipe(new ResourceLocation(MODID, "chunk_of_absolute_singularity_recipe"), new ItemStack(AFSUItems.SINGULARITY_ITEM.get()), new ItemStack(AFSUItems.CHUNK_OF_ABSOLUTE_SINGULARITY.get()), 200000000);

                    ItemStack enrichedSunnarium = new ItemStack(
                            ForgeRegistries.ITEMS.getValue(new ResourceLocation("advanced_solars", "enriched_sunnarium_alloy")));
                    list.addDualRecipe(new ResourceLocation(MODID, "absolute_sonnarium_recipe"), enrichedSunnarium, new ItemStack(AFSUItems.ABSOLUTE_SONNARIUM_ALLOY.get()), 150000000);
                });
            } catch (Exception e) {
                System.err.println("[AFSU] Failed to register Molecular Transformer recipes: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void clientSetup(final net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent event) {
        com.afsu.mod.client.AFSUClientSetup.init(event);
    }
}
