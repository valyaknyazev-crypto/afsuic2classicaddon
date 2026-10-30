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
        new CustomFluidCellItem(HYDROGEN_PEROXIDE, new Item.Properties().stacksTo(64).tab(com.afsu.mod.AFSUMod.AFSU_TAB))
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
        new CustomFluidCellItem(SULFURIC_ACID, new Item.Properties().stacksTo(64).tab(com.afsu.mod.AFSUMod.AFSU_TAB))
    );
    public static final ForgeFlowingFluid.Properties SULFURIC_ACID_PROPS = new ForgeFlowingFluid.Properties(
        SULFURIC_ACID_TYPE, SULFURIC_ACID, SULFURIC_ACID_FLOWING
    ).block(SULFURIC_ACID_BLOCK);

    // Waste Acid
    public static final RegistryObject<FluidType> WASTE_ACID_TYPE = FLUID_TYPES.register("waste_acid", () ->
        new BaseFluidType(
            new ResourceLocation("afsu", "block/fluids/waste_acid"),
            new ResourceLocation("afsu", "block/fluids/waste_acid"),
            FluidType.Properties.create().descriptionId("fluid.afsu.waste_acid").fallDistanceModifier(0F).canExtinguish(false).canConvertToSource(false).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL).sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY))
    );
    public static final RegistryObject<FlowingFluid> WASTE_ACID = FLUIDS.register("waste_acid", () ->
        new ForgeFlowingFluid.Source(AFSUFluids.WASTE_ACID_PROPS)
    );
    public static final RegistryObject<FlowingFluid> WASTE_ACID_FLOWING = FLUIDS.register("waste_acid_flowing", () ->
        new ForgeFlowingFluid.Flowing(AFSUFluids.WASTE_ACID_PROPS)
    );
    public static final RegistryObject<LiquidBlock> WASTE_ACID_BLOCK = BLOCKS.register("waste_acid", () ->
        new LiquidBlock(WASTE_ACID, BlockBehaviour.Properties.of(Material.WATER).noCollission().strength(100.0F).noLootTable())
    );
    public static final RegistryObject<Item> WASTE_ACID_CELL = ITEMS.register("waste_acid_cell", () ->
        new CustomFluidCellItem(WASTE_ACID, new Item.Properties().stacksTo(64).tab(com.afsu.mod.AFSUMod.AFSU_TAB))
    );
    public static final ForgeFlowingFluid.Properties WASTE_ACID_PROPS = new ForgeFlowingFluid.Properties(
        WASTE_ACID_TYPE, WASTE_ACID, WASTE_ACID_FLOWING
    ).block(WASTE_ACID_BLOCK);

    // Toxic Sludge
    public static final RegistryObject<FluidType> TOXIC_SLUDGE_TYPE = FLUID_TYPES.register("toxic_sludge", () ->
        new BaseFluidType(
            new ResourceLocation("afsu", "block/fluids/toxic_sludge"),
            new ResourceLocation("afsu", "block/fluids/toxic_sludge"),
            FluidType.Properties.create().descriptionId("fluid.afsu.toxic_sludge").fallDistanceModifier(0F).canExtinguish(false).canConvertToSource(false).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL).sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY))
    );
    public static final RegistryObject<FlowingFluid> TOXIC_SLUDGE = FLUIDS.register("toxic_sludge", () ->
        new ForgeFlowingFluid.Source(AFSUFluids.TOXIC_SLUDGE_PROPS)
    );
    public static final RegistryObject<FlowingFluid> TOXIC_SLUDGE_FLOWING = FLUIDS.register("toxic_sludge_flowing", () ->
        new ForgeFlowingFluid.Flowing(AFSUFluids.TOXIC_SLUDGE_PROPS)
    );
    public static final RegistryObject<LiquidBlock> TOXIC_SLUDGE_BLOCK = BLOCKS.register("toxic_sludge", () ->
        new LiquidBlock(TOXIC_SLUDGE, BlockBehaviour.Properties.of(Material.WATER).noCollission().strength(100.0F).noLootTable())
    );
    public static final RegistryObject<Item> TOXIC_SLUDGE_CELL = ITEMS.register("toxic_sludge_cell", () ->
        new CustomFluidCellItem(TOXIC_SLUDGE, new Item.Properties().stacksTo(64).tab(com.afsu.mod.AFSUMod.AFSU_TAB))
    );
    public static final ForgeFlowingFluid.Properties TOXIC_SLUDGE_PROPS = new ForgeFlowingFluid.Properties(
        TOXIC_SLUDGE_TYPE, TOXIC_SLUDGE, TOXIC_SLUDGE_FLOWING
    ).block(TOXIC_SLUDGE_BLOCK);

    public static void register(IEventBus bus) {
        FLUID_TYPES.register(bus);
        FLUIDS.register(bus);
        BLOCKS.register(bus);
        ITEMS.register(bus);
    }
}

