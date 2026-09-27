package com.afsu.mod.registry;

import net.minecraft.resources.ResourceLocation;
import com.afsu.mod.BaseFluidType;
import com.afsu.mod.CustomFluidCellItem;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AFSUFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, "afsu");
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, "afsu");
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, "afsu");
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "afsu");

    // Hydrogen Peroxide
    public static final RegistryObject<FluidType> HYDROGEN_PEROXIDE_TYPE = FLUID_TYPES.register("hydrogen_peroxide", () ->
        new BaseFluidType(
            new ResourceLocation("afsu", "block/fluids/hydrogen_peroxide_still"),
            new ResourceLocation("afsu", "block/fluids/hydrogen_peroxide_flow"),
            FluidType.Properties.create().descriptionId("fluid.afsu.hydrogen_peroxide").fallDistanceModifier(0F).canExtinguish(true).canConvertToSource(false).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL).sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY))
    );
    public static final RegistryObject<FlowingFluid> HYDROGEN_PEROXIDE = FLUIDS.register("hydrogen_peroxide", () ->
        new ForgeFlowingFluid.Source(AFSUFluids.HYDROGEN_PEROXIDE_PROPS)
    );
    public static final RegistryObject<FlowingFluid> HYDROGEN_PEROXIDE_FLOWING = FLUIDS.register("hydrogen_peroxide_flowing", () ->
        new ForgeFlowingFluid.Flowing(AFSUFluids.HYDROGEN_PEROXIDE_PROPS)
    );
    public static final RegistryObject<LiquidBlock> HYDROGEN_PEROXIDE_BLOCK = BLOCKS.register("hydrogen_peroxide", () ->
        new LiquidBlock(HYDROGEN_PEROXIDE, BlockBehaviour.Properties.of(Material.WATER).noCollission().strength(100.0F).noLootTable())
    );
    public static final RegistryObject<Item> HYDROGEN_PEROXIDE_CELL = ITEMS.register("hydrogen_peroxide_cell", () ->
        new CustomFluidCellItem(HYDROGEN_PEROXIDE, new Item.Properties().stacksTo(64).tab(net.minecraft.world.item.CreativeModeTab.TAB_MISC))
    );
    public static final ForgeFlowingFluid.Properties HYDROGEN_PEROXIDE_PROPS = new ForgeFlowingFluid.Properties(
        HYDROGEN_PEROXIDE_TYPE, HYDROGEN_PEROXIDE, HYDROGEN_PEROXIDE_FLOWING
    ).block(HYDROGEN_PEROXIDE_BLOCK);

    // Sulfuric Acid
    public static final RegistryObject<FluidType> SULFURIC_ACID_TYPE = FLUID_TYPES.register("sulfuric_acid", () ->
        new BaseFluidType(
            new ResourceLocation("afsu", "block/fluids/sulfuric_acid_still"),
            new ResourceLocation("afsu", "block/fluids/sulfuric_acid_flow"),
            FluidType.Properties.create().descriptionId("fluid.afsu.sulfuric_acid").fallDistanceModifier(0F).canExtinguish(false).canConvertToSource(false).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL).sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY))
    );
    public static final RegistryObject<FlowingFluid> SULFURIC_ACID = FLUIDS.register("sulfuric_acid", () ->
        new ForgeFlowingFluid.Source(AFSUFluids.SULFURIC_ACID_PROPS)
    );
    public static final RegistryObject<FlowingFluid> SULFURIC_ACID_FLOWING = FLUIDS.register("sulfuric_acid_flowing", () ->
        new ForgeFlowingFluid.Flowing(AFSUFluids.SULFURIC_ACID_PROPS)
    );
    public static final RegistryObject<LiquidBlock> SULFURIC_ACID_BLOCK = BLOCKS.register("sulfuric_acid", () ->
        new LiquidBlock(SULFURIC_ACID, BlockBehaviour.Properties.of(Material.WATER).noCollission().strength(100.0F).noLootTable())
    );
    public static final RegistryObject<Item> SULFURIC_ACID_CELL = ITEMS.register("sulfuric_acid_cell", () ->
        new CustomFluidCellItem(SULFURIC_ACID, new Item.Properties().stacksTo(64).tab(net.minecraft.world.item.CreativeModeTab.TAB_MISC))
    );
    public static final ForgeFlowingFluid.Properties SULFURIC_ACID_PROPS = new ForgeFlowingFluid.Properties(
        SULFURIC_ACID_TYPE, SULFURIC_ACID, SULFURIC_ACID_FLOWING
    ).block(SULFURIC_ACID_BLOCK);

    public static void register(IEventBus bus) {
        FLUID_TYPES.register(bus);
        FLUIDS.register(bus);
        BLOCKS.register(bus);
        ITEMS.register(bus);
    }
}

